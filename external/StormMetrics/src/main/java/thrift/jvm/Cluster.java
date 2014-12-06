package thrift.jvm;

import java.sql.Timestamp;
import java.util.List;

import thrift.jvm.util.CommonUtils;

public class Cluster {
	
	int runId;
	int id ;
	String version;
	int nimbus_uptime; 
	int supervisors;
	int used_slots;
	int free_slots; 
	int total_slots;
	int executors;
	int tasks;
	Timestamp datetime;
	
	List<Topology> topologies;
	
	Cluster(int runId, int id,	String version,	int nimbus_uptime, int supervisors,	int used_slots,	int free_slots,
			int total_slots, int executors, int tasks, Timestamp datetime, List<Topology> topologies){
		this.runId = runId;
		this.id = id;
		this.version = version;
		this.nimbus_uptime = nimbus_uptime;
		this.supervisors = supervisors;
		this.used_slots = used_slots;
		this.free_slots = free_slots;
		this.total_slots = total_slots;
		this.executors = executors;
		this.tasks = tasks;
		this.datetime = datetime;
		this.topologies = topologies;
		
	}
	
	Cluster(int runId, int id,	String version,	int nimbus_uptime, int supervisors,	int used_slots,	int free_slots,
			int total_slots, int executors, int tasks, Timestamp datetime){
		this(runId, id, version, nimbus_uptime, supervisors, used_slots, free_slots,
				total_slots, executors, tasks, datetime, null);
	}
	
	public Cluster(){
		this(0, 0, "", 0, 0, 0, 0, 0, 0, 0, CommonUtils.getCurrentTimeStamp(), null);
	}
	
	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public List<Topology> getTopologies() {
		return topologies;
	}
	public void setTopologies(List<Topology> topologies) {
		this.topologies = topologies;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getNimbus_uptime() {
		return nimbus_uptime;
	}
	public void setNimbus_uptime(int nimbus_uptime) {
		this.nimbus_uptime = nimbus_uptime;
	}
	public int getSupervisors() {
		return supervisors;
	}
	public void setSupervisors(int supervisors) {
		this.supervisors = supervisors;
	}
	public int getUsed_slots() {
		return used_slots;
	}
	public void setUsed_slots(int used_slots) {
		this.used_slots = used_slots;
	}
	public int getFree_slots() {
		return free_slots;
	}
	public void setFree_slots(int free_slots) {
		this.free_slots = free_slots;
	}
	public int getTotal_slots() {
		return total_slots;
	}
	public void setTotal_slots(int total_slots) {
		this.total_slots = total_slots;
	}
	public int getExecutors() {
		return executors;
	}
	public void setExecutors(int executors) {
		this.executors = executors;
	}
	public int getTasks() {
		return tasks;
	}
	public void setTasks(int tasks) {
		this.tasks = tasks;
	}
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

}
