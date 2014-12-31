package cz.muni.acon.graph.connectors;

import cz.muni.acon.models.NodeModel;
import cz.muni.acon.models.RelationshipModel;
import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public interface IGraphConnector {
    
    public List<NodeModel> createNodes(final List<NodeModel> nodeModels);
    
    public void createRelationship(List<RelationshipModel> completedRelationshipModels);
    
    public void setUniqueProperty(final String label, final String property);
    
    public void reduceNMrelationships();
    
    public void closeDatabase();
}
