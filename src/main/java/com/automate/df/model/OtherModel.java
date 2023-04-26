package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OtherModel   {
	private String sno;
    private Integer id;	
	private String otherMaker;
	private String otherModel;
	private Integer othermakerId;
	private String status;
	String orgId;
	String bulkUploadId;
	private String createdBy;
	private String updatedBy;
	private String createdAt;
	private String updatedAt;

}