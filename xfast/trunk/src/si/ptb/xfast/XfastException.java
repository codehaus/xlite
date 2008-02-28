package si.ptb.xfast;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 10:15:47 PM
 */
public class XfastException extends RuntimeException {    //todo FIX THIS EXCEPTION CLASS - IT DOES NOT SHOW PROPER MESSAGES

    private Throwable cause;

    public XfastException() {
        this("", null);
    }

    public XfastException(Throwable cause) {
        this("", cause);
    }

    public XfastException(String message) {
        this(message, null);
    }

    public XfastException(String message, Throwable cause) {
        super(message + (cause == null ? "" : " : " + cause.getMessage()));
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
