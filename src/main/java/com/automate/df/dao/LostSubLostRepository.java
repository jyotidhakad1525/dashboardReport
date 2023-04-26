package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.automate.df.entity.LostReasons;

public interface LostSubLostRepository extends CrudRepository<LostReasons, Integer> {
	 @Query(value = "SELECT * FROM lost_reasons WHERE org_id =?1 and stage_name=?2 and status='Active'", nativeQuery = true)
	    List<LostReasons> getAllSubLost(String orgId,String stageName);
}
