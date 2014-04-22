package jp.fkmsoft.libs.sqlitekvs;

/**
 * Exception
 */
public class KVSException extends RuntimeException {
    public KVSException() {
        super();
    }

    public KVSException(String detailMessage) {
        super(detailMessage);
    }

    public KVSException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public KVSException(Throwable throwable) {
        super(throwable);
    }
}
