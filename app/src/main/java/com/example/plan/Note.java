package com.example.plan;

public class Note {
    String mTitle;
    String mNote;

    public Note(String title, String note){
        mTitle = title;
        mNote = note;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getNote() {
        return mNote;
    }
}
