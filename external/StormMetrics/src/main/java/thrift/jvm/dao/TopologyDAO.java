package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.Topology;
import thrift.jvm.util.CommonUtils;

public class TopologyDAO implements ITopology{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(Topology topology) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO topology (runid, name, topo_id, status, topo_uptime, num_workers, "
            				+ "num_executors, num_tasks, datetime) VALUES (?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, topology.getRunId());
            preparedStatement.setString(2, topology.getName());
            preparedStatement.setString(3, topology.getTopo_id());
            preparedStatement.setString(4, topology.getStatus());
            preparedStatement.setInt(5, topology.getTopo_uptime());
            preparedStatement.setInt(6, topology.getNum_workers());
            preparedStatement.setInt(7, topology.getNum_executors());
            preparedStatement.setInt(8, topology.getNum_tasks());
            preparedStatement.setTimestamp(9, topology.getDatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<Topology> select() {
		List<Topology> clusters = new LinkedList<Topology>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM topology");
                
               Topology cluster = null;
               while(resultSet.next()){
            	   cluster = new Topology();
            	   cluster.setRunId(resultSet.getInt("runid"));
            	   cluster.setName(resultSet.getString("name"));
            	   cluster.setTopo_id(resultSet.getString("topo_id"));
            	   cluster.setStatus(resultSet.getString("status"));
            	   cluster.setTopo_uptime(resultSet.getInt("topo_uptime"));
            	   cluster.setNum_workers(resultSet.getInt("num_workers"));
            	   cluster.setNum_executors(resultSet.getInt("num_executors"));
            	   cluster.setNum_tasks(resultSet.getInt("num_tasks"));
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
