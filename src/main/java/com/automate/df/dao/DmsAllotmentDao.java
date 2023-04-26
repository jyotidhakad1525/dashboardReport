package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.sales.lead.DmsAllotment;

public interface DmsAllotmentDao extends JpaRepository<DmsAllotment, Integer>{
	
	@Query(value="select * from dms_allotment where lead_id=:id",nativeQuery = true)
	List<DmsAllotment> getByLeadId(@Param(value="id") Integer id);

}
