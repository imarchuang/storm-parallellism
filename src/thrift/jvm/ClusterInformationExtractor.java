package thrift.jvm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ErrorInfo;
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Client;


//TSocket tsocket = new TSocket("localhost", 8772);
//String topologyId = "exclam_wMetrics-10-1415929187";

/*
 * Library to extract Storm Web UI Parameter Values
*/
public class ClusterInformationExtractor {
  public static void main(String[] args) {
   TSocket socket = new TSocket("localhost", 8772);
  TFramedTransport transport = new TFramedTransport(socket);
  TBinaryProtocol protocol = new TBinaryProtocol(transport);
  Client client = new Client(protocol);
  try {
   transport.open();
   ClusterSummary summary = client.getClusterInfo();

    // Cluster Details
    System.out.println("**** Storm UI Home Page ****");
    System.out.println(" ****Cluster Summary**** ");
   int nimbusUpTime = summary.getNimbus_uptime_secs();
   System.out.println("Nimbus Up Time: "  + nimbusUpTime);
   System.out.println("Number of Supervisors: "  + summary.getSupervisorsSize());
   System.out.println("Number of Topologies: "  + summary.getTopologiesSize());

    // Topology stats
    System.out.println(" ****Topology summary**** ");
    Map<String, String> topologyConfigurationParamValues = new HashMap<String, String>();
    List<TopologySummary> topologies = summary.getTopologies();
    Iterator<TopologySummary> topologiesIterator = summary.getTopologiesIterator();
   while(topologiesIterator.hasNext()) {
     TopologySummary topology = topologiesIterator.next();
    System.out.println("Topology ID: "  + topology.getId());
    System.out.println("Topology Name: " + topology.getName());
    System.out.println("Number of Executors: " + topology.getNum_executors());
    System.out.println("Number of Tasks: " + topology.getNum_tasks());
    System.out.println("Number of Workers: " + topology.getNum_workers());
    System.out.println("Status : " + topology.getStatus());
    System.out.println("UpTime in Seconds: " + topology.getUptime_secs());
   }

    // Supervisor stats
    System.out.println("**** Supervisor summary ****");
    List<SupervisorSummary> supervisors = summary.getSupervisors();
   Iterator<SupervisorSummary> supervisorsIterator = summary.getSupervisorsIterator();
   while(supervisorsIterator.hasNext()) {
    SupervisorSummary supervisor = supervisorsIterator.next();
    System.out.println("Supervisor ID: "  + supervisor.getSupervisor_id());
    System.out.println("Host: " + supervisor.getHost());
    System.out.println("Number of used workers: " + supervisor.getNum_used_workers());
    System.out.println("Number of workers: " + supervisor.getNum_workers());
    System.out.println("Supervisor uptime: " + supervisor.getUptime_secs());
   }

    // Nimbus config parameter-values
  System.out.println("****Nimbus Configuration****");
  Map<String, String> nimbusConfigurationParamValues = new HashMap<String, String>();
   String nimbusConfigString = client.getNimbusConf();
   nimbusConfigString = nimbusConfigString.substring(1, nimbusConfigString.length()-1);
   String [] nimbusConfParameters = nimbusConfigString.split(",\"");
    for(String nimbusConfParamValue : nimbusConfParameters) {
    String [] paramValue = nimbusConfParamValue.split(":");
    String parameter = paramValue[0].substring(0, paramValue[0].length()-1);
    String parameterValue = paramValue[1];
    if(paramValue[1].startsWith("\"")) {
     parameterValue = paramValue[1].substring(1, paramValue[1].length()-1);
    }
    nimbusConfigurationParamValues.put(parameter, parameterValue);   
   }

    Set<String> nimbusConfigurationParameters = nimbusConfigurationParamValues.keySet();
   Iterator<String> parameters = nimbusConfigurationParameters.iterator();
   while(parameters.hasNext()) {
    String key = parameters.next();
    System.out.println("Parameter : " + key + " Value : " + nimbusConfigurationParamValues.get(key));
   }

    System.out.println(" **** End of Storm UI Home Page Details**** ");

     // Topology stats
    System.out.println(" **** Topology Home Page Details **** ");
    topologiesIterator = summary.getTopologiesIterator();
   while(topologiesIterator.hasNext()) {
    TopologySummary topology = topologiesIterator.next();
    System.out.println("**** Topology summary ****");
    System.out.println("Topology Id: "  + topology.getId());
    System.out.println("Topology Name: " + topology.getName());
    System.out.println("Number of Executors: " + topology.getNum_executors());
    System.out.println("Number of Tasks: " + topology.getNum_tasks());
    System.out.println("Number of Workers: " + topology.getNum_workers());
    System.out.println("Status: " + topology.getStatus());
    System.out.println("UpTime in Seconds: " + topology.getUptime_secs());
                                
     // Spouts (All time)
     System.out.println("**** Spouts (All time) ****");
    TopologyInfo topology_info = client.getTopologyInfo(topology.getId());
    Iterator<ExecutorSummary> executorStatusItr = topology_info.getExecutorsIterator();
    while(executorStatusItr.hasNext()) {
     // get the executor
     ExecutorSummary executor_summary =  executorStatusItr.next();
     ExecutorStats execStats = executor_summary.getStats();
     ExecutorSpecificStats execSpecStats = execStats.getSpecific();
     String componentId = executor_summary.getComponent_id();
     // if the executor is a spout
     if(execSpecStats.isSetSpout()) {
      SpoutStats spoutStats = execSpecStats.getSpout();
      System.out.println("Spout Id: " + componentId);
      System.out.println("Transferred: " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
      System.out.println("Emitted: " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
      System.out.println("Acked: " + getStatValueFromMap(spoutStats.getAcked(), ":all-time"));
      System.out.println("Failed: " + getStatValueFromMap(spoutStats.getFailed(), ":all-time"));
     }
    }
                                
    // Bolts (All time)
     System.out.println("****Bolts (All time)****");
    executorStatusItr = topology_info.getExecutorsIterator();
    while(executorStatusItr.hasNext()) {
     // get the executor
     ExecutorSummary executor_summary =  executorStatusItr.next();
     ExecutorStats execStats = executor_summary.getStats();
     ExecutorSpecificStats execSpecStats = execStats.getSpecific();
     String componentId = executor_summary.getComponent_id();
     if(execSpecStats.isSetBolt()) {
      BoltStats boltStats = execSpecStats.getBolt();
      System.out.println("Bolt Id: " + componentId);
      System.out.println("Transferred: " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
      System.out.println("Emitted: " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
      System.out.println("Acked: " + getBoltStatLongValueFromMap(boltStats.getAcked(), ":all-time"));
      System.out.println("Failed: " + getBoltStatLongValueFromMap(boltStats.getFailed(), ":all-time"));
      System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), ":all-time"));
      System.out.println("Execute Latency (ms): " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), ":all-time"));
      System.out.println("Process Latency (ms): " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), ":all-time"));
     }
    }
                                
    // Topology Configuration
     System.out.println("**** Topology Configuration ****");
     String topologyConfigString = client.getTopologyConf(topology.getId());
    topologyConfigString = topologyConfigString.substring(1, topologyConfigString.length()-1);
    String [] topologyConfParameters = topologyConfigString.split(",\"");

     for(String topologyConfParamValue : topologyConfParameters) {
     String [] paramValue = topologyConfParamValue.split(":");
     String parameter = paramValue[0].substring(0, paramValue[0].length()-1);
     String parameterValue = paramValue[1];
     if(paramValue[1].startsWith("\"")) {
      parameterValue = paramValue[1].substring(1, paramValue[1].length()-1);
     }
     topologyConfigurationParamValues.put(parameter, parameterValue);   
    }
     Set<String> topologyConfigurationParameters = topologyConfigurationParamValues.keySet();
    Iterator<String> topologyParameters = topologyConfigurationParameters.iterator();
    while(topologyParameters.hasNext()) {
     String key = topologyParameters.next();
     System.out.println("Parameter: " + key + " Value : " + topologyConfigurationParamValues.get(key));
    }
   }
   System.out.println(" ****  End of Topology Home Page Details ****");
                        
   //  Spout Home Page Details
    System.out.println(" **** Spout Home Page Details ****");
   topologiesIterator = summary.getTopologiesIterator();
   while(topologiesIterator.hasNext()) {
    TopologySummary topology = topologiesIterator.next();
    TopologyInfo topology_info = client.getTopologyInfo(topology.getId());
    Iterator<ExecutorSummary> executorStatusItr = topology_info.getExecutorsIterator();
    while(executorStatusItr.hasNext()) {
     // get the executor
     ExecutorSummary executor_summary =  executorStatusItr.next();
     ExecutorStats execStats = executor_summary.getStats();
     ExecutorSpecificStats execSpecStats = execStats.getSpecific();
     String componentId = executor_summary.getComponent_id();
     // if the executor is a spout
     if(execSpecStats.isSetSpout()) {
      spoutSpecificStats(topology_info, topology, executor_summary, componentId);
     }
    }
   }
    System.out.println(" **** End of Spout Home Page Details**** ");
                        
   // Bolt Home Page Details
    System.out.println(" **** Bolt Home Page Details ****");
   topologiesIterator = summary.getTopologiesIterator();
   while(topologiesIterator.hasNext()) {
    TopologySummary topology = topologiesIterator.next();
    TopologyInfo topology_info = client.getTopologyInfo(topology.getId());
    Iterator<ExecutorSummary> executorStatusItr = topology_info.getExecutorsIterator();
    while(executorStatusItr.hasNext()) {
     // get the executor
     ExecutorSummary executor_summary =  executorStatusItr.next();
     ExecutorStats execStats = executor_summary.getStats();
     ExecutorSpecificStats execSpecStats = execStats.getSpecific();
     String componentId = executor_summary.getComponent_id();
     // if the executor is a bolt
     if(execSpecStats.isSetBolt()) {
      boltSpecificStats(topology_info, topology, executor_summary, componentId);
     }
    }
   }
    System.out.println(" **** End of Bolt Home Page Details **** ");
   transport.close();
   } catch (TTransportException e) {
   e.printStackTrace();
  } catch (TException e) {
   e.printStackTrace();
  } catch (NotAliveException e) {
   e.printStackTrace();
  }
 }
 
/*
 * Calculate spout specific stats
 */  
private static void spoutSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary,
   String componentId) {
  ExecutorStats execStats = executorSummary.getStats();
  ExecutorSpecificStats execSpecStats = execStats.getSpecific();
  SpoutStats spoutStats = execSpecStats.getSpout();
  System.out.println("**** Component summary ****");
  System.out.println("Id : " + componentId);
  System.out.println("Topology Name  : " + topology.getName());
  System.out.println("Executors : " + "1");
  System.out.println("Tasks : " + "1");
  System.out.println("**** Spout stats ****");
  System.out.println("**** Window Size ****  " + "600");
   System.out.println("Transferred: " + getStatValueFromMap(execStats.getTransferred(), "600"));
  System.out.println("Emitted: " + getStatValueFromMap(execStats.getEmitted(), "600"));
  System.out.println("Acked: " + getStatValueFromMap(spoutStats.getAcked(), "600"));
  System.out.println("Failed: " + getStatValueFromMap(spoutStats.getFailed(), "600"));
  System.out.println("**** Window Size ****  " + "10800");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), "10800"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), "10800"));
  System.out.println("Acked : " + getStatValueFromMap(spoutStats.getAcked(), "10800"));
  System.out.println("Failed : " + getStatValueFromMap(spoutStats.getFailed(), "10800"));
  System.out.println("**** Window Size ****  " + "86400");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), "86400"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), "86400"));
  System.out.println("Acked : " + getStatValueFromMap(spoutStats.getAcked(), "86400"));
  System.out.println("Failed : " + getStatValueFromMap(spoutStats.getFailed(), "86400"));
  System.out.println("**** Window Size ****  " + "all-time");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getStatValueFromMap(spoutStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getStatValueFromMap(spoutStats.getFailed(), ":all-time"));

   System.out.println("**** Output stats (All time) ****");
  System.out.println("Stream : " + "default");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getStatValueFromMap(spoutStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getStatValueFromMap(spoutStats.getFailed(), ":all-time"));

   System.out.println("**** Executors (All time) ****");
   System.out.println("Host : " + executorSummary.getHost());
  System.out.println("Port : " + executorSummary.getPort());
  System.out.println("Up-Time : " + executorSummary.getUptime_secs());
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getStatValueFromMap(spoutStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getStatValueFromMap(spoutStats.getFailed(), ":all-time"));

   System.out.println("**** Errors ****");
  Map<String, List<ErrorInfo>> errors = topologyInfo.getErrors();
  List<ErrorInfo> spoutErrors = errors.get(componentId);
  for(ErrorInfo errorInfo : spoutErrors) {
   System.out.println("Spout Error : " + errorInfo.getError()); 
  }  
 }


/*
 * Calculate bolt specific stats
 */
  private static void boltSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary,
   String componentId) {
  ExecutorStats execStats = executorSummary.getStats();
  ExecutorSpecificStats execSpecStats = execStats.getSpecific();
  BoltStats boltStats = execSpecStats.getBolt();
  System.out.println(":::::::::: Component summary ::::::::::");
  System.out.println("Id : " + componentId);
  System.out.println("Topology Name  : " + topology.getName());
  System.out.println("Executors : " + "1");
  System.out.println("Tasks : " + "1");
  System.out.println(":::::::::: Bolt stats ::::::::::");
  System.out.println("::::::::::: Window Size :::::::::::  " + "600");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), "600"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), "600"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), "600"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), "600"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), "600"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), "600"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), "600"));
  System.out.println("::::::::::: Window Size :::::::::::  " + "10800");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), "10800"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), "10800"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), "10800"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), "10800"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), "10800"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), "10800"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), "10800"));
  System.out.println("::::::::::: Window Size :::::::::::  " + "86400");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), "86400"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), "86400"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), "86400"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), "86400"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), "86400"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), "86400"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), "86400"));
  System.out.println("::::::::::: Window Size :::::::::::  " + "all-time");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), ":all-time"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), ":all-time"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), ":all-time"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), ":all-time"));  

   System.out.println(":::::::::: Output stats (All time) ::::::::::");
  System.out.println("Stream : " + "default");
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), ":all-time"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), ":all-time"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), ":all-time"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), ":all-time"));

   System.out.println(":::::::::: Executors (All time) ::::::::::");
  System.out.println("Host : " + executorSummary.getHost());
  System.out.println("Port : " + executorSummary.getPort());
  System.out.println("Up-Time : " + executorSummary.getUptime_secs());
  System.out.println("Transferred : " + getStatValueFromMap(execStats.getTransferred(), ":all-time"));
  System.out.println("Emitted : " + getStatValueFromMap(execStats.getEmitted(), ":all-time"));
  System.out.println("Acked : " + getBoltStatLongValueFromMap(boltStats.getAcked(), ":all-time"));
  System.out.println("Failed : " + getBoltStatLongValueFromMap(boltStats.getFailed(), ":all-time"));
  System.out.println("Executed : " + getBoltStatLongValueFromMap(boltStats.getExecuted(), ":all-time"));
  System.out.println("Execute Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getExecute_ms_avg(), ":all-time"));
  System.out.println("Process Latency (ms) : " + getBoltStatDoubleValueFromMap(boltStats.getProcess_ms_avg(), ":all-time"));

   System.out.println(":::::::::: Errors ::::::::::");
  Map<String, List<ErrorInfo>> errors = topologyInfo.getErrors();
  System.out.println(errors.keySet());
  List<ErrorInfo> boltErrors = errors.get(componentId);
  for(ErrorInfo errorInfo : boltErrors) {
   System.out.println("Bolt Error : " + errorInfo.getError()); 
  }  
 }
        
/*
 * Utility method to parse a Map<>
 */
  public static Long getStatValueFromMap(Map<String, Map<String, Long>> map, String statName) {
  Long statValue = null;
  Map<String, Long> intermediateMap = map.get(statName);
  statValue = intermediateMap.get("default");
  return statValue;
 }

/*
 * Utility method to parse a Map<> as a special case for Bolts
 */
  public static Double getBoltStatDoubleValueFromMap(Map<String, Map<GlobalStreamId, Double>> map, String statName) {
  Double statValue = 0.0;
  Map<GlobalStreamId, Double> intermediateMap = map.get(statName);
  Set<GlobalStreamId> key = intermediateMap.keySet();
  if(key.size() > 0) {
   Iterator<GlobalStreamId> itr = key.iterator();
   statValue = intermediateMap.get(itr.next());
  }
  return statValue;
 }

/*
 * Utility method for Bolts
 */
  public static Long getBoltStatLongValueFromMap(Map<String, Map<GlobalStreamId, Long>> map, String statName) {
  Long statValue = null;
  Map<GlobalStreamId, Long> intermediateMap = map.get(statName);
  Set<GlobalStreamId> key = intermediateMap.keySet();
  if(key.size() > 0) {
   Iterator<GlobalStreamId> itr = key.iterator();
   statValue = intermediateMap.get(itr.next());
  }
  return statValue;
 }
}
