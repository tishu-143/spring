package com.example.application.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class Employee_Controller {

	@Autowired
	Job job;
	
	@Autowired
	JobLauncher launcher;
	
	
	
	 private static final Logger LOGGER=LoggerFactory.getLogger(Employee_Controller.class);
	 
	 @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(2000))
	@RequestMapping(method = RequestMethod.GET, value = "/employee/load")
	public BatchStatus loadEmployeeData() {
		Map<String, JobParameter> parameter = new HashMap<>();
		parameter.put("time", new JobParameter(System.currentTimeMillis()));
		JobParameters parameters = new JobParameters(parameter);
		JobExecution execution = null;
		try {
			execution = launcher.run(job, parameters);
			System.out.println("Job Execution : " + execution.getStatus());
			System.out.println("Started Running.....");
			
			while(execution.isRunning()) {
				System.out.println("Running.......");
			}
			return execution.getStatus();
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return execution.getStatus();
	}
	 
	 @Recover
	 public String recover(Throwable t) {
		 return "Error :"+t.getClass().getName();
	 }
}
