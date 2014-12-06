package thrift.jvm;

import java.sql.Timestamp;

import thrift.jvm.util.CommonUtils;

public class Component {
	
	int runId;
	String topo_name;
	String component_id;
	String category;
	int seq;
	String from_component;
	String to_component;
	String ip_grouping;
	String op_grouping;
	String ip_fields;
	String op_fields;
	int num_executors;
	int num_tasks;
	String dist_list;
	Timestamp datetime;

	Component(int runId,String topo_name,String component_id,String category,
			int seq,String from_component,String to_component,String ip_grouping,String op_grouping,
			String ip_fields,String op_fields,int num_executors,int num_tasks,String dist_list,Timestamp datetime){
		this.runId = runId;
		this.topo_name = topo_name;
		this.component_id = component_id;
		this.category = category;
		this.seq = seq;
		this.from_component = from_component;
		this.to_component = to_component;
		this.ip_grouping = ip_grouping;
		this.op_grouping = op_grouping;
		this.ip_fields = ip_fields;
		this.op_fields = op_fields;
		this.num_executors = num_executors;
		this.num_tasks = num_tasks;
		this.dist_list = dist_list;
		this.datetime = datetime;
	}
	
	public Component(){
		this(0,"","","",0,"","","","","","",0,0,"",CommonUtils.getCurrentTimeStamp());
	}

	public String getDist_list() {
		return dist_list;
	}

	public void setDist_list(String dist_list) {
		this.dist_list = dist_list;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getFrom_component() {
		return from_component;
	}

	public void setFrom_component(String from_component) {
		this.from_component = from_component;
	}

	public String getTo_component() {
		return to_component;
	}

	public void setTo_component(String to_component) {
		this.to_component = to_component;
	}

	public String getIp_grouping() {
		return ip_grouping;
	}

	public void setIp_grouping(String ip_grouping) {
		this.ip_grouping = ip_grouping;
	}

	public String getOp_grouping() {
		return op_grouping;
	}

	public void setOp_grouping(String op_grouping) {
		this.op_grouping = op_grouping;
	}

	public String getIp_fields() {
		return ip_fields;
	}

	public void setIp_fields(String ip_fields) {
		this.ip_fields = ip_fields;
	}

	public String getOp_fields() {
		return op_fields;
	}

	public void setOp_fields(String op_fields) {
		this.op_fields = op_fields;
	}
	
}
