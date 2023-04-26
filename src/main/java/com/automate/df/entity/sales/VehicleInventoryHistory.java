package com.automate.df.entity.sales;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.automate.df.entity.BranchEntity;
import com.automate.df.entity.OrgEntity;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
@Table(name = "vehicles_inventory_history")
public class VehicleInventoryHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id; 
	
	@ManyToOne
	@JoinColumn(name = "org_id")
	private OrgEntity dmsOrganization; 
	
	@ManyToOne
	@JoinColumn(name = "branch_id")
	private BranchEntity dmsbranch;
	
	@Column(name = "chassis_no")
	private String chassisNo;
	
	@Column(name = "engineno")
	private String engineno;
	
	@Column(name = "vin_number")
	private String vinNumber;
	
	@Column(name = "key_no")
	private String keyNo;
	
	@Column(name = "hsn_number")
	private String hsnNumber;
	
	@Column(name = "make")
	private String make;
	
	@Column(name = "model")
	private String model;
	
	@Column(name = "vehicle_id")
	private int modelId;
	
	@Column(name = "variant")
	private String variant;
	
	@Column(name = "varient_id")
	private int varientId;
	
	@Column(name = "fuel")
	private String fuel;
	
	@Column(name = "transmission")
	private String transmission;
	
	@Column(name = "colour")
	private String colour;
	
	@Column(name = "alloted")  
	private String alloted;
	
	@Column(name = "alloted_date") 
	private Date allotedDate;
	
	@Column(name = "deallocation_date")  
	private Date deallocationDate;
	
	@Column(name = "created_datetime") 
	private Date createdDatetime;
	
	@Column(name = "modified_datetime")
	private Date modifiedDatetime;
	
	@Column(name = "lead_id") 
	private int leadId;
	
	@Column(name = "stage") 
	private String stage;
	
	@Column(name = "remarks") 
	private String remarks;
	
	@Column(name = "status")  
	private String status;
	
	@Column(name = "purchase_date")
	private Date purchaseDate;
	
	@Column(name = "ageing")
	private int ageing;
	
	@Column(name = "cgst") 
	private String cgst;
	
	@Column(name = "sgst")
	private String sgst;
	
	@Column(name = "igst")
	private String igst;
	
	@Column(name = "utgst")  
	private String utgst;
	
	@Column(name = "cess") 
	private String cess;
	
	@Column(name = "tcs")
	private String tcs;
	
	@Column(name = "invoice_price")
	private String invoicePrice;
	
	@Column(name = "invoice_number")
	private String invoiceNumber;
	
	@Column(name = "vehicle_received_date")
	private Date vehicleReceivedDate;
	
	@Column(name = "invoice_document")
	private String invoiceDocument;
	
	@Column(name = "gst_number")
	private String gstNumber;
	
	@Column(name = "ex_showroom_price")
	private double exShowroomPrice;
	
	@Column(name = "location")
	private String location;
	
	@Column(name = "location_id")
	private int locationId;
	
	@Column(name = "stockyard_branch_id")
	private int stockyardBranchId;
	
	@Column(name = "stockyard_branch_name")
	private String stockyardBranchName;
	
	@Column(name = "stockyard_location_id")
	private int stockyardLocationId;
	
	@Column(name = "stockyard_location")
	private String stockyardLocation;
	
	@Column(name = "purchased_from")
	private String purchasedFrom;
	
	@Column(name = "state_type")
	private String stateType;
	
	@Column(name = "gst_rate")
	private String gstRate;
	
	@Column(name = "e_way_bill")
	private String eWayBill;
	

}
