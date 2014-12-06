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
import thrift.jvm.StormStats;
import thrift.jvm.Topology;
import thrift.jvm.util.CommonUtils;

public class StormStatsDAO implements IStormStats{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(StormStats stormStats) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO stormstats (runId,topo_name,component_id,executor_id,level,window,"
            				+ "emitted,transferred,complete_latency,"
            				+ "acked,failed,capacity,execute_latency,process_latency,executed,datetime) "
            				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, stormStats.getRunId());
            preparedStatement.setString(2, stormStats.getTopo_name());
            preparedStatement.setString(3, stormStats.getComponent_id());
            preparedStatement.setString(4, stormStats.getExecutor_id());
            preparedStatement.setString(5, stormStats.getLevel());
            preparedStatement.setString(6, stormStats.getWindow());
            preparedStatement.setLong(7, stormStats.getEmitted());
            preparedStatement.setLong(8, stormStats.getTransferred());
            preparedStatement.setDouble(9, stormStats.getComplete_latency());
            preparedStatement.setLong(10, stormStats.getAcked());
            preparedStatement.setLong(11, stormStats.getFailed());
            preparedStatement.setDouble(12, stormStats.getCapacity());
            preparedStatement.setDouble(13, stormStats.getExecute_latency());
            preparedStatement.setDouble(14, stormStats.getProcess_latency());
            preparedStatement.setLong(15, stormStats.getExecuted());
            preparedStatement.setTimestamp(16, stormStats.getDatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<StormStats> findByTopology(Topology topology) {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
        	   String sql = "SELECT * FROM executor WHERE runid=? AND topo_name=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, topology.getRunId());
        	   statement.setString(2, topology.getName());

               ResultSet resultSet = statement.executeQuery();
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id(resultSet.getString("component_id"));
            	   stormStats.setExecutor_id(resultSet.getString("executor_id"));
            	   stormStats.setExecutor_id(resultSet.getString("level"));
            	   stormStats.setExecutor_id(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
	}

	@Override
	public List<StormStats> findByComponent(Component component) {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
        	   String sql = "SELECT * FROM executor WHERE runid=? AND topo_name=? AND component_id=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, component.getRunId());
        	   statement.setString(2, component.getTopo_name());
        	   statement.setString(3, component.getComponent_id());

               ResultSet resultSet = statement.executeQuery();
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id(resultSet.getString("component_id"));
            	   stormStats.setExecutor_id(resultSet.getString("executor_id"));
            	   stormStats.setExecutor_id(resultSet.getString("level"));
            	   stormStats.setExecutor_id(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
	}
	
	@Override
	public List<StormStats> findByExecutor(Executor executor) {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
        	   String sql = "SELECT * FROM executor WHERE runid=? AND topo_name=? AND component_id=?"
        	   		+ " AND executor_id=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, executor.getRunId());
        	   statement.setString(2, executor.getTopo_name());
        	   statement.setString(3, executor.getComponent_id());
        	   statement.setString(4, executor.getExecutor_id());

               ResultSet resultSet = statement.executeQuery();
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id(resultSet.getString("component_id"));
            	   stormStats.setExecutor_id(resultSet.getString("executor_id"));
            	   stormStats.setExecutor_id(resultSet.getString("level"));
            	   stormStats.setExecutor_id(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
	}
	
	@Override
	public List<StormStats> aggregateByComponentId(int activeRunId) {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
        	   String sql = "SELECT runid, topo_name, component_id, window, sum(emitted) as emitted, "
        	   		+ "sum(transferred) as transferred, sum(executed) as executed, sum(acked) as acked, "
        	   		+ "sum(failed) as failed, sum(complete_latency*emitted)/sum(emitted) as complete_latency, "
        	   		+ "sum(execute_latency*executed)/sum(executed) as execute_latency, "
        	   		+ "sum(process_latency*executed)/sum(executed) as process_latency, "
        	   		+ "sum(capacity*executed)/sum(executed) as capacity "
        	   		+ "FROM stormstats "
        	   		+ "WHERE runid = ? AND level = 'Executor' "
        	   		+ "AND component_id <> '__acker' AND SUBSTR(component_id,1,9) <> '__metrics' "
        	   		+ "GROUP BY runid, topo_name, component_id, window";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, activeRunId);

               ResultSet resultSet = statement.executeQuery();
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id(resultSet.getString("component_id"));
            	   stormStats.setExecutor_id("NULL");
            	   stormStats.setLevel("Component");
            	   stormStats.setWindow(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
	}
	
	@Override
	public List<StormStats> aggregateByTopo(int activeRunId) {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
        	   String sql = "SELECT runid, topo_name, window, sum(emitted) as emitted, "
        	   		+ "sum(transferred) as transferred, sum(executed) as executed, sum(acked) as acked, "
        	   		+ "sum(failed) as failed, sum(complete_latency*emitted)/sum(emitted) as complete_latency, "
        	   		+ "sum(execute_latency*executed)/sum(executed) as execute_latency, "
        	   		+ "sum(process_latency*executed)/sum(executed) as process_latency, "
        	   		+ "sum(capacity*executed)/sum(executed) as capacity "
        	   		+ "FROM stormstats "
        	   		+ "WHERE runid = ? AND level = 'Component' "
        	   		+ "GROUP BY runid, topo_name, window";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, activeRunId);

               ResultSet resultSet = statement.executeQuery();
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id("NULL");
            	   stormStats.setExecutor_id("NULL");
            	   stormStats.setLevel("Topology");
            	   stormStats.setWindow(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
	}

	
	@Override
	public List<StormStats> select() {
		List<StormStats> lStormStats = new LinkedList<StormStats>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM executor");
                
               StormStats stormStats = null;
               while(resultSet.next()){
            	   stormStats = new StormStats();
            	   stormStats.setRunId(resultSet.getInt("runid"));
            	   stormStats.setTopo_name(resultSet.getString("topo_name"));
            	   stormStats.setComponent_id(resultSet.getString("component_id"));
            	   stormStats.setExecutor_id(resultSet.getString("executor_id"));
            	   stormStats.setExecutor_id(resultSet.getString("level"));
            	   stormStats.setExecutor_id(resultSet.getString("window"));
            	   stormStats.setEmitted(resultSet.getLong("emitted"));
            	   stormStats.setTransferred(resultSet.getLong("transferred"));
            	   stormStats.setComplete_latency(resultSet.getDouble("complete_latency"));
            	   stormStats.setAcked(resultSet.getLong("acked"));
            	   stormStats.setFailed(resultSet.getLong("failed"));
            	   stormStats.setCapacity(resultSet.getDouble("capacity"));
            	   stormStats.setExecute_latency(resultSet.getDouble("execute_latency"));
            	   stormStats.setProcess_latency(resultSet.getDouble("process_latency"));
            	   stormStats.setExecuted(resultSet.getLong("executed"));
            	   stormStats.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   lStormStats.add(stormStats);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return lStormStats;
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
