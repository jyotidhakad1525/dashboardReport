package com.automate.df.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
import com.automate.df.exception.MyFileNotFoundException;
import com.automate.df.model.AutoSave;
import com.automate.df.model.BulkUploadModel;
import com.automate.df.model.BulkUploadResponse;
import com.automate.df.model.DropDownData;
import com.automate.df.model.ETVRequest;
import com.automate.df.model.QueryRequestV2;
import com.automate.df.model.WizardReq;
import com.automate.df.service.DFReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/dynamic-reports")
@CrossOrigin
@Api(value = "/dynamic-reports", tags = "Dynamic Reports API", description = "Dynamic Reports")
@Slf4j
public class DFReportController {

	@Autowired
	Environment env;

	@Autowired
	DFReportService dFReportService;

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "QueryGenerationV2")
	@PostMapping(value = "v2-generate-query")
	public ResponseEntity<?> generateDynamicQueryV2(@RequestBody QueryRequestV2 request)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(request).isPresent()) {
			response = dFReportService.generateDynamicQueryV2(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "QueryGenerationV2")
	@PostMapping(value = "v2-dropdown-query")
	public ResponseEntity<?> generateDropdownQueryV2(@RequestBody QueryRequestV2 request)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(request).isPresent()) {
			response = dFReportService.generateDropdownQueryV2(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "QueryGenerationV2")
	@GetMapping(value = "/dropdown")
	public ResponseEntity<?> getDummyDropdownData() throws DynamicFormsServiceException {

		List<DropDownData> list = new ArrayList<>();
		DropDownData data = new DropDownData();
		data.setId("1");
		data.setName("Bayern Munich");
		data.setSportId("1");

		DropDownData data_1 = new DropDownData();
		data_1.setId("2");
		data_1.setName("Real Madrid");
		data_1.setSportId("1");

		list.add(data);
		list.add(data_1);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@CrossOrigin
	@PostMapping(value = "/autosave")
	public ResponseEntity<?> saveAutoSave(@RequestBody AutoSave req) throws DynamicFormsServiceException {
		AutoSaveEntity response = null;
		if (Optional.of(req).isPresent()) {
			response = dFReportService.saveAutoSave(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@CrossOrigin
	@PutMapping(value = "/autosave")
	public ResponseEntity<?> updateDFFormData(@RequestBody AutoSaveEntity req) throws DynamicFormsServiceException {
		AutoSaveEntity response = null;
		if (Optional.of(req).isPresent()) {
			response = dFReportService.updateAutoSsave(req);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@CrossOrigin
	@GetMapping(value = "/autosave-get-all")
	public ResponseEntity<?> getAllAutoSave(String type, int pageNo, int size) throws DynamicFormsServiceException {
		List<AutoSaveEntity> response = dFReportService.getAllAutoSave(type, pageNo, size);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@CrossOrigin
	@GetMapping(value = "/autosave-get-uid/{uid}")
	public ResponseEntity<?> getAutoSaveByUid(@PathVariable("uid") String uid) throws DynamicFormsServiceException {
		return new ResponseEntity<>(dFReportService.getAutoSaveByUid(uid), HttpStatus.OK);

	}

	@CrossOrigin
	@DeleteMapping(value = "/autosave")
	public ResponseEntity<String> deleteAutoSave(@RequestParam(name = "id") int id)
			throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(id).isPresent()) {
			response = dFReportService.deleteAutoSave(id);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Value("${tmp.path}")
	String tmpPath;

	/*
	 * @CrossOrigin
	 * 
	 * @ApiOperation(value = "Dynamic Query Generation V2", tags = "ETVBRL REPORT")
	 * 
	 * @PostMapping(path="/etvbrl_report_1",produces =
	 * MediaType.APPLICATION_OCTET_STREAM_VALUE)
	 * 
	 * public ResponseEntity<StreamingResponseBody> downloadZip(HttpServletResponse
	 * response,
	 * 
	 * @RequestBody ETVRequest etvReq) throws DynamicFormsServiceException {
	 * 
	 * 
	 * List<String> paths = dFReportService.generateETVBRLReport(etvReq);
	 * 
	 * int BUFFER_SIZE = 1024;
	 * 
	 * StreamingResponseBody streamResponseBody = out -> {
	 * 
	 * final ZipOutputStream zipOutputStream = new
	 * ZipOutputStream(response.getOutputStream()); ZipEntry zipEntry = null;
	 * InputStream inputStream = null;
	 * 
	 * try { for (String path : paths) { File file = new File(path); if(file!=null
	 * && file.exists()) { zipEntry = new ZipEntry(file.getName());
	 * 
	 * inputStream = new FileInputStream(file);
	 * 
	 * zipOutputStream.putNextEntry(zipEntry); byte[] bytes = new byte[BUFFER_SIZE];
	 * int length; while ((length = inputStream.read(bytes)) >= 0) {
	 * zipOutputStream.write(bytes, 0, length); } }
	 * 
	 * } // set zip size in response response.setContentLength((int) (zipEntry !=
	 * null ? zipEntry.getSize() : 0)); } catch (IOException e) {
	 * log.error("Exception while reading and streaming data {} ", e); } finally {
	 * if (inputStream != null) { inputStream.close(); } if (zipOutputStream !=
	 * null) { zipOutputStream.close(); } }
	 * 
	 * }; String zipFileName ="ETVBRL_" + System.currentTimeMillis() + ".zip";
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Content-Disposition",
	 * "attachment; filename="+zipFileName); response.addHeader("Pragma",
	 * "no-cache"); response.addHeader("Expires", "0");
	 * 
	 * return ResponseEntity.ok(streamResponseBody); }
	 * 
	 * @ApiOperation(value = "Dynamic Query Generation V2", tags = "ETVBRL REPORT")
	 * 
	 * @RequestMapping(value = "/download-file/1.0", produces =
	 * "application/zip",method = RequestMethod.POST) public ResponseEntity<?>
	 * downloadFile(@RequestBody ETVRequest req) { //String dirPath =
	 * "your-location-path"; byte[] fileBytes = null; String fileName =""; try {
	 * File file = new File("D:\\automatenda\\upload\\ETVBRL_1645454023555.zip");
	 * fileName = file.getName(); fileBytes = Files.readAllBytes(Paths.get(
	 * "D:\\automatenda\\upload\\ETVBRL_1645454023555.zip"));
	 * 
	 * } catch (IOException e) { e.printStackTrace(); }
	 * System.out.println("filename "+fileName);
	 * 
	 * return ResponseEntity.ok()
	 * .contentType(MediaType.parseMediaType("application/zip"))
	 * .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName
	 * + "\"") .body(fileBytes); }
	 */

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "ETVBRL REPORT")
	@PostMapping("/etvbrl_report")
	public ResponseEntity<?> downloadFile(@RequestBody ETVRequest etvReq)
			throws DynamicFormsServiceException, IOException {
		Map<String, String> res = null;
		if (Optional.of(etvReq).isPresent()) {
			res = dFReportService.generateETVBRLReport(etvReq);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);

	}

	public Resource loadFileAsResource(String fileName) {
		try {

			Path filePath = Paths.get(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}
	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "ETVBRL REPORT FOR MD")
	@PostMapping("/etvbrl_report_md")
	public ResponseEntity<?> downloadFileETVBRL(@RequestBody ETVRequest etvReq)
			throws DynamicFormsServiceException, IOException {
		Map<String, String> res = null;
		if (Optional.of(etvReq).isPresent()) {
			res = dFReportService.generateETVBRLReport(etvReq);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);

	}
	/*
	 * @CrossOrigin
	 * 
	 * @ApiOperation(value = "Dynamic Query Generation V2", tags = "ETVBRL REPORT")
	 * 
	 * @PostMapping(value = "etvbrl_report") public ResponseEntity<?>
	 * generateETVBRLReport(@RequestBody ETVRequest request) throws
	 * DynamicFormsServiceException { String response = null; if
	 * (Optional.of(request).isPresent()) { response =
	 * dFReportService.generateETVBRLReport(request); } else { throw new
	 * DynamicFormsServiceException(env.getProperty("BAD_REQUEST"),
	 * HttpStatus.BAD_REQUEST); } return new ResponseEntity<>(response,
	 * HttpStatus.OK); }
	 */

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "Wizard Generic view Details")
	@PostMapping(value = "wizard-generic-view-details")
	public ResponseEntity<?> generateWizardView(@RequestBody WizardReq request) throws DynamicFormsServiceException {
		String response = null;
		if (Optional.of(request).isPresent()) {
			response = dFReportService.generateWizardView(request);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "Wizard Generic view Page")
	@GetMapping(value = "get-wizard-generic-view/{orgId}")
	public ResponseEntity<?> generateWizardViewPage(@PathVariable("orgId") String orgId)
			throws DynamicFormsServiceException {
		List<WizardEntity> response = null;
		if (Optional.of(orgId).isPresent()) {
			response = dFReportService.generateWizardViewPage(orgId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "Wizard Generic view Page")
	@GetMapping(value = "get-wizard-generic-all-view")
	public ResponseEntity<?> generateWizardViewPage()
			throws DynamicFormsServiceException {
		List<WizardEntity> response = null;
			response = dFReportService.generateWizardViewAllPage();
			//throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@ApiOperation(value = "Dynamic Query Generation V2", tags = "Organization Wizard Generic view Page")
	@GetMapping(value = "get-organization-wizard-view/{isBulkUpload}")
	public ResponseEntity<?> generateOrgWizardViewPage(@PathVariable("isBulkUpload") String isBulkUpload)
			throws DynamicFormsServiceException {
		List<DmsOrganizationWizard> response = null;
		if (Optional.of(isBulkUpload).isPresent()) {
			response = dFReportService.generateWizardOrgViewPage(isBulkUpload);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	    
	@CrossOrigin
	@GetMapping(value = "/get-qrCode/{orgId}")
	public ResponseEntity<?> getQrCode(@PathVariable(name = "orgId") int orgId) throws DynamicFormsServiceException {
		List<DmsOrganizationWizard> response = null;
		if (Optional.of(orgId).isPresent()) {
			response = dFReportService.getQrCode(orgId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping(value = "/get-empPic/{empId}")
	public ResponseEntity<?> getEmpPic(@PathVariable(name = "empId") int empId) throws DynamicFormsServiceException {
		List<EmployeeEntity> response = null;
		if (Optional.of(empId).isPresent()) {
			response = dFReportService.getEmpPic(empId);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping(value = "/get-followup/{orgId}/{stageName}")
	public ResponseEntity<?> getfollowupReasons(@PathVariable(name = "orgId") String orgId,@PathVariable(name="stageName") String stageName) throws DynamicFormsServiceException {
		List<FollowupReasonsEntity> response = null;
		if (Optional.of(orgId).isPresent()) {
			response = dFReportService.getfollowupReasons(orgId,stageName);
		} else {
			throw new DynamicFormsServiceException(env.getProperty("BAD_REQUEST"), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping(value = "/uploadBulkUploadForOtherMaker")
	public ResponseEntity<?> uploadBulkUploadForOtherMaker(@RequestPart("file") MultipartFile bulkExcel,
		    @RequestPart("bumodel") BulkUploadModel bUModel) {
		Integer empId=bUModel.getEmpId();
		Integer orgId=bUModel.getOrgid();
		BulkUploadResponse  response =null;
		try {	
		if(null != empId && null != orgId) {
			response = dFReportService.processBulkExcelForOtherMaker(bulkExcel,empId,orgId);	
		}
		} catch (Exception e) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
	
	@CrossOrigin
	@PostMapping(value = "/uploadBulkUploadForOtherModel")
	public ResponseEntity<?> uploadBulkUploadForOhterModel(@RequestPart("file") MultipartFile bulkExcel,
		    @RequestPart("bumodel") BulkUploadModel bUModel) {
		Integer empId=bUModel.getEmpId();
		Integer orgId=bUModel.getOrgid();
		BulkUploadResponse  response =null;
		try {	
		if(null != empId && null != orgId) {
			response = dFReportService.processBulkExcelForOtherModel(bulkExcel,empId,orgId);	
		}
		} catch (Exception e) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
	
	@CrossOrigin
	@PostMapping(value = "/bulkUploadForFollowupReason")
	public ResponseEntity<?> uploadBulkUploadForFollowupReason(@RequestPart("file") MultipartFile bulkExcel,
		    @RequestPart("bumodel") BulkUploadModel bUModel) {
		Integer empId=bUModel.getEmpId();
		Integer orgId=bUModel.getOrgid();
		BulkUploadResponse  response =null;
		try {	
		if(null != empId && null != orgId) {
			response = dFReportService.processBulkExcelForFollowupReason(bulkExcel,empId,orgId);	
		}
		} catch (Exception e) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
	
	@CrossOrigin
	@PostMapping(value = "/uploadBulkUploadForDeliveryCheckList")
	public ResponseEntity<?> uploadBulkUploadForDeliveryCheckList(
			@RequestPart("file") MultipartFile bulkExcel,
		    @RequestPart("bumodel") BulkUploadModel bUModel){
		Integer empId=bUModel.getEmpId();
		Integer orgId=bUModel.getOrgid();
		BulkUploadResponse  response =null;
		try {	
		if(null != empId && null != orgId) {
			response = dFReportService.processBulkUploadForDeliveryCheckList(bulkExcel,empId,orgId);	
		}
		} catch (Exception e) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
	
	@CrossOrigin
	@PostMapping(value = "/uploadBulkUploadForInsurenceCompanyName")
	public ResponseEntity<?> uploadBulkUploadFornsurenceCompanyName(
			@RequestPart("file") MultipartFile bulkExcel,
		    @RequestPart("bumodel") BulkUploadModel bUModel) {
		Integer empId=bUModel.getEmpId();
		Integer orgId=bUModel.getOrgid();
		BulkUploadResponse  response =null;
		try {	
		if(null != empId && null != orgId) {
			response = dFReportService.processBulkUploadForInsurenceCompanyName(bulkExcel,empId,orgId);	
		}
		} catch (Exception e) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
	
}