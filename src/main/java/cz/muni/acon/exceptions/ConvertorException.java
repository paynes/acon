package cz.muni.acon.exceptions;

/**
 *
 * @author Jan Koscak
 */
public class ConvertorException extends Exception {

    /**
     * Creates a new instance of <code>COnvertorException</code> without detail message.
     */
    public ConvertorException() {
    }


    /**
     * Constructs an instance of <code>COnvertorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ConvertorException(String msg) {
        super(msg);
    }
}
