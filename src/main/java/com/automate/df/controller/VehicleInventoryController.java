package com.automate.df.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.automate.df.dao.BranchRepo;
import com.automate.df.dao.VehicleIndentRepo;
import com.automate.df.dao.VehicleInventoryRepo;
import com.automate.df.entity.BranchEntity;
import com.automate.df.entity.sales.VehicleIndents;
import com.automate.df.entity.sales.VehicleInventory;
import com.automate.df.model.BaseFilter;
import com.automate.df.model.InventoryResponce;
import com.automate.df.service.impl.VehicleInventoryService;

@RestController
@CrossOrigin
@RequestMapping("/vehicle-inventory")
public class VehicleInventoryController {

	@Autowired
	private VehicleInventoryService vehicleInventoryService;
	@Autowired
	private VehicleInventoryRepo vehicleInventoryRepo;
	@Autowired
	private BranchRepo dmsBranchDao;
	@Autowired
	private VehicleIndentRepo vehicleIndentRepo;

	@CrossOrigin
	@PostMapping("/save-inventory")
	public InventoryResponce saveInventory(@RequestBody List<VehicleInventory> vehicleInventory) {
		InventoryResponce res = vehicleInventoryService.saveInventory(vehicleInventory);
		return res;
	}
	
	@CrossOrigin
	@PostMapping("/update-inventory")
	public InventoryResponce supdateInventory(@RequestBody VehicleInventory vehicleInventory) {
		InventoryResponce res = vehicleInventoryService.updateInventory(vehicleInventory);
		return res;
	}

	@CrossOrigin
	@PostMapping("/create-indent")
	public InventoryResponce createIndent(@RequestBody VehicleIndents vehicleInventory) {
		InventoryResponce res = vehicleInventoryService.createIndent(vehicleInventory);
		return res;
	}

	@CrossOrigin
	@PostMapping("/allocate-vehicle")
	public InventoryResponce allocateToDealer(@RequestBody List<VehicleInventory> vehicleInventory) {
		InventoryResponce res = vehicleInventoryService.allocateToDealer(vehicleInventory);
		return res;
	}

	@CrossOrigin
	@GetMapping("/get-inventory/{orgId}")
	public List<VehicleInventory> getInventory(@PathVariable("orgId") int orgId) {
		List<VehicleInventory> res = vehicleInventoryRepo.getData(orgId);
		List<VehicleInventory> result = new ArrayList<>();
		for (VehicleInventory single : res) {
			int ageing = vehicleInventoryRepo.ageing(single.getId());
			single.setAgeing(ageing);
			result.add(single);
		}

		return result;
	}
	
	@CrossOrigin
	@GetMapping("/getOne/{id}")
	public VehicleInventory getOneRecord(@PathVariable("id") int id) {
		VehicleInventory res = vehicleInventoryRepo.get(id);
		return res;
	}

	@CrossOrigin
	@GetMapping("/get-indents/{orgId}")
	public List<VehicleIndents> getIndents(@PathVariable("orgId") int orgId) {
		List<VehicleIndents> res = vehicleIndentRepo.getData(orgId);
		return res;
	}

	@CrossOrigin
	@GetMapping("/get-branches/{orgMapId}")
	public BranchEntity getBranches(@PathVariable("orgMapId") int orgMapId) {
		BranchEntity res = dmsBranchDao.findBranches(orgMapId);
		return res;
	}

	@CrossOrigin
	@PostMapping("/getInventory")
	public Map<String, Object> getInventoryForMystock(@RequestBody BaseFilter baseFilter) {
		Map<String, Object> res = vehicleInventoryService.getInventoryForMystock(baseFilter);

		return res;
	}

	@CrossOrigin
	@PostMapping("/get-locationBasedVehicles")
	public Map<String, Object> getLocationBasedVehicles(@RequestBody BaseFilter baseFilter) {
		Map<String, Object> res = vehicleInventoryService.getLocationBasedVehicles(baseFilter);
		return res;
	}

	@CrossOrigin
	@PostMapping("/get-branchBasedVehicles")
	public Map<String, Object> getBranchBasedVehicles(@RequestBody BaseFilter baseFilter) {
		Map<String, Object> res = vehicleInventoryService.getBranchBasedVehicles(baseFilter);

		return res;
	}

	@CrossOrigin
	@PostMapping("/get-modelBasedVehicles")
	public Map<String, Object> getModelBasedVehicles(@RequestBody BaseFilter baseFilter) {
		Map<String, Object> res = vehicleInventoryService.getModelBasedVehicles(baseFilter);

		return res;
	}

	@CrossOrigin
	@PostMapping("/get-varientBasedVehicles")
	public Map<String, Object> getVarientBased(@RequestBody BaseFilter baseFilter) {
		Map<String, Object> res = vehicleInventoryService.getVarientBased(baseFilter);

		return res;
	}

}
