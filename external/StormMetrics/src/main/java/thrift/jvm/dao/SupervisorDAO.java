package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.Supervisor;
import thrift.jvm.util.CommonUtils;

public class SupervisorDAO implements ISupervisor{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(Supervisor supervisor) {
		try {
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO supervisor (runid, sup_id, host, supervisor_uptime, slots, used_slots, datetime)"
            				+ " VALUES (?,?,?,?,?,?,?)");
            preparedStatement.setInt(1, supervisor.getRunId());
            preparedStatement.setString(2, supervisor.getSup_id());
            preparedStatement.setString(3, supervisor.getHost());
            preparedStatement.setInt(4, supervisor.getSupervisor_uptime());
            preparedStatement.setInt(5, supervisor.getSlots());
            preparedStatement.setInt(6, supervisor.getUsed_slots());
            preparedStatement.setTimestamp(7, supervisor.getDatetime());
            
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<Supervisor> select() {
		List<Supervisor> supervisors = new LinkedList<Supervisor>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM supervisor");
                
               Supervisor supervisor = null;
               while(resultSet.next()){
            	   supervisor = new Supervisor();
            	   supervisor.setRunId(resultSet.getInt("runid"));
            	   supervisor.setSup_id(resultSet.getString("sup_id"));
            	   supervisor.setHost(resultSet.getString("host"));
            	   supervisor.setSupervisor_uptime(resultSet.getInt("supervisor_uptime"));
            	   supervisor.setSlots(resultSet.getInt("slots"));
            	   supervisor.setUsed_slots(resultSet.getInt("used_slots"));
            	   supervisor.setDatetime(resultSet.getTimestamp("datetime"));
                    
            	   supervisors.add(supervisor);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return supervisors;
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
