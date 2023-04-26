package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtherMaker  {
	private String sno;
    private int id;	
	private String otherMaker;
	private String vehicleSegment;
	private String status;
	String orgId;
    String bulkUploadId;
	private String createdBy;
	private String updatedBy;
	private String createdAt;
	private String updatedAt;


}