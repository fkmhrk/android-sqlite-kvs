package jp.fkmsoft.libs.sqlitekvs.json;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import jp.fkmsoft.libs.sqlitekvs.impl.KVSImpl;

/**
 * Target class
 */
public class JsonKVS extends KVSImpl<JSONObject> {
    private static final String CHARSET = "utf-8";

    protected JsonKVS(Context context, String dbName) {
        super(context, dbName);
    }

    @Override
    protected JSONObject createFromBlob(byte[] bytes) {
        try {
            String body = new String(bytes, CHARSET);
            return new JSONObject(body);
        } catch (UnsupportedEncodingException e) {
            return new JSONObject();
        } catch (JSONException e2) {
            return new JSONObject();
        }

    }

    @Override
    protected byte[] toBytes(JSONObject value) {
        try {
            return value.toString().getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }
}
