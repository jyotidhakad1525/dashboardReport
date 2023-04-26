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
@Table(name = "vehicle_indents")
public class VehicleIndents implements Serializable {
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
	
	@Column(name = "status")
	private String status; 
	
	@ManyToOne
	@JoinColumn(name = "vehicle_inventory_id")
	private VehicleInventory vehicleInventory;
	
	@Column(name = "created_date")
	private Date created_date;
	
	@Column(name = "updated_date")
	private Date updated_date;

}
