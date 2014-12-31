package cz.muni.acon.rdbms.connectors;

import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.models.NodeModel;
import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public interface IRDBMSConnector {

    public List<String> getTables() throws ConvertorException;
    
    public void setSchema(final String schema);
    
    public List<NodeModel> getNodeModels(final String tableName) throws ConvertorException;
}
