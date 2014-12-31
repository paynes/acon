package cz.muni.acon.graph.providers;

/**
 *
 * @author Jan Koscak
 * @param <T>
 */
public interface IGraphProvider<T> {

    public T getDatabaseConnection();
    
    public void closeDatabaseConnection();
}
