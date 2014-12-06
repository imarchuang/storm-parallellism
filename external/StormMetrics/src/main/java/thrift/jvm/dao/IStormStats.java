package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Component;
import thrift.jvm.Executor;
import thrift.jvm.StormStats;
import thrift.jvm.Topology;

public interface IStormStats {
	
	public void insert(StormStats stormStats);
    public List<StormStats> select();
    public List<StormStats> findByTopology(Topology topology);
    public List<StormStats> findByComponent(Component component);
    public List<StormStats> findByExecutor(Executor executor);
    public List<StormStats> aggregateByComponentId(int activeRunId);
    public List<StormStats> aggregateByTopo(int activeRunId);

}
