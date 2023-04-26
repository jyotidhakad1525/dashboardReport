package com.automate.df.model;



import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WizardReq {
	
	String tableName;
	String primaryKeyColumnName;
	String primaryKeyColumnValue;
	String bulkUploadedColumnValue;
	String bulkUploadColName;

}
