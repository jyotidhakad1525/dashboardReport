package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Source {
	private String sno;
	String name;
	String description;
	String value;
	String status;
	String orgId;
	private String bulkUploadId;
	String createdBy;
	String updatedBy;
}

