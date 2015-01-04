package CommonUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import backtype.storm.generated.GlobalStreamId;

public class CommonUtils {
	
	/*
	 * Utility method to parse a Map<>
	 */
	  public static Long getStatValueFromMap(Map<String, Map<String, Long>> map, String statName) {
			  Long statValue = null;
			  Map<String, Long> intermediateMap = map.get(statName);
			  statValue = intermediateMap.get("default");
			  return statValue;
	  }
	  
	  /*
	   * Utility method to parse a Map<>
	   */
	  public static Double getStatDoubleValueFromMap(Map<String, Map<String, Double>> map, String statName) {
			  Double statValue = null;
			  Map<String, Double> intermediateMap = map.get(statName);
			  statValue = intermediateMap.get("default");
			  return statValue;
	  }

	/*
	 * Utility method to parse a Map<> as a special case for Bolts
	 */
	  public static Double get_boltStatDoubleValueFromMap(Map<String, Map<GlobalStreamId, Double>> map, String statName) {
			  Double statValue = 0.0;
			  Map<GlobalStreamId, Double> intermediateMap = map.get(statName);
			  Set<GlobalStreamId> key = intermediateMap.keySet();
			  if(key.size() > 0) {
				   Iterator<GlobalStreamId> itr = key.iterator();
				   statValue = intermediateMap.get(itr.next());
			  }
			  return statValue;
	  }

	/*
	 * Utility method for Bolts
	 */
	  public static Long get_boltStatLongValueFromMap(Map<String, Map<GlobalStreamId, Long>> map, String statName) {
			  Long statValue = null;
			  Map<GlobalStreamId, Long> intermediateMap = map.get(statName);
			  Set<GlobalStreamId> key = intermediateMap.keySet();
			  if(key.size() > 0) {
				   Iterator<GlobalStreamId> itr = key.iterator();
				   statValue = intermediateMap.get(itr.next());
			  }
			  return statValue;
	  }
	  
	  public static Timestamp getCurrentTimeStamp() {
		  
			Date today = new Date();
			return new Timestamp(today.getTime());
		 
	  }
	  
	  public static Connection getConnection(){
	        
		   // JDBC driver name and database URL
		   final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		   final String DB_URL = "jdbc:mysql://localhost/StormStats";

		   //  Database credentials
		   final String USER = "storm";
		   final String PASS = "storm";
		  
		    Connection connection = null;
		    
		    try {
	            Class.forName("com.mysql.jdbc.Driver");
	            if(connection == null)
	                connection = DriverManager.getConnection(DB_URL,USER,PASS);
	 
	        } catch (ClassNotFoundException e) {
	 
	            e.printStackTrace();
	             
	        } catch (SQLException e) {
	             
	            e.printStackTrace();
	             
	        }
	        return connection;
	    }
	  
	    public static void closeConnection(Connection connection){
	        try {
	              if (connection != null) {
	                  connection.close();
	              }
	            } catch (Exception e) { 
	            	e.printStackTrace();
	            }
	    }
	    
	    /*
	     * Bolt(
				bolt_object:
					<ComponentObject serialized_java:80 01 00 02 00 00 00 0B 67 65 74 54 6F 70 6F 6C 6F 67 79 00 00 00 05 0C 00 00 0D 00 01 0B 0C 00 00 00 01 00 00 00 05 73 70 6F 75 74 0C 00 01 0B 00 01 00 00 01 08 AC ED 00 05 73 72 00 27 73 74 6F 72 6D 2E 73 74 61 72 74 65 72 2E 73 70 6F 75 74 2E 52 61 6E 64 6F 6D 53 65 6E 74 65 6E 63 65 53 70 6F 75 74 91 19 5D CC 90 E2 DA EF 02 00 02 4C 00 0A 5F 63 6F 6C 6C 65 63 74 6F 72 74 00 2B...>, 
					common:ComponentCommon(
						inputs:{GlobalStreamId(componentId:split, streamId:default)=<Grouping fields:[word]>}, 
						streams:{
							__ack_ack=StreamInfo(output_fields:[id, ack-val], direct:false), 
							default=StreamInfo(output_fields:[word, count], direct:false), 
							__ack_fail=StreamInfo(output_fields:[id], direct:false), 
							__metrics=StreamInfo(output_fields:[task-info, data-points], direct:false), 
							__system=StreamInfo(output_fields:[event], direct:false)}, 
						parallelism_hint:12, 
						json_conf:{"topology.tasks":12}
					)
			)
	     */
	    
}
