package com.ss.solutions.call;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


class PostRequestData {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("recordingLayout")
    @Expose
    private String recordingLayout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRecordingLayout() {
        return recordingLayout;
    }

    public void setRecordingLayout(String recordingLayout) {
        this.recordingLayout = recordingLayout;
    }
}
