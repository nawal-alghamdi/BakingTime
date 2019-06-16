package com.example.bakingtime.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
    // Id for each step
    private int stepId;
    // Short step description
    private String shortDescription;
    // The full step description
    private String description;
    // The videoUrl that explain the step
    private String videoUrl;

    public Step(int stepId, String shortDescription, String description, String videoUrl) {
        this.stepId = stepId;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoUrl = videoUrl;
    }

    protected Step(Parcel in) {
        stepId = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoUrl = in.readString();
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stepId);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoUrl);
    }
}
