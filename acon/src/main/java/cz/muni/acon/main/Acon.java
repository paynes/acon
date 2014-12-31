package cz.muni.acon.main;


import cz.muni.acon.convertor.Convertor;
import cz.muni.acon.exceptions.ConvertorException;
import cz.muni.acon.graph.connectors.Neo4JConnector;
import cz.muni.acon.rdbms.connectors.IRDBMSConnector;
import cz.muni.acon.rdbms.connectors.RDBMSConnector;
import cz.muni.acon.convertor.Property;
import cz.muni.acon.graph.providers.Neo4jProvider;
import cz.muni.acon.rdbms.providers.PostgreSQLProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jan Koscak
 */
public class Acon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String postgresUrl = "";
            String user = "";
            String password = "";
            String neo4jUrl = "";
            String schema = "";
            
            if (args.length == 4) {
                postgresUrl = args[0];
                user = args[1];
                password = args[2];
                neo4jUrl = args[3];
            } else if (args.length == 5) {
                postgresUrl = args[0];
                user = args[1];
                password = args[2];
                schema = args[3];
                neo4jUrl = args[4];
            }
            
            PostgreSQLProvider sqlProvider = new PostgreSQLProvider(postgresUrl, user, password);
            RDBMSConnector rdbmsConnector = new RDBMSConnector(sqlProvider);
            rdbmsConnector.setSchema(schema);
            
            Neo4jProvider neo4jProvider = new Neo4jProvider(neo4jUrl);
            Neo4JConnector neo4jConnector = new Neo4JConnector(neo4jProvider);
            
            Convertor convertor = new Convertor(rdbmsConnector, neo4jConnector);
            convertor.convert();
            
        } catch (ConvertorException ex) {
            System.out.println(ex);
        }
    }
    
}
