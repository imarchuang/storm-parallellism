package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.Component;
import thrift.jvm.Executor;
import thrift.jvm.Topology;
import thrift.jvm.util.CommonUtils;

public class ExecutorDAO implements IExecutor{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(Executor executor) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO executor (runid,topo_name,component_id,executor_id,category,host,port,"
            				+ "executor_uptime,datetime) VALUES (?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, executor.getRunId());
            preparedStatement.setString(2, executor.getTopo_name());
            preparedStatement.setString(3, executor.getComponent_id());
            preparedStatement.setString(4, executor.getExecutor_id());
            preparedStatement.setString(5, executor.getCategory());
            preparedStatement.setString(6, executor.getHost());
            preparedStatement.setString(7, executor.getPort());
            preparedStatement.setInt(8, executor.getExecutor_uptime());
            preparedStatement.setTimestamp(9, executor.getDatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<Executor> findByTopology(Topology topology) {
		List<Executor> executors = new LinkedList<Executor>();
        try {
        	   String sql = "SELECT * FROM executor WHERE runid=? AND topo_name=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, topology.getRunId());
        	   statement.setString(2, topology.getName());

               ResultSet resultSet = statement.executeQuery();
                
               Executor executor = null;
               while(resultSet.next()){
            	   executor = new Executor();
            	   executor.setRunId(resultSet.getInt("runid"));
            	   executor.setTopo_name(resultSet.getString("topo_name"));
            	   executor.setComponent_id(resultSet.getString("component_id"));
            	   executor.setExecutor_id(resultSet.getString("executor_id"));
            	   executor.setExecutor_id(resultSet.getString("category"));
            	   executor.setHost(resultSet.getString("host"));
            	   executor.setPort(resultSet.getString("port"));
            	   executor.setExecutor_uptime(resultSet.getInt("executor_uptime"));
            	   executor.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   executors.add(executor);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return executors;
	}

	@Override
	public List<Executor> findByComponent(Component component) {
		List<Executor> executors = new LinkedList<Executor>();
        try {
        	   String sql = "SELECT * FROM executor WHERE runid=? AND topo_name=? AND component_id=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, component.getRunId());
        	   statement.setString(2, component.getTopo_name());
        	   statement.setString(3, component.getComponent_id());

               ResultSet resultSet = statement.executeQuery();
                
               Executor executor = null;
               while(resultSet.next()){
            	   executor = new Executor();
            	   executor.setRunId(resultSet.getInt("runid"));
            	   executor.setTopo_name(resultSet.getString("topo_name"));
            	   executor.setComponent_id(resultSet.getString("component_id"));
            	   executor.setExecutor_id(resultSet.getString("executor_id"));
            	   executor.setExecutor_id(resultSet.getString("category"));
            	   executor.setHost(resultSet.getString("host"));
            	   executor.setPort(resultSet.getString("port"));
            	   executor.setExecutor_uptime(resultSet.getInt("executor_uptime"));
            	   executor.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   executors.add(executor);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return executors;
	}

	@Override
	public List<Component> findNumExecutorsAndListPerComponent(int activeRunId) {
		List<Component> components = new LinkedList<Component>();
        try {
        	   String sql = "SELECT runid, topo_name,component_id, GROUP_CONCAT(count SEPARATOR ';') AS dist_list, "
        	   		+ "SUM(c) as num_executors FROM (SELECT DISTINCT runid, topo_name,component_id, "
        	   		+ "Concat(host,':',port,',',COUNT(1)) as count, COUNT(1) as c "
        	   		+ "FROM executor "
        	   		+ "WHERE runid = ? "
        	   		+ "GROUP BY runid, topo_name,component_id, host, port "
        	   		+ "ORDER BY runid, topo_name,component_id, host, port) t "
        	   		+ "GROUP BY runid, topo_name,component_id";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, activeRunId);

               ResultSet resultSet = statement.executeQuery();
                
               Component component = null;
               while(resultSet.next()){
            	   component = new Component();
            	   component.setRunId(resultSet.getInt("runid"));
            	   component.setTopo_name(resultSet.getString("topo_name"));
            	   component.setComponent_id(resultSet.getString("component_id"));
            	   component.setDist_list(resultSet.getString("dist_list"));
            	   component.setNum_executors(resultSet.getInt("num_executors"));
                    
            	   components.add(component);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return components;
	}

	
	@Override
	public List<Executor> select() {
		List<Executor> executors = new LinkedList<Executor>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM executor");
                
               Executor executor = null;
               while(resultSet.next()){
            	   executor = new Executor();
            	   executor.setRunId(resultSet.getInt("runid"));
            	   executor.setTopo_name(resultSet.getString("topo_name"));
            	   executor.setComponent_id(resultSet.getString("component_id"));
            	   executor.setExecutor_id(resultSet.getString("executor_id"));
            	   executor.setExecutor_id(resultSet.getString("category"));
            	   executor.setHost(resultSet.getString("host"));
            	   executor.setPort(resultSet.getString("port"));
            	   executor.setExecutor_uptime(resultSet.getInt("executor_uptime"));
            	   executor.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   executors.add(executor);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return executors;
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
