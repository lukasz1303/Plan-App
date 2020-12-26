package com.example.plan.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PlanContract {

    private PlanContract(){}
    public static final String CONTENT_AUTHORITY = "com.example.plan";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PLAN = "plan";
    public static final String PATH_NOTES = "notes";

    public static final class PlanEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PLAN);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAN;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAN;

        public static final String TABLE_NAME = "plan";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_LECTURER = "lecturer";
        public static final String COLUMN_TYPE_OF_CLASSES = "type_of_classes";
        public static final String COLUMN_ROOM = "room";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_DAY = "day";

        public static final int TYPE_LECTURE = 0;
        public static final int TYPE_EXERCISES = 1;
        public static final int TYPE_LABS = 2;
        public static final int TYPE_NONE = 3;

        public static final int COLOR_TRANSPARENT = 0;
        public static final int COLOR_LIGHT_BLUE = 1;
        public static final int COLOR_DARK_BLUE = 2;
        public static final int COLOR_GREEN = 3;
        public static final int COLOR_LIGHT_GREEN = 4;
        public static final int COLOR_PURPLE = 5;
        public static final int COLOR_ORANGE = 6;
        public static final int COLOR_LIGHT_ORANGE = 7;
        public static final int COLOR_RED = 8;
        public static final int COLOR_YELLOW = 9;
        public static final int COLOR_DARK_YELLOW = 10;
        public static final int COLOR_GREY = 11;
        public static final int COLOR_WHITE = 12;

        public static final int DAY_MONDAY = 0;
        public static final int DAY_TUESDAY = 1;
        public static final int DAY_WEDNESDAY = 2;
        public static final int DAY_THURSDAY = 3;
        public static final int DAY_FRIDAY = 4;
    }

    public static final class NotesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NOTES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

        public static final String TABLE_NAME = "notes";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ID_OF_SUBJECT = "id_of_subject";
        public static final String COLUMN_TITLE= "title";
        public static final String COLUMN_CONTENT = "content";

    }
}
