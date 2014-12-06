package thrift.jvm;

import java.sql.Timestamp;

import thrift.jvm.util.CommonUtils;

public class Topology {

	int runId;
	String name;
	String topo_id;
	String status;
	int topo_uptime;
	int num_workers;
	int num_executors;
	int num_tasks;
	Timestamp datetime;

	Topology(int runId,	String name, String topo_id, String status,	int topo_uptime, int num_workers, 
			int num_executors, int num_tasks, Timestamp datetime){
		this.runId = runId;
		this.name = name;
		this.topo_id = topo_id;
		this.status = status;
		this.topo_uptime = topo_uptime;
		this.num_workers = num_workers;
		this.num_executors = num_executors;
		this.num_tasks = num_tasks;
		this.datetime = datetime;

		
	}
	
	public Topology(){
		this(0, "", "", "", 0, 0, 0, 0, CommonUtils.getCurrentTimeStamp());
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runid) {
		this.runId = runid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTopo_id() {
		return topo_id;
	}

	public void setTopo_id(String topo_id) {
		this.topo_id = topo_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTopo_uptime() {
		return topo_uptime;
	}

	public void setTopo_uptime(int nimbus_uptime) {
		this.topo_uptime = nimbus_uptime;
	}

	public int getNum_workers() {
		return num_workers;
	}

	public void setNum_workers(int num_workers) {
		this.num_workers = num_workers;
	}

	public int getNum_executors() {
		return num_executors;
	}

	public void setNum_executors(int num_executors) {
		this.num_executors = num_executors;
	}

	public int getNum_tasks() {
		return num_tasks;
	}

	public void setNum_tasks(int num_tasks) {
		this.num_tasks = num_tasks;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
}
