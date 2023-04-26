package com.automate.df.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author shubham9/1/23
 *
 */

@Table(name = "union_territory_tax")
@Entity
@Data
@NoArgsConstructor
public class UnionTerritoryTaxEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "unit")
	private String unit;

	@Column(name = "utgst")
	private String utgst;

	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "total")
	private String total;

	@Column(name = "bulk_upload_id")
	private String bulkUploadId;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "status")
	private String status;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "created_by")
	private String credatedBy;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "fuel_type")
	private String fuelType;
}
