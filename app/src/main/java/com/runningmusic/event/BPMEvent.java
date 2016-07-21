package com.runningmusic.event;

/**
 * Created by guofuming on 24/5/16.
 */
public class BPMEvent {

    public int bpm;

    public int step;
    


    public BPMEvent(int stepInput, int bpmInput) {
        this.step = stepInput;
        this.bpm = bpmInput;
    }
}
