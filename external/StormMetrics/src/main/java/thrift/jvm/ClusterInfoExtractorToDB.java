package thrift.jvm;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransportException;

import backtype.storm.generated.Bolt;
import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ErrorInfo;
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.Grouping;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.SpoutSpec;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.StreamInfo;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.Nimbus.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thrift.jvm.dao.ClusterDAO;
import thrift.jvm.dao.ComponentDAO;
import thrift.jvm.dao.ExecutorDAO;
import thrift.jvm.dao.RunStateDAO;
import thrift.jvm.dao.StormStatsDAO;
import thrift.jvm.dao.SupervisorDAO;
import thrift.jvm.dao.TopologyDAO;
import thrift.jvm.util.CommonUtils;

public class ClusterInfoExtractorToDB {
	
  private static final Logger logger = LoggerFactory.getLogger(ClusterInfoExtractorToDB.class);
  final static int NIMBUS_THRIFT_PORT = 6627;
  final static String NIMBUS_THRIFT_HOST = "nimbus";
  final static int INTERVAL_MIN = 10;
  
  public static void main(String[] args) {
	TSocket socket = new TSocket(NIMBUS_THRIFT_HOST, NIMBUS_THRIFT_PORT);
	logger.info("**** Begin the transport connections via thrift socket port ****");
	TFramedTransport transport = new TFramedTransport(socket);
	TBinaryProtocol protocol = new TBinaryProtocol(transport);
	Client client = new Client(protocol);

	try {
		transport.open();
		ClusterSummary summary = client.getClusterInfo();

		/* Cluster Details */
		logger.info("**** Storm UI Home Page Start ****");
		logger.info("**** Cluster Summary **** ");
		int nimbusUpTime = summary.get_nimbus_uptime_secs();
		int supervisorSize = summary.get_supervisors_size();
		int topologySize = summary.get_topologies_size();
		
		logger.info("Begin the runState mysql connections");
		/* get the most recent runId from runState table in mySql */
		RunStateDAO runStateDAO = new RunStateDAO();
		RunState lastRunState = runStateDAO.lastRunState();
		Timestamp nextRunTime = new Timestamp(System.currentTimeMillis() + INTERVAL_MIN*60*1000);
		/* set the next run planDateTime from the begging of this run */
		RunState insertRunState = new RunState(0, "PLANNED", nextRunTime, null, null);
		int activeRunId = lastRunState.getRunId();

		logger.info("update the activeRunState to RUNNING");
		RunState updateRunState = new RunState(lastRunState.getRunId(), "RUNNING", lastRunState.getPlandatetime(), CommonUtils.getCurrentTimeStamp(), null);
		runStateDAO.update(updateRunState);

		/* to update the cluster table later */
		int clusterTotalNumUsedWorkers = 0;
		int clusterTotalNumWorkers = 0;
		int clusterTotalExecutors = 0;
		int clusterTotalTasks = 0;		

		/* Topology Summary */
		logger.info("**** Topology Summary ****");
	    logger.info("Begin the topology mysql connections");
	    /* prepare to insert to topology table in mySql */
	    Topology dTopology = new Topology();
	    dTopology.setRunId(activeRunId);
	    TopologyDAO topologyDAO = new TopologyDAO();
	    
	    List<TopologySummary> topologies = summary.get_topologies();
	    Iterator<TopologySummary> topologiesIterator = summary.get_topologies_iterator();
	    int[] executors = updateTopologyTable(topologiesIterator,topologyDAO,dTopology);
	    clusterTotalExecutors = executors[0];
	    clusterTotalTasks = executors[1];
	    
	    logger.info("Close the topology mysql connections");
		topologyDAO.closeConnection();
		
		/* Supervisor Stats */
		logger.info("**** Supervisor Summary ****");
		logger.info("Begin the supervisor mysql connections");
		/* prepare to insert to supervisor table in mySql */
		Supervisor dSupervisor = new Supervisor();
		dSupervisor.setRunId(activeRunId);
		SupervisorDAO supervisorDAO = new SupervisorDAO();
    
		List<SupervisorSummary> supervisors = summary.get_supervisors();
		Iterator<SupervisorSummary> supervisorsIterator = summary.get_supervisors_iterator();
		int[] workers = updateSupervisorTable(supervisorsIterator,supervisorDAO, dSupervisor);
		clusterTotalNumUsedWorkers = workers[0];
		clusterTotalNumWorkers = workers[1];
		logger.info("Close the supervisor mysql connections");
		supervisorDAO.closeConnection();

		/* insert to cluster table in mySql */
		logger.info("Begin the cluster mysql connections");
		Cluster dCluster = new Cluster(activeRunId, 1, "0.9.2-incubating", nimbusUpTime, supervisorSize,
				clusterTotalNumUsedWorkers,(clusterTotalNumWorkers-clusterTotalNumUsedWorkers),
				clusterTotalNumWorkers,clusterTotalExecutors,clusterTotalTasks,CommonUtils.getCurrentTimeStamp());
		ClusterDAO clusterDAO = new ClusterDAO();
		clusterDAO.insert(dCluster);
		/* test the select() function in ClusterDAO */
		//List<Cluster> lClusters = clusterDAO.select();
    
		String clusterDetails = "Cluster_Summary:: Nimbus_uptime="+ nimbusUpTime + "^NumOfSupervisors=" + summary.get_supervisors_size() 
    		+ "^NumOfTopologies=" + summary.get_topologies_size() ;
		logger.info(clusterDetails);
		
		/* Nimbus config parameter-values */
		logger.trace("**** Nimbus Configuration ****");
		String nimbusConfigString = client.getNimbusConf();
		outputNimbusConf(nimbusConfigString);

	    logger.info("**** End of Storm UI Home Page Details****");
	
	    /* Topology Stats */
	    logger.info("**** Topology Home Page Details ****");
    
	    logger.info("Begin the stormstats mysql connections");
		/* prepare to insert to stats table in mysql */
		StormStats dStormStats = new StormStats();
		dStormStats.setRunId(activeRunId);
		StormStatsDAO stormStatsDAO = new StormStatsDAO();
		
		logger.info("Begin the component mysql connections");
		/* prepare to insert into mysql component table */
	    Component dComponent = new Component();
	    dComponent.setRunId(activeRunId);
	    ComponentDAO componentDAO = new ComponentDAO();
	    
	    logger.info("Begin the executor mysql connections");
	    /* prepare to insert into mysql component table */
	    Executor dExecutor = new Executor();
	    dExecutor.setRunId(activeRunId);
	    ExecutorDAO executorDAO = new ExecutorDAO();
	    
		topologiesIterator = summary.get_topologies_iterator();
	    while(topologiesIterator.hasNext()) {
	        TopologySummary topology = topologiesIterator.next();
                            
		    /* Spouts (All time) */
	        logger.info("**** TopoId = "+ topology.get_id() +"****");
	        TopologyInfo topology_info = client.getTopologyInfo(topology.get_id());
		    
		    StormTopology topo = client.getTopology(topology.get_id());
	    
		    /* prepare to insert into mysql component table */
		    dComponent.setTopo_name(topology.get_name());
		    logger.info("**** Begin inserting the component object ****");
		    updateComponentTable(topo, componentDAO,dComponent);
		    logger.info("**** End inserting the component object ****");
		    
		    /* prepare to insert into mysql executor table */
		    dExecutor.setTopo_name(topology.get_name());
	    
		    /* prepare to insert into mysql stormstats table */
		    dStormStats.setTopo_name(topology.get_name());
		    
		    Iterator<ExecutorSummary> executorStatusItr = topology_info.get_executors_iterator();
		    logger.info("**** Begin inserting the executor object ****");
		    updateExecutorTable(executorStatusItr, executorDAO, dExecutor);
		    logger.info("**** End inserting the executor object ****");
		    
		    logger.info("**** Begin getting the num_executors & dist_list per component object ****");
		    List<Component> components = executorDAO.findNumExecutorsAndListPerComponent(activeRunId);
		    logger.info("**** End getting the num_executors & dist_list per component object ****");
		    
		    logger.info("**** Begin updating the component object ****");
		    for(int i=0; i<components.size();i++){
		    	Component component = components.get(i);
		    	componentDAO.updateNumTasksAndList(component);
		    }
		    logger.info("**** End updating the component object ****");
		    
		    logger.info("**** Begin inserting the stormstats object executor level ****");
		    executorStatusItr = topology_info.get_executors_iterator();
		    updateStormStatsTable(executorStatusItr,stormStatsDAO,dStormStats);
		    /*
		    executorStatusItr = topology_info.get_executors_iterator();
		    updateStormStatsTable(executorStatusItr,stormStatsDAO,dStormStats,"10800",capacity);
		    executorStatusItr = topology_info.get_executors_iterator();
		    updateStormStatsTable(executorStatusItr,stormStatsDAO,dStormStats,"86400",capacity);
		    executorStatusItr = topology_info.get_executors_iterator();
		    updateStormStatsTable(executorStatusItr,stormStatsDAO,dStormStats,":all-time",capacity);
		    */
		    logger.info("**** End inserting the stormstats object executor level ****");
                               
			/* Topology Configuration */
			logger.trace("**** Topology Configuration ****");
			String topologyConfigString = client.getTopologyConf(topology.get_id());
			outputTopoConf(topologyConfigString);
	    }
	    
	    logger.info("**** Begin inserting the stormstats object component level ****");
	    List<StormStats> lStormStats = stormStatsDAO.aggregateByComponentId(activeRunId);
	    for(int i=0; i<lStormStats.size();i++){
	    	StormStats stormStats = lStormStats.get(i);
	    	stormStatsDAO.insert(stormStats);
	    }
	    logger.info("**** End inserting the stormstats object component level ****");
	    
	    logger.info("**** Begin inserting the stormstats object topology level ****");
	    lStormStats = stormStatsDAO.aggregateByTopo(activeRunId);
	    for(int i=0; i<lStormStats.size();i++){
	    	StormStats stormStats = lStormStats.get(i);
	    	stormStatsDAO.insert(stormStats);
	    }
	    logger.info("**** End inserting the stormstats object topology level ****");
	    
	    logger.info("Close the component, executor and stormstats mysql connections");
	    componentDAO.closeConnection();
	    executorDAO.closeConnection();
	    stormStatsDAO.closeConnection();
	    logger.info("****  End of Topology Home Page Details ****");
    
	    logger.info("Close the cluster mysql connections");
	    /* need to get used_slots, slots are updated */
		clusterDAO.closeConnection();
		
	    /* end of this run */
	    updateRunState.setState("FINISHED");
	    updateRunState.setEnddatetime(CommonUtils.getCurrentTimeStamp());
	    runStateDAO.update(updateRunState);
	    logger.info("Insert the next runState");
	    runStateDAO.insert(insertRunState);
	    logger.info("Close the runState mysql connections");
	    runStateDAO.closeConnection();
                        
	    //  Spout Home Page Details
		logger.debug(" **** Spout/Bolts Error logs ****");
		topologiesIterator = summary.get_topologies_iterator();
		outputComponentErrors(topologiesIterator, client);
		logger.info("Close the transport connections");
	    transport.close();
	} catch (TTransportException e) {
	   	e.printStackTrace();
	   	logger.debug(e.toString());
   	} catch (TException e) {
   		e.printStackTrace();
   		logger.debug(e.toString());
   	} catch (NotAliveException e) {
   		e.printStackTrace();
   		logger.debug(e.toString());
   	}
  }
  
  private static int[] updateTopologyTable(Iterator<TopologySummary> topologiesIterator, 
		  TopologyDAO topologyDAO, Topology dTopology){
	  int[] executors = new int[2];
	  while(topologiesIterator.hasNext()) {
	        TopologySummary topology = topologiesIterator.next();
	        String topoId = topology.get_id();
	        String topoName = topology.get_name();
	        int topoNumExecutors = topology.get_num_executors();
	        int topoNumTasks = topology.get_num_tasks();
	        int topoNumWorkers = topology.get_num_workers();
	        String topoStatus = topology.get_status();
	        int topoUptime = topology.get_uptime_secs();
	        
	        executors[0] += topoNumExecutors;
	        executors[1] += topoNumTasks;
	        
	        String topoSummary = "Topology_Summary:: Topology_id="+ topoId + "^Topology_name=" + topoName 
	        		+ "^NumOfExecutors=" + topoNumExecutors + "^NumOfTasks=" + topoNumTasks 
	        		+ "^NumOfWorkers=" + topoNumWorkers
	        		+ "^Status=" + topoStatus + "^UpTime_in_sec=" + topoUptime;
	        logger.info(topoSummary);
	        /* insert to topology table */
	        dTopology.setName(topoName);
	        dTopology.setTopo_id(topoId);
	        dTopology.setStatus(topoStatus);
	        dTopology.setTopo_uptime(topoUptime);
	        dTopology.setNum_workers(topoNumWorkers);
	        dTopology.setNum_executors(topoNumExecutors);
	        dTopology.setNum_tasks(topoNumTasks);
	        
	        topologyDAO.insert(dTopology);
	  }
	  return executors;
  }
 
  private static int[] updateSupervisorTable(Iterator<SupervisorSummary> supervisorsIterator, 
		  SupervisorDAO supervisorDAO, Supervisor dSupervisor){
	  int[] workers = new int[2];
		
	  while(supervisorsIterator.hasNext()) {
	        SupervisorSummary supervisor = supervisorsIterator.next();
	        String supervisorId = supervisor.get_supervisor_id();
	        String supervisorHost = supervisor.get_host();
	        int supervisorNumUsedWorkders = supervisor.get_num_used_workers();
	        int supervisorNumWorkders = supervisor.get_num_workers();
	        int supervisorUptime = supervisor.get_uptime_secs();
	        String topoSummary = "Supervisor_Summary:: Supervisor_id="+ supervisorId + "^Host=" + supervisorHost 
	        		+ "^NumOfUsedWorkers=" + supervisorNumUsedWorkders
	        		+ "^NumOfWorkers=" + supervisorNumWorkders
	        		+ "^Supervisor_uptime=" + supervisorUptime;
	        logger.info(topoSummary);
	        
	        workers[0] += supervisorNumUsedWorkders;
	        workers[1] += supervisorNumWorkders;
	        
	        /* insert into supervisor table */
	        dSupervisor.setSup_id(supervisor.get_supervisor_id());
	        dSupervisor.setHost(supervisor.get_host());
	        dSupervisor.setSupervisor_uptime(supervisor.get_uptime_secs());
	        dSupervisor.setSlots(supervisor.get_num_workers());
	        dSupervisor.setUsed_slots(supervisor.get_num_used_workers());
	       
	        supervisorDAO.insert(dSupervisor);
	  }
	  return workers;
  }

  private static void updateComponentTable(StormTopology topo, ComponentDAO componentDAO, Component dComponent){
	  
	    Map<String, Bolt> oBolts = topo.get_bolts();
	    Set<Entry<String, Bolt>> entrySet = oBolts.entrySet();
	    Iterator<Entry<String, Bolt>> itrEntrySet = entrySet.iterator();
	    Set<String> boltKeys = oBolts.keySet();
	    
	    /* inintialize the values before the loop */
	    int paraHint = 0;
	    String jsonConf = "";
	    Map<GlobalStreamId, Grouping> boltIp = null;
	    Map<String, StreamInfo> boltStreams = null;
	    while(itrEntrySet.hasNext()) {
	    	Entry<String, Bolt> boltPair = itrEntrySet.next();
	    	String boltKey = boltPair.getKey();
	    	Bolt bolt = boltPair.getValue();
	    	paraHint = bolt.get_common().get_parallelism_hint();
	    	jsonConf = bolt.get_common().get_json_conf();
	    	boltIp = bolt.get_common().get_inputs();
	    	String inputComponentId = "";
	    	String boltInputGrouping = "";
	    	Iterator<Entry<GlobalStreamId, Grouping>> itrBoltInput = boltIp.entrySet().iterator();
	    	while(itrBoltInput.hasNext()){
	    		Entry<GlobalStreamId, Grouping> boltInputPair = itrBoltInput.next();
	    		GlobalStreamId boltInputPairKey = boltInputPair.getKey();
	    		inputComponentId = boltInputPairKey.get_componentId();
	    		String inputStreamtId = boltInputPairKey.get_streamId();
	    		Grouping boltInputPairValue = boltInputPair.getValue();
	    		boltInputGrouping = boltInputPairValue.toString();
	    		//boltInputPairValue.get_all();
	    		//List<String> boltInputFields = boltInputPairValue.get_fields();
	    		//boltInputFields.
	    	}
	    	boltStreams = bolt.get_common().get_streams();
	    	Set<Entry<String, StreamInfo>> boltStreamsSet = boltStreams.entrySet();
	    	String sBoltStreamFields = "";
	    	Iterator<Entry<String, StreamInfo>> itrBoltStream = boltStreamsSet.iterator();
	    	while(itrBoltStream.hasNext()){
	    		Entry<String, StreamInfo> boltStreamPair = itrBoltStream.next();
	    		String boltStreamKey = boltStreamPair.getKey();
	    		StreamInfo boltStreamValue = boltStreamPair.getValue();
	    		List<String> boltStreamFields = boltStreamValue.get_output_fields();
	    		sBoltStreamFields = boltStreamFields.toString();
	    		if (boltStreamKey.equals("default")){
	    			break;
	    		}
	    	}
	    	
	    	dComponent.setComponent_id(boltKey);
	    	dComponent.setCategory("Bolt");
	    	dComponent.setFrom_component(inputComponentId);
	    	dComponent.setTo_component("unknown");
	    	dComponent.setIp_fields("unknown");
	    	dComponent.setOp_fields(sBoltStreamFields);
	    	dComponent.setIp_grouping(boltInputGrouping);
	    	dComponent.setOp_grouping("unknown");
	    	dComponent.setNum_tasks(paraHint);
	    	dComponent.setDatetime(CommonUtils.getCurrentTimeStamp());
	    		    	
	    	componentDAO.insert(dComponent);
	    }
    
	    Map<String, SpoutSpec> oSpouts = topo.get_spouts();
	    Set<Entry<String, SpoutSpec>> spoutEntrySet = oSpouts.entrySet();
	    Iterator<Entry<String, SpoutSpec>> itrSpoutEntrySet = spoutEntrySet.iterator();
	    Set<String> spoutKeys = oSpouts.keySet();

	    Map<GlobalStreamId, Grouping> spoutIp = null;
	    Map<String, StreamInfo> spoutStreams = null;
	    while(itrSpoutEntrySet.hasNext()) {
	    	Entry<String, SpoutSpec> spoutPair = itrSpoutEntrySet.next();
	    	String spoutKey = spoutPair.getKey();
	    	SpoutSpec spout = spoutPair.getValue();
	    	paraHint = spout.get_common().get_parallelism_hint();
	    	jsonConf = spout.get_common().get_json_conf();
	    	spoutIp = spout.get_common().get_inputs();
	    	String inputComponentId = "";
	    	String spoutInputGrouping = "";
	    	Iterator<Entry<GlobalStreamId, Grouping>> itrSpoutInput = spoutIp.entrySet().iterator();
	    	while(itrSpoutInput.hasNext()){
	    		Entry<GlobalStreamId, Grouping> spoutInputPair = itrSpoutInput.next();
	    		GlobalStreamId spoutInputPairKey = spoutInputPair.getKey();
	    		inputComponentId = spoutInputPairKey.get_componentId();
	    		String inputStreamtId = spoutInputPairKey.get_streamId();
	    		Grouping spoutInputPairValue = spoutInputPair.getValue();
	    		spoutInputGrouping = spoutInputPairValue.toString();
	    		//boltInputPairValue.get_all();
	    		//List<String> boltInputFields = boltInputPairValue.get_fields();
	    		//boltInputFields.
	    	}
	    	spoutStreams = spout.get_common().get_streams();
	    	Set<Entry<String, StreamInfo>> spoutStreamsSet = spoutStreams.entrySet();
	    	String sSpoutStreamFields = "";
	    	Iterator<Entry<String, StreamInfo>> itrSpoutStream = spoutStreamsSet.iterator();
	    	while(itrSpoutStream.hasNext()){
	    		Entry<String, StreamInfo> spoutStreamPair = itrSpoutStream.next();
	    		String spoutStreamKey = spoutStreamPair.getKey();
	    		StreamInfo spoutStreamValue = spoutStreamPair.getValue();
	    		List<String> spoutStreamFields = spoutStreamValue.get_output_fields();
	    		sSpoutStreamFields = spoutStreamFields.toString();
	    		if (spoutStreamKey.equals("default")){
	    			break;
	    		}
    	}
    	
    	dComponent.setComponent_id(spoutKey);
    	dComponent.setCategory("Spout");
    	dComponent.setFrom_component(inputComponentId);
    	dComponent.setTo_component("unknown");
    	dComponent.setIp_fields("unknown");
    	dComponent.setOp_fields(sSpoutStreamFields);
    	dComponent.setIp_grouping(spoutInputGrouping);
    	dComponent.setOp_grouping("unknown");
    	dComponent.setNum_tasks(paraHint);
    	dComponent.setDatetime(CommonUtils.getCurrentTimeStamp());
    		    	
    	componentDAO.insert(dComponent);
    }
  }
  
  private static void updateStormStatsTable(Iterator<ExecutorSummary> executorStatusItr, StormStatsDAO stormStatsDAO, 
		  StormStats dStormStats){
	double capa = 0.0;
	while(executorStatusItr.hasNext()){
	  	  /* get the executor */
	      ExecutorSummary executor_summary =  executorStatusItr.next();
	      ExecutorStats execStats = executor_summary.get_stats();
	      ExecutorSpecificStats execSpecStats = execStats.get_specific();
	      String componentId = executor_summary.get_component_id();
	      
	      /* Stats Object (Executor Level) */
	      dStormStats.setComponent_id(componentId);
	      dStormStats.setExecutor_id(executor_summary.get_executor_info().get_task_start()
	      		+"-"+executor_summary.get_executor_info().get_task_end());
	      dStormStats.setLevel("Executor");
	      List<String> windows = Arrays.asList("600", "10800", "86400",":all-time");
	      
	      /* for (Iterator<String> window = windows.iterator(); window.hasNext();) { */
	      for (String window : windows) {
	    	  dStormStats.setWindow(window);
		      /* if the executor is a spout */
		      if(execSpecStats.is_set_spout()) {
		          SpoutStats spoutStats = execSpecStats.get_spout();
		          long emitted = 0L;
		          if (CommonUtils.getStatValueFromMap(execStats.get_emitted(), window) != null){
		        	  emitted = CommonUtils.getStatValueFromMap(execStats.get_emitted(), window);
		          }
		          long transferred = 0L;
		          if (CommonUtils.getStatValueFromMap(execStats.get_transferred(), window) != null){
		        	  transferred = CommonUtils.getStatValueFromMap(execStats.get_transferred(), window);
		          }
		          long acked = 0L;
		          if (CommonUtils.getStatValueFromMap(spoutStats.get_acked(), window) != null){
		        	  acked = CommonUtils.getStatValueFromMap(spoutStats.get_acked(), window);
		          }
		          long failed = 0L;
		          if (CommonUtils.getStatValueFromMap(spoutStats.get_failed(), window) != null){
		        	  failed = CommonUtils.getStatValueFromMap(spoutStats.get_failed(), window);
		          }
				  double complete_latency = 0.0;
				  if(CommonUtils.getStatDoubleValueFromMap(spoutStats.get_complete_ms_avg(),window) != null){
					complete_latency = CommonUtils.getStatDoubleValueFromMap(spoutStats.get_complete_ms_avg(),window);
				  }
		          String spoutStats_log = "Spout_Stats:: Spout_id="+ componentId + "^Transferred=" + transferred
		          		+ "^Emitted=" + emitted + "^Acked=" + acked 
		          		+ "^Failed=" + failed + "^Window=" + window;
		          logger.info(spoutStats_log);
		          
		          //Stats object (Executor Level)
		          dStormStats.setEmitted(emitted);
		          dStormStats.setTransferred(transferred);
		          dStormStats.setAcked(acked);
		          dStormStats.setFailed(failed);
		          dStormStats.setComplete_latency(complete_latency);
		          dStormStats.setProcess_latency(0.0);
		          dStormStats.setExecute_latency(0.0);
		          dStormStats.setExecuted(0L);
		          dStormStats.setCapacity(0.0);
		          
		          stormStatsDAO.insert(dStormStats);
		      }
		      
		      /* if the executor is a bolt */
		      if(execSpecStats.is_set_bolt()) {
		          BoltStats boltStats = execSpecStats.get_bolt();
		          
		          long emitted = 0L;
		          if(CommonUtils.getStatValueFromMap(execStats.get_emitted(), window) != null){
		          	emitted = CommonUtils.getStatValueFromMap(execStats.get_emitted(), window);
		          }
		          long transferred = 0L;
		          if(CommonUtils.getStatValueFromMap(execStats.get_transferred(), window) != null){
		          	transferred = CommonUtils.getStatValueFromMap(execStats.get_transferred(), window);
		          }
		          long acked = 0L;
		          if (CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), window) != null){
		          	acked = CommonUtils.get_boltStatLongValueFromMap(boltStats.get_acked(), window);
		          }
		          long failed = 0L;
		          if (CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), window) != null){
		          	failed = CommonUtils.get_boltStatLongValueFromMap(boltStats.get_failed(), window);
		          }
		          long executed = 0L;
		          if (CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), window) != null){
		          	executed = CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), window);
		          }
		          
		          double execute_latency = 0.0;
				  if(CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), window) != null){
					execute_latency = CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), window);
				  }
					
				  double process_latency = 0.0;
				  if(CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), window) != null){
					process_latency = CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), window);
				  }
				  
				  if (window.equals("600")){
					  capa = 100*executed*execute_latency/(1000*600);
				  } 
		          String boltStats_log = "Bolt_Stats:: Bolt_id="+ componentId + "^Transferred=" + transferred
		          		+ "^Emitted=" + emitted + "^Acked=" + acked 
		          		+ "^Failed=" + failed 
		          		+ "^Executed=" + executed
		          		+ "^Execute_Latency=" + execute_latency
		          		+ "^Process_Latency=" + process_latency + "^Window=" + window;
		          logger.info(boltStats_log);
		          
		          /* Stats Object (Executor Level) */
		          dStormStats.setEmitted(emitted);
		          dStormStats.setTransferred(transferred);
		          dStormStats.setAcked(acked);
		          dStormStats.setFailed(failed);
		          dStormStats.setExecuted(executed);
		          dStormStats.setExecute_latency(execute_latency);
		          dStormStats.setProcess_latency(process_latency);
		          dStormStats.setComplete_latency(0.0);
		          dStormStats.setCapacity(capa);
		          
		          stormStatsDAO.insert(dStormStats);
		      }
	      }
      }
  }
  
  private static void updateExecutorTable(Iterator<ExecutorSummary> executorStatusItr, ExecutorDAO executorDAO, 
		  Executor dExecutor){
	  while(executorStatusItr.hasNext()) {
		    /* get the executor */
		    ExecutorSummary executor_summary =  executorStatusItr.next();
		    ExecutorStats execStats = executor_summary.get_stats();
		    ExecutorSpecificStats execSpecStats = execStats.get_specific();
		    String componentId = executor_summary.get_component_id();

	        /* put to executor table in mysql */
	        dExecutor.setComponent_id(componentId);
	        dExecutor.setExecutor_id(executor_summary.get_executor_info().get_task_start()
	        		+"-"+executor_summary.get_executor_info().get_task_end());
	        String cat = "";
	        if (execSpecStats.is_set_spout()){
	        	cat = "Spout";
	        } else if (execSpecStats.is_set_bolt()){
	        	cat = "Bolt";
	        } else {
	        	cat = "unknow";
	        }
	        dExecutor.setCategory(cat);
	        dExecutor.setHost(executor_summary.get_host());
	        dExecutor.setPort(Integer.toString(executor_summary.get_port()));
	        dExecutor.setExecutor_uptime(executor_summary.get_uptime_secs());
	       
	        executorDAO.insert(dExecutor);
	        
	        String executor_log = "Bolt_Stats:: Bolt_id="+ componentId + "^Category=" + cat 
	        		+ "^Executor_id=" + executor_summary.get_executor_info().get_task_start() +"-"+executor_summary.get_executor_info().get_task_end()
	          		+ "^Host=" + executor_summary.get_host() + "^Port=" + executor_summary.get_port() 
	          		+ "^Uptime=" + executor_summary.get_uptime_secs();
	        logger.info(executor_log);
      
	   }
  }
  
  private static void outputNimbusConf(String nimbusConfigString){
	    Map<String, String> nimbusConfigurationParamValues = new HashMap<String, String>();
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
	        logger.trace("Parameter : " + key + " Value : " + nimbusConfigurationParamValues.get(key));
	    }
  }
  
  private static void outputTopoConf(String topologyConfigString){
	    
	    Map<String, String> topologyConfigurationParamValues = new HashMap<String, String>();
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
				logger.trace("Parameter: " + key + " Value : " + topologyConfigurationParamValues.get(key));
		}
  }
  private static void outputComponentErrors(Iterator<TopologySummary> topologiesIterator, 
		  Client client) throws NotAliveException, TException{
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
			    	  logger.debug("**** Errors ****");
					  Map<String, List<ErrorInfo>> errors = topology_info.get_errors();
					  List<ErrorInfo> spoutErrors = errors.get(componentId);
					  for(ErrorInfo errorInfo : spoutErrors) {
						  logger.debug("Spout Error::" + errorInfo.get_error());
					  } 
			     }
			     if(execSpecStats.is_set_bolt()) {
			    	  Map<String, List<ErrorInfo>> errors = topology_info.get_errors();
			    	  logger.debug(errors.keySet().toString());
					  List<ErrorInfo> boltErrors = errors.get(componentId);
					  for(ErrorInfo errorInfo : boltErrors) {
						  logger.debug("Bolt Error:: " + errorInfo.get_error());
					  }
			     }
		  }
	  }
  }
}

