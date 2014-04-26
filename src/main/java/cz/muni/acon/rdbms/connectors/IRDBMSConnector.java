package cz.muni.acon.rdbms.connectors;

import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.graph.elements.Property;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Koscak
 */
public interface IRDBMSConnector {

    public List<String> getTables() throws ConvertorException;
    
    public List<String> getColumns(final String tableName) throws ConvertorException;
    
    public Map<String,String> getForeignKeys(String tableName) throws ConvertorException;
    
    public List<Property> getData(String tableName, List<String> columnNames) throws ConvertorException;
}
