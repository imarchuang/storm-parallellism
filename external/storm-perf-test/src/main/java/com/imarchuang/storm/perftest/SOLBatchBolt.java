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

public class SOLBatchBolt extends BaseRichBolt {
  //public static Logger LOG = LoggerFactory.getLogger(SOLBatchBolt.class);
  private OutputCollector _collector;
  private static int _size;
  //Map<String, Integer> _counters;
  List<String> _batch = new ArrayList();

  public SOLBatchBolt(int size) {
	  _size = size;
  }

  @Override
  public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
	  _collector = collector;
  }

  @Override
  public void execute(Tuple tuple) {
	//LOG.info("in BatchBolt execute");
	
	if(tuple.getSourceStreamId().equals("signals")){
		String signal = tuple.getStringByField("action");
		if("push".equals(signal))
		    if(_batch.size()>0){
				//LOG.info("received singals to push");
				_collector.emit(new Values(_batch.toString().substring(1, _batch.toString().length()-1)));
				//LOG.info("tuple:"+signal);
			    //LOG.info("tupleB:"+_batch.toString().substring(1, _batch.toString().length()-1));
				_batch.clear();
		    }
	}else {
		String message = null; 
		try{
			message = tuple.getStringByField("message");
		}catch (IllegalArgumentException e) {
			//LOG.debug(e.toString());
		}
		if(_batch!=null){
			if(_batch.size() >= _size){
				_collector.emit(new Values(_batch.toString().substring(1, _batch.toString().length()-1)));
				//LOG.info("tuple:"+message);
			    //LOG.info("tupleB:"+_batch.toString().substring(1, _batch.toString().length()-1));
				_batch.clear();
				_batch.add(message);
			}else{
				_batch.add(message);
				//LOG.info("tuple:"+message);
			    //LOG.info("tupleB:"+_batch.toString().substring(1, _batch.toString().length()-1));
			}
		} else{
			//for first time
			_batch.add(message);
			//LOG.info("tuple:"+message);
		    //LOG.info("tupleB:"+_batch.toString().substring(1, _batch.toString().length()-1));
		}
		
	}
    //LOG.info("field:"+tuple.getFields().toString());
    //LOG.info("tupleS:"+tuple.getString(0));
    //Set the tuple as Acknowledge
    //_collector.ack(tuple);
  }

  private int length() {
	// TODO Auto-generated method stub
	return 0;
}

public static int get_size() {
	return _size;
  }

@Override
  public void cleanup() {
	if(_batch.size() >= _size){
		_collector.emit(new Values(_batch.toString().substring(1, _batch.toString().length()-1)));
		//LOG.info("tuple:"+message);
	    //LOG.info("tupleB:"+_batch.toString().substring(1, _batch.toString().length()-1));
	}
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("messageB"));
  }
}
