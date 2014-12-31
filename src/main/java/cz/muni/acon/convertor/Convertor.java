package cz.muni.acon.convertor;

import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.graph.connectors.IGraphConnector;
import cz.muni.acon.rdbms.connectors.IRDBMSConnector;
import cz.muni.acon.models.NodeModel;
import cz.muni.acon.models.RelationshipModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jan Koscak
 */
public class Convertor {
    
    private final IRDBMSConnector rdbms;
    private final IGraphConnector graph;

    public Convertor(IRDBMSConnector rdbmsConnector, IGraphConnector graphConnector) {
        this.rdbms = rdbmsConnector;
        this.graph = graphConnector;
    }
    
    private Map<List<Property>,Long> extractPrimaryKeys(List<NodeModel> nodeModels) {
        Map<List<Property>,Long> extractedPrimaryKeys = new HashMap<List<Property>,Long>();
        for (NodeModel nm : nodeModels) {
            extractedPrimaryKeys.put(nm.getPkList(), nm.getNodeID());
        }
        return extractedPrimaryKeys;
    }
    
    private Map<RelationshipModel, List<Property>> createRelationShipsModels(List<NodeModel> nodeModels) {
        Map<RelationshipModel, List<Property>> relationhips = new HashMap<RelationshipModel, List<Property>>();
        for (NodeModel nm : nodeModels) {
            boolean isFirst = true;
            for (List<Property> fk : nm.getFkList()) {
                RelationshipModel relationship = new RelationshipModel(nm.getTableName(),nm.getNodeID());
                if (nm.getColumnsList().isEmpty() && nm.getFkList().size() == 2 && isFirst) {
                    relationship.setFirst(isFirst);
                    isFirst = false;
                }
                relationhips.put(relationship, fk);
            }
        }
        return relationhips;
    }
    
    private Map<RelationshipModel, List<Property>> completeRelationShips(Map<RelationshipModel, List<Property>> incompleteModels, Map<List<Property>,Long> primaryKeys) {
        Map<RelationshipModel, List<Property>> completeRelationships = new HashMap<RelationshipModel, List<Property>>();
        for (RelationshipModel model : incompleteModels.keySet()) {
            if (primaryKeys.containsKey(incompleteModels.get(model))) {
                model.setStartNode(primaryKeys.get(incompleteModels.get(model)));
                completeRelationships.put(model, incompleteModels.get(model));
            } else {
                completeRelationships.put(model, incompleteModels.get(model));
            }
        }
        return completeRelationships;
    }
    
    private Map<RelationshipModel, List<Property>> createRelationhips(Map<RelationshipModel, List<Property>> completeRelationshipModels) {
        Map<RelationshipModel, List<Property>> incompleteRelationships = new HashMap<RelationshipModel, List<Property>>();
        List<RelationshipModel> completeRelationships = new ArrayList<RelationshipModel>();
        for (RelationshipModel model : completeRelationshipModels.keySet()) {
            if (model.isCompleted()) {
                completeRelationships.add(model);
            } else {
                incompleteRelationships.put(model,completeRelationshipModels.get(model));
            }
        }
        this.graph.createRelationship(completeRelationships);
        return incompleteRelationships;
    }
    
    public void convert() throws ConvertorException{
        try {
            Map<List<Property>,Long> primaryKeys = new HashMap<List<Property>,Long>();
            Map<RelationshipModel, List<Property>> incompleteModels = new HashMap<RelationshipModel, List<Property>>();
            for (String tableName : this.rdbms.getTables()) {
                System.out.println(tableName);
                List<NodeModel> nodeModels = this.rdbms.getNodeModels(tableName);
                nodeModels = this.graph.createNodes(nodeModels);
                //ziskame primarne kluce
                Map<List<Property>,Long> extractedPrimaryKeys = this.extractPrimaryKeys(nodeModels);
                //zo ziskanych klucov sa snazime vytvorit relationships
                Map<RelationshipModel, List<Property>> completedRelationshipModels = this.completeRelationShips(incompleteModels, extractedPrimaryKeys);
                //vytvarame nove modely relationships
                Map<RelationshipModel, List<Property>> newIncompletRelationshipModels = this.createRelationShipsModels(nodeModels);
                //snazime sa vytvorit relationships z novych modelov
                completedRelationshipModels.putAll(this.completeRelationShips(newIncompletRelationshipModels, primaryKeys));
                //dokoncene modely posielame do graph provideru a nedokoncene modely pridavame do zoznamu
                incompleteModels = this.createRelationhips(completedRelationshipModels);
                //ziskane kluce pridavame do zoznamu
                primaryKeys.putAll(extractedPrimaryKeys);
            }
            this.graph.reduceNMrelationships();
            this.graph.closeDatabase();
            
        } catch (ConvertorException ex) {
            throw new ConvertorException(ex.getMessage());
        }
    }
}
