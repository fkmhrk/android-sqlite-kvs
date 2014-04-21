package jp.fkmsoft.libs.sqlitekvs.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Helper class for {@link android.database.sqlite.SQLiteOpenHelper}
 */
class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    static final String TABLE_NAME = "kvs";

    interface Columns extends BaseColumns {
        static final String ID = "id";
        static final String VALUE = "value";
    }

    private static final String SQL_CREATE =
            "create table " + TABLE_NAME + "(" +
            Columns._ID + " integer primary key autoincrement," +
            Columns.ID + " text," +
            Columns.VALUE + " blob)";
    private static final String SQL_INDEX =
            "create unique index idx_" + TABLE_NAME +
            " on " + TABLE_NAME +
            "(" + Columns.ID + ")";

    DBHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
        db.execSQL(SQL_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
