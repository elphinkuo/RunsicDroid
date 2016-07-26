package com.runningmusic.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioPlayer {
    protected MediaExtractor extractor;
    protected MediaCodec codec;
    protected AudioTrack track;
    protected MediaFormat format;
    protected Sonic sonic = new Sonic(44100, 2);
    protected PlayThread playThread;
    protected Runnable timeUpdate;
    protected OnTimeUpdateListener onTimeUpdateListener;
    protected OnCompletionListener onCompletionListener;
    protected OnErrorListener onErrorListener;
    protected STATE state = STATE.STOPPED;
    protected boolean playWhenLoaded = false;

    public enum STATE {
        LOADING, PLAYING, PAUSED, STOPPED
    }

    public enum ERROR {
        LOAD, PLAY
    }

    public AudioPlayer() {
        sonic.setQuality(1);
        startTimeUpdater();
    }

    public interface OnTimeUpdateListener {
        void onTimeUpdate( long time );
    }

    public interface OnCompletionListener {
        void onCompletion();
    }

    public interface OnErrorListener {
        void onError( ERROR type, Exception e );
    }

    public void setOnTimeUpdateListener(OnTimeUpdateListener listener) {
        onTimeUpdateListener = listener;
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        onCompletionListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
    }

    protected void startTimeUpdater() {
        final Handler handler = new Handler();
        timeUpdate = new Runnable() {
            @Override
            public void run() {
                if (onTimeUpdateListener != null) {
                    if (state == STATE.PLAYING) {
                        onTimeUpdateListener.onTimeUpdate(getCurrentTime());
                    }
                }
                handler.postDelayed(timeUpdate, 500);
            }
        };
        handler.post(timeUpdate);
    }

    public void load(final String path) {
        state = STATE.LOADING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                stop();
                extractor = new MediaExtractor();

                try {
                    extractor.setDataSource(path);
                } catch (IOException e) {
                    state = STATE.STOPPED;
                    extractor.release();
                    extractor = null;
                    if (onErrorListener != null) {
                        onErrorListener.onError(ERROR.LOAD, e);
                    }
                    return;
                }

                extractor.selectTrack(0);
                format = extractor.getTrackFormat(0);
                playThread = new PlayThread();

                if (playWhenLoaded) {
                    startPlay();
                } else {
                    state = STATE.PAUSED;
                }
            }
        }).start();
    }

    public long getDuration() {
        if (state == STATE.PLAYING || state == STATE.PAUSED) {
            return format.getLong(MediaFormat.KEY_DURATION) / 1000;
        } else {
            return -1L;
        }
    }

    public int getSampleRate() {
        return format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
    }

    public int getChannelCount() {
        return format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
    }

    public String getType() {
        return format.getString(MediaFormat.KEY_MIME);
    }

    protected void startPlay() {
        state = STATE.PLAYING;
        if (playThread.isAlive()) {
            playThread.play();
        } else {
            playThread.start();
        }
    }

    public void play() {
        if (state == STATE.LOADING) {
            playWhenLoaded = true;
        } else if (state == STATE.PAUSED) {
            startPlay();
        }
    }

    public void pause() {
        state = STATE.PAUSED;
        playThread.pause();
    }

    public void seek(long time) {
        extractor.seekTo(time * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
    }

    public void stop() {
        if (state != STATE.STOPPED) {
            if (playThread != null && playThread.isAlive()) {
                playThread.quit();
            }

            if (extractor != null) {
                extractor.release();
                extractor = null;
            }

            if (track != null) {
                track.release();
                track = null;
            }

            state = STATE.STOPPED;
        }
    }

    public STATE getState() {
        return state;
    }

    public long getCurrentTime() {
        return extractor.getSampleTime() / 1000;
    }

    public void setSpeed(double speed) {
        sonic.setSpeed((float) speed);
    }

    protected void initTrack() {
        int channelCount = getChannelCount();
        int sampleRate = getSampleRate();
        int channelConfig = channelCount == 2 ?
                AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;

        track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(
                        sampleRate,
                        channelConfig,
                        AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM);

        if (sonic.getSampleRate() != sampleRate) {
            sonic.setSampleRate(sampleRate);
        }

        if (sonic.getNumChannels() != channelCount) {
            sonic.setSampleRate(channelCount);
        }
    }

    protected void initCodec() {
        try {
            codec = MediaCodec.createDecoderByType(getType());
        } catch (IOException e) {
            if (onErrorListener != null) {
                onErrorListener.onError(ERROR.PLAY, e);
            }
        }

        codec.configure(format, null, null, 0);
        codec.start();
    }

    protected void processOutputBuffer(ByteBuffer buffer) {
        byte[] input = new byte[buffer.remaining()];
        byte[] output = new byte[input.length];
        int size;

        buffer.get(input);
        sonic.writeBytesToStream(input, input.length);
        while ((size = sonic.readBytesFromStream(output, output.length)) > 0) {
            track.write(output, 0, size);
        }
    }

    protected class PlayThread extends Thread {
        protected boolean paused = false;
        protected boolean stop = false;

        @Override
        public void run() {
            initTrack();
            initCodec();
            track.play();

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();

            BufferInfo bufferInfo = new BufferInfo();
            int sampleSize = 0;

            while (!stop && sampleSize >= 0) {
                try {
                    int inputBufferId = codec.dequeueInputBuffer(0);
                    if (inputBufferId >= 0) {
                        ByteBuffer inputBuffer = inputBuffers[inputBufferId];
                        sampleSize = extractor.readSampleData(inputBuffer, 0);
                        codec.queueInputBuffer(inputBufferId, 0, sampleSize, extractor.getSampleTime(), 0);
                        extractor.advance();
                    }

                    int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 0);
                    if (outputBufferId >= 0) {
                        processOutputBuffer(outputBuffers[outputBufferId]);
                        codec.releaseOutputBuffer(outputBufferId, false);
                    }

                    check();
                } catch (Exception e) {
                    if (onErrorListener != null) {
                        onErrorListener.onError(ERROR.PLAY, e);
                    }
                    break;
                }
            }

            if (!stop && onCompletionListener != null) {
                onCompletionListener.onCompletion();
            }

            codec.release();
        }

        protected void check() {
            synchronized (this) {
                if (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        if (onErrorListener != null) {
                            onErrorListener.onError(ERROR.PLAY, e);
                        }
                    }
                }
            }
        }

        protected void pause() {
            paused = true;
            track.pause();
        }

        protected void play() {
            synchronized (this) {
                paused = false;
                track.play();
                notify();
            }
        }

        protected void quit() {
            synchronized (this) {
                paused = false;
                stop = true;
                notify();
            }
        }
    }
}
