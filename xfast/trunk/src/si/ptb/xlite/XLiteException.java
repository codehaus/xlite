package si.ptb.xlite;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 10:15:47 PM
 */
public class XLiteException extends RuntimeException {    //todo FIX THIS EXCEPTION CLASS - IT DOES NOT SHOW PROPER MESSAGES

    private Throwable cause;

    public XLiteException() {
        this("", null);
    }

    public XLiteException(Throwable cause) {
        this("", cause);
    }

    public XLiteException(String message) {
        this(message, null);
    }

    public XLiteException(String message, Throwable cause) {
        super(message + (cause == null ? "" : " : " + cause.getMessage()));
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
