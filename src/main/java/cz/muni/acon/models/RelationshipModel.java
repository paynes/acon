package cz.muni.acon.models;


/**
 *
 * @author Jan Koscak
 */
public class RelationshipModel {

    private final String endNodeLabel;
    
    private boolean isFirst;
    
    private final Long endNode;
    
    private Long startNode;
    
    private boolean isCompleted;
    
    public RelationshipModel(final String label, final Long endNode) {
        this.endNodeLabel = label;
        this.endNode = endNode;
        this.isCompleted = false;
        this.isFirst = false;
    }
    
    public String getEndNodeLabel() {
        return this.endNodeLabel;
    }
    
    public Long getEndNode() {
        return this.endNode;
    }
    
    public Long getStartNode() {
        return this.startNode;
    }
    
    public void setStartNode(Long startNode) {
        this.startNode = startNode;
        this.isCompleted = true;
    }
    
    public boolean isCompleted() {
        return this.isCompleted;
    }
    
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }
    
    public boolean isFirst() {
        return this.isFirst;
    }
}
