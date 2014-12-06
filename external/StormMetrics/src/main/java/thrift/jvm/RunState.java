package thrift.jvm;

import java.sql.Timestamp;

import thrift.jvm.util.CommonUtils;

public class RunState {
	
	int runId;
	String state;
	Timestamp plandatetime;
	Timestamp startdatetime;
	Timestamp enddatetime;
	
	RunState(int runId, String state, Timestamp plandatetime, Timestamp startdatetime, Timestamp enddatetime){
		
		this.runId = runId;
		this.state = state;
		this.plandatetime = plandatetime;
		this.startdatetime = startdatetime;
		this.enddatetime = enddatetime;
		
	}
	
	public RunState(){
		this(0, "PLANNED",CommonUtils.getCurrentTimeStamp(),null,null);
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Timestamp getPlandatetime() {
		return plandatetime;
	}

	public void setPlandatetime(Timestamp plandatetime) {
		this.plandatetime = plandatetime;
	}

	public Timestamp getStartdatetime() {
		return startdatetime;
	}

	public void setStartdatetime(Timestamp startdatetime) {
		this.startdatetime = startdatetime;
	}

	public Timestamp getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(Timestamp enddatetime) {
		this.enddatetime = enddatetime;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}	
	
}
