package com.automate.df.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.sales.VehicleInventory;
@Repository
public interface VehicleInventoryRepo extends JpaRepository<VehicleInventory, Integer>,JpaSpecificationExecutor<VehicleInventory>{
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1 and vin_number=?2", nativeQuery = true)
    VehicleInventory getRecordByVin(int orgId,String vin);
	
	@Modifying
	@Transactional
	@Query(value = "update vehicles_inventory set branch_id=?1,branch_name=?2,location=?3,location_id=?4,alloted='YES' WHERE id =?5", nativeQuery = true)
	int allocate(int branchId,String branchName,String location,int locationId,int id);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1", nativeQuery = true)
    List<VehicleInventory> getData(int orgId);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE id =?1", nativeQuery = true)
    VehicleInventory get(int id);
	
	@Query(value = "SELECT distinct location FROM vehicles_inventory WHERE org_id =?1", nativeQuery = true)
    List<String> getLocations(int orgId);
	
	@Query(value = "SELECT distinct stockyard_branch_name FROM vehicles_inventory WHERE org_id =?1 and location=?2", nativeQuery = true)
    List<String> getYardBranches(int orgId,String locationName);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1 and location=?2", nativeQuery = true)
    List<VehicleInventory> getLocationBasedVehicles(int orgId,String locationName);

	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2", nativeQuery = true)
    List<VehicleInventory> getBranchBasedVehicles(int orgId,String branchName);
	
	@Query(value = "SELECT distinct model FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2", nativeQuery = true)
    List<String> getModels(int orgId,String branchName);
	
	@Query(value = "select name from location_node_data where id in (select parent_id from location_node_data where id in (select org_map_id from dms_branch where branch_id=:branchId))", nativeQuery = true)
    String getLocation(int branchId);
	
	@Query(value = "SELECT DATEDIFF(CURDATE(),purchase_date) FROM vehicles_inventory WHERE id=?1", nativeQuery = true)
    int ageing(int id);
	
	@Query(value = "SELECT count(*) FROM vehicles_inventory WHERE org_id =?1 and location=?2 and status=?3", nativeQuery = true)
    long getLocationCount(int orgId,String locationName,String status);
	
	@Query(value = "SELECT sum(ex_showroom_price) FROM vehicles_inventory WHERE org_id =?1 and location=?2 and status=?3", nativeQuery = true)
    String getLocationSumStockPrize(int orgId,String locationName,String status);
	
	@Query(value = "SELECT count(*) FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and status=?3", nativeQuery = true)
    long getBranches(int orgId,String locationName,String status);
	
	@Query(value = "SELECT sum(ex_showroom_price) FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and status=?3", nativeQuery = true)
    String getBranchesSumPrize(int orgId,String locationName,String status);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE model =?1 and org_id =?2 and stockyard_branch_name=?3 and status=?4", nativeQuery = true)
	List<VehicleInventory> getModelcount(String model,int orgId,String branchName,String status);
	
	@Query(value = "SELECT sum(ex_showroom_price) FROM vehicles_inventory WHERE model =?1 and org_id =?2 and stockyard_branch_name=?3 and status=?4", nativeQuery = true)
	String getModelPrize(String model,int orgId,String branchName,String status);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1 and model=?2 and stockyard_branch_name=?3", nativeQuery = true)
    List<VehicleInventory> getModelBasedVehicles(int orgId,String model,String branchName);
	
	@Query(value = "SELECT distinct variant FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and model=?3", nativeQuery = true)
    List<String> getVarientsBymodel(int orgId,String branchName,String model);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE model =?1 and org_id =?2 and stockyard_branch_name=?3 and status=?4 and variant=?5", nativeQuery = true)
	List<VehicleInventory> getVarientcount(String model,int orgId,String branchName,String status,String varient);
	
	@Query(value = "SELECT sum(ex_showroom_price) FROM vehicles_inventory WHERE model =?1 and org_id =?2 and stockyard_branch_name=?3 and status=?4 and variant=?5", nativeQuery = true)
	String getVarientPrize(String model,int orgId,String branchName,String status,String varient);
	
	@Query(value = "SELECT distinct colour FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and model =?3 and variant=?4", nativeQuery = true)
    List<String> getColourWiseData(int orgId,String branchName,String model,String varient);
	
	@Query(value = "SELECT * FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and model =?3 and variant=?4 and colour=?5 and status=?6", nativeQuery = true)
    List<VehicleInventory> getByColour(int orgId,String branchName,String model,String varient,String colour,String status);
	
	@Query(value = "SELECT sum(ex_showroom_price) FROM vehicles_inventory WHERE org_id =?1 and stockyard_branch_name=?2 and model =?3 and variant=?4 and colour=?5 and status=?6", nativeQuery = true)
    String getByColourPrize(int orgId,String branchName,String model,String varient,String colour,String status);
}
