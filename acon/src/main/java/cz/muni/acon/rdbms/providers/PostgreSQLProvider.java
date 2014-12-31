package cz.muni.acon.rdbms.providers;

import com.google.common.base.Preconditions;
import cz.muni.acon.exceptions.ConvertorException;
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
    
    public PostgreSQLProvider(final String url,final String user,String password) throws IllegalArgumentException {
        Preconditions.checkArgument(url != null && !url.isEmpty(), "Postgres url should not be bull.");
        Preconditions.checkArgument(user != null && !user.isEmpty(), "Postgres user should not be bull.");
        Preconditions.checkArgument(password != null && !password.isEmpty(), "Postgres password should not be bull.");
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    private Connection init(final String path,final String user, final String password) throws ConvertorException {
        try {
            this.connection = DriverManager.getConnection(path, user, password);
        } catch (SQLException ex) {
            throw new ConvertorException(ex.getLocalizedMessage());
        }
        return this.connection;
    }
    
    @Override
    public Connection getConnection() throws ConvertorException {
        if (this.connection == null) {
            try {
                return this.init(this.url,this.user,this.password);
            } catch (ConvertorException ex) {
                throw new ConvertorException("Cannot create connection to RDBMS database: " + ex);
            }
        } else {
            return this.connection;
        }
    }

    @Override
    public void closeConnection() throws ConvertorException {
        try {
            this.connection.close();
            this.connection = null;
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot close connection to RDBMS: " + ex);
        }
    }
    
    
}
