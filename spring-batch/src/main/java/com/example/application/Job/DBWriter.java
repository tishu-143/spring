package com.example.application.Job;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.application.Entity.Employee_Data;
import com.example.application.Repo.Employee_Repo;

@Component
public class DBWriter implements ItemWriter<Employee_Data>{

	@Autowired
	private Employee_Repo repo;
	
	@Override
	public void write(List<? extends Employee_Data> employees) throws Exception {
		// TODO Auto-generated method stub
		repo.saveAll(employees);
	}

}
