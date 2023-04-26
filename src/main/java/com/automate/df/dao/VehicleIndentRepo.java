package com.automate.df.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.automate.df.entity.sales.VehicleIndents;

public interface VehicleIndentRepo extends JpaRepository<VehicleIndents, Integer>{
	
	@Query(value = "SELECT * FROM vehicle_indents WHERE org_id =?1 and branch_id=?2 and vehicle_inventory_id=?3", nativeQuery = true)
	VehicleIndents getIdent(int orgId,int branchId,int inventoryId);
	
	@Query(value = "SELECT * FROM vehicle_indents WHERE org_id =?1", nativeQuery = true)
	List<VehicleIndents> getData(int orgId);

}
