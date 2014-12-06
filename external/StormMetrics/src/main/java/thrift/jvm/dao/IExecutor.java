package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Component;
import thrift.jvm.Executor;
import thrift.jvm.Topology;

public interface IExecutor {
	
	public void insert(Executor executor);
    public List<Executor> select();
    public List<Executor> findByTopology(Topology topology);
    public List<Executor> findByComponent(Component component);
    public List<Component> findNumExecutorsAndListPerComponent(int activeRunId);

}
