package com.automate.df.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.automate.df.entity.sales.VehicleInventoryHistory;
@Repository
public interface VehicleInventoryHistoryRepo extends JpaRepository<VehicleInventoryHistory, Integer>,JpaSpecificationExecutor<VehicleInventoryHistory>{

}
