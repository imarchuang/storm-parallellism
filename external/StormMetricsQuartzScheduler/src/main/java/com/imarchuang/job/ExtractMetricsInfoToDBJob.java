package com.imarchuang.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import thrift.jvm.ClusterInfoExtractorToDB;

public class ExtractMetricsInfoToDBJob implements Job {

	private Logger log = Logger.getLogger(ExtractMetricsInfoToDBJob.class);
	
	public void execute(JobExecutionContext jExeCtx) throws JobExecutionException {
		
		ClusterInfoExtractorToDB.main(null);
		log.debug("ExtractMetricsInfoToDBJob runs successfully...");
	}
	
}
