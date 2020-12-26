package com.example.plan;

public class Subject {

    private String mStartTime;
    private String mEndTime;
    private String mName;
    private String mLecturer;
    private String mRoom;
    private static int NO_IMAGE_PROVIDED = -1;
    private int mColorResourceId = NO_IMAGE_PROVIDED;

    public Subject(String startTime, String endTime, String name, String lecturer, String room,int color) {
        mStartTime = startTime;
        mEndTime = endTime;
        mName = name;
        mLecturer = lecturer;
        mRoom = room;
        mColorResourceId = color;
    }

    public Subject(String startTime, String endTime, String name, String lecturer, String room) {
        mStartTime = startTime;
        mEndTime = endTime;
        mName = name;
        mLecturer = lecturer;
        mRoom = room;
    }

    public String getStartTime(){
        return mStartTime;
    }
    public String getEndTime(){
        return mEndTime;
    }
    public String getName(){
        return mName;
    }
    public String getLecturer(){
        return mLecturer;
    }
    public String getRoom(){
        return mRoom;
    }
    public int getColorResourceId(){
        return mColorResourceId;
    }
    public boolean hasColor(){
        return mColorResourceId!=NO_IMAGE_PROVIDED;
    }
}
