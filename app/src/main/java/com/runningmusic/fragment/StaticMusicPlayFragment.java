package com.runningmusic.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.androidquery.AQuery;
import com.runningmusic.adapter.HorizontalAdapter;
import com.runningmusic.music.CurrentMusic;
import com.runningmusic.music.CurrentMusicList;
import com.runningmusic.music.Music;
import com.runningmusic.music.MusicPlayCallback;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.runninspire.R;
import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Util;
import com.runningmusic.view.Blur;
import com.runningmusic.view.RecyclerViewPager;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaticMusicPlayFragment extends Fragment implements Observer, View.OnClickListener, OnBackPressedListener{
    private static String TAG = StaticMusicPlayFragment.class.getName();

    private RelativeLayout wholeRelativeLayout;
    private RunsicService runsicService;
    private RelativeLayout musicStaticHeader;
    private RecyclerViewPager recyclerView;
    private ImageLoader imageLoader;
    private TextView songName;
    private TextView artistName;
    private AQuery aQuery_;
    private Context context;

    private Music currentMusic;
    private ArrayList<Music> musicCurrentStaticList;

    private OnStaticMusicPlayFragmentClose mCallBack;

    private MusicPlayCallback musicPlayCallback;





    public StaticMusicPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        runsicService = RunsicService.getInstance();
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_static_music_play, container, false);
        aQuery_ = new AQuery(view);
        wholeRelativeLayout = (RelativeLayout) view.findViewById(R.id.fragment_static_music_play);
        musicStaticHeader = (RelativeLayout) view.findViewById(R.id.music_static_header);
        imageLoader = ImageSingleton.getInstance(this.getActivity()).getImageLoader();
        songName = (TextView) view.findViewById(R.id.music_static_title);
        artistName = (TextView) view.findViewById(R.id.music_static_artist);

        aQuery_.id(R.id.static_music_next).clickable(true).clicked(this);
        aQuery_.id(R.id.static_music_previous).clickable(true).clicked(this);
        aQuery_.id(R.id.static_music_back).clickable(true).clicked(this);
        aQuery_.id(R.id.static_music_play_or_pause).clickable(true).clicked(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerViewPager) view.findViewById(R.id.static_list);
        recyclerView.setLayoutManager(layoutManager);
        if (Util.DEBUG)
            Log.e(TAG, "" + runsicService.musicCurrentList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wholeRelativeLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }

        RunsicService.getInstance().addCurrentMusicObserver(this);
        RunsicService.getInstance().addCurrentListObserver(this);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
//                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
//                mPositionText.setText("First: " + mRecyclerViewPager.getFirstVisiblePosition());
                if (recyclerView == null) return;
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getWidth();
                int padding = (Util.getScreenWidth() - width) / 2;
//                int width = Util.getScreenWidth();
//                int padding = 0;

                Log.e("DEBUG", "width is " + width + " padding is " + padding);
//                mCountText.setText("Count: " + childCount);

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
                    float rate = 0;
                    ;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                        v.setScaleX(1 - rate * 0.1f);

                    } else {
                        //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                        if (v.getLeft() <= width - padding) {
                            rate = (width - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setScaleX(0.9f + rate * 0.1f);
                    }
                }
            }
        });
        recyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                if (Util.DEBUG) {
                    Log.d("test", "oldPosition:" + oldPosition + " newPosition:" + newPosition);
                }
                Music music = musicCurrentStaticList.get(newPosition);
                RunsicService.getInstance().musicCurrent.setCurrentMusic(music);
            }
        });

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (recyclerView.getChildCount() < 3) {
                    if (recyclerView.getChildAt(1) != null) {
                        if (recyclerView.getCurrentPosition() == 0) {
                            View v1 = recyclerView.getChildAt(1);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        } else {
                            View v1 = recyclerView.getChildAt(0);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        }
                    }
                } else {
                    if (recyclerView.getChildAt(0) != null) {
                        View v0 = recyclerView.getChildAt(0);
                        v0.setScaleY(0.9f);
                        v0.setScaleX(0.9f);
                    }
                    if (recyclerView.getChildAt(2) != null) {
                        View v2 = recyclerView.getChildAt(2);
                        v2.setScaleY(0.9f);
                        v2.setScaleX(0.9f);
                    }
                }

            }
        });
        musicPlayCallback = RunsicService.getInstance();
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void update(Observable observable, Object data) {

        if (observable instanceof CurrentMusic) {

            CurrentMusic currentMusic = (CurrentMusic) observable;
            Music music = currentMusic.getCurrentMusic();
            this.currentMusic = music;
            recyclerView.scrollToPosition(musicCurrentStaticList.indexOf(music));
            songName.setText(music.title);
            artistName.setText(music.artist);
            imageLoader.get(music.coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onResponse");
                    if (isImmediate && imageContainer.getBitmap() == null) return;
//                    wholeRelativeLayout.setBackground(new BitmapDrawable(imageContainer.getBitmap()));

                    new AsyncTask<String, Integer, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(String... params) {

//                            imageContainer.getBitmap().compress(0, 20, )
                            ;
                            return Blur.fastblur(context, ThumbnailUtils.extractThumbnail(imageContainer.getBitmap(), 150, 250), 80);
                        }

                        @Override
                        protected void onPostExecute(Bitmap result) {
                            if (result != null) {
                                wholeRelativeLayout.setBackground(new BitmapDrawable(result));
                            }
                        }

                    }.execute();
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onErrorResponse");
                }
            });
        } else if (observable instanceof CurrentMusicList) {
            if (Util.DEBUG)
                Log.e(TAG, "onReceive List Change");
            CurrentMusicList musicList = (CurrentMusicList) observable;
            musicCurrentStaticList = musicList.getCurrentMusicList();
            recyclerView.setAdapter(new HorizontalAdapter(context, R.layout.horizontal_item, musicCurrentStaticList));
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RunsicService.getInstance().deleteCurrentMusicObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        context = this.getActivity();


        if (RunsicService.getInstance().musicCurrent.getCurrentMusic()!=null) {
            currentMusic = RunsicService.getInstance().musicCurrent.getCurrentMusic();
            songName.setText(currentMusic.title);
            artistName.setText(currentMusic.artist);
        }

        if (RunsicService.getInstance().musicCurrentList!=null ) {
            if (Util.DEBUG)
                Log.e(TAG, "set adapter currentList");
            musicCurrentStaticList = RunsicService.getInstance().musicCurrentList.getCurrentMusicList();
            recyclerView.setAdapter(new HorizontalAdapter(this.getActivity(), R.layout.horizontal_item, musicCurrentStaticList));
        }
        if (musicCurrentStaticList !=null && musicCurrentStaticList.contains(currentMusic)) {
            recyclerView.scrollToPosition(musicCurrentStaticList.indexOf(currentMusic));
        }

        if (RunsicService.getInstance().musicCurrent.getCurrentMusic()!=null) {
            imageLoader.get(RunsicService.getInstance().musicCurrent.getCurrentMusic().coverURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(final ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onResponse");
                    if (isImmediate && imageContainer.getBitmap() == null) return;
                    new AsyncTask<String, Integer, Bitmap>() {

                        @Override
                        protected Bitmap doInBackground(String... params) {
                            return Blur.fastblur(context, ThumbnailUtils.extractThumbnail(imageContainer.getBitmap(), 150, 250), 80);
                        }

                        @Override
                        protected void onPostExecute(Bitmap result) {
                            if (result != null) {
                                wholeRelativeLayout.setBackground(new BitmapDrawable(result));
                            }
                        }

                    }.execute();
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (Util.DEBUG)
                        Log.e(TAG, "onErrorResponse");
                }
            });
        }

        if (RunsicService.getInstance().getPlayerStatus()) {
            aQuery_.id(R.id.static_music_play_or_pause).background(R.mipmap.yinyuejiemian_pause);
        } else {
            aQuery_.id(R.id.static_music_play_or_pause).background(R.mipmap.yinyuejiemian_play);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallBack = (OnStaticMusicPlayFragmentClose) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " music implement OnStaticMusicPlayFragment");
        }
    }

    @Override
    public void onClick(View v) {
        if (Util.DEBUG)
            Log.e(TAG, "" + v.getId());
        switch (v.getId()) {
            case R.id.static_music_back:
                mCallBack.onStaticMusicPlayFragmentClose();
                break;

            case R.id.static_music_play_or_pause:
                boolean play = RunsicService.getInstance().getPlayerStatus();
                if (!play) {
                    musicPlayCallback.onMusicGoOn();
                    aQuery_.id(R.id.static_music_play_or_pause).background(R.mipmap.yinyuejiemian_pause);
                } else {
                    musicPlayCallback.onMusicPause();
                    aQuery_.id(R.id.static_music_play_or_pause).background(R.mipmap.yinyuejiemian_play);
                }

                break;

            case R.id.static_music_previous:
                int positionPrevious = recyclerView.getCurrentPosition();
                if (Util.DEBUG)
                    Log.e(TAG, "music previous + position is " + positionPrevious + " size is " + recyclerView.getChildCount());
                if (positionPrevious <=0) {
                    return;
                } else if (positionPrevious >= musicCurrentStaticList.size()) {
                    return;
                } else {
                    positionPrevious -= 1;
                    recyclerView.smoothScrollToPosition(positionPrevious);
                    Music music = musicCurrentStaticList.get(positionPrevious);
                    songName.setText(music.title);
                    artistName.setText(music.artist);
                    aQuery_.id(R.id.music_control_current_bpm).text(music.tempo);
                    RunsicService.getInstance().musicCurrent.setCurrentMusic(music);
                }
                break;
            case R.id.static_music_next:
                int positionNext = recyclerView.getCurrentPosition();
                if (Util.DEBUG)
                    Log.e(TAG, "music next + position is " + positionNext + " size is " + musicCurrentStaticList.size());
                if (positionNext <0) {
                    return;
                } else if (positionNext >= (musicCurrentStaticList.size())) {
                    return;
                } else if (positionNext == (musicCurrentStaticList.size()-1)) {
                    RunsicService.getInstance().motionMusicChange(currentMusic.tempo);
                } else {
                    positionNext += 1;
                    recyclerView.smoothScrollToPosition(positionNext);
                    Music music = musicCurrentStaticList.get(positionNext);
                    songName.setText(music.title);
                    artistName.setText(music.artist);
                    aQuery_.id(R.id.music_control_current_bpm).text(music.tempo);
                    RunsicService.getInstance().musicCurrent.setCurrentMusic(music);
                }
                break;
            default:

                break;



        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (Util.DEBUG)
            Log.e(TAG, "back");
        mCallBack.onStaticMusicPlayFragmentClose();
    }

    public interface OnStaticMusicPlayFragmentClose {
        public void onStaticMusicPlayFragmentClose();
    }

}
