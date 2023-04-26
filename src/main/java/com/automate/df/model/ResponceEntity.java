package com.automate.df.model;

import java.util.List;

import com.automate.df.entity.sales.VehicleIndents;
import com.automate.df.entity.sales.VehicleInventory;

import lombok.Data;
@Data
public class ResponceEntity {
	private List<VehicleInventory> inventory;
	private VehicleInventory inventorySingle;
	private VehicleIndents vehicleIndents;
	
	public ResponceEntity() {
	}
}
