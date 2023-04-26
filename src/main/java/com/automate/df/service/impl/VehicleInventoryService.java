package com.automate.df.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.automate.df.dao.BranchRepo;
import com.automate.df.dao.VehicleIndentRepo;
import com.automate.df.dao.VehicleInventoryHistoryRepo;
import com.automate.df.dao.VehicleInventoryRepo;
import com.automate.df.entity.sales.VehicleIndents;
import com.automate.df.entity.sales.VehicleInventory;
import com.automate.df.entity.sales.VehicleInventoryHistory;
import com.automate.df.model.BaseFilter;
import com.automate.df.model.FuelResponce;
import com.automate.df.model.InventoryResponce;
import com.automate.df.model.ResponceEntity;
import com.automate.df.util.CustomSpecification;
import com.automate.df.util.Utils;

@Service
public class VehicleInventoryService {
	@Autowired
	private VehicleInventoryRepo vehicleInventoryRepo;
	@Autowired
	private VehicleInventoryHistoryRepo vehicleInventoryHistoryRepo;
	@Autowired
	private VehicleIndentRepo vehicleIndentRepo;

	public InventoryResponce saveInventory(List<VehicleInventory> vehicleInventory) {
		InventoryResponce responce = new InventoryResponce();

		List<VehicleInventory> dublicateresponce = new ArrayList<>();

		for (VehicleInventory single : vehicleInventory) {

			VehicleInventory isExist = vehicleInventoryRepo.getRecordByVin(single.getDmsOrganization().getOrgId(),
					single.getVinNumber());

			if (isExist == null) {
//				String locationName=vehicleInventoryRepo.getLocation(single.getDmsbranch().getBranchId());
//				single.setLocation(locationName);
				int branch=branchRepo.getBranch(single.getStockyardBranchId());
				single.setStockyardBranchId(branch);
				vehicleInventoryRepo.save(single);
				vehicleInventoryHistoryRepo.save(entityToEntity(single));
			} else {
				dublicateresponce.add(single);
			}
		}

		if (dublicateresponce.isEmpty()) {
			responce.setErrorMessage("All records are succussfully saved");
			responce.setError(false);
			responce.setSuccess(true);
			return responce;
		} else {
			ResponceEntity entity = new ResponceEntity();
			entity.setInventory(dublicateresponce);
			responce.setError(true);
			responce.setSuccess(false);
			responce.setErrorMessage("Some records have dublicate vin numbers");
			responce.setDmsEntity(entity);
			return responce;
		}

	}
	
	public VehicleInventoryHistory entityToEntity(VehicleInventory req) {
		VehicleInventoryHistory responce=new VehicleInventoryHistory();
		responce.setVinNumber(req.getVinNumber());
		responce.setVehicleReceivedDate(req.getVehicleReceivedDate());
		responce.setVarientId(req.getVarientId());
		responce.setVariant(req.getVariant());
		responce.setUtgst(req.getUtgst());
		responce.setTransmission(req.getTransmission());
		responce.setTcs(req.getTcs());
		responce.setStockyardLocationId(req.getStockyardLocationId());
		responce.setStockyardLocation(req.getStockyardLocation());
		responce.setStockyardBranchName(req.getStockyardBranchName());
		responce.setStockyardBranchId(req.getStockyardBranchId());
		responce.setStatus(req.getStatus());
		responce.setStage(req.getStage());
		responce.setSgst(req.getSgst());
		responce.setRemarks(req.getRemarks());
		responce.setPurchaseDate(req.getPurchaseDate());
		responce.setModifiedDatetime(req.getModifiedDatetime());
		responce.setModelId(req.getModelId());
		responce.setModel(req.getModel());
		responce.setMake(req.getMake());
		responce.setLocation(req.getLocation());
		responce.setKeyNo(req.getKeyNo());
		responce.setInvoicePrice(req.getInvoicePrice());
		responce.setInvoiceNumber(req.getInvoiceNumber());
		responce.setInvoiceDocument(req.getInvoiceDocument());
		responce.setIgst(req.getIgst());
		responce.setHsnNumber(req.getHsnNumber());
		responce.setGstNumber(req.getGstNumber());
		responce.setFuel(req.getFuel());
		responce.setExShowroomPrice(req.getExShowroomPrice());
		responce.setEngineno(req.getEngineno());
		responce.setDmsOrganization(req.getDmsOrganization());
		responce.setDmsbranch(req.getDmsbranch());
		responce.setDeallocationDate(req.getDeallocationDate());
		responce.setCreatedDatetime(req.getCreatedDatetime());
		responce.setColour(req.getColour());
		responce.setChassisNo(req.getChassisNo());
		responce.setCgst(req.getCgst());
		responce.setCess(req.getCess());
		responce.setAllotedDate(req.getAllotedDate());
		responce.setAlloted(req.getAlloted());
		responce.setStateType(req.getStateType());
		responce.setGstRate(req.getGstRate());
		responce.setEWayBill(req.getEWayBill());
		responce.setPurchasedFrom(req.getPurchasedFrom());
		
		return responce;
		
	}
	
	public VehicleInventory entityToEntit(VehicleInventory req) {
		
		VehicleInventory responce=new VehicleInventory();
		responce.setId(req.getId());
		responce.setVinNumber(req.getVinNumber());
		responce.setVehicleReceivedDate(req.getVehicleReceivedDate());
		responce.setVarientId(req.getVarientId());
		responce.setVariant(req.getVariant());
		responce.setUtgst(req.getUtgst());
		responce.setTransmission(req.getTransmission());
		responce.setTcs(req.getTcs());
		responce.setStockyardLocationId(req.getStockyardLocationId());
		responce.setStockyardLocation(req.getStockyardLocation());
		responce.setStockyardBranchName(req.getStockyardBranchName());
		responce.setStockyardBranchId(req.getStockyardBranchId());
		responce.setStatus(req.getStatus());
		responce.setStage(req.getStage());
		responce.setSgst(req.getSgst());
		responce.setRemarks(req.getRemarks());
		responce.setPurchaseDate(req.getPurchaseDate());
		responce.setModifiedDatetime(req.getModifiedDatetime());
		responce.setModelId(req.getModelId());
		responce.setModel(req.getModel());
		responce.setMake(req.getMake());
		responce.setLocation(req.getLocation());
		responce.setKeyNo(req.getKeyNo());
		responce.setInvoicePrice(req.getInvoicePrice());
		responce.setInvoiceNumber(req.getInvoiceNumber());
		responce.setInvoiceDocument(req.getInvoiceDocument());
		responce.setIgst(req.getIgst());
		responce.setHsnNumber(req.getHsnNumber());
		responce.setGstNumber(req.getGstNumber());
		responce.setFuel(req.getFuel());
		responce.setExShowroomPrice(req.getExShowroomPrice());
		responce.setEngineno(req.getEngineno());
		responce.setDmsOrganization(req.getDmsOrganization());
		responce.setDmsbranch(req.getDmsbranch());
		responce.setDeallocationDate(req.getDeallocationDate());
		responce.setCreatedDatetime(req.getCreatedDatetime());
		responce.setColour(req.getColour());
		responce.setChassisNo(req.getChassisNo());
		responce.setCgst(req.getCgst());
		responce.setCess(req.getCess());
		responce.setAllotedDate(req.getAllotedDate());
		responce.setAlloted(req.getAlloted());
		responce.setStateType(req.getStateType());
		responce.setGstRate(req.getGstRate());
		responce.setEWayBill(req.getEWayBill());
		responce.setPurchasedFrom(req.getPurchasedFrom());
		return responce;
		
	}
	
	public InventoryResponce updateInventory(VehicleInventory req) {
		
		InventoryResponce result=new InventoryResponce();
		ResponceEntity res=new ResponceEntity();
		
		VehicleInventory isExist=vehicleInventoryRepo.get(req.getId());
		if (isExist != null) {
			VehicleInventory responce = vehicleInventoryRepo.save(entityToEntit(req));
			res.setInventorySingle(responce);
			vehicleInventoryHistoryRepo.save(entityToEntity(req));
		}else {
			result.setError(true);
			result.setErrorMessage("record not exist");
		}
		result.setDmsEntity(res);
		result.setError(false);
		result.setSuccess(true);
		result.setErrorMessage("Record Updated Succussfully");
		return result;
		
	}
	

	public InventoryResponce createIndent(VehicleIndents vehicleInventory) {
		InventoryResponce responce = new InventoryResponce();
		ResponceEntity responceEntity = new ResponceEntity();
		VehicleIndents isExist = vehicleIndentRepo.getIdent(vehicleInventory.getDmsOrganization().getOrgId(),
				vehicleInventory.getDmsbranch().getBranchId(), vehicleInventory.getVehicleInventory().getId());
		if (isExist == null) {
			VehicleIndents create = vehicleIndentRepo.save(vehicleInventory);
			responceEntity.setVehicleIndents(create);
			responce.setDmsEntity(responceEntity);
			responce.setSuccess(true);
			return responce;
		} else {
			responce.setSuccess(false);
			responce.setErrorMessage("dublicate request");

			return responce;
		}
	}
	
	@Autowired
    private BranchRepo branchRepo;

	public InventoryResponce allocateToDealer(List<VehicleInventory> vehicleInventory) {
		InventoryResponce inventoryResponce = new InventoryResponce();
		if (!vehicleInventory.isEmpty()) {
			for (VehicleInventory single : vehicleInventory) {
				int branch=branchRepo.getBranch(single.getDmsbranch().getBranchId());
				int al = vehicleInventoryRepo.allocate(branch,single.getDmsbranch().getName(),single.getLocation(),single.getLocationId() ,single.getId());

			}
			inventoryResponce.setErrorMessage("Allocated Succussfully");
			inventoryResponce.setError(true);
			inventoryResponce.setSuccess(false);
			return inventoryResponce;
		} else {
			inventoryResponce.setErrorMessage("Something Went Wrong");
			inventoryResponce.setError(true);
			inventoryResponce.setSuccess(false);
			return inventoryResponce;
		}

	}

	public Map<String, Object> getInventoryForMystock(BaseFilter baseFilter) {

		if (baseFilter.getMinAge() != null || baseFilter.getMaxAge() != null || baseFilter.getLocationId() != null
				|| baseFilter.getBranchId() != null) {

			Specification<VehicleInventory> specification = null;
			if (Utils.isNotEmpty(baseFilter.getOrgId())) {
				BigInteger org = new BigInteger(baseFilter.getOrgId());
				specification = Specification.where(CustomSpecification.orgId(org));
			}

			if (Utils.isNotEmpty(baseFilter.getBranchId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getBranchName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification.where(CustomSpecification.attribute("model", baseFilter.getModel()));
				} else {
					specification = specification.and(CustomSpecification.attribute("model", baseFilter.getModel()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				}
			}

			List<VehicleInventory> res = vehicleInventoryRepo.findAll(specification);

			List<VehicleInventory> ageingResponce = new ArrayList<>();

			if (baseFilter.getMinAge() != null && baseFilter.getMaxAge() != null) {
				for (VehicleInventory single : res) {
					int ageing = vehicleInventoryRepo.ageing(single.getId());
					single.setAgeing(ageing);

					if (ageing >= Integer.valueOf(baseFilter.getMinAge())
							&& ageing <= Integer.valueOf(baseFilter.getMaxAge())) {
						ageingResponce.add(single);
					}
				}
			} else {
				ageingResponce.addAll(res);
			}

			HashMap<String, Object> resultMap = new HashMap<>();

			List<ResponceObject> locationWiseAvailableData = new ArrayList<>();

			List<String> locationNames = ageingResponce.stream().map(VehicleInventory::getStockyardLocation)
					.collect(Collectors.toList());

			for (String single : locationNames) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Available"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				ResponceObject responce = new ResponceObject();
				responce.setCount(count.size());
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(Double.toString(prize));

				locationWiseAvailableData.add(responce);

			}
			resultMap.put("locationWise_available_count", locationWiseAvailableData);

			List<ResponceObject> locationWiseIntransitData = new ArrayList<>();

			for (String single : locationNames) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Intransit"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				ResponceObject responce = new ResponceObject();
				responce.setCount(count.size());
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(Double.toString(prize));

				locationWiseIntransitData.add(responce);

			}
			resultMap.put("locationWise_intrsnsit_count", locationWiseIntransitData);

			return resultMap;

		} else {

			HashMap<String, Object> resultMap = new HashMap<>();

			List<ResponceObject> locationWiseAvailableData = new ArrayList<>();

			List<String> locationNames = vehicleInventoryRepo.getLocations(Integer.valueOf(baseFilter.getOrgId()));
			for (String single : locationNames) {
				long count = vehicleInventoryRepo.getLocationCount(Integer.valueOf(baseFilter.getOrgId()), single,
						"Available");
				String prize = vehicleInventoryRepo.getLocationSumStockPrize(Integer.valueOf(baseFilter.getOrgId()),
						single, "Available");
				ResponceObject responce = new ResponceObject();
				responce.setCount(count);
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(prize);

				locationWiseAvailableData.add(responce);

			}
			resultMap.put("locationWise_available_count", locationWiseAvailableData);

			List<ResponceObject> locationWiseIntransitData = new ArrayList<>();

			for (String single : locationNames) {
				long count = vehicleInventoryRepo.getLocationCount(Integer.valueOf(baseFilter.getOrgId()), single,
						"Intransit");
				String prize = vehicleInventoryRepo.getLocationSumStockPrize(Integer.valueOf(baseFilter.getOrgId()),
						single, "Intransit");
				ResponceObject responce = new ResponceObject();
				responce.setCount(count);
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(prize);

				locationWiseIntransitData.add(responce);

			}
			resultMap.put("locationWise_intrsnsit_count", locationWiseIntransitData);
			return resultMap;
		}

	}

	public Map<String, Object> getLocationBasedVehicles(BaseFilter baseFilter) {

		if (baseFilter.getMinAge() != null || baseFilter.getMaxAge() != null || baseFilter.getLocationId() != null
			|| baseFilter.getBranchId() != null) {

			Specification<VehicleInventory> specification = null;
			if (Utils.isNotEmpty(baseFilter.getOrgId())) {
				BigInteger org = new BigInteger(baseFilter.getOrgId());
				specification = Specification.where(CustomSpecification.orgId(org));
			}

			if (Utils.isNotEmpty(baseFilter.getBranchId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getBranchName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification.where(CustomSpecification.attribute("model", baseFilter.getModel()));
				} else {
					specification = specification.and(CustomSpecification.attribute("model", baseFilter.getModel()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				}
			}

			List<VehicleInventory> res = vehicleInventoryRepo.findAll(specification);

			List<VehicleInventory> ageingResponce = new ArrayList<>();

			if (baseFilter.getMinAge() != null && baseFilter.getMaxAge() != null) {
				for (VehicleInventory single : res) {
					int ageing = vehicleInventoryRepo.ageing(single.getId());
					single.setAgeing(ageing);

					if (ageing >= Integer.valueOf(baseFilter.getMinAge())
							&& ageing <= Integer.valueOf(baseFilter.getMaxAge())) {
						ageingResponce.add(single);
					}
				}
			} else {
				ageingResponce.addAll(res);
			}

			HashMap<String, Object> resultMap = new HashMap<>();

			List<ResponceObject> locationWiseAvailableData = new ArrayList<>();

			List<String> branches = ageingResponce.stream().map(VehicleInventory::getStockyardBranchName)
					.collect(Collectors.toList());

			for (String single : branches) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Available"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				ResponceObject responce = new ResponceObject();
				responce.setCount(count.size());
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(Double.toString(prize));

				locationWiseAvailableData.add(responce);

			}
			resultMap.put("branchWise_available_count", locationWiseAvailableData);

			List<ResponceObject> locationWiseIntransitData = new ArrayList<>();

			for (String single : branches) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Intransit"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				ResponceObject responce = new ResponceObject();
				responce.setCount(count.size());
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(Double.toString(prize));

				locationWiseIntransitData.add(responce);

			}
			resultMap.put("branchWise_intransit_count", locationWiseIntransitData);

			return resultMap;

		}

		else {

			HashMap<String, Object> resultMap = new HashMap<>();

			List<ResponceObject> locationWiseAvailableData = new ArrayList<>();

			List<String> branches = vehicleInventoryRepo.getYardBranches(Integer.valueOf(baseFilter.getOrgId()),
					baseFilter.getLocationName());
			for (String single : branches) {
				long count = vehicleInventoryRepo.getBranches(Integer.valueOf(baseFilter.getOrgId()), single,
						"Available");
				String prize = vehicleInventoryRepo.getBranchesSumPrize(Integer.valueOf(baseFilter.getOrgId()), single,
						"Available");
				ResponceObject responce = new ResponceObject();
				responce.setCount(count);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setName(single);
				responce.setStockValue(prize);

				locationWiseAvailableData.add(responce);

			}
			resultMap.put("branchWise_available_count", locationWiseAvailableData);

			List<ResponceObject> locationWiseIntransitData = new ArrayList<>();

			for (String single : branches) {
				long count = vehicleInventoryRepo.getBranches(Integer.valueOf(baseFilter.getOrgId()), single,
						"Intransit");
				String prize = vehicleInventoryRepo.getBranchesSumPrize(Integer.valueOf(baseFilter.getOrgId()), single,
						"Intransit");
				ResponceObject responce = new ResponceObject();
				responce.setCount(count);
				responce.setName(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(prize);

				locationWiseIntransitData.add(responce);

			}
			resultMap.put("branchWise_intransit_count", locationWiseIntransitData);
			return resultMap;
		}
	}

	public Map<String, Object> getBranchBasedVehicles(BaseFilter baseFilter) {

		if (baseFilter.getMinAge() != null || baseFilter.getMaxAge() != null || baseFilter.getLocationId() != null
			 || baseFilter.getBranchId() != null) {

			Specification<VehicleInventory> specification = null;
			if (Utils.isNotEmpty(baseFilter.getOrgId())) {
				BigInteger org = new BigInteger(baseFilter.getOrgId());
				specification = Specification.where(CustomSpecification.orgId(org));
			}

			if (Utils.isNotEmpty(baseFilter.getBranchId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getBranchName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification.where(CustomSpecification.attribute("model", baseFilter.getModel()));
				} else {
					specification = specification.and(CustomSpecification.attribute("model", baseFilter.getModel()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				}
			}

			List<VehicleInventory> res = vehicleInventoryRepo.findAll(specification);

			List<VehicleInventory> ageingResponce = new ArrayList<>();

			if (baseFilter.getMinAge() != null && baseFilter.getMaxAge() != null) {
				for (VehicleInventory single : res) {
					int ageing = vehicleInventoryRepo.ageing(single.getId());
					single.setAgeing(ageing);

					if (ageing >= Integer.valueOf(baseFilter.getMinAge())
							&& ageing <= Integer.valueOf(baseFilter.getMaxAge())) {
						ageingResponce.add(single);
					}
				}
			} else {
				ageingResponce.addAll(res);
			}

			HashMap<String, Object> resultMap = new HashMap<>();

			List<FuelResponce> locationWiseAvailableData = new ArrayList<>();

			List<String> model = ageingResponce.stream().map(VehicleInventory::getModel).collect(Collectors.toList());

			for (String single : model) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Available"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setModel(single);
//				responce.setModelId(count.get(0).getModelId());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
//				responce.setBranchId(count.get(0).getStockyardBranchId());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(Double.toString(prize));
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				locationWiseAvailableData.add(responce);

			}
			resultMap.put("modelWise_available_stock", locationWiseAvailableData);

			List<FuelResponce> locationWiseIntransitData = new ArrayList<>();

			for (String single : model) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Intransit"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setModel(single);
//				responce.setModelId(count.get(0).getModelId());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
//				responce.setBranchId(count.get(0).getStockyardBranchId());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(Double.toString(prize));
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				locationWiseIntransitData.add(responce);

			}
			resultMap.put("modelWise_intransit_stock", locationWiseIntransitData);

			return resultMap;

		} else {

			HashMap<String, Object> resultMap = new HashMap<>();

			List<String> models = vehicleInventoryRepo.getModels(Integer.valueOf(baseFilter.getOrgId()),
					baseFilter.getBranchName());

			List<FuelResponce> modelWiseAvailableData = new ArrayList<>();

			for (String single : models) {
				List<VehicleInventory> count = vehicleInventoryRepo.getModelcount(single,
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Available");
				String prize = vehicleInventoryRepo.getModelPrize(single, Integer.valueOf(baseFilter.getOrgId()),
						baseFilter.getBranchName(), "Available");
				FuelResponce responce = new FuelResponce();
				responce.setModel(single);
//			responce.setModelId(count.get(0).getModelId());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
//			responce.setBranchId(count.get(0).getStockyardBranchId());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(prize);
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());

				modelWiseAvailableData.add(responce);

			}
			resultMap.put("modelWise_available_stock", modelWiseAvailableData);

			List<FuelResponce> modelWiseIntrsnsitData = new ArrayList<>();

			for (String single : models) {
				List<VehicleInventory> count = vehicleInventoryRepo.getModelcount(single,
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Intransit");
				String prize = vehicleInventoryRepo.getModelPrize(single, Integer.valueOf(baseFilter.getOrgId()),
						baseFilter.getBranchName(), "Intransit");
				FuelResponce responce = new FuelResponce();
				responce.setModel(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setStockValue(prize);
//			responce.setModelId(count.get(0).getModelId());
//			responce.setBranchId(count.get(0).getStockyardBranchId());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				modelWiseIntrsnsitData.add(responce);

			}
			resultMap.put("modelWise_intransit_stock", modelWiseIntrsnsitData);
			return resultMap;
		}
	}

	public Map<String, Object> getModelBasedVehicles(BaseFilter baseFilter) {

		if (baseFilter.getMinAge() != null || baseFilter.getMaxAge() != null || baseFilter.getLocationId() != null
				|| baseFilter.getBranchId() != null) {

			Specification<VehicleInventory> specification = null;
			if (Utils.isNotEmpty(baseFilter.getOrgId())) {
				BigInteger org = new BigInteger(baseFilter.getOrgId());
				specification = Specification.where(CustomSpecification.orgId(org));
			}

			if (Utils.isNotEmpty(baseFilter.getBranchId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getBranchName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification.where(CustomSpecification.attribute("model", baseFilter.getModel()));
				} else {
					specification = specification.and(CustomSpecification.attribute("model", baseFilter.getModel()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				}
			}

			List<VehicleInventory> res = vehicleInventoryRepo.findAll(specification);

			List<VehicleInventory> ageingResponce = new ArrayList<>();

			if (baseFilter.getMinAge() != null && baseFilter.getMaxAge() != null) {
				for (VehicleInventory single : res) {
					int ageing = vehicleInventoryRepo.ageing(single.getId());
					single.setAgeing(ageing);

					if (ageing >= Integer.valueOf(baseFilter.getMinAge())
							&& ageing <= Integer.valueOf(baseFilter.getMaxAge())) {
						ageingResponce.add(single);
					}
				}
			} else {
				ageingResponce.addAll(res);
			}

			HashMap<String, Object> resultMap = new HashMap<>();

			List<FuelResponce> locationWiseAvailableData = new ArrayList<>();

			List<String> varients = ageingResponce.stream().map(VehicleInventory::getVariant)
					.collect(Collectors.toList());

			for (String single : varients) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x ->  x.getStatus().equalsIgnoreCase("Available"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setVarient(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				if(baseFilter.getBranchName() != null) {
					responce.setBranchName(baseFilter.getBranchName());
				}else {
				
				List<String> branchname=count.stream()
						.map(VehicleInventory::getStockyardBranchName)
						.collect(Collectors.toList());
				responce.setBranchName(branchname.get(0));
				}
				responce.setStockValue(Double.toString(prize));
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				locationWiseAvailableData.add(responce);

			}
			resultMap.put("varientWise_available_stock", locationWiseAvailableData);

			List<FuelResponce> locationWiseIntransitData = new ArrayList<>();

			for (String single : varients) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x -> x.getStatus().equalsIgnoreCase("Intransit")).collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setVarient(single);
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				if(baseFilter.getBranchName() != null) {
					responce.setBranchName(baseFilter.getBranchName());
				}else {
				
				List<String> branchname=count.stream()
						.map(VehicleInventory::getStockyardBranchName)
						.collect(Collectors.toList());
				responce.setBranchName(branchname.get(0));
				}
				responce.setStockValue(Double.toString(prize));
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				locationWiseIntransitData.add(responce);

			}
			resultMap.put("varientWise_intransit_stock", locationWiseIntransitData);

			return resultMap;

		} else {

			HashMap<String, Object> resultMap = new HashMap<>();

			List<String> varients = vehicleInventoryRepo.getVarientsBymodel(Integer.valueOf(baseFilter.getOrgId()),
					baseFilter.getBranchName(), baseFilter.getModel());

			List<FuelResponce> modelWiseAvailableData = new ArrayList<>();

			for (String single : varients) {
				List<VehicleInventory> count = vehicleInventoryRepo.getVarientcount(baseFilter.getModel(),
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Available", single);
				String prize = vehicleInventoryRepo.getVarientPrize(baseFilter.getModel(),
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Available", single);
				FuelResponce responce = new FuelResponce();
				responce.setVarient(single);
				responce.setModel(baseFilter.getModel());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(prize);
//			responce.setVarientId(count.get(0).getVarientId());
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());

				modelWiseAvailableData.add(responce);

			}
			resultMap.put("varientWise_available_stock", modelWiseAvailableData);

			List<FuelResponce> modelWiseIntrsnsitData = new ArrayList<>();

			for (String single : varients) {
				List<VehicleInventory> count = vehicleInventoryRepo.getVarientcount(baseFilter.getModel(),
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Intransit", single);
				String prize = vehicleInventoryRepo.getVarientPrize(baseFilter.getModel(),
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), "Intransit", single);
				FuelResponce responce = new FuelResponce();
				responce.setVarient(single);
				responce.setModel(baseFilter.getModel());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(prize);
//			responce.setVarientId(count.get(0).getVarientId());
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				modelWiseIntrsnsitData.add(responce);

			}
			resultMap.put("varientWise_intransit_stock", modelWiseIntrsnsitData);
			return resultMap;
		}
	}

	public Map<String, Object> getVarientBased(BaseFilter baseFilter) {

		if (baseFilter.getMinAge() != null || baseFilter.getMaxAge() != null || baseFilter.getLocationId() != null
			 || baseFilter.getBranchId() != null) {

			Specification<VehicleInventory> specification = null;
			if (Utils.isNotEmpty(baseFilter.getOrgId())) {
				BigInteger org = new BigInteger(baseFilter.getOrgId());
				specification = Specification.where(CustomSpecification.orgId(org));
			}

			if (Utils.isNotEmpty(baseFilter.getBranchId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchId", baseFilter.getBranchId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getBranchName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardBranchName", baseFilter.getBranchName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationId())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocationId", baseFilter.getLocationId()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("stockyardLocation", baseFilter.getLocationName()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification.where(CustomSpecification.attribute("model", baseFilter.getModel()));
				} else {
					specification = specification.and(CustomSpecification.attribute("model", baseFilter.getModel()));
				}
			}

			if (Utils.isNotEmpty(baseFilter.getLocationName())) {
				if (Utils.isEmpty(specification)) {
					specification = Specification
							.where(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				} else {
					specification = specification
							.and(CustomSpecification.attribute("variant", baseFilter.getVarient()));
				}
			}

			List<VehicleInventory> res = vehicleInventoryRepo.findAll(specification);

			List<VehicleInventory> ageingResponce = new ArrayList<>();

			if (baseFilter.getMinAge() != null && baseFilter.getMaxAge() != null) {
				for (VehicleInventory single : res) {
					int ageing = vehicleInventoryRepo.ageing(single.getId());
					single.setAgeing(ageing);

					if (ageing >= Integer.valueOf(baseFilter.getMinAge())
							&& ageing <= Integer.valueOf(baseFilter.getMaxAge())) {
						ageingResponce.add(single);
					}
				}
			} else {
				ageingResponce.addAll(res);
			}

			HashMap<String, Object> resultMap = new HashMap<>();

			List<FuelResponce> locationWiseAvailableData = new ArrayList<>();

			List<String> colours = ageingResponce.stream().map(VehicleInventory::getColour)
					.collect(Collectors.toList());

			for (String single : colours) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x -> x.getStatus().equalsIgnoreCase("Available")).collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				responce.setColourName(single);
				if(baseFilter.getVarient() != null) {
					responce.setVarient(baseFilter.getVarient());
				}else {
				
				List<String> varientn=count.stream()
						.map(VehicleInventory::getVariant)
						.collect(Collectors.toList());
				responce.setVarient(varientn.get(0));
				}
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setModel(baseFilter.getModel());
				if(baseFilter.getBranchName() != null) {
					responce.setBranchName(baseFilter.getBranchName());
				}else {
				
				List<String> branchname=count.stream()
						.map(VehicleInventory::getStockyardBranchName)
						.collect(Collectors.toList());
				responce.setBranchName(branchname.get(0));
				}
				responce.setStockValue(Double.toString(prize));
				locationWiseAvailableData.add(responce);

			}
			resultMap.put("colourWise_available_stock", locationWiseAvailableData);

			List<FuelResponce> locationWiseIntransitData = new ArrayList<>();

			for (String single : colours) {
				List<VehicleInventory> count = ageingResponce.stream()
						.filter(x -> x.getStatus().equalsIgnoreCase("Intransit"))
						.collect(Collectors.toList());
				double prize = count.stream().mapToDouble(VehicleInventory::getExShowroomPrice).sum();
				FuelResponce responce = new FuelResponce();
				responce.setPetrolCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(count.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				responce.setColourName(single);
				responce.setVarient(baseFilter.getVarient());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setModel(baseFilter.getModel());
				if(baseFilter.getBranchName() != null) {
					responce.setBranchName(baseFilter.getBranchName());
				}else {
				
				List<String> branchname=count.stream()
						.map(VehicleInventory::getStockyardBranchName)
						.collect(Collectors.toList());
				responce.setBranchName(branchname.get(0));
				}
				responce.setStockValue(Double.toString(prize));
				locationWiseIntransitData.add(responce);

			}
			resultMap.put("colourWise_intransit_stock", locationWiseIntransitData);

			return resultMap;

		}

		else {
			List<String> colourData = vehicleInventoryRepo.getColourWiseData(Integer.valueOf(baseFilter.getOrgId()),
					baseFilter.getBranchName(), baseFilter.getModel(), baseFilter.getVarient());

			HashMap<String, Object> resultMap = new HashMap<>();
			List<FuelResponce> colourWiseAvailableData = new ArrayList<>();
			for (String single : colourData) {
				List<VehicleInventory> dataSet = vehicleInventoryRepo.getByColour(
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), baseFilter.getModel(),
						baseFilter.getVarient(), single, "Available");
				String prize = vehicleInventoryRepo.getByColourPrize(Integer.valueOf(baseFilter.getOrgId()),
						baseFilter.getBranchName(), baseFilter.getModel(), baseFilter.getVarient(), single,
						"Available");

				FuelResponce responce = new FuelResponce();
				responce.setPetrolCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				responce.setColourName(single);
				responce.setVarient(baseFilter.getVarient());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setModel(baseFilter.getModel());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(prize);

				colourWiseAvailableData.add(responce);
			}

			resultMap.put("colourWise_available_stock", colourWiseAvailableData);

			List<FuelResponce> colourWiseIntransitData = new ArrayList<>();
			for (String single : colourData) {
				List<VehicleInventory> dataSet = vehicleInventoryRepo.getByColour(
						Integer.valueOf(baseFilter.getOrgId()), baseFilter.getBranchName(), baseFilter.getModel(),
						baseFilter.getVarient(), single, "Intransit");
				String prize = vehicleInventoryRepo.getByColourPrize(Integer.valueOf(baseFilter.getOrgId()),
						baseFilter.getBranchName(), baseFilter.getModel(), baseFilter.getVarient(), single,
						"Intransit");

				FuelResponce responce = new FuelResponce();
				responce.setPetrolCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("Petrol")).count());
				responce.setDieselCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("Diesel")).count());
				responce.setElectricCount(dataSet.stream().filter(x -> x.getFuel().equalsIgnoreCase("EV")).count());
				responce.setColourName(single);
				responce.setVarient(baseFilter.getVarient());
				responce.setOrgId(Integer.valueOf(baseFilter.getOrgId()));
				responce.setModel(baseFilter.getModel());
				responce.setBranchName(baseFilter.getBranchName());
				responce.setStockValue(prize);

				colourWiseIntransitData.add(responce);
			}

			resultMap.put("colourWise_intransit_stock", colourWiseIntransitData);
			return resultMap;

		}
	}

}
