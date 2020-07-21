package com.example.application.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.Entity.Employee_Data;

public interface Employee_Repo extends JpaRepository<Employee_Data, String>{

}
