package com.automate.df.entity.sales;

import java.io.Serializable;
import java.math.BigInteger;

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
@Table(name = "dms_organization")
public class DmsOrganizationWizard implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "org_id")
	int orgId;

	String brand;

	@Column(name = "domain_name")
	String domainName;

	String email;

	@Column(name = "logo_big_url")
	String logoBigUrl;

	@Column(name = "logo_small_url")
	String logoSmallUrl;

	BigInteger mobile;

	@Column(name = "cin")
	String cinNumber;

	String name;

	BigInteger phone;

	String status;

	String website;

	String url;

	@Column(name = "s3_name")
	String s3Name;

	@Column(name = "document_url")
	String documentUrl;

	@Column(name = "bulk_upload_id")
	String bulkUploadId;

	@Column(name = "is_bulk_upload")
	String isBulkUpload;
	
	@Column(name = "qr_code")
	String qrCode;

}
