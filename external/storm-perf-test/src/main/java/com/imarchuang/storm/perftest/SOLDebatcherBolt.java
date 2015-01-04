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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class SOLDebatcherBolt extends BaseRichBolt {
  //public static Logger LOG = LoggerFactory.getLogger(SOLDebatcherBolt.class);
  private OutputCollector _collector;
  //keep the size in sync with the batcher bolt
  private int _size = SOLBatchBolt.get_size();
  //Map<String, Integer> _counters;

  public SOLDebatcherBolt() {
	  //do nothing
  }

   
  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
	  _collector = collector;
  }

  @Override
  public void execute(Tuple tuple) {
	//LOG.debug("in DebatcherBolt execute");
	String messageB = null; 
	try{
		messageB = tuple.getStringByField("messageB");
	}catch (IllegalArgumentException e) {
		//Do nothing
	}

    //LOG.debug("tupleD:"+messageB);
    //LOG.debug("tupleDGetS:"+tuple.getString(0));
    
    String[] messages = messageB.split(",");
    for(String message : messages){
    	//message = message.trim();
        if(!message.isEmpty()){
            _collector.emit(new Values(message));
        }
    }

    //LOG.info("field:"+tuple.getFields().toString());
    //LOG.info("tupleS:"+tuple.getString(0));
    //Set the tuple as Acknowledge
    //_collector.ack(tuple);
  }

  @Override
  public void cleanup() {
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("message"));
  }
}
