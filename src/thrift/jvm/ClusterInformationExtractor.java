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
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
//import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thrift.jvm.util.CommonUtils;


//TSocket tsocket = new TSocket("localhost", 8772);
//String topologyId = "exclam_wMetrics-10-1415929187";

/*
 * Library to extract Storm Web UI Parameter Values
*/
public class ClusterInformationExtractor {
	
  private static final Logger logger = LoggerFactory.getLogger(ClusterInformationExtractor.class);
  
  //static CommonUtils utils = new CommonUtils();
	
  public static void main(String[] args) {
  TSocket socket = new TSocket("nimbus", 6627);
  TFramedTransport transport = new TFramedTransport(socket);
  TBinaryProtocol protocol = new TBinaryProtocol(transport);
  Client client = new Client(protocol);
  try {
    transport.open();
    ClusterSummary summary = client.getClusterInfo();
    
    logger.info("***************starting*********************************");

    // Cluster Details
    System.out.println("**** Storm UI Home Page ****");
    System.out.println(" ****Cluster Summary**** ");
    int nimbusUpTime = summary.get_nimbus_uptime_secs();
    System.out.println("Nimbus Up Time: "  + nimbusUpTime);
    System.out.println("Number of Supervisors: "  + summary.get_supervisors_size());
    System.out.println("Number of Topologies: "  + summary.get_topologies_size());
    
    String clusterDetails = "Cluster_Summary:: Nimbus_uptime="+ nimbusUpTime + "^NumOfSupervisors=" + summary.get_supervisors_size() 
    		+ "^NumOfTopologies=" + summary.get_topologies_size() ;
    logger.info(clusterDetails);

    // Topology stats
    System.out.println(" ****Topology summary**** ");
    Map<String, String> topologyConfigurationParamValues = new HashMap<String, String>();
    List<TopologySummary> topologies = summary.get_topologies();
    Iterator<TopologySummary> topologiesIterator = summary.get_topologies_iterator();
    while(topologiesIterator.hasNext()) {
        TopologySummary topology = topologiesIterator.next();
        System.out.println("Topology ID: "  + topology.get_id());
        System.out.println("Topology Name: " + topology.get_name());
        System.out.println("Number of Executors: " + topology.get_num_executors());
        System.out.println("Number of Tasks: " + topology.get_num_tasks());
        System.out.println("Number of Workers: " + topology.get_num_workers());
        System.out.println("Status : " + topology.get_status());
        System.out.println("UpTime in Seconds: " + topology.get_uptime_secs());
        String topoSummary = "Topology_Summary:: Topology_id="+ topology.get_id() + "^Topology_name=" + topology.get_name() 
        		+ "^NumOfExecutors=" + topology.get_num_executors() + "^NumOfTasks=" + topology.get_num_tasks() 
        		+ "^NumOfWorkers=" + topology.get_num_workers()
        		+ "^Status=" + topology.get_status() + "^UpTime_in_sec=" + topology.get_uptime_secs();
        logger.info(topoSummary);
    }

    // Supervisor stats
    System.out.println("**** Supervisor summary ****");
    List<SupervisorSummary> supervisors = summary.get_supervisors();
    Iterator<SupervisorSummary> supervisorsIterator = summary.get_supervisors_iterator();
    while(supervisorsIterator.hasNext()) {
        SupervisorSummary supervisor = supervisorsIterator.next();
        System.out.println("Supervisor ID: "  + supervisor.get_supervisor_id());
        System.out.println("Host: " + supervisor.get_host());
        System.out.println("Number of used workers: " + supervisor.get_num_used_workers());
        System.out.println("Number of workers: " + supervisor.get_num_workers());
        System.out.println("Supervisor uptime: " + supervisor.get_uptime_secs());
        String topoSummary = "Supervisor_Summary:: Supervisor_id="+ supervisor.get_supervisor_id() + "^Host=" + supervisor.get_host() 
        		+ "^NumOfUsedWorkers=" + supervisor.get_num_used_workers()
        		+ "^NumOfWorkers=" + supervisor.get_num_workers()
        		+ "^Supervisor_uptime=" + supervisor.get_uptime_secs();
        logger.info(topoSummary);
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
        //String paraPair = "ParaPair:: Parameter="+ key + "^Value=" + nimbusConfigurationParamValues.get(key);
        //logger.info(paraPair);
    }

    System.out.println(" **** End of Storm UI Home Page Details**** ");

    // Topology stats
    System.out.println(" **** Topology Home Page Details **** ");
    topologiesIterator = summary.get_topologies_iterator();
    while(topologiesIterator.hasNext()) {
        TopologySummary topology = topologiesIterator.next();
        System.out.println("**** Topology summary ****");
        System.out.println("Topology Id: "  + topology.get_id());
        System.out.println("Topology Name: " + topology.get_name());
        System.out.println("Number of Executors: " + topology.get_num_executors());
        System.out.println("Number of Tasks: " + topology.get_num_executors());
        System.out.println("Number of Workers: " + topology.get_num_executors());
        System.out.println("Status: " + topology.get_status());
        System.out.println("UpTime in Seconds: " + topology.get_uptime_secs());
        
        String topoDetails = "Topology_Details:: Topology_id="+ topology.get_id() + "^Topology_name=" + topology.get_name() 
        		+ "^NumOfExecutors=" + topology.get_num_executors() + "^NumOfTasks=" + topology.get_num_tasks() 
        		+ "^NumOfWorkers=" + topology.get_num_workers()
        		+ "^Status=" + topology.get_status() + "^UpTime_in_sec=" + topology.get_uptime_secs();
        logger.info(topoDetails);
                                
	    // Spouts (All time)
	    System.out.println("**** Spouts (All time) ****");
	    TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
	    Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
	    while(executorStatusItr.hasNext()) {
	        // get the executor
	        ExecutorSummary executor_summary =  executorStatusItr.next();
	        ExecutorStats execStats = executor_summary.get_stats();
	        ExecutorSpecificStats execSpecStats = execStats.get_specific();
	        String componentId = executor_summary.get_component_id();
	        // if the executor is a spout
	        if(execSpecStats.is_set_spout()) {
	            SpoutStats spoutStats = execSpecStats.get_spout();
	            System.out.println("Spout Id: " + componentId);
	            System.out.println("Transferred: " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
	            System.out.println("Emitted: " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
	            System.out.println("Acked: " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
	            System.out.println("Failed: " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), ":all-time"));
	            String spoutStats_log = "Spout_Stats:: Spout_id="+ componentId + "^Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time")
	            		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time") + "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), ":all-time") 
	            		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), ":all-time");
	            logger.info(spoutStats_log);
	        }
	    }
                                
	    // Bolts (All time)
	    System.out.println("****Bolts (All time)****");
	    executorStatusItr = topology_info.get_executors_iterator();
	    while(executorStatusItr.hasNext()) {
	        // get the executor
	        ExecutorSummary executor_summary =  executorStatusItr.next();
	        ExecutorStats execStats = executor_summary.get_stats();
	        ExecutorSpecificStats execSpecStats = execStats.get_specific();
	        String componentId = executor_summary.get_component_id();
	        // if the executor is a bolt
	        if(execSpecStats.is_set_bolt()) {
	            BoltStats boltStats = execSpecStats.get_bolt();
	            System.out.println("Bolt Id: " + componentId);
	            System.out.println("Transferred: " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
	            System.out.println("Emitted: " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
	            System.out.println("Acked: " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
	            System.out.println("Failed: " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
	            System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
	            System.out.println("Execute Latency (ms): " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
	            System.out.println("Process Latency (ms): " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));
	            String boltStats_log = "Bolt_Stats:: Bolt_id="+ componentId + "^Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time")
	            		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time") + "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), ":all-time") 
	            		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), ":all-time") 
	            		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), ":all-time")
	            		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time")
	            		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time");
	            logger.info(boltStats_log);
	        }
	    }
                                
		// Topology Configuration
		System.out.println("**** Topology Configuration ****");
		String topologyConfigString = client.getTopologyConf(topology.get_id());
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
				//String topoParaPair = "TopoParaPair:: Parameter="+ key + "^Value=" + topologyConfigurationParamValues.get(key);
		        //logger.info(topoParaPair);
		}
	}
    System.out.println(" ****  End of Topology Home Page Details ****");
                        
	//  Spout Home Page Details
	System.out.println(" **** Spout Home Page Details ****");
	topologiesIterator = summary.get_topologies_iterator();
	while(topologiesIterator.hasNext()) {
	    TopologySummary topology = topologiesIterator.next();
	    TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
	    Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
	    while(executorStatusItr.hasNext()) {
			     // get the executor
			     ExecutorSummary executor_summary =  executorStatusItr.next();
			     ExecutorStats execStats = executor_summary.get_stats();
			     ExecutorSpecificStats execSpecStats = execStats.get_specific();
			     String componentId = executor_summary.get_component_id();
			     // if the executor is a spout
			     if(execSpecStats.is_set_spout()) {
			    	 spoutSpecificStats(topology_info, topology, executor_summary, componentId);
			     }
	    }
	}
   
	System.out.println(" **** End of Spout Home Page Details**** ");
                        
	// Bolt Home Page Details
	System.out.println(" **** Bolt Home Page Details ****");
	topologiesIterator = summary.get_topologies_iterator();
	while(topologiesIterator.hasNext()) {
	    TopologySummary topology = topologiesIterator.next();
	    TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
	    Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
	    while(executorStatusItr.hasNext()) {
			     // get the executor
			     ExecutorSummary executor_summary =  executorStatusItr.next();
			     ExecutorStats execStats = executor_summary.get_stats();
			     ExecutorSpecificStats execSpecStats = execStats.get_specific();
			     String componentId = executor_summary.get_component_id();
			     // if the executor is a bolt
			     if(execSpecStats.is_set_bolt()) {
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
	private static void spoutSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary, String componentId) {
		  ExecutorStats execStats = executorSummary.get_stats();
		  ExecutorSpecificStats execSpecStats = execStats.get_specific();
		  SpoutStats spoutStats = execSpecStats.get_spout();
		  System.out.println("**** Component summary ****");
		  System.out.println("Id : " + componentId);
		  System.out.println("Topology Name  : " + topology.get_name());
		  System.out.println("Executors : " + "1");
		  System.out.println("Tasks : " + "1");
		  String component_log = "Component_Summary:: Component_id="+ componentId + "^Topology_name=" + topology.get_name()
	          		+ "^Executors=1" 
	          		+ "^Tasks=1";
	      logger.info(component_log);
	          
		  System.out.println("**** Spout stats ****");
		  System.out.println("**** Window Size ****  " + "600");
		  String winSize = "600";
		  System.out.println("Transferred: " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "600"));
		  System.out.println("Emitted: " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "600"));
		  System.out.println("Acked: " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), "600"));
		  System.out.println("Failed: " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), "600"));
		  
		  String spout_stat_10min_log = "Window_Size_10min:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), winSize);
	      logger.info(spout_stat_10min_log);
	          
	       
		  System.out.println("**** Window Size ****  " + "10800");
		  winSize = "10800";
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "10800"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "10800"));
		  System.out.println("Acked : " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), "10800"));
		  System.out.println("Failed : " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), "10800"));
		  
		  String spout_stat_3h_log = "Window_Size_3h:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), winSize);
	      logger.info(spout_stat_3h_log);
	      
		  System.out.println("**** Window Size ****  " + "86400");
		  winSize = "86400";
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "86400"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "86400"));
		  System.out.println("Acked : " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), "86400"));
		  System.out.println("Failed : " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), "86400"));
		  String spout_stat_1d_log = "Window_Size_1day:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), winSize);
	      logger.info(spout_stat_1d_log);
	      
		  System.out.println("**** Window Size ****  " + "all-time");
		  winSize = ":all-time";
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), ":all-time"));		
		  String spout_stat_all_time_log = "Window_Size_all_time:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), winSize);
	      logger.info(spout_stat_all_time_log);
		  
		  System.out.println("**** Output stats (All time) ****");
		  System.out.println("Stream : " + "default");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), ":all-time"));
		
		  System.out.println("**** Executors (All time) ****");
		  winSize = ":all-time";
		  System.out.println("Host : " + executorSummary.get_host());
		  System.out.println("Port : " + executorSummary.get_port());
		  System.out.println("Up-Time : " + executorSummary.get_uptime_secs());
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), ":all-time"));
		  String executor_stats_all_time_log = "Executor_stats_all_time:: "+"Host=" + executorSummary.get_host()+"^Port=" + executorSummary.get_port()
				    + "^Up_Time=" + executorSummary.get_uptime_secs()
				  	+ "^Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.getStatValueFromMap(spoutStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.getStatValueFromMap(spoutStats.get_failed(), winSize);
	      logger.info(executor_stats_all_time_log);
		  
		
		  System.out.println("**** Errors ****");
		  Map<String, List<ErrorInfo>> errors = topologyInfo.get_errors();
		  List<ErrorInfo> spoutErrors = errors.get(componentId);
		  for(ErrorInfo errorInfo : spoutErrors) {
			  System.out.println("Spout Error : " + errorInfo.get_error());
			  logger.debug("Spout Error::" + errorInfo.get_error());
		  }  
	}


  /*
   * Calculate bolt specific stats
   */
  private static void boltSpecificStats(TopologyInfo topologyInfo, TopologySummary topology, ExecutorSummary executorSummary, String componentId) {
		  ExecutorStats execStats = executorSummary.get_stats();
		  ExecutorSpecificStats execSpecStats = execStats.get_specific();
		  BoltStats boltStats = execSpecStats.get_bolt();
		  System.out.println(":::::::::: Component summary ::::::::::");
		  System.out.println("Id : " + componentId);
		  System.out.println("Topology Name  : " + topology.get_name());
		  System.out.println("Executors : " + "1");
		  System.out.println("Tasks : " + "1");
		  String component_log = "Component_Summary:: Component_id="+ componentId + "^Topology_name=" + topology.get_name()
	          		+ "^Executors=1" 
	          		+ "^Tasks=1";
	      logger.info(component_log);
	      
		  System.out.println(":::::::::: Bolt stats ::::::::::");
		  System.out.println("::::::::::: Window Size :::::::::::  " + "600");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "600"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "600"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), "600"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), "600"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), "600"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "600"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "600"));
		  String winSize = "600";
		  String spout_stat_10min_log = "Window_Size_10min:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), winSize)
	          		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), winSize)
	          		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), winSize)
	          		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), winSize);
	      logger.info(spout_stat_10min_log);
	      
		  System.out.println("::::::::::: Window Size :::::::::::  " + "10800");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "10800"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "10800"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), "10800"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), "10800"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), "10800"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "10800"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "10800"));
		  winSize = "10800";
		  String spout_stat_3h_log = "Window_Size_3h:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), winSize)
	          		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), winSize)
	          		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), winSize)
	          		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), winSize);
	      logger.info(spout_stat_3h_log);
	      
		  System.out.println("::::::::::: Window Size :::::::::::  " + "86400");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), "86400"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), "86400"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), "86400"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), "86400"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), "86400"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "86400"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "86400"));
		  winSize = "86400";
		  String spout_stat_1d_log = "Window_Size_1day:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), winSize)
	          		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), winSize)
	          		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), winSize)
	          		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), winSize);
	      logger.info(spout_stat_1d_log);
	      
		  System.out.println("::::::::::: Window Size :::::::::::  " + "all-time");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time")); 
		  winSize = ":all-time";
		  String spout_stat_all_time_log = "Window_Size_all_time:: "+"Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), winSize)
	          		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), winSize)
	          		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), winSize)
	          		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), winSize);
	      logger.info(spout_stat_all_time_log);
		
		  System.out.println(":::::::::: Output stats (All time) ::::::::::");
		  System.out.println("Stream : " + "default");
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));
		
		  System.out.println(":::::::::: Executors (All time) ::::::::::");
		  System.out.println("Host : " + executorSummary.get_host());
		  System.out.println("Port : " + executorSummary.get_port());
		  System.out.println("Up-Time : " + executorSummary.get_uptime_secs());
		  System.out.println("Transferred : " + CommonUtils.getStatValueFromMap(execStats.get_transferred(), ":all-time"));
		  System.out.println("Emitted : " + CommonUtils.getStatValueFromMap(execStats.get_emitted(), ":all-time"));
		  System.out.println("Acked : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), ":all-time"));
		  System.out.println("Failed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), ":all-time"));
		  System.out.println("Executed : " + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), ":all-time"));
		  System.out.println("Execute Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), ":all-time"));
		  System.out.println("Process Latency (ms) : " + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), ":all-time"));
		  String executor_stat_all_time_log = "executor_stats_all_time:: " + "Host=" + executorSummary.get_host()+ "^Port=" + executorSummary.get_port()
		  			+ "^Up_Time=" + executorSummary.get_uptime_secs()
				    + "^Transferred=" + CommonUtils.getStatValueFromMap(execStats.get_transferred(), winSize)
	          		+ "^Emitted=" + CommonUtils.getStatValueFromMap(execStats.get_emitted(), winSize)
	          		+ "^Acked=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), winSize) 
	          		+ "^Failed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), winSize)
	          		+ "^Executed=" + CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), winSize)
	          		+ "^Execute_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), winSize)
	          		+ "^Process_Latency=" + CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), winSize);
	      logger.info(executor_stat_all_time_log);
		
		  System.out.println(":::::::::: Errors ::::::::::");
		  Map<String, List<ErrorInfo>> errors = topologyInfo.get_errors();
		  System.out.println(errors.keySet());
		  List<ErrorInfo> boltErrors = errors.get(componentId);
		  for(ErrorInfo errorInfo : boltErrors) {
			  System.out.println("Bolt Error : " + errorInfo.get_error()); 
			  logger.debug("Bolt Error:: " + errorInfo.get_error());
		  }  
  }
}

