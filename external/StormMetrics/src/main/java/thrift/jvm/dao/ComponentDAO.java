package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.Component;
import thrift.jvm.Topology;
import thrift.jvm.util.CommonUtils;

public class ComponentDAO implements IComponent{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(Component component) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO component (runid,topo_name,component_id,category,"
            				+ "seq,from_component,to_component,ip_grouping,op_grouping,ip_fields,"
            				+"op_fields,num_executors,num_tasks,datetime) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, component.getRunId());
            preparedStatement.setString(2, component.getTopo_name());
            preparedStatement.setString(3, component.getComponent_id());
            preparedStatement.setString(4, component.getCategory());
            preparedStatement.setInt(5, component.getSeq());
            preparedStatement.setString(6, component.getFrom_component());
            preparedStatement.setString(7, component.getTo_component());
            preparedStatement.setString(8, component.getIp_grouping());
            preparedStatement.setString(9, component.getOp_grouping());
            preparedStatement.setString(10, component.getIp_fields());
            preparedStatement.setString(11, component.getOp_fields());
            preparedStatement.setInt(12, component.getNum_executors());
            preparedStatement.setInt(13, component.getNum_tasks());
            preparedStatement.setTimestamp(14, component.getDatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<Component> findByTopology(Topology topology) {
		List<Component> components = new LinkedList<Component>();
        try {
        	   String sql = "SELECT * FROM Components WHERE runid=? AND topo_name=?";
               PreparedStatement statement = conn.
               		prepareStatement(sql);
        	   statement.setInt(1, topology.getRunId());
        	   statement.setString(2, topology.getName());

               ResultSet resultSet = statement.executeQuery();
                
               Component component = null;
               while(resultSet.next()){
            	   component = new Component();
            	   component.setRunId(resultSet.getInt("runid"));
            	   component.setTopo_name(resultSet.getString("topo_name"));
            	   component.setComponent_id(resultSet.getString("component_id"));
            	   component.setCategory(resultSet.getString("category"));
            	   component.setRunId(resultSet.getInt("seq"));
            	   component.setTopo_name(resultSet.getString("from_component"));
            	   component.setComponent_id(resultSet.getString("to_component"));
            	   component.setCategory(resultSet.getString("ip_grouping"));
            	   component.setTopo_name(resultSet.getString("op_grouping"));
            	   component.setComponent_id(resultSet.getString("ip_fields"));
            	   component.setCategory(resultSet.getString("op_fields"));
            	   component.setNum_executors(resultSet.getInt("num_executors"));
            	   component.setNum_tasks(resultSet.getInt("num_tasks"));
            	   component.setDatetime(resultSet.getTimestamp("datetime"));
                    
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
	public void updateNumTasksAndList(Component component) {
		try {
			PreparedStatement preparedStatement = null;
			preparedStatement = conn.
            		prepareStatement("UPDATE component SET dist_list=?, num_executors=? WHERE runid = ? AND "
            				+ " topo_name= ? AND component_id = ?");
            preparedStatement.setString(1, component.getDist_list());
            preparedStatement.setInt(2, component.getNum_executors());
            preparedStatement.setInt(3, component.getRunId());
            preparedStatement.setString(4, component.getTopo_name());
            preparedStatement.setString(5, component.getComponent_id());
            
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
	
	@Override
	public List<Component> select() {
		List<Component> components = new LinkedList<Component>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM Components");
                
               Component component = null;
               while(resultSet.next()){
            	   component = new Component();
            	   component.setRunId(resultSet.getInt("runid"));
            	   component.setTopo_name(resultSet.getString("topo_name"));
            	   component.setComponent_id(resultSet.getString("component_id"));
            	   component.setCategory(resultSet.getString("category"));
            	   component.setRunId(resultSet.getInt("seq"));
            	   component.setTopo_name(resultSet.getString("from_component"));
            	   component.setComponent_id(resultSet.getString("to_component"));
            	   component.setCategory(resultSet.getString("ip_grouping"));
            	   component.setTopo_name(resultSet.getString("op_grouping"));
            	   component.setComponent_id(resultSet.getString("ip_fields"));
            	   component.setCategory(resultSet.getString("op_fields"));
            	   component.setNum_executors(resultSet.getInt("num_executors"));
            	   component.setNum_tasks(resultSet.getInt("num_tasks"));
            	   component.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   components.add(component);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return components;
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
