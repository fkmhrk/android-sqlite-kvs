package jp.fkmsoft.libs.sqlitekvs.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jp.fkmsoft.libs.sqlitekvs.KVS;
import jp.fkmsoft.libs.sqlitekvs.KVSException;

/**
 * Implementation of {@link jp.fkmsoft.libs.sqlitekvs.KVS}
 */
public abstract class KVSImpl<T> implements KVS<T> {

    private DBHelper mHelper;
    private SQLiteDatabase mDB;

    private static final String CLAUSE_ID_EQ = DBHelper.Columns.ID + "=?";

    protected KVSImpl(Context context, String dbName) {
        this.mHelper = new DBHelper(context, dbName);
        mDB = mHelper.getWritableDatabase();
        if (mDB == null) {
            throw new KVSException("failed to get writable database");
        }
        mDB.beginTransaction();
    }

    @Override
    public void put(String key, T value) {
        if (key == null) {
            throw new KVSException("key must not be null");
        }
        if (value == null) {
            delete(key);
            return;
        }
        // insert
        ContentValues values = new ContentValues();
        values.put(DBHelper.Columns.ID, key);
        values.put(DBHelper.Columns.VALUE, toBytes(value));
        try {
            long rowId = mDB.insert(DBHelper.TABLE_NAME, null, values);
            if (rowId != -1) {
                return;
            }
        } catch (SQLException e) {
            throw new KVSException(e);
        }

        // update
        try {
            String[] args = { key };
            mDB.update(DBHelper.TABLE_NAME, values, CLAUSE_ID_EQ, args);
        } catch (SQLException e) {
            throw new KVSException(e);
        }
    }

    private void delete(String key) {
        String[] args = { key };
        mDB.delete(DBHelper.TABLE_NAME, CLAUSE_ID_EQ, args);
    }

    @Override
    public T get(String key) {
        if (key == null) {
            throw new KVSException("key must not be null");
        }

        String[] args = { key };
        Cursor c = mDB.query(DBHelper.TABLE_NAME, null, CLAUSE_ID_EQ, args, null, null, null, "1");
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
    public List<T> getAll() {
        Cursor c = mDB.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (c == null) { return new ArrayList<T>(); }
        if (!c.moveToFirst()) {
            c.close();
            return new ArrayList<T>();
        }
        List<T> list = new ArrayList<T>(c.getCount());
        int valueIndex = c.getColumnIndex(DBHelper.Columns.VALUE);
        do {
            byte[] blob = c.getBlob(valueIndex);
            list.add(createFromBlob(blob));
        } while (c.moveToNext());
        c.close();

        return list;
    }

    @Override
    public boolean commit() {
        if (mDB == null) { return true; }
        try {
            mDB.setTransactionSuccessful();
            return true;
        } catch (IllegalStateException e) {
            return false;
        } finally {
            mDB.endTransaction();
            mDB.beginTransaction();
        }
    }

    @Override
    public boolean close() {
        if (mDB != null) {
            mDB.endTransaction();
            mDB.close();
        }
        mHelper.close();
        return true;
    }

    protected abstract T createFromBlob(byte[] bytes);

    protected abstract byte[] toBytes(T value);
}
