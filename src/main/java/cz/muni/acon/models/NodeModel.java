package cz.muni.acon.models;

import cz.muni.acon.convertor.Property;
import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public class NodeModel {

    private List<Property> primaryKeyList;
    
    private List<Property> columnsList;
    
    private List<List<Property>> fkList;
    
    private String tableName;
    
    private Long nodeID;
    
    public void setPkList(List<Property> pkList) {
        this.primaryKeyList = pkList;
    }
    
    public List<Property> getPkList() {
        return this.primaryKeyList;
    }
    
    public void setColumnsList(List<Property> columnsList) {
        this.columnsList = columnsList;
    }
    
    public List<Property> getColumnsList() {
        return this.columnsList;
    }
    
    public void setFkList(List<List<Property>> fkList) {
        this.fkList = fkList;
    }
    
    public List<List<Property>> getFkList() {
        return this.fkList;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }
    
    public Long getNodeID() {
        return this.nodeID;
    }
}
