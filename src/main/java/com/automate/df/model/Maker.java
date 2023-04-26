package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Maker {

	private String sno;
	private int id;
	private String make;
	private String vehicleSegment;
	private String status;
	String orgId;
	private String bulkUploadId;
	private String createdBy;
	private String updatedBy;
	private String createdAt;
	private String updatedAt;

}
