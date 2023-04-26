package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BankFinancier {

	private String sno;
	private String bulkUploadId;
	private String bankName;
	private String bankType;
	private String createdDatetime;
	private String status;
	String orgId;
	String createdBy;
	String updatedBy;
	String createAt;
	String updatedAt;

}
