package com.automate.df.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadModel {
	    private Integer empId;
	    private Integer branchId;
	    private Integer orgid; 
}
