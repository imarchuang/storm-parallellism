package thrift.jvm;

import java.sql.Timestamp;

import thrift.jvm.util.CommonUtils;

public class Supervisor {
	
	int runId;
	String sup_id;
	String host;
	int supervisor_uptime; 
	int slots;
	int used_slots;
	Timestamp datetime;
	
	Supervisor(int runId, String sup_id, String host, int supervisor_uptime, int slots, int used_slots, Timestamp datetime){
		this.runId = runId;
		this.sup_id = sup_id;
		this.host = host;
		this.supervisor_uptime = supervisor_uptime;
		this.slots = slots;
		this.used_slots = used_slots;
		this.datetime = datetime;
		
	}
	
	public Supervisor(){
		this(0,"","",0,0,0,CommonUtils.getCurrentTimeStamp());
	}
	
	public int getRunId() {
		return runId;
	}
	public void setRunId(int runId) {
		this.runId = runId;
	}
	public String getSup_id() {
		return sup_id;
	}
	public void setSup_id(String sup_id) {
		this.sup_id = sup_id;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getSupervisor_uptime() {
		return supervisor_uptime;
	}
	public void setSupervisor_uptime(int supervisor_uptime) {
		this.supervisor_uptime = supervisor_uptime;
	}
	public int getSlots() {
		return slots;
	}
	public void setSlots(int slots) {
		this.slots = slots;
	}
	public int getUsed_slots() {
		return used_slots;
	}
	public void setUsed_slots(int used_slots) {
		this.used_slots = used_slots;
	}
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

}
