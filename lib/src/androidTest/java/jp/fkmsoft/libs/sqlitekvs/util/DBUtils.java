package jp.fkmsoft.libs.sqlitekvs.util;

import android.content.Context;

import java.io.File;

/**
 * Utility class for DB
 */
public class DBUtils {
    public static void deleteDB(Context context, String name) {
        File file = context.getDatabasePath(name + ".db");
        if (file.exists()) {
            file.delete();
        }

    }
}
