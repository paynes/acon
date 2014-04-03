
import cz.muni.acon.providers.IRDBMSProvider;
import cz.muni.acon.providers.PostgreSQLProvider;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author paynes
 */
public class main {

    public static void main(String[] argv) {
        IRDBMSProvider con = new PostgreSQLProvider("127.0.0.1", "5432", "drunken_panda", "paynes", "test");
        if (con.getConnection() != null) {
            System.out.println("____YES___");
        } else {
            System.out.println("____NO___");
        }
        
        try {
            String   catalog          = null;
            String   schemaPattern    = null;
            String   tableNamePattern = null;
            String[] types            = {"TABLE"};
            //System.out.println(con.getConnection().getSchema());
            DatabaseMetaData rs = con.getConnection().getMetaData();
            
            ResultSet a = rs.getTables(con.getConnection().getCatalog(), null, null, types);
            while(a.next()) {
                System.out.println(a.getString(3));
            }
            //System.out.println(con.getConnection().getMetaData().getTableTypes());
            //con.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");
 
		try {
 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return;
 
		}
 
		System.out.println("PostgreSQL JDBC Driver Registered!");
 
		Connection connection = null;
 
		try {
 
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/drunken_panda", "paynes",
					"test");
 
		} catch (SQLException e) {
 
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
 
		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
    }
}
