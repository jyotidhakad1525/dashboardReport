package com.automate.df.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
@Table(name = "dms_branch")
@NamedQuery(name = "BranchEntity.findAll", query = "SELECT d FROM BranchEntity d")
public class BranchEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "branch_id")
	private int branchId;

	@Column(name = "branch_type")
	private String branchType;

	private String name;

	private String status;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private OrgEntity dmsOrganization;

	@Column(name = "org_map_id")
	private int orgMapId;

	@Column(name="active")
	private String active;


}
