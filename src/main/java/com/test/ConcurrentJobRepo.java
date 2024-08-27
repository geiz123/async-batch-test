package com.test;

import java.util.Collection;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;

public class ConcurrentJobRepo implements JobRepository
{

	@Override
	public boolean isJobInstanceExists(String jobName, JobParameters jobParameters)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExecution createJobExecution(String jobName, JobParameters jobParameters)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(JobExecution jobExecution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void add(StepExecution stepExecution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(Collection<StepExecution> stepExecutions)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update(StepExecution stepExecution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecutionContext(StepExecution stepExecution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecutionContext(JobExecution jobExecution)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getStepExecutionCount(JobInstance jobInstance, String stepName)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
