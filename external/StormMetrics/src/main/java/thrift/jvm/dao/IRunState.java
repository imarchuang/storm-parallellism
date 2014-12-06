package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.RunState;

public interface IRunState {
	 
    public void insert(RunState runState);
	public void update(RunState runState);
	public RunState find(int runId);
	public RunState lastRunState();
    public List<RunState> select();

}
