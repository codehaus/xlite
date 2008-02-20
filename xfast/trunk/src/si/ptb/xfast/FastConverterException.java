package si.ptb.fastconverter;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 10:15:47 PM
 */
public class FastConverterException extends RuntimeException {

    private String nodeName;
    private String nodeValue;
    private Class targetClass;
    private String fieldName;
    private Class fieldType;
    private Throwable cause;



    public FastConverterException(String message) {
        super(message);
    }

    public FastConverterException(String message, String nodeName, String nodeValue , Class targetClass, String fieldName, Class fieldType){
        super(message);
        this.nodeName=nodeName;
        this.nodeValue =nodeValue;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
    public FastConverterException(String message, Throwable cause, String nodeName, String nodeValue , Class targetClass, String fieldName, Class fieldType){
        super(message);
        this.nodeName=nodeName;
        this.nodeValue =nodeValue;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.cause = cause;
    }

    public String getMessage() {
        StringBuilder msg = new StringBuilder();
        msg.append(super.getMessage());

        msg.append("\n   node name: ").append(nodeName);
        msg.append("\n  node value: ").append(nodeValue);
        msg.append("\ntarget class: ").append(targetClass);
        msg.append("\n  field name: ").append(fieldName);
        msg.append("\n  field type: ").append(fieldType.getName());
        if(cause != null){
            msg.append("\nCaused by: : ").append(cause.getMessage());                      
        }

        return msg.toString();
    }
}
