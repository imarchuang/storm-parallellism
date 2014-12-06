package com.imarchuang.job;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class CleanStormStatsDBJob implements Job {
	
		private static Logger logger = Logger.getLogger(CleanStormStatsDBJob.class);
	 
		public static void main(String[] args) {
	 
			CleanStormStatsDBJob obj = new CleanStormStatsDBJob();
			 
			String scriptFullPath = "C://Users//marc//Documents//GitHub//storm-parallellism//pl//cleanStormStats.pl";
	 
			//in windows
			String command = "perl -w " + scriptFullPath;
	 
			String output = obj.executeCommand(command);
	 
			//System.out.println(output);
			logger.debug(output);
		}

		private String executeCommand(String command) {
			StringBuffer output = new StringBuffer();
			 
			Process p;
			try {
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader reader = 
	                            new BufferedReader(new InputStreamReader(p.getInputStream()));
	 
	                        String line = "";			
				while ((line = reader.readLine())!= null) {
					output.append(line + "\n");
				}
	 
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug(e);
			}
	 
			return output.toString();
		}

		public void execute(JobExecutionContext arg0)
				throws JobExecutionException {
			CleanStormStatsDBJob.main(null);
			logger.debug("CleanStormStatsDBJob runs successfully...");
			
		}

}
