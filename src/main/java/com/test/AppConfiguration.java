package com.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SpringBootApplication
public class AppConfiguration extends DefaultBatchConfiguration {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(AppConfiguration.class, args);
        
        JobLauncher jobLauncher = (JobLauncher) ctx.getBean("jobLauncher");
        Job job = (Job) ctx.getBean("testJob");
        System.out.println("Starting the batch job");
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed");
            SpringApplication.exit(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
	}

	@Autowired
	DataSource dataSource;
	
	@Autowired
	@Qualifier("metaDs")
	DataSource metaDataSource;

	@Bean
	public Job testJob() {
		return new JobBuilder("testJobName", this.jobRepository())
				.preventRestart()
				.start(splitFlows())
				.end()
				.build();
	}
	
	@Bean
	public Flow splitFlows() {
		return new FlowBuilder<SimpleFlow>("splitFlows")
				.split(taskExecutor())
				.add(
						partitionedFlow(),
						partitionedFlow()
						)
				.build();
	}
	
	@Bean
	public Flow partitionedFlow()
	{
		return new FlowBuilder<SimpleFlow>("partitionedFlow")
				.start(partitionedStep())
				.build();
	}
	
	@Bean
	Step partitionedStep() {
		return new StepBuilder("partitionedStep", this.jobRepository()).partitioner(workStep())
				.partitioner("workerStep", new SimplePartitioner())
				.gridSize(8)
				.taskExecutor(taskExecutor())
				.build();
	}

	@Bean
	public Step workStep() {
		return new StepBuilder("step1", this.jobRepository())
				.<String, String>chunk(2, this.getTransactionManager())
				.reader(reader())
				.writer(writer())
				.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
		t.setCorePoolSize(10);
		t.setMaxPoolSize(10);
		return t;
	}
	
	@Bean
	@StepScope
	ItemStreamReader<String> reader() {

		return new ItemStreamReader<String>() {
			AtomicInteger i = new AtomicInteger();
			Connection c = null;
			@Override
			public void open(ExecutionContext executionContext) throws ItemStreamException {

				try {
					c = dataSource.getConnection();
					// H2
//					c.prepareStatement("SELECT 1");
					// HSQLDB
					c.prepareStatement(" SELECT CURRENT_DATE AS today, CURRENT_TIME AS now FROM (VALUES(0))");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public String read()
					throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
				System.out.println(dataSource.toString());
				int x = i.getAndIncrement();
				return x < 10 ? (""+x) : null;
			}
			
			@Override
			public void close() throws ItemStreamException {
				try {
					c.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	@Bean
	@StepScope
	ItemWriter<String> writer() {
		return new ItemWriter<String>() {
			@Override
			public void write(Chunk<? extends String> chunk) throws Exception {
				System.out.println(chunk.toString());
			}
		};
	}
	
	@Override
	public JobRepository jobRepository() throws BatchConfigurationException {
		JobRepositoryFactoryBean repoBean = new JobRepositoryFactoryBean();
		repoBean.setTransactionManager(getTransactionManager());
		repoBean.setDataSource(this.getDataSource());
		try {
			repoBean.afterPropertiesSet();
			return repoBean.getObject();
		} catch (Exception e) {
			throw new BatchConfigurationException(e);
		}
	}
	
	@Override
	protected PlatformTransactionManager getTransactionManager() {
		return new JdbcTransactionManager(this.getDataSource());
	}
	
//	EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
//			.generateUniqueName(true)
//			.addScript("/org/springframework/batch/core/schema-derby.sql")
//			.setType(EmbeddedDatabaseType.DERBY)
//			.build();
//	
//	EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
//			.generateUniqueName(true)
//			.addScript("/org/springframework/batch/core/schema-h2.sql")
//			.setType(EmbeddedDatabaseType.H2)
//			.build();
	
//	EmbeddedDatabase embeddedDatabase = new EmbeddedDatabaseBuilder()
//			.generateUniqueName(true)
//			.addScript("/org/springframework/batch/core/schema-hsqldb.sql")
//			.setType(EmbeddedDatabaseType.HSQL)
//			.build();
	
	@Override
	protected DataSource getDataSource() {
		return metaDataSource;
	}
	
	
}
