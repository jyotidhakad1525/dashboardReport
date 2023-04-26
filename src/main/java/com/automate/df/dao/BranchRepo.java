package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.automate.df.entity.BranchEntity;

public interface BranchRepo extends JpaRepository<BranchEntity, Integer>{
	@Query(value="select * FROM dms_branch where org_map_id=?1",nativeQuery = true)
	BranchEntity findBranches(int id);
	
	@Query(value="select branch_id FROM dms_branch where org_map_id=?1",nativeQuery = true)
	int getBranch(int id);
}
