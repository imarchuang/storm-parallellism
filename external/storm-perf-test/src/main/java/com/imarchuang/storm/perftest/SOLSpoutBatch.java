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
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class SOLSpoutBatch extends BaseRichSpout {
  private int _sizeInBytes;
  private long _messageCount;
  private SpoutOutputCollector _collector;
  private String [] _messages = null;
  private boolean _ackEnabled;
  private Random _rand = null;
  private int _sleepMs = 0;
  List<String> _batch = new ArrayList();
  private int _size = 1;
  public SOLSpoutBatch(int sizeInBytes, boolean ackEnabled, int sleepMs, int size) {
    if(sizeInBytes < 0) {
      sizeInBytes = 0;
    }
    if(sleepMs < 1) {
    	sleepMs = 0;
      }
    _sizeInBytes = sizeInBytes;
    _messageCount = 0;
    _ackEnabled = ackEnabled;
    _sleepMs = sleepMs;
    _size = size;
  }

  public boolean isDistributed() {
    return true;
  }

  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _rand = new Random();
    _collector = collector;
    final int differentMessages = 100;
    _messages = new String[differentMessages];
    for(int i = 0; i < differentMessages; i++) {
      StringBuilder sb = new StringBuilder(_sizeInBytes);
      //Even though java encodes strings in UCS2, the serialized version sent by the tuples
      // is UTF8, so it should be a single byte
      for(int j = 0; j < _sizeInBytes; j++) {
        sb.append(_rand.nextInt(9));
      }
      _messages[i] = sb.toString();
    }
  }

  @Override
  public void close() {
    //Empty
  }

  @Override
  public void nextTuple() {
    final String message = _messages[_rand.nextInt(_messages.length)];
    if (_sleepMs >= 1){
    	Utils.sleep(_sleepMs);
    }
    if(_batch!=null){
		if(_batch.size() >= _size){
			_collector.emit(new Values(_batch.toString().substring(1, _batch.toString().length()-1)));
			System.out.println(_batch.toString().substring(1, _batch.toString().length()-1));
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
    _messageCount++;
  }


  @Override
  public void ack(Object msgId) {
    //Empty
  }

  @Override
  public void fail(Object msgId) {
    //Empty
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("messageB"));
  }
}
