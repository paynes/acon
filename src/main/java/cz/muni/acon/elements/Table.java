package cz.muni.acon.elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan Koscak
 */
public class Table {

    private String tableName;
    private List<Column> tableColumns;
    
    public Table() {
        this.tableColumns = new ArrayList();
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void addColumn(Column column) {
        if (column != null) {
            this.tableColumns.add(column);
        }
    }
}
