package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.Cluster;
import thrift.jvm.util.CommonUtils;

public class ClusterDAO implements ICluster{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(Cluster cluster) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO cluster (runid, id, version, nimbus_uptime, supervisors, used_slots, "
            				+ "free_slots, total_slots, executors, tasks, datetime) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, cluster.getRunId());
            preparedStatement.setInt(2, cluster.getId());
            preparedStatement.setString(3, cluster.getVersion());
            preparedStatement.setInt(4, cluster.getNimbus_uptime());
            preparedStatement.setInt(5, cluster.getSupervisors());
            preparedStatement.setInt(6, cluster.getUsed_slots());
            preparedStatement.setInt(7, cluster.getFree_slots());
            preparedStatement.setInt(8, cluster.getTotal_slots());
            preparedStatement.setInt(9, cluster.getExecutors());
            preparedStatement.setInt(10, cluster.getTasks());
            preparedStatement.setTimestamp(11, cluster.getDatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<Cluster> select() {
		List<Cluster> clusters = new LinkedList<Cluster>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM cluster");
                
               Cluster cluster = null;
               while(resultSet.next()){
            	   cluster = new Cluster();
            	   cluster.setId(resultSet.getInt("runid"));
            	   cluster.setId(resultSet.getInt("id"));
            	   cluster.setVersion(resultSet.getString("version"));
            	   cluster.setNimbus_uptime(resultSet.getInt("nimbus_uptime"));
            	   cluster.setSupervisors(resultSet.getInt("supervisors"));
            	   cluster.setUsed_slots(resultSet.getInt("used_slots"));
            	   cluster.setFree_slots(resultSet.getInt("free_slots"));
            	   cluster.setTotal_slots(resultSet.getInt("total_slots"));
            	   cluster.setExecutors(resultSet.getInt("executors"));
            	   cluster.setTasks(resultSet.getInt("tasks"));
            	   cluster.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   clusters.add(cluster);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return clusters;
	}
	
	@Override
	public void update(Cluster cluster) {
		try {
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.
            		prepareStatement("UPDATE cluster SET used_slots=?, free_slots=?, total_slots=?, "
            				+ "executors=?, tasks=? WHERE runid = ?");
            preparedStatement.setInt(1, cluster.getUsed_slots());
            preparedStatement.setInt(2, cluster.getFree_slots());
            preparedStatement.setInt(3, cluster.getTotal_slots());
            preparedStatement.setInt(4, cluster.getExecutors());
            preparedStatement.setInt(5, cluster.getTasks());
            preparedStatement.setInt(6, cluster.getRunId());
            
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	public void closeConnection(){
        try {
              if (conn != null) {
                  conn.close();
              }
            } catch (Exception e) { 
            	e.printStackTrace();
            }
    }

}
