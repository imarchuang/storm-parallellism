package thrift.jvm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import thrift.jvm.RunState;
import thrift.jvm.util.CommonUtils;

public class RunStateDAO implements IRunState{

	Connection conn = CommonUtils.getConnection();
	@Override
	public void insert(RunState runState) {
		try {
			/*INSERT INTO runstate (state, plandatetime) VALUES ('PLANNED', NOW());*/
            PreparedStatement preparedStatement = conn.
            		prepareStatement("INSERT INTO runstate (state, plandatetime) VALUES (?,?)");
            preparedStatement.setString(1, runState.getState());
            preparedStatement.setTimestamp(2, runState.getPlandatetime());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public List<RunState> select() {
		List<RunState> runStates = new LinkedList<RunState>();
        try {
               Statement statement = conn.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM runstate");
                
               RunState runState = null;
               while(resultSet.next()){
            	   runState = new RunState();
            	   runState.setRunId(resultSet.getInt("runid"));
            	   runState.setState(resultSet.getString("state"));
            	   runState.setPlandatetime(resultSet.getTimestamp("plandatetime"));
            	   runState.setStartdatetime(resultSet.getTimestamp("startdatetime"));
            	   runState.setEnddatetime(resultSet.getTimestamp("enddatetime"));
                    
            	   runStates.add(runState);
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return runStates;
	}
	
	@Override
	public void update(RunState runState) {
		/**UPDATE runstate SET state='RUNNING', startdatetime=NOW() WHERE runid = &runid;
		 *UPDATE runstate SET state='FINISHED', enddatetime=NOW() WHERE runid = &runid; 
		 **/
		try {
			PreparedStatement preparedStatement = null;
			if (runState.getState() == "RUNNING"){
				preparedStatement = conn.
	            		prepareStatement("UPDATE runstate SET state=?, startdatetime=? WHERE runid = ?");
	            preparedStatement.setString(1, runState.getState());
	            preparedStatement.setTimestamp(2, runState.getStartdatetime());
	            preparedStatement.setInt(3, runState.getRunId());
            } else if (runState.getState() == "FINISHED") {
            	preparedStatement = conn.
	            		prepareStatement("UPDATE runstate SET state=?, enddatetime=? WHERE runid = ?");
	            preparedStatement.setString(1, runState.getState());
	            preparedStatement.setTimestamp(2, runState.getEnddatetime());
	            preparedStatement.setInt(3, runState.getRunId());
            } else {
            	//do nothing
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

	}

	@Override
	public RunState find(int runId) {
		RunState runState = new RunState();
        try {
               String sql = "SELECT * FROM runstate WHERE runid = ?";
               PreparedStatement statement = conn.prepareStatement(sql);
        	   statement.setInt(1, runId);
               ResultSet resultSet = statement.executeQuery();
               if (resultSet.next()){
	        	   runState.setRunId(resultSet.getInt("runid"));
	        	   runState.setState(resultSet.getString("state"));
	        	   runState.setPlandatetime(resultSet.getTimestamp("plandatetime"));
	        	   runState.setStartdatetime(resultSet.getTimestamp("startdatetime"));
	        	   runState.setEnddatetime(resultSet.getTimestamp("enddatetime"));
               }
               resultSet.close();
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return runState;

	}

	@Override
	public RunState lastRunState() {
		
		RunState runState = new RunState();
		try {
			/*SELECT * from runstate where runid in (SELECT max(runid) AS runid FROM runstate)*/
            String sql = "SELECT * from runstate where runid in (SELECT max(runid) AS runid FROM runstate)";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()){
	            runState.setRunId(resultSet.getInt("runid"));
	     	    runState.setState(resultSet.getString("state"));
	     	    runState.setPlandatetime(resultSet.getTimestamp("plandatetime"));
	     	    runState.setStartdatetime(resultSet.getTimestamp("startdatetime"));
	     	    runState.setEnddatetime(resultSet.getTimestamp("enddatetime"));
            }
            resultSet.close();
            statement.close();
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return runState;
		
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
