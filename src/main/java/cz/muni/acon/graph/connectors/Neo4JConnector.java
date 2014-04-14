package cz.muni.acon.graph.connectors;

import cz.muni.acon.graph.elements.Property;
import cz.muni.acon.graph.providers.Neo4jProvider;
import java.util.List;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Jan Koscak
 */
public class Neo4JConnector implements GraphConnectors {

    private final Neo4jProvider provider;
    
    public Neo4JConnector(Neo4jProvider provider) {
        this.provider = provider;
    }
    
    public Long createNode(final String label) {
        try (Transaction tx = this.getTransaction())
        {
            final Label neo4jlabel = new Label() {

                @Override
                public String name() {
                    return label;
                }              
            };
            final Node node = this.getGraphDB().createNode(neo4jlabel);
            tx.success();
            return node.getId();
        }
    }
    
    public void setNodeProperties(Long nodeID, List<Property> properties) {
        try (Transaction tx = this.getTransaction()) {
            final Node node = this.getGraphDB().getNodeById(nodeID);
            for (Property property : properties) {
                //pridat transformaciu typov
                node.setProperty(property.getPropertyKey(), property.getPropertyValue());
            }
            tx.success();
        }
    }
    
    public Long createRelationship(Long firstNodeID, Long secondNodeID, final String label) {
        try (Transaction tx = this.getTransaction()) {
            Node first = this.getGraphDB().getNodeById(firstNodeID);
            Node second = this.getGraphDB().getNodeById(secondNodeID);
            Relationship relationship = first.createRelationshipTo(second, new RelationshipType() {

                @Override
                public String name() {
                    return label;
                }
            });
            tx.success();
            return relationship.getId();
        }
    }
    
    public void setRelationshipProperties(Long relationshipID, List<Property> properties) {
        try (Transaction tx = this.getTransaction()) {
            Relationship relationship = this.getGraphDB().getRelationshipById(relationshipID);
            for (Property property : properties) {
                //pridat transformaciu typov
                relationship.setProperty(property.getPropertyKey(), property.getPropertyValue());
            }
            tx.success();
        }
    }
    
    private Transaction getTransaction() {
        return this.provider.getDatabaseConnection().beginTx();
    }
    
    private GraphDatabaseService getGraphDB() {
        return this.provider.getDatabaseConnection();
    }
}
