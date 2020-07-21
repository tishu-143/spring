package com.example.application.Job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import com.example.application.Entity.Employee_Data;
import com.example.application.Repo.Employee_Repo;
import com.example.application.Repo.Employee_Repo;


@Configuration
@EnableBatchProcessing
public class Batch_Job_Config {

	
	@Autowired
	private Employee_Repo repo;
	
	@Autowired
	private DataSource datasource;
	
	
	
	// Configuring Job
	@Bean
	public Job employeeJob(JobBuilderFactory jbf
							,StepBuilderFactory sbf
							,ItemReader<Employee_Data> reader
							,ItemWriter<Employee_Data> writer) {
		
		Step step = sbf.get("Employee_Job_Steps")
				.<Employee_Data, Employee_Data>chunk(100)
				.reader(reader)
				.writer(writer)
				.build();
				
		return jbf.get("Employee_Job")
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}

		
	
	
	
	
	// Reader for reading the csv file
	
	@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(2000))
	@Bean
	public FlatFileItemReader<Employee_Data> employee_Data_Reader(){
		FlatFileItemReader<Employee_Data> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource("MOCK_DATA.csv"));
		reader.setName("csv reader");
		reader.setLinesToSkip(1);
		reader.setLineMapper(lineMapper());
		return reader;
	}
	
	private LineMapper<Employee_Data> lineMapper() {
		// TODO Auto-generated method stub
		DefaultLineMapper<Employee_Data> mapper = new DefaultLineMapper<Employee_Data>();
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		
		tokenizer.setDelimiter(",");
		tokenizer.setStrict(false);
		tokenizer.setNames(new String[] {"id", "first_name", "last_name", "email", "gender", "ip_address"});
		
		BeanWrapperFieldSetMapper<Employee_Data> wrapper = new BeanWrapperFieldSetMapper<>();
		wrapper.setTargetType(Employee_Data.class);
		
		
		
		
		mapper.setLineTokenizer(tokenizer);
		mapper.setFieldSetMapper(wrapper);
		return mapper;
	}


	@Recover
	public String recover(Throwable t) {
		return "Error :"+t.getClass().getName();
	}
	
}
