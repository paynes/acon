package cz.muni.acon.graph.connectors;

import com.google.common.collect.Lists;
import cz.muni.acon.convertor.Property;
import cz.muni.acon.graph.providers.Neo4jProvider;
import cz.muni.acon.models.NodeModel;
import cz.muni.acon.models.RelationshipModel;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

/**
 *
 * @author Jan Koscak
 */
public class Neo4JConnector implements IGraphConnector {

    private final Neo4jProvider provider;
    
    public Neo4JConnector(Neo4jProvider provider) {
        this.provider = provider;
    }
    
    
    @Override
    public List<NodeModel> createNodes(final List<NodeModel> nodeModels) {
        try (Transaction tx = this.getTransaction()) {
            List<NodeModel> newNodeModels = new ArrayList<NodeModel>();
            for (NodeModel nm : nodeModels) {
                final Node node = this.getGraphDB().createNode(DynamicLabel.label(nm.getTableName()));
                this.setNodeProperties(node, nm.getColumnsList());
                newNodeModels.add(this.createNodeModel(nm, node.getId()));
            }
            tx.success();
            return newNodeModels;
        } catch (SQLException ex) {
            Logger.getLogger(Neo4JConnector.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("CHYBA");
        }
        return null;
    }
    
    private NodeModel createNodeModel(final NodeModel nm, final Long id) {
        NodeModel newNM = new NodeModel();
        newNM.setPkList(nm.getPkList());
        newNM.setColumnsList(nm.getColumnsList());
        newNM.setFkList(nm.getFkList());
        newNM.setTableName(nm.getTableName());
        newNM.setNodeID(id);
        return newNM;
    }
    
    private void setNodeProperties(final Node node, final List<Property> properties) throws SQLException {
        for (Property property : properties) {
            if (property.getPropertyValue() != null) {
                transformValueType(property.getPropertyValue());
                node.setProperty(property.getPropertyKey(), this.transformValueType(property.getPropertyValue()));
            }
        }
    }
    
    @Override
    public void createRelationship(List<RelationshipModel> completedRelationshipModels) {
        try (Transaction tx = this.getTransaction()) {
            for (final RelationshipModel model : completedRelationshipModels) {
                Node first = this.getGraphDB().getNodeById(model.getStartNode());
                Node second = this.getGraphDB().getNodeById(model.getEndNode());
                if (model.isFirst()) {
                    second.addLabel(DynamicLabel.label(Long.toString(first.getId())));
                }
                first.createRelationshipTo(second, new RelationshipType() {

                    @Override
                    public String name() {
                        return model.getEndNodeLabel();
                    }
                });
            }
            tx.success();
        }
    }
    
    @Override
    public void setUniqueProperty(final String label, final String property) {
        try (Transaction tx = this.getTransaction()) {
            this.getGraphDB().schema()
                    .constraintFor(DynamicLabel.label(label))
                    .assertPropertyIsUnique(property)
                    .create();
            tx.success();
        }
    }
    
    @Override
    public void closeDatabase() {
        this.provider.closeDatabaseConnection();
    }
    
    @Override
    public void reduceNMrelationships() {
        try (Transaction tx = this.getTransaction()) {
            for (Node node : GlobalGraphOperations.at(this.getGraphDB()).getAllNodes()) {
                List<Relationship> relationships = Lists.newArrayList(node.getRelationships());
                if (relationships.size() != 2) {
                    continue;
                }
            
                if (!(isWithoutProperties(node))) {
                    continue;
                }
            
                if (!(areRelationshipsPaired(node))) {
                    continue;
                }
                this.removeNode(node);
            }
            tx.success();
        }
    }
    
    private Object transformValueType(Object value) throws SQLException {
        
        if (value instanceof Date) {
            return value.toString();
        } else if (value instanceof Blob) {
            Blob blob = (Blob) value;
            return blob.getBytes(1, Long.valueOf(blob.length()).intValue());
        } else if (value == null){
            return null;
        }
        
        return value.toString();
    }
    
    private void removeNode(Node node) {
            List<Relationship> relationships = Lists.newArrayList(node.getRelationships(Direction.INCOMING));
            Node first = relationships.get(0).getStartNode();
            RelationshipType firstType = relationships.get(0).getType();
            Node second = relationships.get(1).getStartNode();
            RelationshipType secondType = relationships.get(1).getType();
            for (Label label : node.getLabels()) {
                if (!(label.name().equals(firstType.name()) || label.name().equals(secondType.name()))) {
                    long id = Long.valueOf(label.name());
                    if (first.getId() == id) {
                        first.createRelationshipTo(second, secondType);
                    } else if (second.getId() == id) {
                        second.createRelationshipTo(first, firstType);
                    }
                    node.removeLabel(label);
                }
            }
            for (Relationship r : relationships) {
                r.delete();
            }
            node.delete();
    }
    
    private boolean areRelationshipsPaired(Node node) {
        List<Relationship> incoming = Lists.newArrayList(node.getRelationships(Direction.INCOMING));
        return incoming.get(0).isType(incoming.get(1).getType());
    }
    
    private boolean isWithoutProperties(Node node) {
        return Lists.newArrayList(node.getPropertyKeys()).isEmpty();
    }
    
    private Transaction getTransaction() {
        return this.provider.getDatabaseConnection().beginTx();
    }
    
    private GraphDatabaseService getGraphDB() {
        return this.provider.getDatabaseConnection();
    }
}
