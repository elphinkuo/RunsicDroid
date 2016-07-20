package com.runningmusic.runninspire;

import com.runningmusic.service.RunsicService;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

import java.util.LinkedList;

/**
 * Created by guofuming on 18/1/16.
 */
public class BeatsCache {

//    private final int DEFAULT_SIZE = 20;
    private final int TEMPO_SIZE = 8;

    private final int BPM_LIST_SIZE = 6;

    private final int SwitchDuration = 8000;
    private final int SwitchBPMDelta = 12;


    private long previous = 0;
    private long now = 0;

    public LinkedList<Integer> bpmList;

    /*
     *
     */
    private long duration;

    public long averageDuration;
    private long averageDurationTemp;

    private int size;
    public int BPM;
    public int BPMTemp;
    private int delta;
    private float Variance;

    public BeatsCache() {
        bpmList = new LinkedList<Integer>();
    }

    public void add(int bpm) {
        if (bpmList.size() < BPM_LIST_SIZE) {
            bpmList.add(bpm);
        } else {
            bpmList.removeFirst();
            bpmList.add(bpm);
        }
        if (Util.DEBUG)
            Log.e("BEATCACHE", ""+ bpmList);
    }


    public float getVariance() {

        if (bpmList.size() < 3) {
            return 100;
        }
        int mean = getMean();
        int square = 0;
        for (int a : bpmList)
            square += (mean-a)*(mean-a);
        float variance = square/bpmList.size();
        if (Util.DEBUG)
            Log.e("BPM", "variance is " + variance);
        return square/bpmList.size();
    }

    public int getMean() {
        if (bpmList.size() < 3) {
            return 150;
        }
        int sum = 0;
        for (int a : bpmList)
            sum += a;
        int mean = sum/bpmList.size();
        return mean;
    }



    public int getBPMSize(){
        int size = 0;
        size = bpmList.size();
        return size;
    }

    public boolean getSwitchable(int bpm) {
        now = System.currentTimeMillis();
        if (this.getVariance() < SwitchBPMDelta && Math.abs(RunsicService.getInstance().currentMusicTempo - getMean()) > 12 && (now - previous) > SwitchDuration && bpmList.size() > 4 ) {
            previous = now;
            return true;
        } else {
            return false;
        }
    }

    public void BeatsCacheClear() {
        BPM = 0;
        BPMTemp = 0;
        bpmList.clear();
        now = 0;
        previous = 0;
        duration = 0;
        averageDuration = 0;
        averageDurationTemp = 0;
        size = 0;
        delta = 0;
    }

}
