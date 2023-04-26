package com.automate.df.model;

import lombok.Data;

@Data
public class FuelResponce {
	
	String model;
	int modelId;
	String varient;
	int varientId;
	String colourName;
	int colourId;
	int orgId;
	int branchId;
	String branchName;
	long petrolCount;
	long dieselCount;
	long electricCount;
	String stockValue;

}
