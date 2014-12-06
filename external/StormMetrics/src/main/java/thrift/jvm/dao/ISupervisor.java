package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Supervisor;

public interface ISupervisor {
	
	public void insert(Supervisor supervisor);
    public List<Supervisor> select();

}
