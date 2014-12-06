package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Topology;

public interface ITopology {
	
	public void insert(Topology topology);
    public List<Topology> select();
    //public void update(Topology topology);

}
