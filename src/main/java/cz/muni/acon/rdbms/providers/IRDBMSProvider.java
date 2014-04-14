package cz.muni.acon.providers;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Jan Koscak
 */
public interface IRDBMSProvider {
    
    public Connection getConnection();
    
    public void closeConnection() throws SQLException;
}
