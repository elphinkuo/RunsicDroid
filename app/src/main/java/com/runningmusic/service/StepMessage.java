package com.runningmusic.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guofuming on 18/1/16.
 */
public class StepMessage implements Parcelable {
    public static final int NOTIFICATION_NONE = 0,
            NOTIFICATION_FAKE_STEP = 1,
            NOTIFICATION_START_REAL_STEP = 2,
            NOTIFICATION_REAL_STEP = 3,
            NOTIFICATION_FAKE_STOP = 4,
            NOTIFICATION_REAL_STOP = 5;

    public int notificationType;
    public int fakeStep;
    public int bpm;

    public static final Parcelable.Creator<StepMessage> CREATOR = new Parcelable.Creator<StepMessage>() {
        public StepMessage createFromParcel(Parcel in) {
            return new StepMessage(in);
        }

        public StepMessage[] newArray(int size) {
            return new StepMessage[size];
        }
    };

    public StepMessage(Parcel in) {
        notificationType = in.readInt();
        fakeStep = in.readInt();
        bpm = in.readInt();
    }

    public StepMessage() {
        notificationType = NOTIFICATION_NONE;
        fakeStep = 0;
        bpm = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(notificationType);
        out.writeInt(fakeStep);
        out.writeInt(bpm);
    }
}
