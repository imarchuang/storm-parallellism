package com.imarchuang.storm.perftest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class SOLSignalsSpout extends BaseRichSpout{
	//public static Logger LOG = LoggerFactory.getLogger(SOLSignalsSpout.class);
	private SpoutOutputCollector collector;
	private int _freq;

	public SOLSignalsSpout(int freq) {
		_freq = freq;
	}

	@Override
	public void nextTuple() {	
		try {
			Thread.sleep(_freq);
		} catch (InterruptedException e) {}
		//LOG.debug("signals out push");
		collector.emit("signals",new Values("push"));
	}

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("signals",new Fields("action"));
	}

}
