package jp.fkmsoft.libs.sqlitekvs;

import java.util.List;

/**
 * Root interface of Key-Value storage
 */
public interface KVS<T> {
    void put(String key, T value);

    T get(String key);

    List<T> getAll();

    boolean commit();

    boolean close();
}
