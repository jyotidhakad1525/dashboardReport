package com.automate.df.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.automate.df.entity.AutoSaveEntity;
import com.automate.df.entity.FollowupReasonsEntity;
import com.automate.df.entity.OtherMaker;
import com.automate.df.entity.OtherModel;
import com.automate.df.entity.sales.DmsOrganizationWizard;
import com.automate.df.entity.sales.WizardEntity;
import com.automate.df.entity.sales.employee.EmployeeEntity;
import com.automate.df.entity.sales.lead.DmsDeliveryCheckList;
import com.automate.df.entity.sales.lead.DmsInsurenceCompanyMd;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.AutoSave;
import com.automate.df.model.BulkUploadResponse;
import com.automate.df.model.ETVRequest;
import com.automate.df.model.QueryRequestV2;
import com.automate.df.model.WizardReq;

public interface DFReportService {

	public String generateDynamicQueryV2(QueryRequestV2 request) throws DynamicFormsServiceException;

	public AutoSaveEntity saveAutoSave(AutoSave req);

	public AutoSaveEntity updateAutoSsave(AutoSaveEntity req);

	public List<AutoSaveEntity> getAllAutoSave(String type, int pageNo, int sizes);

	public String deleteAutoSave(int id);

	String generateDropdownQueryV2(QueryRequestV2 request) throws DynamicFormsServiceException;

	public String getAutoSaveByUid(String uid) throws DynamicFormsServiceException;

	public Map<String, String> generateETVBRLReport(ETVRequest request) throws DynamicFormsServiceException;

	public String generateWizardView(WizardReq request) throws DynamicFormsServiceException;

	public List<WizardEntity> generateWizardViewPage(String orgId) throws DynamicFormsServiceException;
	
	public List<WizardEntity> generateWizardViewAllPage() throws DynamicFormsServiceException;

	public List<DmsOrganizationWizard> generateWizardOrgViewPage(String isBulkUpload) throws DynamicFormsServiceException;

	public List<DmsOrganizationWizard> getQrCode(int orgId) throws DynamicFormsServiceException;
	
	
	public	List<EmployeeEntity> getEmpPic(int empId) throws DynamicFormsServiceException;
	
	public List<FollowupReasonsEntity> getfollowupReasons(String orgId,String stageName) throws DynamicFormsServiceException;

	public BulkUploadResponse processBulkExcelForOtherMaker(MultipartFile bulkExcel,Integer empId,Integer orgId) throws Exception;
	
	public BulkUploadResponse processBulkExcelForOtherModel(MultipartFile bulkExcel,Integer empId,Integer orgId) throws Exception;
	
	public BulkUploadResponse processBulkExcelForFollowupReason(MultipartFile bulkExcel,Integer empId,Integer orgId) throws Exception;
	
	public BulkUploadResponse processBulkUploadForDeliveryCheckList(MultipartFile bulkExcel,Integer empId,Integer orgId) throws Exception;
	
	public BulkUploadResponse processBulkUploadForInsurenceCompanyName(MultipartFile bulkExcel,Integer empId,Integer orgId) throws Exception;
}
