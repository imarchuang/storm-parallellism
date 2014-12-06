package com.imarchuang;

import java.text.ParseException;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.imarchuang.job.CleanStormStatsDBJob;
import com.imarchuang.job.ExtractMetricsInfoToDBJob;

/**
 * @author onlinetechvision.com
 * @since 17 Sept 2011
 * @version 1.0.0
 *
 */
public class JobScheduler {
	
	public static void main(String[] args) {
		
		try {
			
			// specify the job' s details..
			JobDetail jobExtract = JobBuilder.newJob(ExtractMetricsInfoToDBJob.class)
			    .withIdentity("extractJob")
			    .build();
			JobDetail jobClean = JobBuilder.newJob(CleanStormStatsDBJob.class)
				    .withIdentity("cleanDBJob")
				    .build();
			
			// specify the running period of the job
			Trigger cronTriggerExtract = TriggerBuilder.newTrigger()
			      .withSchedule(  
	                    SimpleScheduleBuilder.simpleSchedule()
	                    .withIntervalInSeconds(600)
	                    .repeatForever())  
                             .build(); 
			
			Trigger cronTriggerClean = TriggerBuilder
					.newTrigger()
					.withIdentity("cronTriggerClean")
					.withSchedule(
						CronScheduleBuilder.cronSchedule("0 48 12 ? * *"))
					.build();
    	
			//schedule the job
			SchedulerFactory schFactory = new StdSchedulerFactory();
			Scheduler sch = schFactory.getScheduler();
	    	sch.start();	    	
	    	sch.scheduleJob(jobExtract, cronTriggerExtract);	
	    	sch.scheduleJob(jobClean, cronTriggerClean);	
		
		} catch (SchedulerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
}
