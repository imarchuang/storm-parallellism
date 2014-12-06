package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Cluster;

public interface ICluster {
	
	public void insert(Cluster cluster);
    public List<Cluster> select();
    public void update(Cluster cluster);

}
