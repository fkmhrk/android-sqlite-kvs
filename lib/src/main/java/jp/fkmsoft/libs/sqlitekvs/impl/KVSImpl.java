package jp.fkmsoft.libs.sqlitekvs.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import jp.fkmsoft.libs.sqlitekvs.KVS;
import jp.fkmsoft.libs.sqlitekvs.KVSException;

/**
 * Implementation of {@link jp.fkmsoft.libs.sqlitekvs.KVS}
 */
public abstract class KVSImpl<T> implements KVS<T> {

    private DBHelper mHelper;
    private SQLiteDatabase mDB;

    protected KVSImpl(Context context, String dbName) {
        this.mHelper = new DBHelper(context, dbName);
    }

    @Override
    public void put(String key, T value) {
        if (mDB == null) {
            mDB = mHelper.getWritableDatabase();
            mDB.beginTransaction();
        }
        // insert
        ContentValues values = new ContentValues();
        values.put(DBHelper.Columns.ID, key);
        values.put(DBHelper.Columns.VALUE, toBytes(value));
        try {
            mDB.insert(DBHelper.TABLE_NAME, null, values);
            return;
        } catch (SQLiteConstraintException e1) {
            // nop
        } catch (SQLException e) {
            throw new KVSException(e);
        }

        // update
        try {
            String where = DBHelper.Columns.ID + "=?";
            String[] args = { key };
            mDB.update(DBHelper.TABLE_NAME, values, where, args);
        } catch (SQLException e) {
            throw new KVSException(e);
        }
    }

    @Override
    public T get(String key) {
        if (mDB == null){
            mDB = mHelper.getWritableDatabase();
            mDB.beginTransaction();
        }
        String selection = DBHelper.Columns.ID + "=?";
        String[] args = { key };
        Cursor c = mDB.query(DBHelper.TABLE_NAME, null, selection, args, null, null, null, "1");
        if (c == null) { return null; }
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        int valueIndex = c.getColumnIndex(DBHelper.Columns.VALUE);
        byte[] blob = c.getBlob(valueIndex);
        c.close();

        return createFromBlob(blob);
    }

    @Override
    public boolean commit() {
        if (mDB == null) { return true; }
        try {
            mDB.setTransactionSuccessful();
            mDB.close();
            mDB = null;
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public boolean close() {
        mHelper.close();
        return true;
    }

    protected abstract T createFromBlob(byte[] bytes);

    protected abstract byte[] toBytes(T value);
}
