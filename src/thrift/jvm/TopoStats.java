package thrift.jvm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransportException;

import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ErrorInfo;
import backtype.storm.generated.ExecutorInfo;
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Client;


public class TopoStats{
   public static void main(String[] args) {
	TSocket tsocket = new TSocket("localhost", 6227);
	TFramedTransport tTransport = new TFramedTransport(tsocket);
	TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(tTransport);
	Nimbus.Client client = new Nimbus.Client(tBinaryProtocol);
	String topologyId = "exclam_wMetrics-10-1415929187";


	try {
		tTransport.open();
		ClusterSummary clusterSummary = client.getClusterInfo();
		StormTopology stormTopology = client.getTopology(topologyId);
		TopologyInfo topologyInfo = client.getTopologyInfo(topologyId);
		List<ExecutorSummary> executorSummaries = topologyInfo.get_executors();

		List<TopologySummary> topologies = clusterSummary.get_topologies();
		for(ExecutorSummary executorSummary : executorSummaries){

			String id = executorSummary.get_component_id();
			ExecutorInfo executorInfo = executorSummary.get_executor_info();
			ExecutorStats executorStats = executorSummary.get_stats();
			System.out.println("executorSummary :: " + id + " emit size :: " + executorStats.get_emitted_size());
		}
	} catch (TTransportException e) {
		e.printStackTrace();
	} catch (TException e) {
		e.printStackTrace();
	} catch (NotAliveException e) {
		e.printStackTrace();
	}
   }
}
