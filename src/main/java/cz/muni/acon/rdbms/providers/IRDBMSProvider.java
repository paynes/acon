package cz.muni.acon.rdbms.providers;

import cz.muni.acon.exceptions.ConvertorException;

/**
 *
 * @author Jan Koscak
 * @param <T>
 */
public interface IRDBMSProvider<T> {
    
    public T getConnection() throws ConvertorException;
    
    public void closeConnection() throws ConvertorException;
}
