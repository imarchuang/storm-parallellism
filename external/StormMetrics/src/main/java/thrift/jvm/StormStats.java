package thrift.jvm;

import java.sql.Timestamp;
import java.util.List;

import thrift.jvm.util.CommonUtils;

public class StormStats {
	
	int runId;
	String topo_name;
	String component_id;
	String executor_id;
	String level; 
	String window; 
	long emitted;
	long transferred;
	double complete_latency;
	long acked;
	long failed;
	double capacity;  
	double execute_latency;
	double process_latency;
	long executed;
	Timestamp datetime;
	
	List<Topology> topologies;
	
	StormStats(int runId,String topo_name,String component_id,String executor_id,String level,String window,
			long emitted,long transferred,double complete_latency,long acked,long failed,
			double capacity,double execute_latency,double process_latency,long executed,Timestamp datetime){
		this.runId = runId;
		this.topo_name = topo_name;
		this.component_id = component_id;
		this.executor_id = executor_id;
		this.level = level;
		this.window = window;
		this.emitted = emitted;
		this.transferred = transferred;
		this.complete_latency = complete_latency;
		this.acked = acked;
		this.failed = failed;
		this.capacity = capacity;
		this.execute_latency = execute_latency;
		this.process_latency = process_latency;
		this.executed = executed;
		this.datetime = datetime;
		
	}
	
	public StormStats(){
		this(0, "NULL", "NULL", "NULL", "NULL", "NULL", 0, 0, 0.0, 
				0, 0, 0.0, 0.0, 0.0, 0,CommonUtils.getCurrentTimeStamp());
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getWindow() {
		return window;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	public long getEmitted() {
		return emitted;
	}

	public void setEmitted(long emitted) {
		this.emitted = emitted;
	}

	public long getTransferred() {
		return transferred;
	}

	public void setTransferred(long transferred) {
		this.transferred = transferred;
	}

	public double getComplete_latency() {
		return complete_latency;
	}

	public void setComplete_latency(double complete_latency) {
		this.complete_latency = complete_latency;
	}

	public long getAcked() {
		return acked;
	}

	public void setAcked(long acked) {
		this.acked = acked;
	}

	public long getFailed() {
		return failed;
	}

	public void setFailed(long failed) {
		this.failed = failed;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getExecute_latency() {
		return execute_latency;
	}

	public void setExecute_latency(double execute_latency) {
		this.execute_latency = execute_latency;
	}

	public double getProcess_latency() {
		return process_latency;
	}

	public void setProcess_latency(double process_latency) {
		this.process_latency = process_latency;
	}

	public long getExecuted() {
		return executed;
	}

	public void setExecuted(long executed) {
		this.executed = executed;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public List<Topology> getTopologies() {
		return topologies;
	}

	public void setTopologies(List<Topology> topologies) {
		this.topologies = topologies;
	}

}
