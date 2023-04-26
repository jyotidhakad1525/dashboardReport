package com.automate.df.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseFilter {
	
    private String orgId;
    private String branchId;
    private String branchName;
    private String locationName;
    private String locationId;
    private String minAge;
    private String maxAge;
    private String model;
    private String varient;
 
}
