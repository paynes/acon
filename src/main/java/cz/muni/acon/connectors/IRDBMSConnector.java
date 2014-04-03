package cz.muni.acon.connectors;

import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public interface IRDBMSConnector {

    public List<String> getTables();
    
    public List<String> getColumns(String tableName);
    
    
}
