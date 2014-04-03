package cz.muni.acon.exceptions;

/**
 *
 * @author Jan Koscak
 */
public class NoConnectionToDBException extends Exception {

    /**
     * Creates a new instance of <code>NoConnectionToDBException</code> without
     * detail message.
     */
    public NoConnectionToDBException() {
    }

    /**
     * Constructs an instance of <code>NoConnectionToDBException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoConnectionToDBException(String msg) {
        super(msg);
    }
}
