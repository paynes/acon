package cz.muni.acon.providers;

import cz.muni.acon.providers.IRDBMSProvider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Jan Koscak
 */
public final class PostgreSQLProvider implements IRDBMSProvider{
    
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;
    
    public PostgreSQLProvider(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    public PostgreSQLProvider(String ip, String port, String DBName, String user, String password) {
        this(String.format("jdbc:postgresql://%s:%s/%s", ip,port,DBName),user,password);
    }
    
    private Connection init(String path, String user, String password) {
        try {
            this.connection = DriverManager.getConnection(path, user, password);
        } catch (SQLException ex) {
            this.connection = null;
        }
        return this.connection;
    }
    
    public Connection getConnection() {
        if (this.connection == null) {
            return this.init(this.url,this.user,this.password);
        } else {
            return this.connection;
        }
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
        this.connection = null;
    }
    
    
}
