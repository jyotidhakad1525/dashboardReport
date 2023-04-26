package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.sales.DmsOrganizationWizard;

@Repository
public interface OrgnizationDao extends JpaRepository<DmsOrganizationWizard, Integer> {
	@Query(value = "select * from dms_organization where org_id=:orgId", nativeQuery = true)
	public List<DmsOrganizationWizard> getQrCode(@Param(value = "orgId") int orgId);
}
