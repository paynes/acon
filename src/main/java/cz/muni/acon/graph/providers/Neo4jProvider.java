package cz.muni.acon.graph.providers;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author Jan Koscak
 */
public class Neo4jProvider implements IGraphProvider<GraphDatabaseService> {
    
    private final GraphDatabaseService graphDb;
    
    public Neo4jProvider(String path) {
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(path);
    }

    public GraphDatabaseService getDatabaseConnection() {
        return this.graphDb;
    }

    public void closeDatabaseConnection() {
        this.graphDb.shutdown();
    }

}
