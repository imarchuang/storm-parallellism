package thrift.jvm.dao;

import java.util.List;

import thrift.jvm.Component;
import thrift.jvm.Topology;

public interface IComponent {
	
	public void insert(Component component);
    public List<Component> select();
    public List<Component> findByTopology(Topology topology);
    public void updateNumTasksAndList(Component component);

}
