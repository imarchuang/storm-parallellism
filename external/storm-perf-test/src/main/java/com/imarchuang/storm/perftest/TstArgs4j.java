package com.imarchuang.storm.perftest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import static java.lang.Math.pow;


public class TstArgs4j {
	
	private static final Log LOG = LogFactory.getLog(Main.class);
	
	@Option(name="--help", aliases={"-h"}, usage="print help message")
	private boolean _help = false;
	  
	@Option(name="--debug", aliases={"-d"}, usage="enable debug")
	private boolean _debug = false;
	  
	@Option(name="--local", usage="run in local mode")
	private boolean _local = false;
	@Option(name="--name", aliases={"--topologyName"}, metaVar="NAME",
		      usage="base name of the topology (numbers may be appended to the end)")
		  private String _name = "test";
	@Option(name="--messageSizeByte", aliases={"--messageSize"}, metaVar="SIZE",
		      usage="size of the messages generated in bytes")
		  private int _messageSize = 100;
	
	public void realMain(String[] args) throws Exception {
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
	}
	
	public static void main(String[] args) throws Exception {
	    new TstArgs4j().realMain(args);
	    System.out.println(pow(2,10));
	}

}
