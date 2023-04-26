package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.sales.employee.EmployeeEntity;
@Repository

public interface Employee  extends JpaRepository<EmployeeEntity, Integer>{
	
	
	@Query(value = "SELECT * FROM dms_employee where emp_id=:empId", nativeQuery = true)
	List<EmployeeEntity> getEmpPic(int empId);

}
