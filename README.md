android-sqlite-kvs
==================

key-value storage implemented by SQLite 

How to use
----------
install this library to your local maven repository

    sh gradlew clean uploadArchives

add the following dependency entry to your build.gradle

    compile 'jp.fkmsoft.libs:sqlite-kvs:1.+'

extends KVSImpl<T> with your data class. 

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

use in your code. 

    // json1, json2 is JSONObject
    KVS<JSONObject> kvs = new JsonKVS(getContext(), "appUser");
    kvs.put("key1", json1);
    kvs.put("key2", json2);
    // commit changes
    kvs.commit();
    
    // gey by key
    JSONObject data = kvs.get("key1");
    JSONObject data2 = kvs.get("unknownKey");
    assert data2 == null;
    
    // close database
    kvs.close();
    
