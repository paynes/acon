package cz.muni.acon.rdbms.connectors;

import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.convertor.Property;
import cz.muni.acon.rdbms.providers.PostgreSQLProvider;
import cz.muni.acon.models.NodeModel;
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
    
    private static final String[] TABLES = {"TABLE"};
    private static final Integer TABLE = 3;
    private static final Integer PRIMARY_KEY = 4;
    private static final Integer FOREIGN_KEY = 8;
    private static final Integer FOREIGN_KEY_NAME = 12;
    private String schema;
    
    private final PostgreSQLProvider provider;

    public RDBMSConnector(PostgreSQLProvider provider) {
        this.provider = provider;
        this.schema = null;
    }
       
    @Override
    public List<NodeModel> getNodeModels(final String tableName) throws ConvertorException {
        try {
            List<NodeModel> nodeModels = new ArrayList<NodeModel>();
            List<String> pkColumns = this.getPkColumnsNames(tableName);        
            List<String> fkColumns = this.getFKColumnsNames(tableName);
            List<String> columns = this.reduceColumns(this.getColumnsNames(tableName), fkColumns);
            Map<String,Map<String,String>> fkAndPKPairs = this.getFkAndPkPairs(tableName);
            String schema = "";
            if (this.schema != null && !this.schema.isEmpty()) {
                schema = this.schema + ".";
            }
            PreparedStatement ps = provider.getConnection().prepareStatement("SELECT * FROM " + schema + tableName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NodeModel nodeModel = new NodeModel();
                //sparovane primarne kluce
                List<Property> pkData = this.pairedData(pkColumns, rs);
                nodeModel.setPkList(pkData);
                
                //sparovane stlpce
                List<Property> columnData = this.pairedData(columns, rs);
                nodeModel.setColumnsList(columnData);
                
                //sparovane cudzie kluce
                List<Property> fkData = this.pairedData(fkColumns, rs);
                List<List<Property>> fkPairedToPk = this.pairedFkData(fkData, fkAndPKPairs);
                nodeModel.setFkList(fkPairedToPk);
                
                nodeModel.setTableName(tableName);
                nodeModels.add(nodeModel);
            }
            return nodeModels;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("" + ex);
        } finally {
            try {
                provider.closeConnection();
            } catch (ConvertorException ex) {
                throw new ConvertorException(ex.getMessage());
            }
        }
    }
    
    @Override
    public void setSchema(String schema) {
        this.schema = schema;
    }
    
    private List<List<Property>> pairedFkData(List<Property> fkData, Map<String,Map<String,String>> pairs) {
        List<List<Property>> pkData = new ArrayList<List<Property>>();
        for (String s : pairs.keySet()) {
            List<Property> result = new ArrayList<Property>();
            Map<String,String> keyPairs = pairs.get(s);
            for (Property prop : fkData) {
                if (keyPairs.containsKey(prop.getPropertyKey())) {
                    Property property = new Property(keyPairs.get(prop.getPropertyKey()), prop.getPropertyValue());
                    result.add(property);
                }
            }
            pkData.add(result);
        }
        return pkData;
    }
    
    private Map<String,Map<String,String>> getFkAndPkPairs(String tableName) throws ConvertorException {
        try {
            ResultSet rs = provider.getConnection().getMetaData().getImportedKeys(null, null, tableName);
            Map<String,Map<String,String>> fkColumns = new HashMap<String,Map<String,String>>();
            while (rs.next()) {
                if (fkColumns.containsKey(rs.getString(FOREIGN_KEY_NAME))) {
                    fkColumns.get(rs.getString(FOREIGN_KEY_NAME)).put(rs.getString(FOREIGN_KEY), rs.getString(PRIMARY_KEY));
                } else  {
                    Map<String,String> fkPair = new HashMap<String,String>();
                    fkPair.put(rs.getString(FOREIGN_KEY), rs.getString(PRIMARY_KEY));
                    fkColumns.put(rs.getString(FOREIGN_KEY_NAME), fkPair);
                }
            }
            return fkColumns;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("" + ex);
        }
    }
    
    private List<Property> pairedData(List<String> columns, ResultSet rs) throws SQLException {
        List<Property> pairs = new ArrayList<Property>();
        for (String column : columns) {
            Property prop = new Property(column, rs.getObject(rs.findColumn(column)));
            pairs.add(prop);
        }
        return pairs;
    }
    
    private List<String> reduceColumns(List<String> columns, List<String> fkColumns) {
        for (String column : fkColumns) {
            if (columns.contains(column)) {
                columns.remove(column);
            }
        }
        return columns;
    }
    
    private List<String> getColumnsNames(String tableName) throws ConvertorException {
        try {
            ResultSet rs = provider.getConnection().getMetaData().getColumns(null, null, tableName, null);
            List<String> columns = new ArrayList<String>();
            while (rs.next()) {
                columns.add(rs.getString(PRIMARY_KEY));
            }
            return columns;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("" + ex);
        }
    }
    
    private List<String> getFKColumnsNames(String tableName) throws ConvertorException {
        try {
            ResultSet rs = provider.getConnection().getMetaData().getImportedKeys(null, null, tableName);
            List<String> fkColumns = new ArrayList<String>();
            while (rs.next()) {
                fkColumns.add(rs.getString(FOREIGN_KEY));
            }
            return fkColumns;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("" + ex);
        }
    }
    
    private List<String> getPkColumnsNames(String tableName) throws ConvertorException {
        try {
            ResultSet rs = provider.getConnection().getMetaData().getPrimaryKeys(null, null, tableName);
            List<String> pkColumns = new ArrayList<String>();
            while (rs.next()) {
                pkColumns.add(rs.getString(PRIMARY_KEY));
            }
            return pkColumns;
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        } catch (SQLException ex) {
            throw new ConvertorException("" + ex);
        }
    }

    @Override
    public List<String> getTables() throws ConvertorException {
        List<String> tables = new ArrayList<String>();
        try {
            ResultSet rs = provider.getConnection().getMetaData().getTables(null, null, null, TABLES);
            while (rs.next()) {
                String tableName = rs.getString(TABLE);
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
}
