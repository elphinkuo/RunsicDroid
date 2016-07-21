package com.runningmusic.event;

/**
 * Created by guofuming on 21/7/16.
 */
public class LocationChangedEvent {

    public double distance;
    public double speed;

    public LocationChangedEvent(double distanceInput, double speedInput) {
        this.distance = distanceInput;
        this.speed = speedInput;
    }
}
