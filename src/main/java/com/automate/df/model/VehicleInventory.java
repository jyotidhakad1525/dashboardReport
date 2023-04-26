package com.automate.df.model;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VehicleInventory {

	private String sno;
	private int id;
	String model;
	String variant;
	String colour;
	String fuel;
	String orgId;
	String bulkUploadId;
	String transmission;
	String vinNumber;
	Date purchaseDate;
	String ageing;
	String status;
	String createdBy;
	String updatedBy;
	String createAt;
	String updatedAt;

}
