package jp.fkmsoft.libs.sqlitekvs;

/**
 * Root interface of Key-Value storage
 */
public interface KVS<T> {
    void put(String key, T value);

    T get(String key);

    boolean commit();

    boolean close();
}
