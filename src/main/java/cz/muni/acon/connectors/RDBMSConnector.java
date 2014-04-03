package cz.muni.acon.connectors;

import cz.muni.acon.providers.PostgreSQLProvider;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public class RDBMSConnector implements IRDBMSConnector{
    
    private static final String[] TABLES = {"TABLES"};
    
    private final PostgreSQLProvider provider;

    public RDBMSConnector(PostgreSQLProvider provider) {
        this.provider = provider;
    }

    public List<String> getTables() {
        List<String> tables = new ArrayList<String>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getTables(null, null, null, TABLES);
            while (rs.next()) {
                String tableName = rs.getString(3);
                tables.add(tableName);
            }
            provider.closeConnection();
        } catch (SQLException ex) {
            tables = null;
        }
        return tables;
    }

    public List<String> getColumns(String tableName) {
        List<String> columnsNames = new ArrayList<String>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString(1);
                columnsNames.add(columnName);
            }
            provider.closeConnection();
        } catch (SQLException ex) {
            columnsNames = null;
        }
        return columnsNames;
    }
    
    
}
