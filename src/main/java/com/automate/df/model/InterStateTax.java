package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class InterStateTax {

	private String sno;
	String unit;
	String igst;
	String cess;
	String total;
	String orgId;
	String bulkUploadId;
	String createdBy;
	String updatedBy;
	String status;
}
