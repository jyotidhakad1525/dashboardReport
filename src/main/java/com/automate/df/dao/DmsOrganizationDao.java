package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.automate.df.entity.sales.DmsOrganization;

public interface DmsOrganizationDao extends JpaRepository<DmsOrganization, Integer>{

	
	@Query(value="select template_file_nm from dms_organization where org_id=:orgId",nativeQuery = true)
	String getTemplateFileName(@Param(value="orgId") String orgId);

}
