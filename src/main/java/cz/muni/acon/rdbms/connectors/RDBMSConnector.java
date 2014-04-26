package cz.muni.acon.rdbms.connectors;

import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.graph.elements.Property;
import cz.muni.acon.rdbms.providers.PostgreSQLProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<String> getTables() throws ConvertorException {
        List<String> tables = new ArrayList<>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getTables(null, null, null, TABLES);
            while (rs.next()) {
                String tableName = rs.getString(3);
                tables.add(tableName);
            }
            return tables;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot get tables: " + ex);
        }
         finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }

    
    @Override
    public List<String> getColumns(String tableName) throws ConvertorException {
        List<String> columnsNames = new ArrayList<>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString(1);
                columnsNames.add(columnName);
            }
            return columnsNames;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot get columns: " + ex);
        } finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }
    
    @Override
    public Map<String,String> getForeignKeys(String tableName) throws ConvertorException {
        Map<String,String> foreignKeys = new HashMap<>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getImportedKeys(null, null, tableName);
            while (rs.next()) {
                String foreignKeyName = rs.getString(1);
                String primaryKeyName = rs.getString(2);
                foreignKeys.put(foreignKeyName, primaryKeyName);
            }
            return foreignKeys;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot get foreign keys: " + ex);
        } finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }
    
    @Override
    public List<Property> getData(String tableName, List<String> columnNames) throws ConvertorException{
        List<Property> data = new ArrayList<>();
        try {
            ResultSet rs = this.createSql(tableName, columnNames).executeQuery();
            while (rs.next()) {
                for (String column : columnNames) {
                    final Property property = new Property(column, rs.getObject(rs.findColumn(column)));
                    data.add(property);
                }
            }
            return data;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot get data: " + ex);
        } finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }
    
    private PreparedStatement createSql(String tableName, List<String> columnNames) throws ConvertorException {
        String columns = "";
        for (int i = 0; i < columnNames.size(); i++) {
            if (i < columnNames.size() - 1) {
                columns += columnNames.get(i) + ",";
            } else {
                columns += columnNames.get(i);
            }
        }
        
        String sql = "SELECT " + columns + " FROM " + tableName;
        
        try {
            PreparedStatement st = provider.getConnection().prepareStatement(sql);
            return st;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("Cannot prepare statement: " + ex);
        } finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }
}
