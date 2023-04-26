package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.automate.df.entity.FollowupReasonsEntity;

public interface FollowupReasons extends JpaRepository<FollowupReasonsEntity, String>{

	@Query(value = "SELECT * FROM followup_reasons where org_id=:orgId and stage_name=:stageName and status='Active'", nativeQuery = true)
	List<FollowupReasonsEntity> getfollowupReasons(String orgId,String stageName);

	
	
}
