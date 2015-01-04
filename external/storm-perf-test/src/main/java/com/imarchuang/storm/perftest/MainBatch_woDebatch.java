/*
 * Copyright (c) 2013 Yahoo! Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */

package com.imarchuang.storm.perftest;

import java.util.Map;

import static java.lang.Math.pow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import CommonUtils.CommonUtils;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import backtype.storm.utils.NimbusClient;
import backtype.storm.generated.BoltStats;
import backtype.storm.generated.ExecutorSpecificStats;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.KillOptions;
import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.SpoutStats;
import backtype.storm.generated.SupervisorSummary;
import backtype.storm.generated.TopologySummary;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.ExecutorStats;

public class MainBatch_woDebatch {
  private static final Log LOG = LogFactory.getLog(MainBatch_woDebatch.class);

  @Option(name="--help", aliases={"-h"}, usage="print help message")
  private boolean _help = false;
  
  @Option(name="--debug", aliases={"-d"}, usage="enable debug")
  private boolean _debug = false;
  
  @Option(name="--local", usage="run in local mode")
  private boolean _local = false;
  
  @Option(name="--messageSizeByte", aliases={"--messageSize"}, metaVar="SIZE",
      usage="size of the messages generated in bytes")
  private int _messageSize = 100;

  @Option(name="--numTopologies", aliases={"-n"}, metaVar="TOPOLOGIES",
      usage="number of topologies to run in parallel")
  private int _numTopologies = 1;
 
   @Option(name="--numLevels", aliases={"-l"}, metaVar="LEVELS",
      usage="number of levels of bolts per topolgy")
  private int _numLevels = 1;

  @Option(name="--spoutParallel", aliases={"--spout"}, metaVar="SPOUT",
      usage="number of spouts to run in parallel")
  private int _spoutParallel = 3;
  
  @Option(name="--boltParallel", aliases={"--bolt"}, metaVar="BOLT",
      usage="number of bolts to run in parallel")
  private int _boltParallel = 3;
  
  @Option(name="--numWorkers", aliases={"--workers"}, metaVar="WORKERS",
      usage="number of workers to use per topology")
  private int _numWorkers = 3;
  
  @Option(name="--ackers", metaVar="ACKERS", 
      usage="number of acker bolts to launch per topology")
  private int _ackers = 1;
  
  @Option(name="--maxSpoutPending", aliases={"--maxPending"}, metaVar="PENDING",
      usage="maximum number of pending messages per spout (only valid if acking is enabled)")
  private int _maxSpoutPending = -1;
  
  @Option(name="--name", aliases={"--topologyName"}, metaVar="NAME",
      usage="base name of the topology (numbers may be appended to the end)")
  private String _name = "test";
  
  @Option(name="--ackEnabled", aliases={"--ack"}, usage="enable acking")
  private boolean _ackEnabled = false;
  
  @Option(name="--pollFreqSec", aliases={"--pollFreq"}, metaVar="POLL",
      usage="How often should metrics be collected")
  private int _pollFreqSec = 30;
  
  @Option(name="--testTimeSec", aliases={"--testTime"}, metaVar="TIME",
      usage="How long should the benchmark run for.")
  private int _testRunTimeSec = 1 * 60;
  
  @Option(name="--receiveBufferSize", aliases={"--rBufSize"}, metaVar="RECEIVEBUFFER",
	      usage="value of 2^() of TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE to use per topology")
  private int _receiveBufferSize = 10;
  
  @Option(name="--sendBufferSize", aliases={"--sBufSize"}, metaVar="SENDBUFFER",
	      usage="value of 2^() of TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE to use per topology")
  private int _sendBufferSize = 10;
  
  @Option(name="--batchSize", aliases={"--bSize"}, metaVar="BATCHSIZE",
	      usage="the batch size of the wrapper batch bolt")
  private int _batchSize = 10;
  
  @Option(name="--spoutSleepMs", aliases={"--sSleep"}, metaVar="SSLEEP",
	      usage="control the output speed of the spout")
  private int _spoutSleepMs = 1;
  
  @Option(name="--boltSleepMs", aliases={"--bSleep"}, metaVar="BSLEEP",
	      usage="control the execution speed of the user bolt")
  private int _boltSleepMs = 1;

  private static class MetricsState {
    long transferred = 0;
    int slotsUsed = 0;
    long lastTime = 0;
  }


  public void metrics(Nimbus.Client client, int size, int poll, int total) throws Exception {
    System.out.println("status\ttopologies\ttotalSlots\tslotsUsed\ttotalExecutors\texecutorsWithMetrics\ttime\ttime-diff(ms)\ttransferred\tthroughput(MB/s)\tavg_executed_latency\t"
    		+ "avg_complete_latency\ttotalUserBoltsExecuted\ttotalMessageExecutorsEmitted\tavg_capcity");
    MetricsState state = new MetricsState();
    long pollMs = poll * 1000;
    long now = System.currentTimeMillis();
    state.lastTime = now;
    long startTime = now;
    long cycle = 0;
    long sleepTime;
    long wakeupTime;
    while (metrics(client, size, now, state, "WAITING")) {
      now = System.currentTimeMillis();
      cycle = (now - startTime)/pollMs;
      wakeupTime = startTime + (pollMs * (cycle + 1));
      sleepTime = wakeupTime - now;
      if (sleepTime > 0) {
        Thread.sleep(sleepTime);
      }
      now = System.currentTimeMillis();
    }

    now = System.currentTimeMillis();
    cycle = (now - startTime)/pollMs;
    wakeupTime = startTime + (pollMs * (cycle + 1));
    sleepTime = wakeupTime - now;
    if (sleepTime > 0) {
      Thread.sleep(sleepTime);
    }
    now = System.currentTimeMillis();
    long end = now + (total * 1000);
    do {
      metrics(client, size, now, state, "RUNNING");
      now = System.currentTimeMillis();
      cycle = (now - startTime)/pollMs;
      wakeupTime = startTime + (pollMs * (cycle + 1));
      sleepTime = wakeupTime - now;
      if (sleepTime > 0) {
        Thread.sleep(sleepTime);
      }
      now = System.currentTimeMillis();
    } while (now < end);
  }

  public boolean metrics(Nimbus.Client client, int size, long now, MetricsState state, String message) throws Exception {
    ClusterSummary summary = client.getClusterInfo();
    long time = now - state.lastTime;
    state.lastTime = now;
    int numSupervisors = summary.get_supervisors_size();
    int totalSlots = 0;
    int totalUsedSlots = 0;
    for (SupervisorSummary sup: summary.get_supervisors()) {
      totalSlots += sup.get_num_workers();
      totalUsedSlots += sup.get_num_used_workers();
    }
    int slotsUsedDiff = totalUsedSlots - state.slotsUsed;
    state.slotsUsed = totalUsedSlots;

    int numTopologies = summary.get_topologies_size();
    long totalTransferred = 0;
    long totalMessageExecutorsEmitted = 0L;
    int totalExecutors = 0;
    int totalMessageExecutors = 0;
    int totalSpouts = 0;
    double avg_executed_latency = 0.0;
    double totalExecSum = 0.0;
    long totalUserBoltsExecuted = 0L;
    int executorsWithMetrics = 0;
    double avg_complete_latency = 0.0;
    for (TopologySummary ts: summary.get_topologies()) {
      String id = ts.get_id();
      TopologyInfo info = client.getTopologyInfo(id);
      for (ExecutorSummary es: info.get_executors()) {
        ExecutorStats stats = es.get_stats();
        String componentId = es.get_component_id();

        totalExecutors++;
		if (stats != null) {
          Map<String,Map<String,Long>> transferred = stats.get_transferred();
          Map<String,Map<String,Long>> emitted = stats.get_emitted();
          Long dflt = 0L;
          Long dflt_emitted = 0L;
          if ( transferred != null) {
            Map<String, Long> e2 = transferred.get(":all-time");
            if (e2 != null) {
              executorsWithMetrics++;
              //The SOL messages are always on the default stream, so just count those
              dflt = e2.get("default");
              if (dflt != null) {
                totalTransferred += dflt;
              }
            }
          }
          
          if ( emitted != null && componentId.equals("messageBolt")) {
              Map<String, Long> e2 = emitted.get("600");
              if (e2 != null) {
                //The SOL messages are always on the default stream, so just count those
                dflt_emitted = e2.get("default");
                if (dflt_emitted != null) {
                	totalMessageExecutorsEmitted += dflt_emitted;
                }
              }
          }
          
          ExecutorSpecificStats execSpecStats = stats.get_specific();
          if(execSpecStats.is_set_spout()) {
			  SpoutStats spoutStats = execSpecStats.get_spout();
			  double complete_latency = 0.0;
			  //use 10min as the window
			  if(CommonUtils.getStatDoubleValueFromMap(spoutStats.get_complete_ms_avg(),"600") != null){
				  	complete_latency = CommonUtils.getStatDoubleValueFromMap(spoutStats.get_complete_ms_avg(),"600");
			  }
			  avg_complete_latency += complete_latency;
			  totalSpouts++;
          }
          if(execSpecStats.is_set_bolt() && componentId.equals("messageBolt")) {
        	  BoltStats boltStats = execSpecStats.get_bolt();
  
        	  long executed = 0L;
        	  if (CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), "600") != null){
        		  	executed = CommonUtils.get_boltStatLongValueFromMap(boltStats.get_executed(), "600");
        	  }
  
        	  double execute_latency = 0.0;
        	  if(CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "600") != null){
        		  	execute_latency = CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_execute_ms_avg(), "600");
        	  }
	
        	  double process_latency = 0.0;
        	  if(CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "600") != null){
        		  	process_latency = CommonUtils.get_boltStatDoubleValueFromMap(boltStats.get_process_ms_avg(), "600");
        	  }
	  
			  totalExecSum += executed*execute_latency;
			  totalUserBoltsExecuted += executed;
			  totalMessageExecutors++;
          }
        } 
      }  
    }
    if (totalSpouts != 0){
    	avg_complete_latency =  avg_complete_latency/totalSpouts;
    }
    if(totalUserBoltsExecuted != 0){
    	avg_executed_latency = totalExecSum/totalUserBoltsExecuted;
    }
    double avg_capcity = totalExecSum/(10*60*1000)/_boltParallel;
    long transferredDiff = totalTransferred - state.transferred;
    state.transferred = totalTransferred;
    double throughput = (transferredDiff == 0 || time == 0) ? 0.0 : (transferredDiff * size)/(1024.0 * 1024.0)/(time/1000.0);
    System.out.println(message+"\t"+numTopologies+"\t"+totalSlots+"\t"+totalUsedSlots+"\t"+totalExecutors+"\t"+executorsWithMetrics
    		           +"\t"+now+"\t"+time+"\t"+transferredDiff+"\t"+throughput+"\t"+avg_executed_latency+"\t"+avg_complete_latency
    		           +"\t"+totalUserBoltsExecuted+"\t"+totalMessageExecutorsEmitted+"\t"+avg_capcity);
    if ("WAITING".equals(message)) {
      //System.err.println(" !("+totalUsedSlots+" > 0 && "+slotsUsedDiff+" == 0 && "+totalExecutors+" > 0 && "+executorsWithMetrics+" >= "+totalExecutors+")");
    }
    return !(totalUsedSlots > 0 && slotsUsedDiff == 0 && totalExecutors > 0 && executorsWithMetrics >= totalExecutors);
  } 

 
  public void realMain(String[] args) throws Exception {
    Map clusterConf = Utils.readStormConfig();
    clusterConf.putAll(Utils.readCommandLineOpts());
    Nimbus.Client client = NimbusClient.getConfiguredClient(clusterConf).getClient();

    CmdLineParser parser = new CmdLineParser(this);
    parser.setUsageWidth(80);
    try {
      // parse the arguments.
      parser.parseArgument(args);
    } catch( CmdLineException e ) {
      // if there's a problem in the command line,
      // you'll get this exception. this will report
      // an error message.
      System.err.println(e.getMessage());
      _help = true;
    }
    if(_help) {
      parser.printUsage(System.err);
      System.err.println();
      return;
    }
    if (_numWorkers <= 0) {
      throw new IllegalArgumentException("Need at least one worker");
    }
    if (_name == null || _name.isEmpty()) {
      throw new IllegalArgumentException("name must be something");
    }
    if (!_ackEnabled) {
      _ackers = 0;
    }

    try {
      for (int topoNum = 0; topoNum < _numTopologies; topoNum++) {
        TopologyBuilder builder = new TopologyBuilder();
        LOG.info("Adding in "+_spoutParallel +" spouts");
    	builder.setSpout("messageSpout", 
            new SOLSpout(_messageSize , _ackEnabled, _spoutSleepMs), _spoutParallel);
    	int freq = (int) (_testRunTimeSec*1000/3)-10;
    	builder.setSpout("signalsSpout",new SOLSignalsSpout(freq));
    	LOG.info("Adding in "+_boltParallel +" batcher bolts with _batchSize="+_batchSize);
        builder.setBolt("messageBoltBatch", new SOLBatchBolt(_batchSize), _boltParallel)
            .shuffleGrouping("messageSpout");
        LOG.info("Adding in "+_boltParallel +" user bolts with debatcher in itself");
        builder.setBolt("messageBolt", new SOLBolt_wDeBatch(_boltSleepMs), _boltParallel)
            .shuffleGrouping("messageBoltBatch");
    	//LOG.info("Adding in "+_boltParallel +" bolts");
        //builder.setBolt("messageBolt", new SOLBolt(), _boltParallel).shuffleGrouping("messageBoltDeBatch");

        Config conf = new Config();
        conf.setDebug(_debug);
        conf.setNumWorkers(_numWorkers);
        conf.setNumAckers(_ackers);
        if (_maxSpoutPending > 0) {
          conf.setMaxSpoutPending(_maxSpoutPending);
        }
        conf.setStatsSampleRate(1.0);
        Double RECEIVE_BUFFER_SIZE = pow(2,_receiveBufferSize);
        Double SEND_BUFFER_SIZE = pow(2,_sendBufferSize);
        conf.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, RECEIVE_BUFFER_SIZE.intValue());
        conf.put(Config.TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE, SEND_BUFFER_SIZE.intValue());

        StormSubmitter.submitTopology(_name+"_"+topoNum, conf, builder.createTopology());
      }
      metrics(client, _messageSize, _pollFreqSec, _testRunTimeSec);
    } finally {
      //Kill it right now!!!
      KillOptions killOpts = new KillOptions();
      killOpts.set_wait_secs(0);

      for (int topoNum = 0; topoNum < _numTopologies; topoNum++) {
        LOG.info("KILLING "+_name+"_"+topoNum);
        try {
          client.killTopologyWithOpts(_name+"_"+topoNum, killOpts);
        } catch (Exception e) {
          LOG.error("Error tying to kill "+_name+"_"+topoNum,e);
        }
      }
    }
  }
  
  public static void main(String[] args) throws Exception {
    new MainBatch_woDebatch().realMain(args);
  }
}
