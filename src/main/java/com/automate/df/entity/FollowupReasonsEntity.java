package com.automate.df.entity;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "followup_reasons")
public class FollowupReasonsEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "stage_name")
	private String stageName;

	@Column(name = "reason")
	private String reason;

	@Column(name = "status")
	private String status;

	@Column(name = "org_id")
	private String orgId;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_at")
	private String createdAt;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_at")
	private String updatedAt;

	@Column(name = "is_bulk_upload")
	private String isBulkUpload;

	@Column(name = "bulk_upload_id")
	private String bulkUploadId;
}
