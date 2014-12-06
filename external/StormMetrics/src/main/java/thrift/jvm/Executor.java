package thrift.jvm;

import java.sql.Timestamp;

import thrift.jvm.util.CommonUtils;

public class Executor {
	
	int runId;
	String topo_name;
	String component_id;
	String executor_id;
	String category;
	String host;
	String port;

	int executor_uptime;
	Timestamp datetime;

	Executor(int runId, String topo_name, String component_id, String executor_id, String category, String host, 
			String port, int executor_uptime, Timestamp datetime){
		this.runId = runId;
		this.topo_name = topo_name;
		this.component_id = component_id;
		this.executor_id = executor_id;
		this.category = category;
		this.host = host;
		this.port = port;
		this.executor_uptime = executor_uptime;
		this.datetime = datetime;
	}
	
	public Executor(){
		this(0,"","","","","","",0, CommonUtils.getCurrentTimeStamp());
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public String getTopo_name() {
		return topo_name;
	}

	public void setTopo_name(String topo_name) {
		this.topo_name = topo_name;
	}

	public String getComponent_id() {
		return component_id;
	}

	public void setComponent_id(String component_id) {
		this.component_id = component_id;
	}

	public String getExecutor_id() {
		return executor_id;
	}

	public void setExecutor_id(String executor_id) {
		this.executor_id = executor_id;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getExecutor_uptime() {
		return executor_uptime;
	}

	public void setExecutor_uptime(int executor_uptime) {
		this.executor_uptime = executor_uptime;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

}
