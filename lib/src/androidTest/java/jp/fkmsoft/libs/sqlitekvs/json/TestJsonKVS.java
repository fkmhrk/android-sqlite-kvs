package jp.fkmsoft.libs.sqlitekvs.json;

import android.test.AndroidTestCase;

import org.json.JSONObject;

import jp.fkmsoft.libs.sqlitekvs.KVSException;
import jp.fkmsoft.libs.sqlitekvs.util.DBUtils;

/**
 * Test for JsonKVS
 */
public class TestJsonKVS extends AndroidTestCase {
    private static final String TABLE_NAME = "data1";

    private JsonKVS mKvs;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBUtils.deleteDB(getContext(), TABLE_NAME);
        mKvs = new JsonKVS(getContext(), TABLE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mKvs.close();
    }

    public void test_0000_put_get() throws Exception {
        JSONObject json0 = mKvs.get("item");
        assertNull(json0);

        JSONObject json1 = new JSONObject();
        json1.put("name", "fkm");

        mKvs.put("item", json1);
        mKvs.commit();

        JSONObject json2 = mKvs.get("item");
        assertNotNull(json2);
        assertEquals(1, json2.length());
        assertTrue(json2.has("name"));
        assertEquals("fkm", json2.getString("name"));
    }

    public void test_0001_put_update() throws Exception {
        test_0000_put_get();

        JSONObject json1 = new JSONObject();
        json1.put("name", "newFKM");

        mKvs.put("item", json1);
        mKvs.commit();

        JSONObject json2 = mKvs.get("item");
        assertNotNull(json2);
        assertEquals(1, json2.length());
        assertTrue(json2.has("name"));
        assertEquals("newFKM", json2.getString("name"));
    }

    public void test_0002_put_delete() throws Exception {
        test_0000_put_get();

        mKvs.put("item", null);
        mKvs.commit();

        JSONObject json2 = mKvs.get("item");
        assertNull(json2);
    }

    public void test_0003_put_put() throws Exception {
        test_0000_put_get();

        JSONObject json1 = new JSONObject();
        json1.put("name", "newFKM");

        mKvs.put("item2", json1);
        mKvs.commit();

        JSONObject json2 = mKvs.get("item");
        assertNotNull(json2);
        assertEquals(1, json2.length());
        assertTrue(json2.has("name"));
        assertEquals("fkm", json2.getString("name"));

        json2 = mKvs.get("item2");
        assertNotNull(json2);
        assertEquals(1, json2.length());
        assertTrue(json2.has("name"));
        assertEquals("newFKM", json2.getString("name"));
    }

    public void test_0010_put_null() throws Exception {
        JSONObject json1 = new JSONObject();
        json1.put("name", "fkm");

        try {
            mKvs.put(null, json1);
            fail("KVSException must be thrown");
        } catch (KVSException e) {
            // ok
        }
    }

    public void test_0011_get_null() throws Exception {
        try {
            mKvs.get(null);
            fail("KVSException must be thrown");
        } catch (KVSException e) {
            // ok
        }
    }
}
