package com.automate.df.entity.sales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "org_wizard_setup")
public class WizardEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "wizard_name")
	String wizName;

	@Column(name = "wizard_description")
	String wizDesc;

	@Column(name = "api_url")
	String apiUrl;

	@Column(name = "page_identifier")
	String pageId;

	@Column(name = "s3_url_templet")
	String s3UrlTempate;

	@Column(name = "process_id")
	Integer processId;

	@Column(name = "org_id")
	String orgId;

	@Column(name = "is_mandatory")
	String isMandatory;
	
	String tableName;

}
