package com.automate.df.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.automate.df.constants.GsAppConstants;
import com.automate.df.dao.AutoSaveRepo;
import com.automate.df.dao.DmsAccountDao;
import com.automate.df.dao.DmsAllotmentDao;
import com.automate.df.dao.DmsContactDao;
import com.automate.df.dao.DmsDeliveryDao;
import com.automate.df.dao.DmsExchangeBuyerDao;
import com.automate.df.dao.DmsInvoiceDao;
import com.automate.df.dao.Employee;
import com.automate.df.dao.FollowupReasons;
import com.automate.df.dao.LeadStageRefDao;
import com.automate.df.dao.OrgnizationDao;
import com.automate.df.dao.OtherMakerModelRepository;
import com.automate.df.dao.OtherMakerRepository;
import com.automate.df.dao.ReportQueriesDAO;
import com.automate.df.dao.WizardDao;
import com.automate.df.dao.dashboard.DmsLeadDao;
import com.automate.df.dao.dashboard.DmsLeadDropDao;
import com.automate.df.dao.dashboard.DmsSourceOfEnquiryDao;
import com.automate.df.dao.dashboard.DmsWfTaskDao;
import com.automate.df.dao.oh.DeliveryCheckListRepo;
import com.automate.df.dao.oh.DmsBranchDao;
import com.automate.df.dao.oh.DmsInsurenceCompanyMdRepo;
import com.automate.df.dao.salesgap.DmsEmployeeRepo;
import com.automate.df.entity.AutoSaveEntity;
import com.automate.df.entity.FollowupReasonsEntity;
import com.automate.df.entity.LeadStageRefEntity;
import com.automate.df.entity.OtherMaker;
import com.automate.df.entity.OtherModel;
import com.automate.df.entity.ReportQueries;
import com.automate.df.entity.dashboard.DmsLead;
import com.automate.df.entity.dashboard.DmsLeadDrop;
import com.automate.df.entity.dashboard.DmsWFTask;
import com.automate.df.entity.oh.DmsBranch;
import com.automate.df.entity.sales.DmsOrganizationWizard;
import com.automate.df.entity.sales.WizardEntity;
import com.automate.df.entity.sales.employee.DmsExchangeBuyer;
import com.automate.df.entity.sales.employee.EmployeeEntity;
import com.automate.df.entity.sales.lead.DmsAllotment;
import com.automate.df.entity.sales.lead.DmsDelivery;
import com.automate.df.entity.sales.lead.DmsDeliveryCheckList;
import com.automate.df.entity.sales.lead.DmsInsurenceCompanyMd;
import com.automate.df.entity.sales.lead.DmsInvoice;
import com.automate.df.entity.sales.master.DmsSourceOfEnquiry;
import com.automate.df.entity.salesgap.DmsEmployee;
import com.automate.df.enums.Status;
import com.automate.df.exception.DynamicFormsServiceException;
import com.automate.df.model.AutoSave;
import com.automate.df.model.BulkUploadResponse;
import com.automate.df.model.DMSResponse;
import com.automate.df.model.DmsAccessoriesDto;
import com.automate.df.model.DmsAddress;
import com.automate.df.model.DmsAttachmentDto;
import com.automate.df.model.DmsBookingDto;
import com.automate.df.model.DmsContactDto;
import com.automate.df.model.DmsEntity;
import com.automate.df.model.DmsExchangeBuyerDto;
import com.automate.df.model.DmsFinanceDetailsDto;
import com.automate.df.model.DmsLeadData;
import com.automate.df.model.DmsLeadDto;
import com.automate.df.model.DmsLeadProductDto;
import com.automate.df.model.DmsLeadScoreCardDto;
import com.automate.df.model.ETVPreEnquiry;
import com.automate.df.model.ETVRequest;
import com.automate.df.model.QueryParam;
import com.automate.df.model.QueryRequestV2;
import com.automate.df.model.WhereRequest;
import com.automate.df.model.WizardReq;
import com.automate.df.model.sales.lead.DmsAccountDto;
import com.automate.df.model.sales.lead.DmsOnRoadPriceDto;
import com.automate.df.model.salesgap.TargetRoleRes;
import com.automate.df.service.DFReportService;
//import com.automate.df.service.SalesFeignService;
import com.automate.df.service.SalesGapService;
import com.automate.df.util.ExcelUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.itextpdf.html2pdf.HtmlConverter;
import com.opencsv.CSVWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author sruja
 *
 */

@Service
@Slf4j
public class DFReportServiceImpl implements DFReportService {

	@Autowired
	Environment env;

	@Value("${tmp.path}")
	String tmpPath;

	@Value("${file.controller.url}")
	String fileControllerUrl;

	@Value("${lead.enquiry.url}")
	String leadEnqUrl;

	@Value("${lead.onroadprice.url}")
	String leadOnRoadPriceUrl;
	


	@Autowired
	DmsInvoiceDao dmsInvoiceDao;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	ReportQueriesDAO reportQueriesDAO;

	@Autowired
	SalesGapService salesGapService;

	@Autowired
	DmsDeliveryDao dmsDeliveryDao;

	@Autowired
	ExcelUtil excelUtil;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	DmsSourceOfEnquiryDao dmsSourceOfEnquiryDao;

	@Autowired
	DmsContactDao dmsContactDao;

	@Autowired
	DmsAccountDao dmsAccountDao;
	
	@Autowired
	Employee dmsEmployeeDao;

	@Autowired
	DmsAllotmentDao dmsAllotmentDao;

	@Autowired
	FollowupReasons followupReasons;
	@Autowired
	OtherMakerRepository otherMakerRepository;
	@Autowired
	OtherMakerModelRepository othermakermodelrepo;
	@Autowired
	DeliveryCheckListRepo deliveryCheckListRepo;
	@Autowired
	DmsInsurenceCompanyMdRepo dmsInsurenceCompanyMdRepo;
	static Map<String, String> pageMap = new HashMap<>();
	 static {
		 
		 pageMap.put("IntraStateTax","1198");
		 pageMap.put("InterStateTax", "1199");
		 pageMap.put("UnionTerritory", "1200");
		 pageMap.put("Source", "1208");
		 pageMap.put("SubSource", "1209");
		 pageMap.put("CustomerType", "1210");
		 pageMap.put("EnquirySegment", "1211");
		 pageMap.put("LostReasons", "1212");
		 pageMap.put("SubLostReason", "1213");
		 pageMap.put("ComplaintFactor", "1214");
		 pageMap.put("ComplaintTracker", "1215");
		 pageMap.put("TCS", "1216");
		 pageMap.put("EnquiryCategory", "1223");
		 pageMap.put("DeliveryCheckList", "1224");
		 pageMap.put("BankFinancier", "1225");
		 pageMap.put("InsuranceCompanyName", "1226");
		 pageMap.put("OEM", "1227");
		 pageMap.put("OemModelMapping", "1228");
		 pageMap.put("VehicleInventory", "1229");
		 pageMap.put("RulesConfigurationScreen", "1231");
		 pageMap.put("Employee", "1232");
		 pageMap.put("OtherMaker", "1233");
		 pageMap.put("OtherModel","1234");
		 pageMap.put("EvaluationParameters","1265");
		 pageMap.put("FollowupReasons","1268");
		 pageMap.put("CheckListType", "1269");
		 pageMap.put("CheckListItems", "1279");
		 pageMap.put("OtherCharges", "1283");
		 
	 }
	/**
	 * @param empId
	 * @return
	 */
	private TargetRoleRes getPrecendenceRole(int empId) {
		List<TargetRoleRes> empRoles = salesGapService.getEmpRoles(empId);
		Collections.sort(empRoles);
		return empRoles.get(0);
	}

	@Override
	public String generateDynamicQueryV2(QueryRequestV2 request) throws DynamicFormsServiceException {
		String res = null;
		try {

			Integer empId = request.getEmpId();
			// TargetRoleRes empRoleData = getPrecendenceRole(empId);
			TargetRoleRes empRoleData = salesGapService.getEmpRoleData(empId);
			String roleIdentifier = null;
			if (null != empRoleData) {
				if (null != empRoleData.getHrmsRole()) {
					roleIdentifier = empRoleData.getHrmsRole();
				} else {
					throw new DynamicFormsServiceException("There is NO HRMS Role found in DB for the given emp id",
							HttpStatus.BAD_REQUEST);

				}
			} else {
				throw new DynamicFormsServiceException("No Emp records with given empID", HttpStatus.BAD_REQUEST);
			}
			log.debug("HRMS Role Identifier for emp id " + empId + " is " + roleIdentifier);
			log.info("HRMS Role Identifier for emp id " + empId + " is " + roleIdentifier);

			// roleIdentifier = "1"; //added for testing
			Optional<ReportQueries> rqOpt = reportQueriesDAO.findQuery(request.getReportIdentifier(), roleIdentifier);

			if (rqOpt.isPresent()) {
				StringBuilder query = new StringBuilder();
				ReportQueries rq = rqOpt.get();
				String selectQuery = rq.getQuery();
				String tableName = rq.getTableName();
				query.append(selectQuery);

				boolean whereflagInPayload = StringUtils.containsIgnoreCase(selectQuery, "where");
				log.debug("whereflagInPayload " + whereflagInPayload);
				if (null != request.getWhere() && !whereflagInPayload) {
					String whereClasue = whereQueryBuilder(request.getWhere());
					if(request.getReportIdentifier().equalsIgnoreCase("1212") || request.getReportIdentifier().equalsIgnoreCase("1213")) {
						whereClasue = whereClasue.replace("stage_name IN(\"Contacts\")", "stage_name IN(\"Pre Enquiry\")");
						whereClasue = whereClasue.replace("stage_name IN(\"Booking Approval\")", "stage_name IN(\"Pre Booking\")");
					}
					query.append(GsAppConstants.SPACE).append("WHERE").append(GsAppConstants.SPACE).append(whereClasue);

				}
				if (null != request.getWhere() && whereflagInPayload) {
					String whereClasue = whereQueryBuilder(request.getWhere());
					if(request.getReportIdentifier().equalsIgnoreCase("1212") || request.getReportIdentifier().equalsIgnoreCase("1213")) {
						whereClasue = whereClasue.replace("stage_name IN(\"Contacts\")", "stage_name IN(\"Pre Enquiry\")");
						whereClasue = whereClasue.replace("stage_name IN(\"Booking Approval\")", "stage_name IN(\"Pre Booking\")");
					}
					query.append(GsAppConstants.SPACE).append("AND").append(GsAppConstants.SPACE).append(whereClasue);
				}

				if (null != request.getGroupBy() && !request.getGroupBy().isEmpty()) {
					String groupBy = request.getGroupBy().toString();
					if (null != groupBy && groupBy.length() > 0) {
						groupBy = groupBy.substring(1, groupBy.length() - 1);
					}
					query.append(GsAppConstants.SPACE).append(GsAppConstants.GROUP_BY).append(GsAppConstants.SPACE)
							.append(groupBy);
				}

				if (null != request.getOrderBy() && !request.getOrderBy().isEmpty()) {
					String orderBy = request.getOrderBy().toString();
					if (null != orderBy && orderBy.length() > 0) {
						orderBy = orderBy.substring(1, orderBy.length() - 1);
					}
					query.append(GsAppConstants.SPACE).append(GsAppConstants.ORDER_BY).append(GsAppConstants.SPACE)
							.append(orderBy);

					if (null != request.getOrderByType() && request.getOrderByType().length() > 0) {
						query.append(GsAppConstants.SPACE).append(request.getOrderByType());
					}
				}

				log.debug("query::" + query);
				log.debug("tableName " + tableName);
				List<Object[]> colnHeadersList = new ArrayList<>();
				if (null != tableName && !tableName.isEmpty()) {
					colnHeadersList = entityManager.createNativeQuery("DESCRIBE " + tableName).getResultList();
				} else {

					String tmp = selectQuery;
					if (tmp.contains("select") || tmp.contains("from")) {
						tmp = tmp.substring(tmp.indexOf("select"), tmp.indexOf("from"));
						tmp = tmp.replaceAll("select", "");
					}
					if (tmp.contains("SELECT") || tmp.contains("FROM")) {
						tmp = tmp.substring(tmp.indexOf("SELECT"), tmp.indexOf("FROM"));
						tmp = tmp.replaceAll("SELECT", "");
					}
					System.out.println("tmp ::" + tmp);
					String[] tmpArr = tmp.split(",");
					for (String s : tmpArr) {
						String[] a = new String[1];
						a[0] = s;
						colnHeadersList.add(a);
					}
				}
				log.debug("colnHeadersList " + colnHeadersList);

				List<String> headers = new ArrayList<>();
				for (Object[] arr : colnHeadersList) {

					String colName = (String) arr[0];
					if (StringUtils.containsIgnoreCase(colName, " as ")) {
						colName = colName.replaceAll("\"", "");
						colName = colName.replaceAll("\'", "");
						colName = colName.substring(StringUtils.indexOfIgnoreCase(colName, " AS") + 3,
								colName.length());
						colName = colName.trim();
						headers.add(colName);
					}
				}

				log.debug("Coln Headers ::" + headers);
				final List<Map<String, Object>> jObjList = new ArrayList<>();

				int maxItems = rq.getMaxItems();
				StringBuilder limitQuery = new StringBuilder();
				limitQuery.append("Selct * From ( ");
				limitQuery.append(query);
				limitQuery.append(" ) LIMIT ");
				limitQuery.append(maxItems + 1);

				Query q = entityManager.createNativeQuery(query.toString());
				List<Object[]> queryResults = q.getResultList();
				// List<PreEnquiry> preList = new ArrayList<>();
				for (int i = 0; i < queryResults.size(); i++) {
					Object[] objArr = queryResults.get(i);
					Map<String, Object> map = new LinkedHashMap<>();
					for (int j = 0; j < objArr.length; j++) {
						String colName = headers.get(j);
						if(colName.equalsIgnoreCase("Stage Name") && (request.getReportIdentifier().equalsIgnoreCase("1212") || request.getReportIdentifier().equalsIgnoreCase("1213"))) {
							if(objArr[j].equals("Pre Enquiry")) {
								map.put(colName, "Contacts");
							}
							else if(objArr[j].equals("Pre Booking")) {
								map.put(colName, "Booking Approval");
							}else {
								map.put(colName, objArr[j]);
							}
							
						}else{
							map.put(colName, objArr[j]);
						}
					}
					jObjList.add(map);
				}
				log.debug("jObjList size " + jObjList.size());

				Map<String, Object> finalMap = new LinkedHashMap<>();
				// String csvLink = null;
				// String excelLink = null;
				// String pdfLink = null;
				List<Map<String, Object>> filterdList = new ArrayList<>();
				if (!jObjList.isEmpty()) {

					CompletableFuture<String> csvFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generateCsvLink(jObjList, request.getReportIdentifier());
						}
					});
					CompletableFuture<String> excelFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generateExcelLink(jObjList, request.getReportIdentifier());
						}
					});
					CompletableFuture<String> pdfFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generatePDFLinkV2(jObjList, request.getReportIdentifier());
						}
					});
					/*
					 * csvLink = generateCsvLink(jObjList,request.getReportIdentifier()); excelLink
					 * = generateExcelLink(jObjList,request.getReportIdentifier()); pdfLink =
					 * generatePDFLinkV2(jObjList,request.getReportIdentifier());
					 * log.debug("pdfLink "+pdfLink); pdfLink =
					 * fileControllerUrl+"/downloadFile/"+pdfLink; excelLink =
					 * fileControllerUrl+"/downloadFile/"+excelLink; csvLink =
					 * fileControllerUrl+"/downloadFile/"+csvLink;
					 */
					boolean flag = request.isPaginationRequired();
					int totalCnt = jObjList.size();
					log.debug("jObjList ::" + totalCnt);
					finalMap.put("totalCnt", totalCnt);
					if (flag) {
						int size = request.getSize();
						int pageNo = request.getPageNo();

						finalMap.put("pageNo", pageNo);
						finalMap.put("size", size);

						pageNo = pageNo + 1;
						int fromIndex = size * (pageNo - 1);
						int toIndex = size * pageNo;

						if (toIndex > totalCnt) {
							toIndex = totalCnt;
						}
						if (fromIndex > toIndex) {
							fromIndex = toIndex;
						}
						filterdList = jObjList.subList(fromIndex, toIndex);
					} else {

						finalMap.put("pageNo", 0);
						finalMap.put("size", jObjList.size());
						if (jObjList.size() > maxItems) {
							filterdList = jObjList.subList(0, maxItems);
							finalMap.put("MaxItems", maxItems);
						} else {
							filterdList = jObjList;
						}

					}
					log.debug("Size of filterdList " + filterdList.size());

					finalMap.put("excelLink", excelFuture.get());
					finalMap.put("csvLink", csvFuture.get());
					finalMap.put("pdfLink", pdfFuture.get());
				}

				finalMap.put("data", filterdList);
				res = objectMapper.writeValueAsString(finalMap);
			} else {
				throw new DynamicFormsServiceException("Invalid Report Identifier", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res;
	}

	@Override
	public String generateDropdownQueryV2(QueryRequestV2 request) throws DynamicFormsServiceException {
		String res = null;
		try {

			Integer empId = request.getEmpId();
			TargetRoleRes empRoleData = getPrecendenceRole(empId);
			String roleIdentifier = empRoleData.getRoleId();
			log.debug("roleIdentifier " + roleIdentifier);
//			roleIdentifier = "1"; //added for testing
			Optional<ReportQueries> rqOpt = reportQueriesDAO.findQuery(request.getReportIdentifier(), roleIdentifier);

			if (rqOpt.isPresent()) {
				StringBuilder query = new StringBuilder();
				ReportQueries rq = rqOpt.get();
				String selectQuery = rq.getQuery();
				String tableName = rq.getTableName();
				query.append(selectQuery);

				boolean whereflagInPayload = StringUtils.containsIgnoreCase(selectQuery, "where");
				log.debug("whereflagInPayload " + whereflagInPayload);
				if (null != request.getWhere() && !whereflagInPayload) {
					String whereClasue = whereQueryBuilder(request.getWhere());
					query.append(GsAppConstants.SPACE).append("WHERE").append(GsAppConstants.SPACE).append(whereClasue);

				}
				if (null != request.getWhere() && whereflagInPayload) {
					String whereClasue = whereQueryBuilder(request.getWhere());
					query.append(GsAppConstants.SPACE).append("AND").append(GsAppConstants.SPACE).append(whereClasue);
				}

				if (null != request.getGroupBy() && !request.getGroupBy().isEmpty()) {
					String groupBy = request.getGroupBy().toString();
					if (null != groupBy && groupBy.length() > 0) {
						groupBy = groupBy.substring(1, groupBy.length() - 1);
					}
					query.append(GsAppConstants.SPACE).append(GsAppConstants.GROUP_BY).append(GsAppConstants.SPACE)
							.append(groupBy);
				}

				if (null != request.getOrderBy() && !request.getOrderBy().isEmpty()) {
					String orderBy = request.getOrderBy().toString();
					if (null != orderBy && orderBy.length() > 0) {
						orderBy = orderBy.substring(1, orderBy.length() - 1);
					}
					query.append(GsAppConstants.SPACE).append(GsAppConstants.ORDER_BY).append(GsAppConstants.SPACE)
							.append(orderBy);

					if (null != request.getOrderByType() && request.getOrderByType().length() > 0) {
						query.append(GsAppConstants.SPACE).append(request.getOrderByType());
					}
				}

				log.debug("query::" + query);
				log.debug("tableName " + tableName);
				List<Object[]> colnHeadersList = new ArrayList<>();
				if (null != tableName && !tableName.isEmpty()) {
					colnHeadersList = entityManager.createNativeQuery("DESCRIBE " + tableName).getResultList();
				} else {

					String tmp = selectQuery;
					if (tmp.contains("select") || tmp.contains("from")) {
						tmp = tmp.substring(tmp.indexOf("select"), tmp.indexOf("from"));
						tmp = tmp.replaceAll("select", "");
					}
					if (tmp.contains("SELECT") || tmp.contains("FROM")) {
						tmp = tmp.substring(tmp.indexOf("SELECT"), tmp.indexOf("FROM"));
						tmp = tmp.replaceAll("SELECT", "");
					}
					System.out.println("tmp ::" + tmp);
					String[] tmpArr = tmp.split(",");
					for (String s : tmpArr) {
						String[] a = new String[1];
						a[0] = s;
						colnHeadersList.add(a);
					}
				}
				log.debug("colnHeadersList " + colnHeadersList);

				List<String> headers = new ArrayList<>();
				for (Object[] arr : colnHeadersList) {

					String colName = (String) arr[0];
					if (StringUtils.containsIgnoreCase(colName, " as ")) {
						colName = colName.replaceAll("\"", "");
						colName = colName.replaceAll("\'", "");
						colName = colName.substring(StringUtils.indexOfIgnoreCase(colName, " AS") + 3,
								colName.length());
						colName = colName.trim();
						headers.add(colName);
					}
				}

				log.debug("Coln Headers ::" + headers);
				final List<Map<String, Object>> jObjList = new ArrayList<>();

				int maxItems = rq.getMaxItems();
				StringBuilder limitQuery = new StringBuilder();
				limitQuery.append("Selct * From ( ");
				limitQuery.append(query);
				limitQuery.append(" ) LIMIT ");
				limitQuery.append(maxItems + 1);

				Query q = entityManager.createNativeQuery(query.toString());
				List<Object[]> queryResults = q.getResultList();
				// List<PreEnquiry> preList = new ArrayList<>();
				for (int i = 0; i < queryResults.size(); i++) {
					Object[] objArr = queryResults.get(i);
					Map<String, Object> map = new LinkedHashMap<>();
					for (int j = 0; j < objArr.length; j++) {
						String colName = headers.get(j);
						map.put(colName, objArr[j]);
					}
					jObjList.add(map);
				}
				log.debug("jObjList size " + jObjList.size());

				Map<String, Object> finalMap = new LinkedHashMap<>();
				// String csvLink = null;
				// String excelLink = null;
				// String pdfLink = null;
				List<Map<String, Object>> filterdList = new ArrayList<>();
				if (!jObjList.isEmpty()) {

					CompletableFuture<String> csvFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generateCsvLink(jObjList, request.getReportIdentifier());
						}
					});
					CompletableFuture<String> excelFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generateExcelLink(jObjList, request.getReportIdentifier());
						}
					});
					CompletableFuture<String> pdfFuture = CompletableFuture.supplyAsync(new Supplier<String>() {
						public String get() {
							return generatePDFLinkV2(jObjList, request.getReportIdentifier());
						}
					});
					/*
					 * csvLink = generateCsvLink(jObjList,request.getReportIdentifier()); excelLink
					 * = generateExcelLink(jObjList,request.getReportIdentifier()); pdfLink =
					 * generatePDFLinkV2(jObjList,request.getReportIdentifier());
					 * log.debug("pdfLink "+pdfLink); pdfLink =
					 * fileControllerUrl+"/downloadFile/"+pdfLink; excelLink =
					 * fileControllerUrl+"/downloadFile/"+excelLink; csvLink =
					 * fileControllerUrl+"/downloadFile/"+csvLink;
					 */

					if (jObjList.size() > maxItems) {
						filterdList = jObjList.subList(0, maxItems);
						finalMap.put("MaxItems", maxItems);
					} else {
						filterdList = jObjList;
					}
					finalMap.put("excelLink", excelFuture.get());
					finalMap.put("csvLink", csvFuture.get());
					finalMap.put("pdfLink", pdfFuture.get());
				}

				finalMap.put("data", filterdList);
				res = objectMapper.writeValueAsString(filterdList);
			} else {
				throw new DynamicFormsServiceException("Invalid Report Identifier", HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(env.getProperty("InternalServerError"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res;
	}

	private String generatePDFLinkV2(List<Map<String, Object>> jObjList, String reportId) {

		String tableCell = "<td style=\"width: CALCULATED_WIDTH%;\">CELL_VALUE</td>";
		String htmlString = "<html>\r\n <head>\r\n" + "<style>\r\n" + "tr {\r\n" + "  font-size: 8px;\r\n" + "}\r\n"
				+ "\r\n" + "td {\r\n" + "  font-size: 8px !important;\r\n" + "}\r\n" + "</style>\r\n" + "</head>"

				+ "<body>\r\n" + "<table style=\"border-collapse: collapse; width: 100%;\" border=\"1\">\r\n"
				+ "<tbody>\r\n" + "<tr>\r\n" + "TABLE_HEADERS\r\n" + "</tr>\r\n" + "TABLE_CONTENT\r\n" + "</tbody>\r\n"
				+ "</table>\r\n" + "</body>\r\n" + "</html>";
		//String fileName = reportId + "_" + System.currentTimeMillis() + ".pdf";
		String fname="";
		for(Entry<String, String> entry: pageMap.entrySet()) {
			 if(entry.getValue().equals(reportId)) {
		    	 fname =entry.getKey();
		        break;
		      }
		    }	
		String fileName = fname +".pdf";
		try {

			if (null != jObjList) {
				Map<String, Object> map = jObjList.get(0);
				Object[] objArr = map.keySet().toArray();
				String headerString = "";
				int colns = objArr.length;
				int width = (100 / colns);
				log.debug("Excepted table cell width " + width);
				for (int k = 0; k < objArr.length; k++) {
					String tmp = tableCell;
					tmp = StringUtils.replaceAll(tmp, "CALCULATED_WIDTH", String.valueOf(width));
					tmp = StringUtils.replaceAll(tmp, "CELL_VALUE", objArr[k] != null ? objArr[k].toString() : "");
					headerString += tmp;
				}
				// log.debug("headerString "+headerString);

				String valueString = "";
				for (Map<String, Object> dataMap : jObjList) {
					String rowString = "";
					Object[] valArr = dataMap.values().toArray();
					for (int z = 0; z < valArr.length; z++) {
						String tmp = tableCell;
						tmp = StringUtils.replaceAll(tmp, "CALCULATED_WIDTH", String.valueOf(width));
						tmp = StringUtils.replaceAll(tmp, "CELL_VALUE", valArr[z] != null ? valArr[z].toString() : "");
						rowString += tmp;
					}
					rowString = "<tr>" + rowString + "</tr>";
					valueString += rowString;
				}
				htmlString = htmlString.replaceAll("TABLE_HEADERS", headerString);
				htmlString = htmlString.replaceAll("TABLE_CONTENT", valueString);
				// log.debug("htmlString "+htmlString);
				OutputStream fileOutputStream = new FileOutputStream(tmpPath + fileName);
				HtmlConverter.convertToPdf(htmlString, fileOutputStream);
			}

			log.debug("Created File Successfully " + (tmpPath + fileName));
			fileName = fileControllerUrl + "/downloadFile/" + fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;

	}

	private List<Map<String, Object>> getPaginatedList(List<Map<String, Object>> jObjList, int size, int pageNo) {
		int totalCnt = jObjList.size();
		int fromIndex = size * (pageNo - 1);
		int toIndex = size * pageNo;

		if (toIndex > totalCnt) {
			toIndex = totalCnt;
		}
		if (fromIndex > toIndex) {
			fromIndex = toIndex;
		}
		return jObjList.subList(fromIndex, toIndex);
	}

	/*
	 * private String generatePDFLink(String csvLink, String reportId) { String
	 * fileName = reportId + "_" + System.currentTimeMillis() + "_pdf" + ".pdf";
	 * com.aspose.cells.Workbook book; log.debug("csvLink in generatePDF " +
	 * csvLink); try { book = new com.aspose.cells.Workbook(tmpPath + csvLink);
	 * book.save(tmpPath + fileName, SaveFormat.AUTO); } catch (Exception e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * return fileName; }
	 */
	private String generateExcelLink(List<Map<String, Object>> jObjList, String reportIdentifier) {
	//	String fileName = reportIdentifier + "_" + System.currentTimeMillis() + "_excel" + XLS_EXT;
		String fname="";
		for(Entry<String, String> entry: pageMap.entrySet()) {
			 if(entry.getValue().equals(reportIdentifier)) {
		    	 fname =entry.getKey();
		        break;
		      }
		    }	
		String fileName = fname+ XLS_EXT;
		try {

			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet("Data");
			Map<String, Object> map = jObjList.get(0);
			Object[] objArr = map.keySet().toArray();
			String[] headers = new String[objArr.length];
			for (int k = 0; k < objArr.length; k++) {

				headers[k] = objArr[k].toString();
			}
			CellStyle headerCellStyle = workbook.createCellStyle();
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
			}
			int rowNum = 1;
			for (Map<String, Object> dataMap : jObjList) {
				Object[] obj = dataMap.values().toArray();
				Row row = sheet.createRow(rowNum++);
				for (int j = 0; j < obj.length; j++) {
					row.createCell(j).setCellValue(getValue(obj[j]));
				}
			}
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}
			FileOutputStream fileOut = new FileOutputStream(tmpPath + fileName);
			workbook.write(fileOut);
			fileOut.close();
			// workbook.close();
			log.debug("Generated Excel Successfully " + fileName);
			fileName = fileControllerUrl + "/downloadFile/" + fileName;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return fileName;
	}

	private String getValue(Object obj) {
		if (null != obj) {
			return obj.toString();
		} else {
			return "";
		}

	}

	
	private String generateCsvLink(List<Map<String, Object>> jObjList, String reportId) {
	//	String fileName = reportId + "_" + System.currentTimeMillis() + ".csv";
		String fname="";
		for(Entry<String, String> entry: pageMap.entrySet()) {
		      if(entry.getValue().equals(reportId)) {
		    	 fname =entry.getKey();
		        break;
		      }
		    }	
		String fileName = fname + ".csv";
		try {
			FileWriter outputfile = new FileWriter(tmpPath + fileName);
			CSVWriter writer = new CSVWriter(outputfile);
			if (null != jObjList) {
				Map<String, Object> map = jObjList.get(0);
				Object[] objArr = map.keySet().toArray();
				String[] headers = new String[objArr.length];
				for (int k = 0; k < objArr.length; k++) {

					headers[k] = objArr[k].toString();
				}
				writer.writeNext(headers);

				for (Map<String, Object> dataMap : jObjList) {
					Object[] valArr = dataMap.values().toArray();
					String[] values = new String[valArr.length];
					for (int z = 0; z < valArr.length; z++) {
						if (valArr[z] != null) {
							values[z] = valArr[z].toString();
						} else {
							values[z] = "";
						}
					}
					writer.writeNext(values);

				}
				writer.close();
			}

			log.debug("Created File Successfully " + (tmpPath + fileName));
			fileName = fileControllerUrl + "/downloadFile/" + fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private String whereQueryBuilder(List<WhereRequest> whereList) {
		StringBuilder whereQueryMain = new StringBuilder();
		int cnt = 1;

		if (whereList != null && !whereList.isEmpty()) {
			log.debug("whereList Size ::" + whereList.size());

			for (WhereRequest wr : whereList) {
				String tmp = "";
				StringBuilder whereQuery = new StringBuilder();
				List<QueryParam> valList = wr.getValues();
				System.out.println("valList:" + valList.size());

				if (null != valList && !valList.isEmpty()) {

					if (wr.getType().equalsIgnoreCase(GsAppConstants.TEXT)) {
						for (QueryParam param : valList) {
							tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
						}
						tmp = tmp.substring(0, tmp.length() - 1);
						whereQuery.append(wr.getKey()).append(GsAppConstants.SPACE).append(GsAppConstants.IN_OP)
								.append(GsAppConstants.OPEN_BRACE).append(tmp).append(GsAppConstants.CLOSED_BRACE);

					} else if (wr.getType().equalsIgnoreCase(GsAppConstants.NUMBER)) {

						for (QueryParam param : valList) {
							tmp = tmp + param.getValue() + GsAppConstants.COMMA_SEPERATOR;
						}
						tmp = tmp.substring(0, tmp.length() - 1);
						whereQuery.append(wr.getKey()).append(GsAppConstants.SPACE).append(GsAppConstants.IN_OP)
								.append(GsAppConstants.OPEN_BRACE).append(tmp).append(GsAppConstants.CLOSED_BRACE);
					}

					// -- Date operations
					else if (GsAppConstants.DATE.equalsIgnoreCase(wr.getType())) {
						tmp = operateDateCondition(wr, tmp, whereQuery, valList);
					}

					else if (wr.getType().equalsIgnoreCase(GsAppConstants.FROMDATE)) {
						tmp = operateFromDate(wr, tmp, whereQuery, valList);
					}

					else if (wr.getType().equalsIgnoreCase(GsAppConstants.TODATE)) {
						tmp = operateToDate(wr, tmp, whereQuery, valList);
					}

					else if (wr.getType().equalsIgnoreCase(GsAppConstants.EQUALDATE)) {

						for (QueryParam param : valList) {
							tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
						}
						tmp = tmp.substring(0, tmp.length() - 1);
						whereQuery.append(wr.getKey()).append(GsAppConstants.SPACE).append("= ").append(tmp);
					}

					log.debug("tmp::" + tmp);
				}
				log.debug("Cnt ::" + cnt);
				if (cnt < whereList.size()) {
					whereQuery.append(GsAppConstants.SPACE);
					whereQuery.append(GsAppConstants.AND);
					whereQuery.append(GsAppConstants.SPACE);
				}
				whereQueryMain.append(whereQuery);
				cnt++;

			}

		}

		return whereQueryMain.toString();
	}

	private String operateToDate(WhereRequest wr, String tmp, StringBuilder whereQuery, List<QueryParam> valList) {
		for (QueryParam param : valList) {
			tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
		}
		tmp = tmp.substring(0, tmp.length() - 1);

		String key = wr.getKey();
		String[] keys = key.split("to_date.to");
		key = keys.length >= 1 ? keys[1] : keys[0];

		whereQuery.append(key).append(GsAppConstants.SPACE).append("<= ").append(tmp);
		return tmp;
	}

	private String operateFromDate(WhereRequest wr, String tmp, StringBuilder whereQuery, List<QueryParam> valList) {
		for (QueryParam param : valList) {
			tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
		}
		tmp = tmp.substring(0, tmp.length() - 1);

		String key = wr.getKey();
		String[] keys = key.split("from_date.from");
		key = keys.length >= 1 ? keys[1] : keys[0];

		whereQuery.append(key).append(GsAppConstants.SPACE).append(">= ").append(tmp);
		return tmp;
	}

	private String operateDateCondition(WhereRequest wr, String tmp, StringBuilder whereQuery,
			List<QueryParam> valList) {
		String dataKey = wr.getKey();

		int fromIndex = dataKey.indexOf("from_date.from");
		if (fromIndex != -1) {
			for (QueryParam param : valList) {
				tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
			}
			tmp = tmp.substring(0, tmp.length() - 1);

			String[] keys = dataKey.split("from_date.from");

			whereQuery.append(keys[1]).append(GsAppConstants.SPACE).append(">= ").append(tmp);
		} else {
			int toIndex = dataKey.indexOf("to_date.to");
			if (toIndex != -1) {
				for (QueryParam param : valList) {
					tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
				}
				tmp = tmp.substring(0, tmp.length() - 1);

				String[] keys = dataKey.split("to_date.to");
				whereQuery.append(keys[1]).append(GsAppConstants.SPACE).append("<= ").append(tmp);
			} else {
				for (QueryParam param : valList) {
					tmp = tmp + "\"" + param.getValue() + "\"" + GsAppConstants.COMMA_SEPERATOR;
				}
				tmp = tmp.substring(0, tmp.length() - 1);
				whereQuery.append(wr.getKey()).append(GsAppConstants.SPACE).append("= ").append(tmp);
			}
		}
		return tmp;
	}

	public Timestamp getCurrentTmeStamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	@Autowired
	AutoSaveRepo autoSaveRepo;

	@Autowired
	ModelMapper modelMapper;

	@Override
	public AutoSaveEntity saveAutoSave(AutoSave req) {
		AutoSaveEntity ase = new AutoSaveEntity();
		String jsonReq = new Gson().toJson(req.getData());
		System.out.println("jsonReq " + jsonReq);
		ase.setData(jsonReq);
		ase.setStatus(req.getStatus());
		ase.setUniversalId(req.getUniversalId());

		List<AutoSaveEntity> dbList = autoSaveRepo.getDataByUniversalId(req.getUniversalId());
		if (dbList.isEmpty()) {
			return autoSaveRepo.save(ase);
		} else {
			dbList.forEach(x -> {
				autoSaveRepo.delete(x);
				;
			});
			return autoSaveRepo.save(ase);
		}
	}

	@Override
	public AutoSaveEntity updateAutoSsave(AutoSaveEntity req) {
		AutoSaveEntity db = autoSaveRepo.findById(req.getId()).get();
		db.setUniversalId(req.getUniversalId());
		db.setData(req.getData());
		db.setStatus(req.getStatus());
		return autoSaveRepo.save(db);
	}

	@Override
	public List<AutoSaveEntity> getAllAutoSave(String type, int pageNo, int size) {
		Pageable pageable = PageRequest.of(pageNo, size);
		return autoSaveRepo.getAutoSaveBasedOnType(type, pageable).toList();
	}

	@Override
	public String deleteAutoSave(int id) {
		autoSaveRepo.deleteById(id);
		return "Deleted Sucessfully";
	}

	@Override
	public String getAutoSaveByUid(String uid) throws DynamicFormsServiceException {
		Optional<AutoSaveEntity> opt = autoSaveRepo.getDataByUniversalIdV2(uid);
		String str = null;
		AutoSaveEntity auto = null;
		if (opt.isPresent()) {
			auto = opt.get();
			str = new Gson().toJson(auto.getData());
			str = str.replace("\\", "");
			str = str.replaceAll("^\"|\"$", "");
			System.out.println("str " + str);
			auto.setData(str);
			// convertedObject = new Gson().fromJson(auto.getData(), JsonObject.class);
		} else {
			throw new DynamicFormsServiceException("Data Not found in sysem for given universalId",
					HttpStatus.BAD_REQUEST);
		}
		return str;
	}

	// ETVBRL Strats

	@Autowired
	LeadStageRefDao leadStageRefDao;

	@Autowired
	DmsLeadDao dmsLeadDao;

	@Autowired
	DmsBranchDao dmsBranchDao;

	@Autowired
	DmsLeadDropDao dmsLeadDropDao;

	@Autowired
	DmsEmployeeRepo dmsEmployeeRepo;

	@Autowired
	DmsWfTaskDao dmsWfTaskDao;

	@Autowired
	DmsExchangeBuyerDao dmsExchangeBuyerDao;

	String PREENQUIRY = "PREENQUIRY";
	String DROPPED = "DROPPED";
	String ENQUIRY = "ENQUIRY";
	String BOOKING = "BOOKING";
	String INVOICE = "INVOICE";
	String HOME_VISIT = "Home Visit";
	String TEST_DRIVE = "Test Drive";
	String DELIVERY = "DELIVERY";
	String EVALUATION = "Evaluation";
	String XLS_EXT = ".xlsx";

	@Override
	public Map<String, String> generateETVBRLReport(ETVRequest request) throws DynamicFormsServiceException {
		log.debug("Calling generateETVBRLReport(){}");
		Map<String, String> map = new HashMap<>();
		List<String> fileNameList = new ArrayList<>();
		String combineFileName = null;
		try {
			String orgId = request.getOrgId();
			String sd = request.getFromDate();
			String ed = request.getToDate();
			// List<String> branchIdList =request.getBranchIdList();
			List<String> branchIdList = new ArrayList<>();
			for (String str : request.getBranchIdList()) {
				branchIdList.add(String.valueOf(getBrachIdFromLocationID(str)));
			}
			log.debug("branchIdList " + branchIdList);
			String parentBranchId = request.getParentBranchId();

			if (null == orgId && null == sd && null == ed) {
				throw new DynamicFormsServiceException("ORG ID,START DATE AND END_DATE IS MISSING",
						HttpStatus.BAD_REQUEST);
			}
			log.debug("orgID:" + orgId + ",StartDate:" + sd + ",endDate:" + ed);

			sd = sd + " 00:00:00";
			ed = ed + " 23:59:59";

			final String startDate = sd;
			final String endDate = ed;

			log.debug("branchIdList::" + branchIdList);
			log.debug("parentBranchId:" + parentBranchId);

			List<LeadStageRefEntity> leadRefDBListEnq = getLeadRefDBList(orgId, startDate, endDate, ENQUIRY,branchIdList,"ENQUIRYCOMPLETED");
			List<LeadStageRefEntity> leadRefDBListEnqNC = getLeadRefDBList(orgId, startDate, endDate, ENQUIRY,branchIdList);
			List<LeadStageRefEntity> leadRefDBListBooking = getLeadRefDBList(orgId, startDate, endDate, "BOOKING",branchIdList,"BOOKINGCOMPLETED");
			List<LeadStageRefEntity> leadRefDBListBookingNC = getLeadRefDBList(orgId, startDate, endDate, "BOOKING",branchIdList);
			
			List<LeadStageRefEntity> leadRefBranches   = leadStageRefDao.getLeadsForBranches(orgId, startDate, endDate,branchIdList);
	
		
			log.info("Started PREENQUIRY report ");
	  	CompletableFuture<List<String>> future1 = CompletableFuture.supplyAsync(() -> {
				return generatePreEnquiryPDFReport(orgId, startDate, endDate, PREENQUIRY, branchIdList, fileNameList);
			});

			log.info("Started ENQUIRY report ");
			CompletableFuture<List<String>> future2 = CompletableFuture.supplyAsync(() -> {
				try {
					return generateEnquiryPDFReport(orgId, startDate, endDate, ENQUIRY, branchIdList, fileNameList,
							leadRefDBListEnq);
				} catch (DynamicFormsServiceException e) {
					e.printStackTrace();
				}
				return branchIdList;
			});

			log.info("Started BOOKING report ");
			CompletableFuture<List<String>> future3 = CompletableFuture.supplyAsync(() -> {
				return generateBookingPDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,
						leadRefDBListBooking);
			});

			log.info("Started INVOICE report ");
			CompletableFuture<List<String>> future4 = CompletableFuture.supplyAsync(() -> {
				return generateInvoicePDFReport(orgId, startDate, endDate, INVOICE, branchIdList, fileNameList);
			});

			log.info("Started DELIVERY report ");
			CompletableFuture<List<String>> future5 = CompletableFuture.supplyAsync(() -> {
				return generateDeliveryPDFReport(orgId, startDate, endDate, DELIVERY, branchIdList, fileNameList);
			});

			log.info("Started TESTDRIVE report ");
			CompletableFuture<List<String>> future6 = CompletableFuture.supplyAsync(() -> {
				return generateTestDrivePDFReport(orgId, startDate, endDate, branchIdList, fileNameList,leadRefBranches);
			});

			log.info("Started HOMEVISIT report ");
			CompletableFuture<List<String>> future7 = CompletableFuture.supplyAsync(() -> {
				return generateHomeVisitPDFReport(orgId, startDate, endDate, branchIdList, fileNameList,leadRefBranches);
			});

			log.info("Started EVALUATION report ");
			CompletableFuture<List<String>> future8 = CompletableFuture.supplyAsync(() -> {
				return generateEvaluationPDFReport(orgId, startDate, endDate, branchIdList, fileNameList,leadRefBranches);
			});

			log.info("Started ENQUIRY LIVE report ");
			CompletableFuture<List<String>> future9 = CompletableFuture.supplyAsync(() -> {
				try {
					return generateLiveEnquiryPDFReport(orgId, startDate, endDate, ENQUIRY, branchIdList, fileNameList,
							leadRefDBListEnqNC);
				} catch (DynamicFormsServiceException e) {
					e.printStackTrace();
				}
				return branchIdList;
			});

			log.info("Started ENQUIRY LOST  report ");
			CompletableFuture<List<String>> future10 = CompletableFuture.supplyAsync(() -> {
				try {
					return generateEnquiryLostPDFReport(orgId, startDate, endDate, ENQUIRY, branchIdList, fileNameList,
							leadRefDBListEnqNC);
				} catch (DynamicFormsServiceException e) {
					e.printStackTrace();
				}
				return branchIdList;
			});

			// if(leadRefDBListBooking!=null && !leadRefDBListBooking.isEmpty()) {

			log.info("Started BOOKING LIVe report ");
			CompletableFuture<List<String>> future11 = CompletableFuture.supplyAsync(() -> {
				return generateBookingLivePDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,
						leadRefDBListBookingNC);
			});

			log.info("Started BOOKING Lost report ");
			CompletableFuture<List<String>> future12 = CompletableFuture.supplyAsync(() -> {
				return generateBookingLostPDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,
						leadRefDBListBookingNC);
			});
			CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3, future4,
					future5, future6, future7, future8, future9, future10, future11, future12);
			combinedFuture.get();
			
			
			if (future1.isDone() && future2.isDone() && future3.isDone() && future4.isDone() && future5.isDone()
					&& future6.isDone() && future7.isDone() && future8.isDone() && future9.isDone() && future10.isDone()
					&& future11.isDone() && future12.isDone()) {
				log.debug("fileNameList ::" + future8.get());
				if(excelUtil.verifyTmpltFile()) {
					log.info("Template file exists");	
					combineFileName = excelUtil.generateExcelFromTemplate(fileNameList,orgId);	
					
				} else {
					log.info("Template file NOT exists");
					combineFileName = excelUtil.mergeFiles(future8.get());
				}
				log.debug("combineFileName:::::"+combineFileName);
				combineFileName = fileControllerUrl + "/downloadFile/" + combineFileName;
				log.debug("fileControllerUrl:::::"+combineFileName);
				map.put("downloadUrl", combineFileName);
			
			}
			
			/*
			  fileNameList = generatePreEnquiryPDFReport(orgId, startDate, endDate,
			  PREENQUIRY,branchIdList,fileNameList);
			  
			  
			 // fileNameList = generateEnquiryPDFReport(orgId, startDate, endDate,ENQUIRY,branchIdList,fileNameList,leadRefDBListEnq);
			  
			  //fileNameList = generateBookingPDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,leadRefDBListBooking);
			  
			  ///fileNameList = generateLiveEnquiryPDFReport(orgId, startDate, endDate, ENQUIRY, branchIdList, fileNameList,leadRefDBListEnq);
			  
			  
			 // fileNameList = generateBookingLivePDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,leadRefDBListBooking);
			  
			 // fileNameList = generateBookingLostPDFReport(orgId, startDate, endDate, BOOKING, branchIdList, fileNameList,leadRefDBListBooking);
			  
			  fileNameList = generateInvoicePDFReport(orgId, startDate, endDate,INVOICE,branchIdList,fileNameList);
			  
			  fileNameList = generateEvaluationPDFReport(orgId, startDate, endDate,  branchIdList,fileNameList,leadRefBranches);
			  
			 // fileNameList = generateDeliveryPDFReport(orgId, startDate, endDate,DELIVERY,branchIdList,fileNameList);
			  //
			  //fileNameList = generateTestDrivePDFReport(orgId, startDate, endDate,branchIdList,fileNameList,leadRefBranches);
			  
			 // fileNameList = generateHomeVisitPDFReport(orgId, startDate, endDate, branchIdList,fileNameList,leadRefBranches);
			  
			  /*
			  
			  fileNameList = generateInvoicePDFReport(orgId, startDate, endDate,
			  INVOICE,branchIdList,fileNameList);
			  
			  fileNameList = generateDeliveryPDFReport(orgId, startDate, endDate,
			  DELIVERY,branchIdList,fileNameList);
			  
			  
			  
			  			  fileNameList = generateEvaluationPDFReport(orgId, startDate, endDate,
			  branchIdList,fileNameList);
			  
			
			  
			 
			   */

			 
			log.info("Completed ETVBRL Report Generation ");
		} catch (DynamicFormsServiceException e) {
			throw new DynamicFormsServiceException(e.getMessage(), e.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	private List<String> generatePreEnquiryPDFReport(String orgId, String startDate, String endDate, String type,
			List<String> branchIdList, List<String> fileNameList) {
		// logic for preenquiry
		String preFileName = tmpPath + "PRE_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		List<LeadStageRefEntity> leadRefDBList = getLeadRefDBList(orgId, startDate, endDate, PREENQUIRY, branchIdList,"PREENQUIRYCOMPLETED");
		log.debug("leadRefDBList size for PREENQUIRY" + leadRefDBList.size());

		List<Integer> leadIdList = leadRefDBList.stream().map(x -> x.getLeadId()).collect(Collectors.toList());
		List<DmsLead> leadDBList = dmsLeadDao.findAllById(leadIdList);

		if (null != leadDBList) {
			log.debug("leadDBList size for PREENQUIRY " + leadDBList.size());
		}
		List<ETVPreEnquiry> etvList = buildPreEnqList(leadRefDBList, leadDBList);

		log.debug("fileName ::" + preFileName);
		log.debug("etvList::" + etvList.size());
		genearateExcelForPreEnq(etvList, preFileName);
		log.debug("Generated report for Pre Enq");
		fileNameList.add(preFileName);
		return fileNameList;
	}

	private List<String> generateEvaluationPDFReport(String orgId, String startDate, String endDate,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefBranches) {
		List<String> universalIdList = leadRefBranches.stream().map(x->x.getUniversalId()).collect(Collectors.toList());
		String evalFileName = tmpPath + "EVALUATION_LOSTETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		List<DmsWFTask> wfTaskListEval = dmsWfTaskDao.getWfTaskByTaskNameAndUniversalIds(EVALUATION, startDate, endDate,universalIdList);
		List<DMSResponse> dmsResponseEvalList = new ArrayList();
		for (DmsWFTask task : wfTaskListEval) {
			String universalId = task.getUniversalId();
			if (null != task) {
				String tmp = leadEnqUrl.replace("universal_id", universalId);
				try {
					DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
					dmsResponseEvalList.add(dmsResponse);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Exception ", e);
				}
			}
		}

		if (null != dmsResponseEvalList) {
			log.debug("dmsResponseEnqList size for Evaluation " + dmsResponseEvalList.size());
			genearateExcelForEval(dmsResponseEvalList, wfTaskListEval, evalFileName, EVALUATION);
			log.debug("Generated report for Evaluationt");
		}
		fileNameList.add(evalFileName);

		return fileNameList;
	}

	private List<String> generateHomeVisitPDFReport(String orgId, String startDate, String endDate,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefBranches) {
		String hvFileName = tmpPath + "HV_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		List<String> universalIdList = leadRefBranches.stream().map(x->x.getUniversalId()).collect(Collectors.toList());
		
		List<DmsWFTask> wfTaskListHV = dmsWfTaskDao.getWfTaskByTaskNameAndUniversalIds(HOME_VISIT, startDate, endDate,universalIdList);
		List<DMSResponse> dmsResponseHVList = new ArrayList();
		for (DmsWFTask task : wfTaskListHV) {
			String universalId = task.getUniversalId();
			if (null != task) {
				String tmp = leadEnqUrl.replace("universal_id", universalId);
				log.debug("tmp url "+tmp);
				
				try {
					DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
					dmsResponseHVList.add(dmsResponse);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Exception ", e);
				}
			}
		}

		if (null != dmsResponseHVList) {
			log.debug("dmsResponseEnqList size for Home Visit " + dmsResponseHVList.size());
			genearateExcelForTD(dmsResponseHVList, wfTaskListHV, hvFileName, HOME_VISIT,orgId);
			log.debug("Generated report for Home Visit");
		}
		fileNameList.add(hvFileName);
		return fileNameList;
	}

	private List<String> generateTestDrivePDFReport(String orgId, String startDate, String endDate,
			List<String> branchIdList, List<String> fileNameList,List<LeadStageRefEntity> leadRefBranches) {
		String tdFileName = tmpPath + "TD_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		// TestDrive
		List<String> universalIdList = leadRefBranches.stream().map(x->x.getUniversalId()).collect(Collectors.toList());
		List<DmsWFTask> wfTaskListTD = dmsWfTaskDao.getWfTaskByTaskNameAndUniversalIds(TEST_DRIVE, startDate, endDate,universalIdList);
		List<DMSResponse> dmsResponseTDList = new ArrayList();
		for (DmsWFTask task : wfTaskListTD) {
			String universalId = task.getUniversalId();
			if (null != task) {
				String tmp = leadEnqUrl.replace("universal_id", universalId);
				log.debug("tmp::" + tmp);
				try {
					DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
					dmsResponseTDList.add(dmsResponse);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (null != dmsResponseTDList) {
			log.debug("dmsResponseEnqList size for TEST DRIVE " + dmsResponseTDList.size());
			genearateExcelForTD(dmsResponseTDList, wfTaskListTD, tdFileName, TEST_DRIVE,orgId);
			log.debug("Generated report for Test Drive");
		}
		fileNameList.add(tdFileName);
		return fileNameList;
	}

	private List<String> generateDeliveryPDFReport(String orgId, String startDate, String endDate, String string,
			List<String> branchIdList, List<String> fileNameList) {
		// lost for DELIVERY
		String deliveryFileName = tmpPath + "DELIVERY_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		List<LeadStageRefEntity> leadRefDBListDelivery = getLeadRefDBList(orgId, startDate, endDate, "DELIVERY",branchIdList);
		log.debug("leadRefDBList size for DELIVERY:" + leadRefDBListDelivery.size());
		List<DmsLead> dmsLeadDeliveryDbList = dmsLeadDao
				.findAllById(leadRefDBListDelivery.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseDeliveryList = new ArrayList();
		for (DmsLead dmsLead : dmsLeadDeliveryDbList) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				log.debug("DELIVERY SALES URL "+tmp);
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseDeliveryList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseDeliveryList) {
			log.debug("dmsResponseEnqList size for DELIVERY " + dmsResponseDeliveryList.size() + " deliveryFileName "
					+ deliveryFileName);

			genearateExcelForDelivery(dmsResponseDeliveryList, leadRefDBListDelivery, deliveryFileName, orgId);
			log.debug("Generated report for DELIVERY");
		}
		fileNameList.add(deliveryFileName);
		return fileNameList;
	}

	private List<String> generateInvoicePDFReport(String orgId, String startDate, String endDate, String string,
			List<String> branchIdList, List<String> fileNameList) {

		String retailFileName = tmpPath + "RETAIL_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		// lost for RETAIL

		List<LeadStageRefEntity> leadRefDBListInvoice = getLeadRefDBList(orgId, startDate, endDate, "INVOICE",branchIdList,"INVOICECOMPLETED");
		log.debug("leadRefDBList size for INVOICE:" + leadRefDBListInvoice.size());
		List<DmsLead> dmsLeadInvoiceDbList = dmsLeadDao
				.findAllById(leadRefDBListInvoice.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseInvoiceList = new ArrayList();
		for (DmsLead dmsLead : dmsLeadInvoiceDbList) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				log.debug("RETAIL SALES CALL " + tmp);
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseInvoiceList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseInvoiceList) {
			log.debug("dmsResponseEnqList size for INVOICE " + dmsResponseInvoiceList.size());
			genearateExcelForInvoice(dmsResponseInvoiceList, leadRefDBListInvoice, retailFileName,orgId);
			log.debug("Generated report for INVOICE");
		}
		fileNameList.add(retailFileName);
		return fileNameList;
	}

	private List<String> generateBookingLostPDFReport(String orgId, String startDate, String endDate, String string,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListBooking) {

		String bookLostFileName = tmpPath + "BOOKING_LOSTETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		// logic for BOOKING LOST
		List<DmsLead> leadDBBookingList = dmsLeadDao
				.findAllById(leadRefDBListBooking.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
		List<LeadStageRefEntity> leadDBBookingLostList = new ArrayList<>();
		for (DmsLead dmsLead : leadDBBookingList) {
			String leadStg = dmsLead.getLeadStage();
			if (leadStg.equalsIgnoreCase("DROPPED")) {
				String leadId = String.valueOf(dmsLead.getId());
				log.debug("LeadId " + leadId);
				for (LeadStageRefEntity ref : leadRefDBListBooking) {
					String refLeadId = String.valueOf(ref.getLeadId());
					log.debug("refLeadId " + refLeadId);
					if (refLeadId.equals(leadId)) {
						leadDBBookingLostList.add(ref);
					}
				}
			}
		}
		log.debug("leadDBBookingLostList size: " + leadDBBookingLostList.size());

		List<DmsLead> dmsLeadDropBookingList = dmsLeadDao
				.findAllById(leadDBBookingLostList.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
		List<DMSResponse> dmsResponseLostBookingList = new ArrayList<>();
		for (DmsLead dmsLead : dmsLeadDropBookingList) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseLostBookingList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseLostBookingList) {

			genearateExcelForBooking(dmsResponseLostBookingList, leadDBBookingLostList, bookLostFileName,
					"Lost Booking");
			log.debug("Generated report for LOST BOOKING");
		}

		fileNameList.add(bookLostFileName);

		return fileNameList;
	}

	private List<String> generateBookingPDFReport(String orgId, String startDate, String endDate, String string,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListBooking) {

		String bookFileName = tmpPath + "BOOKING_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		List<DmsLead> leadDBListBooking = dmsLeadDao
				.findAllById(leadRefDBListBooking.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseBookingList = new ArrayList();
		for (DmsLead dmsLead : leadDBListBooking) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				log.debug("BOOKING SALES CALL " + tmp);
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseBookingList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseBookingList) {
			log.debug("dmsResponseEnqList size for BOOKING " + dmsResponseBookingList.size());
			genearateExcelForBooking(dmsResponseBookingList, leadRefDBListBooking, bookFileName, "Booking");
			log.debug("Generated report for BOOKING");
		}

		fileNameList.add(bookFileName);

		return fileNameList;
	}

	private List<String> generateBookingLivePDFReport(String orgId, String startDate, String endDate, String string,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListBooking) {

		String bookLiveFileName = tmpPath + "BOOKING_LIVE_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;

		// logic for LIVE BOOKING
		List<LeadStageRefEntity> leadRefDBInvoice = getLeadRefDBList(orgId, startDate, endDate, "INVOICE",branchIdList);
		List<LeadStageRefEntity> leadRefDBLiveBookingList = removeDuplicates(leadRefDBListBooking, leadRefDBInvoice);
		
		// logic for BOOKING LOST
				List<DmsLead> leadDBBookingList = dmsLeadDao
						.findAllById(leadRefDBListBooking.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
				List<LeadStageRefEntity> leadDBBookingLostList = new ArrayList<>();
				for (DmsLead dmsLead : leadDBBookingList) {
					String leadStg = dmsLead.getLeadStage();
					if (leadStg.equalsIgnoreCase("DROPPED")) {
						String leadId = String.valueOf(dmsLead.getId());
						log.debug("LeadId " + leadId);
						for (LeadStageRefEntity ref : leadRefDBListBooking) {
							String refLeadId = String.valueOf(ref.getLeadId());
							log.debug("refLeadId " + refLeadId);
							if (refLeadId.equals(leadId)) {
								leadDBBookingLostList.add(ref);
							}
						}
					}
				}
				log.debug("leadDBBookingLostList size: " + leadDBBookingLostList.size());
				
				leadRefDBLiveBookingList = removeDuplicates(leadRefDBListBooking, leadDBBookingLostList);
		
		log.debug("leadRefDBLiveBookingList size  after " + leadRefDBLiveBookingList.size());
		List<DmsLead> leadDBLiveBookingList = dmsLeadDao
				.findAllById(leadRefDBLiveBookingList.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseLiveBookingList = new ArrayList();
		for (DmsLead dmsLead : leadDBLiveBookingList) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseLiveBookingList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseLiveBookingList) {
			log.debug("dmsResponseEnqList size for LIVE BOOKING " + dmsResponseLiveBookingList.size());
			genearateExcelForBooking(dmsResponseLiveBookingList, leadRefDBLiveBookingList, bookLiveFileName,
					"Live Booking");
			log.debug("Generated report for LIVE BOOKING");
		}

		fileNameList.add(bookLiveFileName);

		return fileNameList;
	}

	private List<String> generateLiveEnquiryPDFReport(String orgId, String startDate, String endDate, String type,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListEnq)
			throws DynamicFormsServiceException {
		String enqLiveFileName = tmpPath + "ENQ_LIVE_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		List<LeadStageRefEntity> leadRefDBListPreBooking = getLeadRefDBList(orgId, startDate, endDate, "PREBOOKING",branchIdList);
		List<LeadStageRefEntity> leadRefDBListLiveEnq = removeDuplicates(leadRefDBListEnq, leadRefDBListPreBooking);
		
		// logic for ENQUIRY LOST
		List<DmsLead> leadDBEnquiryList = dmsLeadDao
				.findAllById(leadRefDBListEnq.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
		List<LeadStageRefEntity> leadDBEnquiryLostList = new ArrayList<>();
		for (DmsLead dmsLead : leadDBEnquiryList) {
			String leadStg = dmsLead.getLeadStage();
			if (leadStg.equalsIgnoreCase("DROPPED")) {
				String leadId = String.valueOf(dmsLead.getId());
				log.debug("LeadId " + leadId);
				for (LeadStageRefEntity ref : leadRefDBListEnq) {
					String refLeadId = String.valueOf(ref.getLeadId());
					log.debug("refLeadId " + refLeadId);
					if (refLeadId.equals(leadId)) {
						leadDBEnquiryLostList.add(ref);
					}
				}
			}
		}
	
				
		leadRefDBListLiveEnq = removeDuplicates(leadRefDBListLiveEnq, leadDBEnquiryLostList);		
		
		log.debug("leadRefDBListLiveEnq size for LIVE ENQUIRY:::" + leadRefDBListLiveEnq.size());
		List<DmsLead> leadDBListLiveEnq = dmsLeadDao
				.findAllById(leadRefDBListLiveEnq.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseLiveEnqList = new ArrayList();
		for (DmsLead dmsLead : leadDBListLiveEnq) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseLiveEnqList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}
		if (null != dmsResponseLiveEnqList) {
			genearateExcelForEnq(dmsResponseLiveEnqList, leadRefDBListLiveEnq, enqLiveFileName, "Live Enquiry");
			log.debug("Generated report for LIVE ENQ");

		}
		fileNameList.add(enqLiveFileName);
		return fileNameList;
	}

	private List<String> generateEnquiryLostPDFReport(String orgId, String startDate, String endDate, String type,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListEnq)
			throws DynamicFormsServiceException {

		String enqLostFileName = tmpPath + "ENQ_LOST_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		// logic for ENQUIRY LOST
		List<DmsLead> leadDBEnquiryList = dmsLeadDao
				.findAllById(leadRefDBListEnq.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
		List<LeadStageRefEntity> leadDBEnquiryLostList = new ArrayList<>();
		for (DmsLead dmsLead : leadDBEnquiryList) {
			String leadStg = dmsLead.getLeadStage();
			if (leadStg.equalsIgnoreCase("DROPPED")) {
				String leadId = String.valueOf(dmsLead.getId());
				log.debug("LeadId " + leadId);
				for (LeadStageRefEntity ref : leadRefDBListEnq) {
					String refLeadId = String.valueOf(ref.getLeadId());
					log.debug("refLeadId " + refLeadId);
					if (refLeadId.equals(leadId)) {
						leadDBEnquiryLostList.add(ref);
					}
				}
			}
		}
		log.debug("leadDBEnquiryLostList size: " + leadDBEnquiryLostList.size());
		log.debug("leadRefDBList size for ENQUIRY:" + leadRefDBListEnq.size());

		
		List<DmsLead> dmsLeadLostEnqList = dmsLeadDao
				.findAllById(leadDBEnquiryLostList.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));

		List<DMSResponse> dmsResponseLostEnqList = new ArrayList<>();
		for (DmsLead dmsLead : dmsLeadLostEnqList) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			try {
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseLostEnqList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}
		if (null != dmsResponseLostEnqList) {
			genearateExcelForEnq(dmsResponseLostEnqList, leadDBEnquiryLostList, enqLostFileName, "Lost Enquiry");
			log.debug("Generated report for LOST ENQ");
		}
		fileNameList.add(enqLostFileName);
		return fileNameList;
	}

	private List<String> generateEnquiryPDFReport(String orgId, String startDate, String endDate, String type,
			List<String> branchIdList, List<String> fileNameList, List<LeadStageRefEntity> leadRefDBListEnq)
			throws DynamicFormsServiceException {

		String enqFileName = tmpPath + "ENQ_ETVBRL_" + System.currentTimeMillis() + XLS_EXT;
		log.debug("leadRefDBList size for ENQUIRY:" + leadRefDBListEnq.size());
		List<DmsLead> leadDBListEnq = dmsLeadDao
				.findAllById(leadRefDBListEnq.stream().map(x -> x.getLeadId()).collect(Collectors.toList()));
		List<DMSResponse> dmsResponseEnqList = new ArrayList();
		for (DmsLead dmsLead : leadDBListEnq) {
			String universalId = dmsLead.getCrmUniversalId();
			String tmp = leadEnqUrl.replace("universal_id", universalId);
			log.debug("Sales CALL " + tmp);
			try {
				DMSResponse dmsResponse = restTemplate.getForEntity(tmp, DMSResponse.class).getBody();
				dmsResponseEnqList.add(dmsResponse);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Exception ", e);
			}
		}

		if (null != dmsResponseEnqList) {
			log.debug("dmsResponseEnqList size for ENQUIRY " + dmsResponseEnqList.size());
			genearateExcelForEnq(dmsResponseEnqList, leadRefDBListEnq, enqFileName, "Enquiry");
			log.debug("Generated report for ENQ");

		}
		fileNameList.add(enqFileName);
		return fileNameList;
	}

	private Integer getBrachIdFromLocationID(String str) {
		log.debug("getBrachIdFromLocationID " + str);
		DmsBranch branch = dmsBranchDao.getBranchByOrgMpId(Integer.parseInt(str));
		if(null!=branch) {
			return branch.getBranchId();
		}
		return 0;

	}
	

	private void genearateExcelForEval(List<DMSResponse> dmsResponseList, List<DmsWFTask> wfTaskList, String fileName,
			String sheetName) {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int rowNum = 0;
		Row row = sheet.createRow(rowNum++);
		List<String> rowHeaders = null;
		rowHeaders = getEvalutionRowHeaders();

		try {
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;
					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsAttachmentDto> dmsAttachments = dmsLeadDto.getDmsAttachments();

					List<DmsAccessoriesDto> dmsAccessoriesList = dmsLeadDto.getDmsAccessories();
					DmsWFTask dmsWFTask = null;
					DmsEntity dmsEntityOnRoadPrice = null;

					DmsBookingDto dmsBookingDto = dmsLeadDto.getDmsBooking();
					DMSResponse dmsResponseOnRoadPrice = null;
					List<DmsExchangeBuyer> dmsExchangeBuyerList = null;
					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();
						DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);
						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						String preEnqId = "";
						Timestamp preEnqDate = null;
						String preEnqMonthYear = "";
						String enqId = "";
						Timestamp enqDate = null;
						String enqMonthYear = "";
						try {
							List<LeadStageRefEntity> leadRefList = leadStageRefDao.findLeadsByLeadId(leadId);
							if (leadRefList != null && !leadRefList.isEmpty()) {
								leadRef = leadRefList.get(0);
							}
							for (LeadStageRefEntity tmpLeadRef : leadRefList) {
								if (tmpLeadRef.getStageName().equalsIgnoreCase(PREENQUIRY)) {
									preEnqId = tmpLeadRef.getRefNo();
									preEnqDate = tmpLeadRef.getStartDate();
									preEnqMonthYear = getMonthAndYear(tmpLeadRef.getStartDate());
								}
								if (tmpLeadRef.getStageName().equalsIgnoreCase(ENQUIRY)) {
									enqId = tmpLeadRef.getRefNo();
									enqDate = tmpLeadRef.getStartDate();
									enqMonthYear = getMonthAndYear(tmpLeadRef.getStartDate());
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						try {
							dmsResponseOnRoadPrice = restTemplate
									.getForEntity(leadOnRoadPriceUrl + leadId, DMSResponse.class).getBody();
							dmsEntityOnRoadPrice = dmsResponseOnRoadPrice.getDmsEntity();
						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						try {
							dmsExchangeBuyerList = dmsExchangeBuyerDao.getDmsExchangeBuyersByLeadId(leadId);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}

						try {
							Optional<DmsWFTask> dmsWFTaskOpt = wfTaskList.stream()
									.filter(x -> x.getUniversalId().equalsIgnoreCase(dmsLeadDto.getCrmUniversalId()))
									.findAny();
							if (dmsWFTaskOpt.isPresent()) {
								dmsWFTask = dmsWFTaskOpt.get();

							}

						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						String branchName = "";

						if (leadRef != null && leadRef.getBranchId() != null) {
							Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());

							if (optBranch.isPresent()) {
								DmsBranch branch = optBranch.get();
								branchName = branch.getName();
							}
						}
						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						// writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getProcessId() : "",
						// cellNum++);
						writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getUniversalId() : "", cellNum++);

						writeIntoCell(detailsRow,
								dmsWFTask != null && dmsWFTask.getTaskActualStartTime() != ""
										? ExcelUtil.getDateFormat(leadRef.getStartDate())
										: "",
								cellNum++);
						writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getTaskActualStartTime() : "",
								cellNum++);
						writeIntoCell(detailsRow, enqId, cellNum++);
						writeIntoCell(detailsRow, enqDate != null ? ExcelUtil.getDateFormat(enqDate) : "", cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(enqMonthYear), cellNum++);
						writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getTaskStatus() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						cellNum = addSingleAddress(addressList, detailsRow, cellNum);
						//
						final List<Map<String, Object>> evalutionList = buildEvaluation(dmsLeadDto.getCrmUniversalId());

						String vehicleType = "";
						String oldCarRegNo = "";
						String oldCarMake = "";
						String oldCarModel = "";
						String oldCarVariant = "";
						String oldCarColor = "";
						String oldCarFuel="";
						String oldCarTransmission = "";
						String vinNo = "";
						String makeYear = "";
						String oldCarManufacturDt = "";
						Date regExpiryDate = null;
						Integer kmsdriven = 0;
						Double expectedPrice = 0D;
						Double offeredPrice = 0D;
						Double gapPrice = 0D;
						Integer evaluatorName = 0;
						Integer evaluatorMgr = 0;

						for (Map<String, Object> m : evalutionList) {
							vehicleType = (String) m.get("vehicle_type");
							oldCarRegNo = (String) m.get("rc_no");
							oldCarMake = (String) m.get("make");
							oldCarModel = (String) m.get("model");
							oldCarVariant = (String) m.get("varient");
							oldCarFuel= (String) m.get("Fuel_Type");
							oldCarColor = (String) m.get("colour");
							oldCarTransmission = (String) m.get("transmission");
							vinNo = (String) m.get("chassis_no");
							oldCarManufacturDt = (String) m.get("year_month_of_manufacturing");
							makeYear = (String) m.get("year_month_of_manufacturing");
							regExpiryDate = (Date) m.get("reg_validity");
							kmsdriven = (Integer) m.get("km_driven");
							BigDecimal expPrice = null;
							try {
								expPrice = (BigDecimal) m.get("cust_expected_price");
							} catch (Exception e) {
								e.printStackTrace();
							}

							expectedPrice = expPrice.doubleValue();
							offeredPrice = 0D;

							if (m.get("evaluator_offer_price") != null) {
								BigDecimal bg = (BigDecimal) m.get("evaluator_offer_price");
								offeredPrice = bg.doubleValue();
							}

							evaluatorName = (Integer) m.get("evalutor_id");
							evaluatorMgr = (Integer) m.get("manager_id");
							if (offeredPrice != null && expectedPrice != null) {
								gapPrice = offeredPrice - expectedPrice;
							}
						}
						writeIntoCell(detailsRow, vehicleType, cellNum++);
						writeIntoCell(detailsRow, oldCarRegNo, cellNum++);
						writeIntoCell(detailsRow, oldCarMake, cellNum++);
						writeIntoCell(detailsRow, oldCarModel, cellNum++);
						writeIntoCell(detailsRow, oldCarVariant, cellNum++);
						writeIntoCell(detailsRow, oldCarColor, cellNum++);
						writeIntoCell(detailsRow, oldCarFuel, cellNum++);
						writeIntoCell(detailsRow, oldCarTransmission, cellNum++);
						writeIntoCell(detailsRow, vinNo, cellNum++);
						writeIntoCell(detailsRow, oldCarManufacturDt, cellNum++);
						writeIntoCell(detailsRow, makeYear, cellNum++);
						writeIntoCell(detailsRow, regExpiryDate != null ? regExpiryDate.toString() : "", cellNum++);
						writeIntoCell(detailsRow, String.valueOf(kmsdriven), cellNum++);
						writeIntoCell(detailsRow, String.format("%.0f", expectedPrice), cellNum++);
						writeIntoCell(detailsRow, String.format("%.0f", offeredPrice), cellNum++);
						writeIntoCell(detailsRow, String.format("%.0f", gapPrice), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getLeadStage(), cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto.getEnquiryCategory(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getBuyerType(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getAging(), cellNum++);
						writeIntoCell(detailsRow, getEmpNameById(evaluatorName), cellNum++);
						writeIntoCell(detailsRow,getEmpNameById(evaluatorMgr), cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSalesConsultant() : "", cellNum++);
				
						
						
						String empID = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, empID, cellNum++);
						String teamLeadName = getTeamLead(empID);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow, getManager(teamLeadName), cellNum++); // Manager

						writeIntoCell(detailsRow, getTaskRemarks(dmsLeadDto.getCrmUniversalId(),"Evaluation Approval"), cellNum++);
					}
				}
			}
			FileOutputStream out;
			out = new FileOutputStream(new File(fileName));
			workbook.write(out);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception:", e);
		}

	}

	private String getEmpNameById(Integer eId) {
		log.debug("getEmpNameById ,eId "+eId);
		String res = "";
		try {
			if (eId > 0) {

				res = dmsEmployeeRepo.findEmpNameById(String.valueOf(eId));

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception in getEmpNameById:", e);
		}
		return res;
	}

	private void genearateExcelForTD(List<DMSResponse> dmsResponseList, List<DmsWFTask> wfTaskListTD, String fileName,
			String sheetName, String orgId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(sheetName);
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = null;

			if (sheetName.equalsIgnoreCase("Test Drive")) {
				rowHeaders = getTestDriveRowHeaders();
			}

			if (sheetName.equalsIgnoreCase("Home Visit")) {
				rowHeaders = getHomeVisitRowHeaders();
			}

			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;
					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsAttachmentDto> dmsAttachments = dmsLeadDto.getDmsAttachments();
					DmsEntity dmsEntityOnRoadPrice = null;

					List<DmsAccessoriesDto> dmsAccessoriesList = dmsLeadDto.getDmsAccessories();
					DmsWFTask dmsWFTask = null;
					DMSResponse dmsResponseOnRoadPrice = null;

					DmsBookingDto dmsBookingDto = dmsLeadDto.getDmsBooking();
					List<DmsExchangeBuyer> dmsExchangeBuyerList = null;
					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();
						DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);

						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						String preEnqId = "";
						Timestamp preEnqDate = null;
						String preEnqMonthYear = "";
						String enqId = "";
						Timestamp enqDate = null;
						String enqMonthYear = "";
						try {
							List<LeadStageRefEntity> leadRefList = leadStageRefDao.findLeadsByLeadId(leadId);
							if (leadRefList != null && !leadRefList.isEmpty()) {
								leadRef = leadRefList.get(0);
							}
							for (LeadStageRefEntity tmpLeadRef : leadRefList) {
								if (tmpLeadRef.getStageName().equalsIgnoreCase(PREENQUIRY)) {
									preEnqId = tmpLeadRef.getRefNo();
									preEnqDate = tmpLeadRef.getStartDate();
									preEnqMonthYear = getMonthAndYear(tmpLeadRef.getStartDate());
								}
								if (tmpLeadRef.getStageName().equalsIgnoreCase(ENQUIRY)) {
									enqId = tmpLeadRef.getRefNo();
									enqDate = tmpLeadRef.getStartDate();
									enqMonthYear = getMonthAndYear(tmpLeadRef.getStartDate());
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						try {
							dmsResponseOnRoadPrice = restTemplate
									.getForEntity(leadOnRoadPriceUrl + leadId, DMSResponse.class).getBody();
							dmsEntityOnRoadPrice = dmsResponseOnRoadPrice.getDmsEntity();
						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						try {
							dmsExchangeBuyerList = dmsExchangeBuyerDao.getDmsExchangeBuyersByLeadId(leadId);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}

						try {
							Optional<DmsWFTask> dmsWFTaskOpt = wfTaskListTD.stream()
									.filter(x -> x.getUniversalId().equalsIgnoreCase(dmsLeadDto.getCrmUniversalId()))
									.findAny();
							if (dmsWFTaskOpt.isPresent()) {
								dmsWFTask = dmsWFTaskOpt.get();

							}

						} catch (Exception e) {
							e.printStackTrace();
							log.error("exception:", e);
						}
						String branchName = "";

						if (leadRef != null && leadRef.getBranchId() != null) {
							//System.out.println("leadRef.getBranchId() " + leadRef.getBranchId());
							Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());

							if (optBranch.isPresent()) {
								DmsBranch branch = optBranch.get();
								branchName = branch.getName();
							}
						}

				
						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						// writeIntoCell(detailsRow, dmsWFTask!=null?dmsWFTask.getProcessId():"",
						// cellNum++);
						writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getUniversalId() : "", cellNum++);
				
						writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getTaskActualStartTime() : "",
								cellNum++);
						// writeIntoCell(detailsRow,
						// getMonthAndYear(dmsWFTask.getTaskActualStartTime()), cellNum++);

						writeIntoCell(detailsRow, preEnqId, cellNum++);
						writeIntoCell(detailsRow, preEnqDate != null ? ExcelUtil.getDateFormat(preEnqDate) : "",
								cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(preEnqMonthYear), cellNum++);
						writeIntoCell(detailsRow, enqId, cellNum++);
						writeIntoCell(detailsRow, enqDate != null ? ExcelUtil.getDateFormat(enqDate) : "", cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(enqMonthYear), cellNum++);
						if (sheetName.equalsIgnoreCase("Test Drive")) {
							writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
							writeIntoCell(detailsRow, dmsLeadDto.getLastName(), cellNum++);
						}

						if (sheetName.equalsIgnoreCase("Home Visit")) {
							writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						}

						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getEmail() : "", cellNum++);

						cellNum = addSingleAddress(addressList, detailsRow, cellNum);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getModel() : "", cellNum++);
						cellNum = addDmsLeadProducts(dmsLeadProductDtoList, detailsRow, cellNum);
						writeIntoCell(detailsRow, getSource(dmsLeadDto != null ? dmsLeadDto.getSourceOfEnquiry() : 0),
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSubSource() : "", cellNum++);

						if (sheetName.equalsIgnoreCase("Test Drive")) {
							List<Map<String, Object>> list = buildTestDrive(dmsLeadDto.getCrmUniversalId());
							
			
							String address = "";
							String model = "";
							String varient = "";
							String location ="";
							String driverId="";
							for (Map<String, Object> m : list) {
								
								location = (String) m.get("location");
								address = (String) m.get("address");
								model = (String) m.get("vehicle_id");
								varient = (String) m.get("varient_id");
								driverId = (String)m.get("driver_id");
								log.debug("model:::"+model);	
								log.debug("varient:::"+varient);
								if(model!=null  && model.length()>0) {
									model = getModelName(orgId, model);
								}
								if(varient!=null  && varient.length()>0) {
									varient = getModelVariant(varient);
								}
								if(location!=null &&  location.equalsIgnoreCase("showroom")) {
									BigInteger branchId = (BigInteger)m.get("branch_id");
									address = getBranchAddress(branchId,orgId);
								}
							}
							
							
							writeIntoCell(detailsRow, address, cellNum++);
							writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getTaskStatus() : "", cellNum++);
							
							writeIntoCell(detailsRow, model, cellNum++);// Test drive Model
							writeIntoCell(detailsRow, varient, cellNum++);// Test drive variant
							writeIntoCell(detailsRow, driverId, cellNum++);// Test drive variant
						}
						
						if (sheetName.equalsIgnoreCase("Home Visit")) {
							writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getTaskStatus() : "", cellNum++);
						}

						//
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSalesConsultant() : "", cellNum++);
						String empId = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, getEmpName(dmsLeadDto.getSalesConsultant()), cellNum++);
						String teamLeadName = getTeamLead(empId);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow, getManager(teamLeadName), cellNum++); // Manager

						//writeIntoCell(detailsRow, dmsWFTask != null ? dmsWFTask.getEmployeeRemarks() : "", cellNum++);
								
						writeIntoCell(detailsRow, getTaskRemarks(dmsLeadDto.getCrmUniversalId(),"Test Drive Approval"), cellNum++);
					}
				}
			}
			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception while generatingExcelForTD ",e);
		}

	}

	private String getBranchAddress(BigInteger branchId, String orgId) {
		log.debug("getBranchAddress ::::");		
		final List<Map<String, Object>> jObjList = new ArrayList<>();
		String res="";
		try {
			String q = "SELECT * FROM salesDataSetup.dms_address where id in (SELECT address FROM salesDataSetup.dms_branch where branch_id="+branchId+")";
			

			List<Object[]> colnHeadersList = new ArrayList<>();
			colnHeadersList = entityManager.createNativeQuery("DESCRIBE salesDataSetup.dms_address").getResultList();

			List<String> headers = new ArrayList<>();
			for (Object[] arr : colnHeadersList) {

				String colName = (String) arr[0];
				headers.add(colName);

			}

			List<Object[]> queryResults = entityManager.createNativeQuery(q).getResultList();

			for (int i = 0; i < queryResults.size(); i++) {
				Object[] objArr = queryResults.get(i);
				Map<String, Object> map = new LinkedHashMap<>();
				for (int j = 0; j < objArr.length; j++) {
					String colName = headers.get(j);
					map.put(colName, objArr[j]);
				}
				jObjList.add(map);
			}
			
		
			for (Map<String, Object> m : jObjList) {

				String hNo = (String)m.get("house_no");
				String street = (String)m.get("street");
				String city = (String)m.get("city");
				String pinCode = (String)m.get("district");
				
				res = hNo+","+street+","+city+","+pinCode;

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in getBranchAddress method ",e);
		}
		return res;
	}

	private String zipFiles(List<File> fileList) {
		String zipFileName = tmpPath + "ETVBRL_" + System.currentTimeMillis() + ".zip";
		try {

			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (null != file && file.exists()) {
					FileInputStream fis = new FileInputStream(file);

					ZipEntry zipEntry = new ZipEntry(file.getName());
					zos.putNextEntry(zipEntry);

					byte[] bytes = new byte[1024];
					int length;
					while ((length = fis.read(bytes)) >= 0) {
						zos.write(bytes, 0, length);
					}

					zos.closeEntry();
					fis.close();
				}

			}

			zos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception ", e);
		}

		return zipFileName;

	}

	private void genearateExcelForDelivery(List<DMSResponse> dmsResponseDeliveryList,
			List<LeadStageRefEntity> leadRefDBList, String fileName, String orgId) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Delivery");
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = getDeliveryRowHeaders();
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseDeliveryList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;
					DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);
					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsAttachmentDto> dmsAttachments = dmsLeadDto.getDmsAttachments();
					List<DmsAccessoriesDto> dmsAccessoriesList = dmsLeadDto.getDmsAccessories();

					DmsBookingDto dmsBookingDto = dmsLeadDto.getDmsBooking();
					DMSResponse dmsResponseOnRoadPrice = null;
					List<DmsDelivery> deliveryList = null;
					DmsDelivery dmsDelivery = null;
					List<DmsInvoice> dmsInvoiceList = null;
					DmsInvoice dmsInvoice = null;
					DmsEntity dmsEntityOnRoadPrice = null;

					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();

						log.debug("leadId:::" + leadId);
						try {
							dmsResponseOnRoadPrice = restTemplate
									.getForEntity(leadOnRoadPriceUrl + leadId, DMSResponse.class).getBody();
							dmsEntityOnRoadPrice = dmsResponseOnRoadPrice.getDmsEntity();
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							deliveryList = dmsDeliveryDao.getDeliveriesWithLeadId(String.valueOf(leadId));
									if (null != deliveryList && !deliveryList.isEmpty()) {
								dmsDelivery = deliveryList.get(0);
							}
						} catch (Exception e) {
							e.printStackTrace();
							log.error("e", e);
						}
						List<LeadStageRefEntity> leadRefList = leadRefDBList.stream()
								.filter(x -> x.getLeadId() != null && (x.getLeadId()) == leadId)
								.collect(Collectors.toList());
						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						if (null != leadRefList && !leadRefList.isEmpty()) {
							leadRef = leadRefList.get(0);
						}
						log.debug("leadRef::" + leadRef);
						Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());
						String branchName = "";
						if (optBranch.isPresent()) {
							DmsBranch branch = optBranch.get();
							branchName = branch.getName();
						}

						try {
							dmsInvoiceList = dmsInvoiceDao.getInvoiceDataWithLeadId(leadId);
							if (dmsInvoiceList != null && !dmsInvoiceList.isEmpty()) {
								dmsInvoice = dmsInvoiceList.get(0);
							}
						} catch (Exception e) {
							e.printStackTrace();

						}
						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto!=null?dmsLeadDto.getCrmUniversalId():"",cellNum++);
						writeIntoCell(detailsRow, leadRef.getRefNo(), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.getDateFormat(leadRef.getStartDate()), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(getMonthAndYear(leadRef.getStartDate())),
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getSalutation() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getLastName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getEmail() : "", cellNum++);
						/*
						 * writeIntoCell(detailsRow, dmsContactDto != null ?
						 * dmsContactDto.getDateOfBirth() : "", cellNum++);
						 */
						cellNum = addSingleAddress(addressList, detailsRow, cellNum);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getModel() : "", cellNum++);
						cellNum = addDmsLeadProducts(dmsLeadProductDtoList, detailsRow, cellNum);

						List<DmsAllotment> optAllot = dmsAllotmentDao.getByLeadId(leadId);
						if (optAllot != null && !optAllot.isEmpty()) {
							DmsAllotment allot = optAllot.get(0);

							writeIntoCell(detailsRow, getEngineCC(allot.getVarient(), orgId), cellNum++);
							writeIntoCell(detailsRow, allot.getVinno(), cellNum++);
							writeIntoCell(detailsRow, allot.getEngineNo(), cellNum++);
							writeIntoCell(detailsRow, allot.getChassisNo(), cellNum++);

						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						cellNum = addAttachments(dmsAttachments, detailsRow, cellNum);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getGstNumber() : "", cellNum++);
						log.debug("LEAD ID TO GET REF "+leadId);
						writeIntoCell(detailsRow, leadStageRefDao.findRefByLeadIdStge(leadId, "BOOKING"), cellNum++);
						writeIntoCell(detailsRow, getDateByStage(dmsLeadData.getId(),"BOOKING"),
								cellNum++);

						writeIntoCell(detailsRow, leadStageRefDao.findRefByLeadIdStge(leadId, "ENQUIRY"), cellNum++); // Enquiry
																														// ID
						// writeIntoCell(detailsRow, getRefNo(leadRef.getLeadId(), ENQUIRY), cellNum++);
						String enqDate = ExcelUtil.getDateFormatV2OnlyDate(dmsLeadDto.getDateOfEnquiry());
						writeIntoCell(detailsRow, enqDate, cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getEnquirySegment() : "", cellNum++);

						String customerType = getCustomerTypeByLeadId(leadId);
						writeIntoCell(detailsRow, customerType, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getEnquirySource() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSubSource() : "", cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getCorporateName() : "",
								cellNum++);
						cellNum = addEventDetails(detailsRow, cellNum, leadId, dmsLeadDto.getEventCode());

						String invoiceDate = leadStageRefDao.findStartTimeByLeadIdStge(leadId, INVOICE);
						String delDate = leadStageRefDao.findStartTimeByLeadIdStge(leadId, DELIVERY);

						if (invoiceDate != null && delDate != null) {
							invoiceDate = invoiceDate.split(" ")[0];
							delDate = delDate.split(" ")[0];
							writeIntoCell(detailsRow, findDaysBetweenDates(invoiceDate, delDate), cellNum++);
						} else {
							writeIntoCell(detailsRow, 0, cellNum++);
						}

						cellNum = addBookingDetails(dmsResponseOnRoadPrice, dmsBookingDto, detailsRow, cellNum,
								dmsInvoice, dmsEntityOnRoadPrice,orgId);
						writeIntoCell(detailsRow, dmsInvoice != null ? dmsInvoice.getInvoiceDate() : "", cellNum++); // Vehicle Purchase date
						writeIntoCell(detailsRow, dmsInvoice != null ? dmsInvoice.getTotalAmount() : "", cellNum++); // Vehicle
																														
						String buyerType = dmsLeadDto != null ? dmsLeadDto.getBuyerType() : "";																							// Amount
						if (dmsLeadDto != null && dmsLeadDto.getBuyerType().equalsIgnoreCase("Replacement Buyer")) {
							writeIntoCell(detailsRow, "YES", cellNum++);
						} else {
							writeIntoCell(detailsRow, "NO", cellNum++);
						}
						writeIntoCell(detailsRow, buyerType, cellNum++);

						final List<Map<String, Object>> evalutionList = buildEvaluation(dmsLeadDto.getCrmUniversalId());

						String evalId = "";
						Timestamp evalDate = null;
						for (Map<String, Object> m : evalutionList) {
							evalId = "" + (Integer) m.get("evalutor_id");
							evalDate = (Timestamp) m.get("updated_date");
						}
						writeIntoCell(detailsRow, evalId, cellNum++); // Evaluation ID
						writeIntoCell(detailsRow, evalDate != null ? evalDate.toString() : "", cellNum++); // Evaluation
																											// Date

						if (dmsExchagedetailsList != null && !dmsExchagedetailsList.isEmpty()) {
							DmsExchangeBuyerDto a1 = dmsExchagedetailsList.get(0);
							if (null != a1) {
								writeIntoCell(detailsRow, a1.getRegNo(), cellNum++);
								writeIntoCell(detailsRow, a1.getBrand(), cellNum++);
								writeIntoCell(detailsRow, a1.getModel(), cellNum++);
								writeIntoCell(detailsRow, a1.getVarient(), cellNum++);
								BigDecimal d = a1.getOfferedPrice();
								if(d!=null) {
									writeIntoCell(detailsRow, d.toString(), cellNum++);
								}else {
									writeIntoCell(detailsRow,"", cellNum++);
								}
								
							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);

							}
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getRelationName() : "", cellNum++);

					

						log.debug("Calling getDeliveriesWithLeadId:::"+leadId);
						try {
						List<DmsDelivery> delList = dmsDeliveryDao.getDeliveriesWithLeadId(String.valueOf(leadId));
						
						if (delList != null && !delList.isEmpty()) {
							DmsDelivery dd = delList.get(0);
							writeIntoCell(detailsRow, dd.getWarrantyTaken()!=null?dd.getWarrantyTaken():"NO", cellNum++);// EW status
							writeIntoCell(detailsRow, dd.getEtdWarrantyNo(), cellNum++);// EW Number
							writeIntoCell(detailsRow, dd.getExWarStartDt(), cellNum++);// EW Start date
							writeIntoCell(detailsRow, dd.getExWarEndDt(), cellNum++);// EW End date"
						} else {

							writeIntoCell(detailsRow, "", cellNum++);// EW status
							writeIntoCell(detailsRow, "", cellNum++);// EW Number
							writeIntoCell(detailsRow, "", cellNum++);// EW Start date
							writeIntoCell(detailsRow, "", cellNum++);// EW End date"
						}
						}catch (Exception e) {
							log.error("Exception while adding getDeliveriesWithLeadId ",e);
							writeIntoCell(detailsRow, "", cellNum++);// EW status
							writeIntoCell(detailsRow, "", cellNum++);// EW Number
							writeIntoCell(detailsRow, "", cellNum++);// EW Start date
							writeIntoCell(detailsRow, "", cellNum++);// EW End date"
						}
				

						String insuranceType="";
						String insuranceNumber = "";
						String insuranceCompany = "";
						java.util.Date insurStartDate = null;
						java.util.Date insurEndDate = null;
						String insurPremium = "";
						if (dmsDelivery != null) {
							insuranceType = dmsDelivery.getInsuranceTaken();
							insuranceNumber = dmsDelivery.getInsurancePolicyNo();
							insuranceCompany = dmsDelivery.getInsuranceCompany();
							insurStartDate = dmsDelivery.getInsuranceDate() != null ? dmsDelivery.getInsuranceDate()
									: null;
							insurEndDate = dmsDelivery.getInsurenceExpDate() != null ? dmsDelivery.getInsurenceExpDate()
									: null;

						}
						writeIntoCell(detailsRow, insuranceType, cellNum++);// Insurane Number
						writeIntoCell(detailsRow, insuranceNumber, cellNum++);// Insurane Number
						writeIntoCell(detailsRow, insuranceCompany, cellNum++);// Insurance Company
						writeIntoCell(detailsRow,
								insurStartDate != null ? ExcelUtil.getFormateDate(insurStartDate) : "", cellNum++);// Insurance
																													// Start
																													// date
						writeIntoCell(detailsRow, insurEndDate != null ? ExcelUtil.getFormateDate(insurEndDate) : "",
								cellNum++);// Insurance End date
						writeIntoCell(detailsRow, insurPremium, cellNum++);// Insurance premium

						Double accessoiresAmt = 0D;
						String parts = "";
						if (dmsAccessoriesList != null && !dmsAccessoriesList.isEmpty()) {
							//DmsAccessoriesDto a = dmsAccessoriesList.get(0);
							for (DmsAccessoriesDto d : dmsAccessoriesList) {
								accessoiresAmt = accessoiresAmt + d.getAmount();
								parts = parts + "," + d.getAccessoriesName(); // need to change
							}

						}

						writeIntoCell(detailsRow, accessoiresAmt, cellNum++);
						writeIntoCell(detailsRow, parts, cellNum++);

						String finCategory = "";
						String finName = "";
						String finBranch = "";
						String finLoanAMt = "";
						String finRoI = "";
						String loanTenure = "";
						String emiAmt = "";
						String payout = "";
						String netpayout = "";
						if (dmsFinanceDetailsList != null && !dmsFinanceDetailsList.isEmpty()) {
							DmsFinanceDetailsDto dmsFinanceDetailsDto = dmsFinanceDetailsList.get(0);
							finCategory = dmsFinanceDetailsDto.getFinanceType();
							finName = dmsFinanceDetailsDto.getFinanceCompany();
							finBranch = dmsFinanceDetailsDto.getLocation();
							finLoanAMt = "" + dmsFinanceDetailsDto.getLoanAmount();
							finRoI = dmsFinanceDetailsDto.getRateOfInterest();
							loanTenure = dmsFinanceDetailsDto.getExpectedTenureYears();
							if(dmsFinanceDetailsDto.getEmi()!=null) {
								emiAmt = "" + dmsFinanceDetailsDto.getEmi();
							}else {
								emiAmt="";
							}
							
							payout = "" + dmsFinanceDetailsDto.getLoanAmount();
							netpayout = "" + dmsFinanceDetailsDto.getLoanAmount();

						}
						writeIntoCell(detailsRow, finCategory, cellNum++);
						writeIntoCell(detailsRow, finName, cellNum++);
						writeIntoCell(detailsRow, finBranch, cellNum++);
						writeIntoCell(detailsRow, finLoanAMt, cellNum++);
						writeIntoCell(detailsRow, finRoI, cellNum++);
						writeIntoCell(detailsRow, loanTenure, cellNum++);
						writeIntoCell(detailsRow, emiAmt, cellNum++);
						writeIntoCell(detailsRow, payout, cellNum++);
						writeIntoCell(detailsRow, netpayout, cellNum++);

						writeIntoCell(detailsRow, "", cellNum++); // Disbursed date
						writeIntoCell(detailsRow, "", cellNum++); // Disbursed amount

						writeIntoCell(detailsRow, "", cellNum++); // Payment ref number

						String cancelDate = "";
						String lostReason = "";
						String lostSubReason = "";
						if (dmsLeadDto.getLeadStage().equalsIgnoreCase(DROPPED)) {
							List<DmsLeadDrop> dropList = dmsLeadDropDao.getByLeadId(leadId);
							if (dropList != null && !dropList.isEmpty()) {
								DmsLeadDrop droppedLead = dropList.get(0);
								cancelDate = droppedLead != null ? droppedLead.getCreatedDateTime() : "";
								lostReason = droppedLead.getLostReason();
								lostSubReason = droppedLead.getLostSubReason();
							}
						}
						writeIntoCell(detailsRow,
								cancelDate != "" ? ExcelUtil.getDateFormat(Timestamp.valueOf(cancelDate)) : "",
								cellNum++);
						writeIntoCell(detailsRow, lostReason, cellNum++);
						writeIntoCell(detailsRow, lostSubReason, cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSalesConsultant() : "", cellNum++);
						String empId = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, getEmpName(dmsLeadDto.getSalesConsultant()), cellNum++);
						String teamLeadName = getTeamLead(empId);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow, getManager(teamLeadName), cellNum++); // Manager

						writeIntoCell(detailsRow, getFinanceExective(dmsLeadDto.getCrmUniversalId()), cellNum++); // Finance Executive
						writeIntoCell(detailsRow, getEvaluator(dmsLeadDto.getCrmUniversalId()), cellNum++); // evaluator name
					}
				}
			}
			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in generateDelivery ",e);
		}
	}

	private String getDateByStage(int id, String stage) {
		
		if(null!=stage) {
			return leadStageRefDao.findDateByLeadIdStge(id, stage);
		}
		return null;
	}

	private String getEngineCC(String varient, String orgId) {
		String cc = "";
		Object obj = null;
		try {
			varient = quote(varient);
			String vehQuery = "SELECT enginecc FROM `vehicle-management`.vehicle_varient_new where name=" + varient
					+ " and org_id=" + orgId;
			log.debug("vehQuery ::" + vehQuery);
			obj = entityManager.createNativeQuery(vehQuery).getSingleResult();
			if (obj != null) {
				cc = (String) obj;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception", e);
		}
		return cc;
	}
	
	private String getModelName(String orgId,String modelId) {
		String cc = "";
		Object obj = null;
		try {
			orgId = quote(orgId);
			modelId = quote(modelId);
			String vehQuery = "SELECT * FROM `vehicle-management`.vehicle_details_new where status='Active' and organization_id="+orgId+"and id="+modelId;
			log.debug("vehQuery ::" + vehQuery);
			obj = entityManager.createNativeQuery(vehQuery).getSingleResult();
			if (obj != null) {
				cc = (String) obj;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception in getModelName", e);
		}
		return cc;
	}
	
	private String getModelVariant(String varientId) {
		String cc = "";
		Object obj = null;
		try {
		
			varientId = quote(varientId);
			String vehQuery = "SELECT * FROM `vehicle-management`.vehicle_varient_new where id="+varientId;
			log.debug("vehQuery ::" + vehQuery);
			obj = entityManager.createNativeQuery(vehQuery).getSingleResult();
			if (obj != null) {
				cc = (String) obj;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception in getModelName", e);
		}
		return cc;
	}
	
	
	

	public static String quote(String s) {
		return new StringBuilder().append('\'').append(s).append('\'').toString();
	}

	private List<Map<String, Object>> buildEvaluation(String crmUniversalId) {
		String q = "select * from ops.vehicle_evalutions where customer_id=";
		q = q + quote(crmUniversalId);
		log.debug("vehicle_evalutions query :" + q);

		List<Object[]> colnHeadersList = new ArrayList<>();
		colnHeadersList = entityManager.createNativeQuery("DESCRIBE ops.vehicle_evalutions").getResultList();

		List<String> headers = new ArrayList<>();
		for (Object[] arr : colnHeadersList) {

			String colName = (String) arr[0];
			headers.add(colName);

		}

		final List<Map<String, Object>> jObjList = new ArrayList<>();

		List<Object[]> queryResults = entityManager.createNativeQuery(q).getResultList();

		for (int i = 0; i < queryResults.size(); i++) {
			Object[] objArr = queryResults.get(i);
			Map<String, Object> map = new LinkedHashMap<>();
			for (int j = 0; j < objArr.length; j++) {
				String colName = headers.get(j);
				map.put(colName, objArr[j]);
			}
			jObjList.add(map);
		}
		return jObjList;
	}

	private List<Map<String, Object>> buildTestDrive(String crmUniversalId) {
		final List<Map<String, Object>> jObjList = new ArrayList<>();
		try {
			String q = "select * from ops.test_drive_details where customer_id=";
			q = q + quote(crmUniversalId);
			log.debug("test_drive_details query :" + q);

			List<Object[]> colnHeadersList = new ArrayList<>();
			colnHeadersList = entityManager.createNativeQuery("DESCRIBE ops.test_drive_details").getResultList();

			List<String> headers = new ArrayList<>();
			for (Object[] arr : colnHeadersList) {

				String colName = (String) arr[0];
				headers.add(colName);

				
			}

			List<Object[]> queryResults = entityManager.createNativeQuery(q).getResultList();

			for (int i = 0; i < queryResults.size(); i++) {
				Object[] objArr = queryResults.get(i);
				Map<String, Object> map = new LinkedHashMap<>();
				for (int j = 0; j < objArr.length; j++) {
					String colName = headers.get(j);
					map.put(colName, objArr[j]);
				}
				jObjList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in buildTestDrive method");
		}
		return jObjList;
	}
	
	

	private List<Map<String, Object>> buildEvents(String eventId) {
		String q = "select * from ops.event_details where id=";
		q = q + quote(eventId);
		log.debug("buildEvents query :" + q);

		List<Object[]> colnHeadersList = new ArrayList<>();
		colnHeadersList = entityManager.createNativeQuery("DESCRIBE ops.event_details").getResultList();

		List<String> headers = new ArrayList<>();
		for (Object[] arr : colnHeadersList) {

			String colName = (String) arr[0];
			headers.add(colName);

		}

		final List<Map<String, Object>> jObjList = new ArrayList<>();

		List<Object[]> queryResults = entityManager.createNativeQuery(q).getResultList();

		for (int i = 0; i < queryResults.size(); i++) {
			Object[] objArr = queryResults.get(i);
			Map<String, Object> map = new LinkedHashMap<>();
			for (int j = 0; j < objArr.length; j++) {
				String colName = headers.get(j);
				map.put(colName, objArr[j]);
			}
			jObjList.add(map);
		}
		log.debug("Events Data " + jObjList);
		return jObjList;
	}

	private Object buildRetailToDeliveryConvesionDays(int leadId, String stage1, String stage2) {
		// TODO Auto-generated method stub
		List<LeadStageRefEntity> list = leadStageRefDao.findLeadsByLeadId(leadId);

		Timestamp ts1 = null;
		Timestamp ts2 = null;
		if (list != null && !list.isEmpty()) {
			for (LeadStageRefEntity ref : list) {

				if (ref.getStageName().equals(stage1)) {
					ts1 = ref.getStartDate();
				}
				if (ref.getStageName().equals(stage2)) {
					ts2 = ref.getStartDate();
				}
			}
		}
		long diff = 0L;
		System.out.println("ts1 " + ts1 + " ts2" + ts2);

		if (ts1 != null && ts2 != null) {
			diff = ts2.getTime() - ts1.getTime();
			diff = TimeUnit.MILLISECONDS.toDays(diff);
		}
		return diff;
	}

	private Object getRefNo(Integer leadId, String stage) {
		String refNo = "";
		try {
			refNo = leadStageRefDao.findRefByLeadIdStge(leadId, stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refNo;
	}


	private void genearateExcelForInvoice(List<DMSResponse> dmsResponseInvoiceList,
			List<LeadStageRefEntity> leadRefDBList, String fileName, String orgId) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Invoice");
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = getRetailRowHeaders();
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseInvoiceList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;
					// DmsContactDto dmsContactDto = dmsEntity.getDmsContactDto();
					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsAttachmentDto> dmsAttachments = dmsLeadDto.getDmsAttachments();
					DmsEntity dmsEntityOnRoadPrice = null;
					DmsBookingDto dmsBookingDto = dmsLeadDto.getDmsBooking();
					DMSResponse dmsResponseOnRoadPrice = null;

					List<DmsInvoice> dmsInvoiceList = null;
					DmsInvoice dmsInvoice = null;
					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();
						DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);

						try {
							dmsResponseOnRoadPrice = restTemplate
									.getForEntity(leadOnRoadPriceUrl + leadId, DMSResponse.class).getBody();
							dmsEntityOnRoadPrice = dmsResponseOnRoadPrice.getDmsEntity();
						} catch (Exception e) {
							e.printStackTrace();
						}

						List<LeadStageRefEntity> leadRefList = leadRefDBList.stream()
								.filter(x -> x.getLeadId() != null && (x.getLeadId()) == leadId)
								.collect(Collectors.toList());
						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						if (null != leadRefList && !leadRefList.isEmpty()) {
							leadRef = leadRefList.get(0);
						}
						log.debug("leadRef::" + leadRef);
						Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());
						String branchName = "";
						if (optBranch.isPresent()) {
							DmsBranch branch = optBranch.get();
							branchName = branch.getName();
						}
						try {
							dmsInvoiceList = dmsInvoiceDao.getInvoiceDataWithLeadId(leadId);
							
							if (dmsInvoiceList != null && !dmsInvoiceList.isEmpty()) {
								dmsInvoice = dmsInvoiceList.get(0);
							}
						} catch (Exception e) {
							e.printStackTrace();

						}
						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto!=null?dmsLeadDto.getCrmUniversalId():"",cellNum++);
						writeIntoCell(detailsRow, leadRef.getRefNo(), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.getDateFormat(leadRef.getStartDate()), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(getMonthAndYear(leadRef.getStartDate())),
								cellNum++);
						// writeIntoCell(detailsRow, leadRef.getRefNo(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getSalutation() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getLastName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getEmail() : "", cellNum++);

						cellNum = addSingleAddress(addressList, detailsRow, cellNum);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getModel() : "", cellNum++);
						cellNum = addDmsLeadProducts(dmsLeadProductDtoList, detailsRow, cellNum);

						List<DmsAllotment> optAllot = dmsAllotmentDao.getByLeadId(leadId);
						if (optAllot != null && !optAllot.isEmpty()) {
							DmsAllotment allot = optAllot.get(0);

							writeIntoCell(detailsRow, allot.getVinno(), cellNum++);
							writeIntoCell(detailsRow, allot.getChassisNo(), cellNum++);
							writeIntoCell(detailsRow, allot.getEngineNo(), cellNum++);
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						cellNum = addAttachments(dmsAttachments, detailsRow, cellNum);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getGstNumber() : "", cellNum++);
						   writeIntoCell(detailsRow, getRefNo(dmsLeadDto.getId(),"BOOKING"), cellNum++);// Booking
																													// ID
						writeIntoCell(detailsRow,
								dmsBookingDto != null && dmsBookingDto.getCreatedDate() != null
										? ExcelUtil.getFormateDate(dmsBookingDto.getCreatedDate())
										: null,
								cellNum++);// Booking Date

						writeIntoCell(detailsRow, leadStageRefDao.findRefByLeadIdStge(leadId, "ENQUIRY"), cellNum++); // Enquiry
																														// ID
						writeIntoCell(detailsRow,
								dmsLeadData != null && dmsLeadData.getEnquiryDate() != null
										? ExcelUtil.getFormateDate(dmsLeadData.getEnquiryDate())
										: "",
								cellNum++);

						writeIntoCell(detailsRow,
								dmsLeadDto != null && dmsLeadDto.getEnquirySegment() != null
										? dmsLeadDto.getEnquirySegment()
										: "",
								cellNum++);

						String customerType = getCustomerTypeByLeadId(leadId);
						writeIntoCell(detailsRow, customerType, cellNum++);
						writeIntoCell(detailsRow, getSource(dmsLeadDto != null ? dmsLeadDto.getSourceOfEnquiry() : 0),
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSubSource() : "", cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getCorporateName() : "",
								cellNum++);

						cellNum = addEventDetails(detailsRow, cellNum, leadId, dmsLeadDto.getEventCode());
						String bookingDate = leadStageRefDao.findStartTimeByLeadIdStge(leadId, "BOOKING");
						String retailDate = leadStageRefDao.findStartTimeByLeadIdStge(leadId, "INVOICE");

						if (bookingDate != null && retailDate != null) {
							bookingDate = bookingDate.split(" ")[0];
							retailDate = retailDate.split(" ")[0];
							log.debug("bookingDate:::::" + bookingDate + ",retailDate::" + retailDate);
							long days = findDaysBetweenDates(bookingDate, retailDate);
							writeIntoCell(detailsRow, days, cellNum++); // Booking to Retail Conversion days
						} else {
							writeIntoCell(detailsRow, 0, cellNum++);
						}

						cellNum = addBookingDetails(dmsResponseOnRoadPrice, dmsBookingDto, detailsRow, cellNum,
								dmsInvoice, dmsEntityOnRoadPrice,orgId);

						if (dmsFinanceDetailsList != null && !dmsFinanceDetailsList.isEmpty()) {
							DmsFinanceDetailsDto dmsFinanceDetailsDto = dmsFinanceDetailsList.get(0);
							writeIntoCell(detailsRow, dmsFinanceDetailsDto.getFinanceType(), cellNum++);
							writeIntoCell(detailsRow, dmsFinanceDetailsDto.getFinanceCompany(), cellNum++);
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getBuyerType() : "", cellNum++);

						//

						if (dmsExchagedetailsList != null && !dmsExchagedetailsList.isEmpty()) {
							DmsExchangeBuyerDto a1 = dmsExchagedetailsList.get(0);
							if (null != a1) {
								writeIntoCell(detailsRow, a1.getRegNo(), cellNum++);
								writeIntoCell(detailsRow, a1.getBrand(), cellNum++);
								writeIntoCell(detailsRow, a1.getModel(), cellNum++);
								writeIntoCell(detailsRow, a1.getVarient(), cellNum++);
								if (a1.getOfferedPrice() != null) {
									BigDecimal b = a1.getOfferedPrice();
									writeIntoCell(detailsRow,b!=null?b.doubleValue():"", cellNum++);
								} else {
									writeIntoCell(detailsRow, "", cellNum++);
								}
							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);

							}
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getRelationName() : "", cellNum++); // relation
																														// with
																														// customer
						String cancelDate = "";
						String lostReason = "";
						String lostSubReason = "";
						if (dmsLeadDto.getLeadStage().equalsIgnoreCase(DROPPED)) {
							List<DmsLeadDrop> dropList = dmsLeadDropDao.getByLeadId(leadId);
							if (dropList != null && !dropList.isEmpty()) {
								DmsLeadDrop droppedLead = dropList.get(0);

								cancelDate = droppedLead.getCreatedDateTime();
								lostReason = droppedLead.getLostReason();
								lostSubReason = droppedLead.getLostSubReason();

							}
						}

						writeIntoCell(detailsRow,
								cancelDate != null && cancelDate != ""
										? ExcelUtil.getFormateDate(Timestamp.valueOf(cancelDate))
										: "",
								cellNum++);
						writeIntoCell(detailsRow, lostReason, cellNum++);
						writeIntoCell(detailsRow, lostSubReason, cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSalesConsultant() : "", cellNum++);
						String empId = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, getEmpName(dmsLeadDto.getSalesConsultant()), cellNum++);
						
							
						
						
						String teamLeadName = getTeamLead(empId);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow,getManager(teamLeadName), cellNum++); // Manager
						writeIntoCell(detailsRow, getFinanceExective(dmsLeadDto.getCrmUniversalId()), cellNum++); // Finance Executive

						//final List<Map<String, Object>> evalutionList = buildEvaluation(dmsLeadDto.getCrmUniversalId());

					
						writeIntoCell(detailsRow, getEvaluator(dmsLeadDto.getCrmUniversalId()), cellNum++); // evaluator name

					}
				}
			}
			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception while generating invoice report ",e);
		}
	}

	private String getEvaluator(String crmUniversalId) {
		String res = "";
		try {
			List<DmsWFTask> list = dmsWfTaskDao.getWfTaskByUniversalIdandTask(crmUniversalId, "Evaluation");
			if (list != null && !list.isEmpty()) {
				DmsWFTask wf = list.get(0);
				String id = wf.getAssigneeId();
				if (null != id) {
					Optional<DmsEmployee> opt = dmsEmployeeRepo.findById(Integer.parseInt(id));
					if (opt.isPresent()) {
						res = opt.get().getEmpName();

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception in getFinanceExective ", e);

		}
		return res;
	}

	private int addBookingDetails(DMSResponse dmsResponseOnRoadPrice, DmsBookingDto dmsBookingDto, Row detailsRow,
			int cellNum, DmsInvoice dmsInvoice, DmsEntity dmsEntityOnRoadPrice, String orgId) {
		String exShowroomPrice = null;
		String tcs = "";
		String corporateOffer = "";
		String consumerOffer = "";
		String specialOffer = "";
		String exchnageOffer = "";
		String additionalOffer1 = "";
		String additionalOffer2 = "";
		String cashDiscount = "";
		String focAcc = "";
	
		if (dmsEntityOnRoadPrice != null) {
			List<DmsOnRoadPriceDto> onRoadPriceDtoList = dmsEntityOnRoadPrice.getDmsOnRoadPriceDtoList();
			if (onRoadPriceDtoList != null && !onRoadPriceDtoList.isEmpty()) {
				DmsOnRoadPriceDto onRoadPricedto = onRoadPriceDtoList.get(0);
				if(null!=onRoadPricedto) {
					
					exShowroomPrice = String.valueOf(
							onRoadPricedto.getExShowroomPrice() != null ? onRoadPricedto.getExShowroomPrice().intValue() :"");
					exchnageOffer = String
							.valueOf(onRoadPricedto.getExchangeOffers() != null ? onRoadPricedto.getExchangeOffers() : "");
					corporateOffer = String
							.valueOf(onRoadPricedto.getCorporateOffer() != null ? onRoadPricedto.getCorporateOffer() : "");
					specialOffer = String
							.valueOf(onRoadPricedto.getSpecialScheme() != null ? onRoadPricedto.getSpecialScheme() : "");
					additionalOffer1 = String.valueOf(
							onRoadPricedto.getAdditionalOffer1() != null ? onRoadPricedto.getAdditionalOffer1() : "");
					additionalOffer2 = String.valueOf(
							onRoadPricedto.getAdditionalOffer2() != null ? onRoadPricedto.getAdditionalOffer2() : "");
					cashDiscount = String
							.valueOf(onRoadPricedto.getCashDiscount() != null ? onRoadPricedto.getCashDiscount() : "");
					
					
					//tcs = String.valueOf(onRoadPricedto.getTcs() != null ? onRoadPricedto.getTcs() : "");
					cashDiscount = String
							.valueOf(onRoadPricedto.getCashDiscount() != null ? onRoadPricedto.getCashDiscount() : "");
					focAcc = String
							.valueOf(onRoadPricedto.getFocAccessories() != null ? onRoadPricedto.getFocAccessories() : "");
					
				}
			
				
				if(null!=exShowroomPrice ) {
					Double tmpInt = Double.valueOf(exShowroomPrice);
					Double per = tmpInt/100;
					tcs = String.valueOf(per);
					
				}
			}

		}
		writeIntoCell(detailsRow, exShowroomPrice, cellNum++);
		
		
				
		if (dmsInvoice != null) {
			String stateType = dmsInvoice.getStateType().name();
			log.debug("State TYPE :::" + stateType);
			writeIntoCell(detailsRow, stateType, cellNum++);// GST Type
			 
			if (stateType.equalsIgnoreCase("Same_State")) {
				writeIntoCell(detailsRow, getSameStatePerc(orgId), cellNum++);// CGST %
			} else {
				writeIntoCell(detailsRow, "", cellNum++);// CGST %
			}
			 
			if (stateType.equalsIgnoreCase("Other_State")) {
				writeIntoCell(detailsRow, getOtherStatePerc(orgId), cellNum++);// CGST %
			}else {
				writeIntoCell(detailsRow, "", cellNum++);// CGST %
			}
			 
			if (stateType.equalsIgnoreCase("Union_Territory")) {
				writeIntoCell(detailsRow,  getUnionTerritoryPerc(orgId), cellNum++);// UTGST %
			}else {
				writeIntoCell(detailsRow, "", cellNum++);// UTGST %
			}
			
			writeIntoCell(detailsRow, dmsInvoice.getCessPercentage(), cellNum++);// CESS %
			writeIntoCell(detailsRow, dmsInvoice.getGst_rate(), cellNum++);// Total GST % %
		}else {
			writeIntoCell(detailsRow, "", cellNum++);// GST Type
			writeIntoCell(detailsRow, "", cellNum++);// CGST % SGST %
			writeIntoCell(detailsRow, "", cellNum++);// IGST %
			writeIntoCell(detailsRow, "", cellNum++);// UTGST % 
			writeIntoCell(detailsRow, "", cellNum++);// CESS % 
			writeIntoCell(detailsRow, "", cellNum++);// Total GST % %
		}
		
 
	
		writeIntoCell(detailsRow, "1%", cellNum++);// TCS %
		writeIntoCell(detailsRow, removeDecimals(tcs), cellNum++);

		writeIntoCell(detailsRow, "", cellNum++); // consumerOffer
		writeIntoCell(detailsRow, removeDecimals(exchnageOffer), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(corporateOffer), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(specialOffer), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(additionalOffer1), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(additionalOffer2), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(cashDiscount), cellNum++);
		writeIntoCell(detailsRow, removeDecimals(focAcc), cellNum++);
		writeIntoCell(detailsRow, dmsInvoice != null ? dmsInvoice.getTotalAmount() : "", cellNum++);// INVOICE AMNT

		return cellNum;
	}

	private String removeDecimals(String str) {
		log.debug("inside removeDecimals "+str);
		if(str!=null && str.length()>0) {
			
			return String.valueOf(str).split("\\.")[0];
		}else {
			return "";
		}
	}

	private String getUnionTerritoryPerc(String orgId) {
		String res = "";
		String empNameQuery = "SELECT total FROM salesDataSetup.union_territory_tax where status='Active' and org_id=<ID>;";
		try {
			if (null != orgId) {
				Object obj = entityManager.createNativeQuery(empNameQuery.replaceAll("<ID>", orgId)).getSingleResult();
				if (null != obj) {
					res = (String) obj;
				}

			} 

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Excetption in getUnionTerritoryPerc  ", e);

		}
		log.debug("CESS IN getUnionTerritoryPerc "+res);
		return res;

	}

	private String getOtherStatePerc(String orgId) {

		String res = "";
		String empNameQuery = "SELECT total FROM salesDataSetup.inter_state_tax where status='Active' and org_id=<ID>;";
		try {
			if (null != orgId) {
				Object obj = entityManager.createNativeQuery(empNameQuery.replaceAll("<ID>", orgId)).getSingleResult();
				if (null != obj) {
					res = (String) obj;
				}

			} 

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Excetption in getOtherStatePerc  ", e);

		}
		log.debug("CESS IN getOtherStatePerc "+res);
		return res;

	
	}

	private String getSameStatePerc(String orgId) {
		String res = "";
		String empNameQuery = "SELECT total FROM salesDataSetup.intra_state_tax where status='Active' and org_id=<ID>;";
		try {
			if (null != orgId) {
				Object obj = entityManager.createNativeQuery(empNameQuery.replaceAll("<ID>", orgId)).getSingleResult();
				if (null != obj) {
					res = (String) obj;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Excetption in getSameStatePerc  ", e);

		}
		log.debug("CESS IN getSameStatePerc "+res);
		return res;

	
	
	}

	private int addEventDetails(Row detailsRow, int cellNum, int leadId, String eventCode) {
		log.debug("Inside addEventDetails,leadId " + leadId + ",eventCode:" + eventCode);
		try {
			final List<Map<String, Object>> dataList = buildEvents(eventCode);
			String evtName = "";
			String evtId = "";
			String evtStartDt = "";
			String evtEndDt = "";
			Integer evtCategory = 0;

			for (Map<String, Object> m : dataList) {

				evtName = (String) m.get("name");
				evtId = (String) m.get("event_id");
				Date evtStartDtTmp = (java.sql.Date) m.get("startdate");
				Date evtEndDtTmp = (java.sql.Date) m.get("enddate");
				evtCategory = (Integer) m.get("category_id");
				evtStartDt = evtStartDtTmp.toString();
				evtEndDt = evtEndDtTmp.toString();

			}
			log.debug("evtName:" + evtName + ",evtId:" + evtId);
			writeIntoCell(detailsRow, evtName, cellNum++); // Event name
			writeIntoCell(detailsRow, evtId, cellNum++); // Event ID
			writeIntoCell(detailsRow, evtStartDt, cellNum++); // Event Start Date
			writeIntoCell(detailsRow, evtEndDt, cellNum++); // Event End date
			writeIntoCell(detailsRow, evtCategory, cellNum++); // Event Cateogry
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in buildEvents ", e);
		}

		return cellNum;
	}

	private int addEventDetailsShort(Row detailsRow, int cellNum, int leadId, String eventCode) {
		log.debug("Inside addEventDetailsShort,leadId " + leadId + ",eventCode:" + eventCode);
		try {
			final List<Map<String, Object>> dataList = buildEvents(eventCode);
			String evtName = "";
			String evtId = "";

			Integer evtCategory = 0;

			for (Map<String, Object> m : dataList) {
				evtName = (String) m.get("name");
				evtCategory = (Integer) m.get("category_id");
			}
			log.debug("evtName:" + evtName + ",evtId:" + evtId);
			writeIntoCell(detailsRow, evtName, cellNum++); // Event name
			writeIntoCell(detailsRow, evtCategory, cellNum++); // Event Cateogry
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in addEventDetailsShort ", e);
		}

		return cellNum;
	}

	private List<LeadStageRefEntity> removeDuplicates(List<LeadStageRefEntity> leadRefDBListBooking,
			List<LeadStageRefEntity> leadRefDBInvoice) {
		log.debug("leadRefDBInvoice size " + leadRefDBInvoice.size() + ", leadRefDBInvoice :" + leadRefDBInvoice);
		log.debug("leadRefDBListBooking size " + leadRefDBListBooking.size() + ", leadRefDBListBooking :"
				+ leadRefDBListBooking);
		List<LeadStageRefEntity> leadRefDBLiveBookingList = new ArrayList<>();
		for (LeadStageRefEntity book : leadRefDBListBooking) {
			String bookID = String.valueOf(book.getLeadId());
			for (LeadStageRefEntity inv : leadRefDBInvoice) {
				String invId = String.valueOf(inv.getLeadId());

				if (!bookID.equals(invId)) {
					leadRefDBLiveBookingList.add(book);
				}
			}
		}
		return leadRefDBLiveBookingList;
	}

	private void genearateExcelForBooking(List<DMSResponse> dmsResponseBookingList,
			List<LeadStageRefEntity> leadRefDBList, String fileName, String sheetName) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(sheetName);
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = getBookingRowHeaders();
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseBookingList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;

					DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);
					
					

					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsAttachmentDto> dmsAttachments = dmsLeadDto.getDmsAttachments();
					List<DmsAccessoriesDto> dmsAccessoriesList = dmsLeadDto.getDmsAccessories();

					DmsBookingDto dmsBookingDto = dmsLeadDto.getDmsBooking();
					DMSResponse dmsResponseOnRoadPrice = null;
					List<DmsExchangeBuyer> dmsExchangeBuyerList = null;
					DmsEntity dmsEntityOnRoadPrice = null;

					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();

						try {
							dmsResponseOnRoadPrice = restTemplate
									.getForEntity(leadOnRoadPriceUrl + leadId, DMSResponse.class).getBody();
							dmsEntityOnRoadPrice = dmsResponseOnRoadPrice.getDmsEntity();
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							dmsExchangeBuyerList = dmsExchangeBuyerDao.getDmsExchangeBuyersByLeadId(leadId);
						} catch (Exception e) {
							e.printStackTrace();
						}
						List<LeadStageRefEntity> leadRefList = leadRefDBList.stream()
								.filter(x -> x.getLeadId() != null && (x.getLeadId()) == leadId)
								.collect(Collectors.toList());
						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						if (null != leadRefList && !leadRefList.isEmpty()) {
							leadRef = leadRefList.get(0);
						}
						log.debug("leadRef::" + leadRef);
						Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());
						String branchName = "";
						if (optBranch.isPresent()) {
							DmsBranch branch = optBranch.get();
							branchName = branch.getName();
						}
						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto!=null?dmsLeadDto.getCrmUniversalId():"",cellNum++);
						writeIntoCell(detailsRow, leadRef.getRefNo(), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.getDateFormat(leadRef.getStartDate()), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(getMonthAndYear(leadRef.getStartDate())),
								cellNum++);
						writeIntoCell(detailsRow, getRefNo(leadRef.getLeadId(),"PREBOOKING"),  cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getSalutation() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getLastName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getEmail() : "", cellNum++);

						String dob = ExcelUtil.getFormateDate(
								dmsLeadData.getDateOfBirth() != null ? dmsLeadData.getDateOfBirth() : null);
						log.debug("dmsLeadData.getDateOfBirth() " + dmsLeadData.getDateOfBirth() + ", DOB " + dob);

						writeIntoCell(detailsRow, dob, cellNum++);
						cellNum = addSingleAddress(addressList, detailsRow, cellNum);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getModel() : "", cellNum++);

						cellNum = addDmsLeadProducts(dmsLeadProductDtoList, detailsRow, cellNum);

					cellNum = addAttachments(dmsAttachments, detailsRow, cellNum);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getGstNumber() : "", cellNum++);

						if (dmsEntityOnRoadPrice != null) {
							List<DmsOnRoadPriceDto> onRoadPriceDtoList = dmsEntityOnRoadPrice
									.getDmsOnRoadPriceDtoList();
							if (onRoadPriceDtoList != null && !onRoadPriceDtoList.isEmpty()) {
								DmsOnRoadPriceDto onRoadPricedto = onRoadPriceDtoList.get(0);
								writeIntoCell(detailsRow,
										onRoadPricedto != null ? onRoadPricedto.getInsuranceType() : "", cellNum++);
								writeIntoCell(detailsRow,
										dmsBookingDto != null ? onRoadPricedto.getInsuranceAddOn() : "", cellNum++);
								writeIntoCell(detailsRow, dmsBookingDto != null ? onRoadPricedto.getWarrantyName() : "",
										cellNum++);
							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
							}
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						if (dmsAccessoriesList != null && !dmsAccessoriesList.isEmpty()) {
							DmsAccessoriesDto dmsAccessoriesDto = dmsAccessoriesList.get(0);
							writeIntoCell(detailsRow, dmsAccessoriesDto.getAmount(), cellNum++);
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
						}
						Double onRoadPrice=0D;
						if (dmsEntityOnRoadPrice != null) {
							List<DmsOnRoadPriceDto> onRoadPriceDtoList = dmsEntityOnRoadPrice
									.getDmsOnRoadPriceDtoList();
							if (onRoadPriceDtoList != null && !onRoadPriceDtoList.isEmpty()) {
								DmsOnRoadPriceDto onRoadPricedto = onRoadPriceDtoList.get(0);
								onRoadPrice = onRoadPricedto.getOnRoadPrice();
								
								Double focAccAmt = onRoadPricedto.getFocAccessories();
								writeIntoCell(detailsRow, focAccAmt, cellNum++);
								writeIntoCell(detailsRow, onRoadPricedto.getCashDiscount(), cellNum++);
								writeIntoCell(detailsRow, onRoadPricedto.getExShowroomPrice(), cellNum++);
								writeIntoCell(detailsRow, String.format("%.0f", onRoadPrice), cellNum++);
								
							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
							}
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						// cellNum = addOnRoadPriceDetails(detailsRow, cellNum,
						// dmsLeadDto.getId(),dmsResponseOnRoadPrice);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getLeadStatus() : "", cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getModeOfPayment() : "",
								cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getBookingAmount() : "",
								cellNum++);

				
						Double totalAmtPaid = getTotalPaidAmt(String.valueOf(dmsLeadDto.getId()));
						log.debug("totalAmtPaid:::"+totalAmtPaid);
						
						Double pendingAmt1 = onRoadPrice-totalAmtPaid;
						
						writeIntoCell(detailsRow, String.valueOf(totalAmtPaid.intValue()), cellNum++);
						writeIntoCell(detailsRow, pendingAmt1, cellNum++);// Pending Amount

						if (dmsFinanceDetailsList != null && !dmsFinanceDetailsList.isEmpty()) {
							DmsFinanceDetailsDto dmsFinanceDetailsDto = dmsFinanceDetailsList.get(0);
							writeIntoCell(detailsRow, dmsFinanceDetailsDto.getFinanceType(), cellNum++);
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
						}
						List<DmsAllotment> optAllot = dmsAllotmentDao.getByLeadId(leadId);
						if (optAllot != null && !optAllot.isEmpty()) {
							DmsAllotment allot = optAllot.get(0);
							String alloDt = ExcelUtil.getDateFormatV2OnlyDate(allot.getCreateDateTime());
							writeIntoCell(detailsRow, alloDt, cellNum++);// Vehicle Allocation Date
							writeIntoCell(detailsRow, getAllotAge(alloDt), cellNum++);// Vehicle Allocation Age
							writeIntoCell(detailsRow, allot.getVinno(), cellNum++);
						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}

						writeIntoCell(detailsRow,
								dmsLeadDto != null
										? ExcelUtil.getDateFormatV2OnlyDate(dmsLeadDto.getDmsExpectedDeliveryDate())
										: "",
								cellNum++);
						writeIntoCell(detailsRow,
								dmsLeadDto != null
										? ExcelUtil.getDateFormatV2OnlyDate(
												dmsLeadDto.getCommitmentDeliveryPreferredDate())
										: "",
								cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getDeliveryLocation() : "",
								cellNum++);
						String enqDate = ExcelUtil.getDateFormatV2OnlyDate(dmsLeadDto.getDateOfEnquiry());
						String bookDate = ExcelUtil.getDateFormatV2OnlyDate(leadRef.getStartDate());

						// writeIntoCell(detailsRow, buildRetailToDeliveryConvesionDays(leadId,
						// "ENQUIRY", "BOOKING"),cellNum++);// Enquiry to booking Conversion days
						writeIntoCell(detailsRow, findDaysBetweenDates(enqDate, bookDate), cellNum++);
						writeIntoCell(detailsRow, getAllotAge(bookDate), cellNum++);// Booking age
						writeIntoCell(detailsRow, enqDate, cellNum++);
						writeIntoCell(detailsRow, getEnqRefFromBookingRef(dmsLeadDto.getReferencenumber()), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getEnquirySegment() : "", cellNum++);

						String customerType = getCustomerTypeByLeadId(leadId);
						writeIntoCell(detailsRow, customerType, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getEnquirySource(), cellNum++);
						writeIntoCell(detailsRow, dmsBookingDto != null ? dmsBookingDto.getCorporateName() : "",
								cellNum++);

						cellNum = addEventDetailsShort(detailsRow, cellNum, leadId, dmsLeadDto.getEventCode());
						writeIntoCell(detailsRow, "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getBuyerType() : "", cellNum++);

						if (dmsExchagedetailsList != null && !dmsExchagedetailsList.isEmpty()) {
							DmsExchangeBuyerDto a1 = dmsExchagedetailsList.get(0);
							if (null != a1) {
								writeIntoCell(detailsRow, a1.getRegNo(), cellNum++);
								writeIntoCell(detailsRow, a1.getModel(), cellNum++);
							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
							}
						} else {

							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);

						}

						final List<Map<String, Object>> evalutionList = buildEvaluation(dmsLeadDto.getCrmUniversalId());

						String evalName = "";
						Timestamp evalDate = null;
						String evalNumber = "";
						String evalStatus = "";
						String custExpectedPrice = "";
						for (Map<String, Object> m : evalutionList) {

							evalName = "" + (Integer) m.get("evalutor_id");
							evalDate = (Timestamp) m.get("updated_date");

							evalNumber = m.get("mobile_num") != null ? (String) m.get("mobile_num") : "";
							evalStatus = m.get("evalution_status") != null ? (String) m.get("evalution_status") : "";

							if (m.get("cust_expected_price") != null) {
								BigDecimal bd = (BigDecimal) m.get("cust_expected_price");
								custExpectedPrice = String.valueOf(bd);
							}

						}
						writeIntoCell(detailsRow, evalName, cellNum++); // Evaluation ID
						writeIntoCell(detailsRow, evalDate != null ? evalDate.toString() : "", cellNum++); // Evaluation
																											// Date
						writeIntoCell(detailsRow, evalNumber, cellNum++); // Evaluation ID
						writeIntoCell(detailsRow, evalStatus, cellNum++);
						writeIntoCell(detailsRow, custExpectedPrice, cellNum++); // Customer Exp. Price

						// writeIntoCell(detailsRow, "", cellNum++); // Customer Exp. Price

						String offredPrice = "";
						String finalPrice = "";
						String exchangeStatus = "";

						if (null != dmsExchangeBuyerList && !dmsExchangeBuyerList.isEmpty()) {
							DmsExchangeBuyer dmsExchangeBuyer = dmsExchangeBuyerList.get(0);
							if (dmsExchangeBuyer != null) {
								offredPrice = ""
										+ (dmsExchangeBuyer != null && dmsExchangeBuyer.getOfferedPrice() != null
												? dmsExchangeBuyer.getOfferedPrice()
												: "");
								finalPrice = "" + (dmsExchangeBuyer != null && dmsExchangeBuyer.getFinalPrice() != null
										? dmsExchangeBuyer.getFinalPrice()
										: "");
								exchangeStatus = (dmsExchangeBuyer != null
										&& dmsExchangeBuyer.getEvaluationStatus() != null
												? dmsExchangeBuyer.getEvaluationStatus()
												: "");
							}
						}
						writeIntoCell(detailsRow, offredPrice, cellNum++); // Offered Price
						writeIntoCell(detailsRow, finalPrice, cellNum++); // Approved Price
						writeIntoCell(detailsRow, exchangeStatus, cellNum++); // Exchnage price Approval status

						String cancelDate = "";
						String lostReason = "";
						String lostSubReason = "";
						if (dmsLeadDto.getLeadStage().equalsIgnoreCase(DROPPED)) {
							List<DmsLeadDrop> dropList = dmsLeadDropDao.getByLeadId(leadId);
							if (dropList != null && !dropList.isEmpty()) {
								DmsLeadDrop droppedLead = dropList.get(0);

								cancelDate = ExcelUtil
										.getDateFormat(droppedLead != null && droppedLead.getCreatedDateTime() != null
												? Timestamp.valueOf(droppedLead.getCreatedDateTime())
												: null);
								lostReason = droppedLead != null && droppedLead.getLostReason() != null
										? droppedLead.getLostReason()
										: "";
								lostSubReason = droppedLead != null && droppedLead.getLostSubReason() != null
										? droppedLead.getLostSubReason()
										: "";

							}

						}

						writeIntoCell(detailsRow, cancelDate, cellNum++);
						writeIntoCell(detailsRow, lostReason, cellNum++);
						writeIntoCell(detailsRow, lostSubReason, cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto.getSalesConsultant(), cellNum++);
						String empId = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, empId, cellNum++);
						String teamLeadName = getTeamLead(empId);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow,getManager(teamLeadName), cellNum++); // Manager
						writeIntoCell(detailsRow, getFinanceExective(dmsLeadDto.getCrmUniversalId()), cellNum++); // Finance Executive
						writeIntoCell(detailsRow, getTaskRemarks(dmsLeadDto.getCrmUniversalId(),"Pre Booking Follow Up"), cellNum++); // Last Remarks
						
					
					

					}
				}
			}
			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in LIVE BOOKING ",e);
		}

	}

	private String getFinanceExective(String crmUniversalId) {
		String res = "";
		try {
			List<DmsWFTask> list = dmsWfTaskDao.getWfTaskByUniversalIdandTask(crmUniversalId, "Finance");
			if (list != null && !list.isEmpty()) {
				DmsWFTask wf = list.get(0);
				String id = wf.getAssigneeId();
				if (null != id) {
					Optional<DmsEmployee> opt = dmsEmployeeRepo.findById(Integer.parseInt(id));
					if (opt.isPresent()) {
						res = opt.get().getEmpName();

					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception in getFinanceExective ", e);

		}
		return res;
	}

	private String getEnqRefFromBookingRef(String bookingRef) {
		
		String ref="";
		try {
				
		log.debug("bookingRef:::"+bookingRef);
		Integer lead = leadStageRefDao.getLeadSIdByRefNo(bookingRef);
		//if(lead!=null) {
			ref = leadStageRefDao.findRefByLeadIdStge(lead, "ENQUIRY");
		//}
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exceptioin in getEnqRefFromBookingRef ",e);
		}
		return ref;
	}

	private long findDaysBetweenDates(String enqDate, String bookDate) {
		
		log.debug("findDaysBetweenDates ::enqDate:"+enqDate+", bookDate::"+bookDate);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate dateBefore = LocalDate.parse(enqDate, formatter);
		LocalDate dateAfter = LocalDate.parse(bookDate, formatter);
		long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
		return noOfDaysBetween;
	}

	
	private long getAllotAge(String dt) {

		// Parsing the date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		LocalDate dateBefore = LocalDate.parse(dt, formatter);
		LocalDate dateAfter = LocalDate.now();

		// calculating number of days in between
		long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);

		// displaying the number of days
		// System.out.println(noOfDaysBetween);
		return noOfDaysBetween;
	}

	private Object getManager(String teamLeadName) {
		String mgrId = "";
		if (teamLeadName != null) {
			String teamLeadId = getEmpName(teamLeadName);
			mgrId = getTeamLead(teamLeadId);
		}
		return mgrId;
	}

	public String getEmpNameWithEmpID(String id) {
		String res = null;
		String empNameQuery = "SELECT emp_name FROM dms_employee where emp_id=<ID>;";
		try {
			if (null != id && !id.equalsIgnoreCase("string")) {
				Object obj = entityManager.createNativeQuery(empNameQuery.replaceAll("<ID>", id)).getSingleResult();
				res = (String) obj;
			} else {
				res = "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	
	public Double getTotalPaidAmt(String id) {
		log.debug("Inside getTotalPaidAmt method , lead id "+id);
		Double amt=0D;
		String empNameQuery = "SELECT sum(amount) FROM salesDataSetup.dms_received_booking_amount where lead_id=<ID>;";
		try {
			if (null != id && !id.equalsIgnoreCase("string")) {
				Object obj = entityManager.createNativeQuery(empNameQuery.replaceAll("<ID>", id)).getSingleResult();
				if (obj != null) {
					amt = (Double) obj;
				}
				log.debug("getTotalPaidAmt res is "+amt);
				}
		} catch (Exception e) {
			log.error("Exception in getTotalPaidAmt ",e);			e.printStackTrace();
		}
		return amt;
	}

	
	
	private String getTeamLead(String empId) {
		String teamLead = "";
		String reportingTo = "";
		if (empId != null) {
			Optional<DmsEmployee> opt = dmsEmployeeRepo.findById(Integer.valueOf(empId));
			if (opt.isPresent()) {
				reportingTo = opt.get().getReportingTo();
			}
		}
		if (reportingTo != null && !reportingTo.isEmpty()) {
			teamLead = getEmpNameWithEmpID(reportingTo);
		}

		return teamLead;
	}

	private int addOnRoadPriceDetails(Row detailsRow, int cellNum, int id, DMSResponse dmsResponseOnRoadPrice) {
		// TODO Auto-generated method stub

		String exShowroomPrice = "";
		String onRoadPrice = "";

		if (dmsResponseOnRoadPrice != null) {
			DmsEntity dmsEntity = dmsResponseOnRoadPrice.getDmsEntity();
			if (null != dmsEntity) {
				DmsOnRoadPriceDto dmsOnRoadPriceDto = dmsEntity.getDmsOnRoadPriceDto();
				if (dmsOnRoadPriceDto != null) {
					exShowroomPrice = "" + dmsOnRoadPriceDto.getExShowroomPrice();
					onRoadPrice = "" + dmsOnRoadPriceDto.getOnRoadPrice();

				}
			}

		}
		writeIntoCell(detailsRow, exShowroomPrice, cellNum++);
		writeIntoCell(detailsRow, onRoadPrice, cellNum++);
		return cellNum;
	}

	private int addAttachments(List<DmsAttachmentDto> dmsAttachmentsList, Row detailsRow, int cellNum) {
		String aadarNumber = "";
		String panNumber = "";
		
		

		if (dmsAttachmentsList != null && !dmsAttachmentsList.isEmpty()) {
			for (DmsAttachmentDto dmsAttachmentDto : dmsAttachmentsList) {

				if (dmsAttachmentDto != null && dmsAttachmentDto.getDocumentType() != null) {
					if (dmsAttachmentDto.getDocumentType().equalsIgnoreCase("aadhar")) {
						aadarNumber = dmsAttachmentDto.getDocumentNumber();
					}
					if (dmsAttachmentDto.getDocumentType().equalsIgnoreCase("pan")) {
						panNumber = dmsAttachmentDto.getDocumentNumber();
					}
				}
				log.info("PanCard:{}", panNumber);
				log.info("AadharCard:{}", aadarNumber);
			}

		}
		writeIntoCell(detailsRow, panNumber, cellNum++);
		writeIntoCell(detailsRow, aadarNumber, cellNum++);
		return cellNum;
	}

	private void genearateExcelForEnq(List<DMSResponse> dmsResponseList, List<LeadStageRefEntity> leadRefDBList,
			String fileName, String sheetName) throws DynamicFormsServiceException {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(sheetName);
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = getEnquiryRowHeaders();
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (DMSResponse res : dmsResponseList) {
				DmsEntity dmsEntity = res.getDmsEntity();
				if (null != dmsEntity) {
				
					Row detailsRow = sheet.createRow(rowNum++);
					cellNum = 0;
					DmsContactDto dmsContactDto = dmsEntity.getDmsContactDto();
					DmsAccountDto dmsAccountDto = dmsEntity.getDmsAccountDto();
					DmsLeadData dmsLeadData = buildDmsLeadData(dmsEntity);
					
					DmsLeadDto dmsLeadDto = dmsEntity.getDmsLeadDto();
					List<DmsAddress> addressList = dmsLeadDto.getDmsAddresses();
					List<DmsLeadProductDto> dmsLeadProductDtoList = dmsLeadDto.getDmsLeadProducts();
					List<DmsFinanceDetailsDto> dmsFinanceDetailsList = dmsLeadDto.getDmsfinancedetails();
					List<DmsExchangeBuyerDto> dmsExchagedetailsList = dmsLeadDto.getDmsExchagedetails();
					List<DmsLeadScoreCardDto> dmsLeadScoreDTOList = dmsLeadDto.getDmsLeadScoreCards();
					if (dmsLeadDto != null) {
						int leadId = dmsLeadDto.getId();

						List<LeadStageRefEntity> leadRefList = leadRefDBList.stream()
								.filter(x -> x.getLeadId() != null && (x.getLeadId()) == leadId)
								.collect(Collectors.toList());
						LeadStageRefEntity leadRef = new LeadStageRefEntity();
						if (null != leadRefList && !leadRefList.isEmpty()) {
							leadRef = leadRefList.get(0);
						}

						Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());
						String branchName = "";
						if (optBranch.isPresent()) {
							DmsBranch branch = optBranch.get();
							branchName = branch.getName();
						}

						writeIntoCell(detailsRow, getLocationNameFromBranch(branchName), cellNum++);
						writeIntoCell(detailsRow, branchName, cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto!=null ? dmsLeadDto.getCrmUniversalId():"", cellNum++);
						writeIntoCell(detailsRow, leadRef.getRefNo(), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.getDateFormat(leadRef.getStartDate()), cellNum++);
						writeIntoCell(detailsRow, ExcelUtil.formatDate(getMonthAndYear(leadRef.getStartDate())),
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getSalutation() : "",
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getFirstName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto.getLastName(), cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getPhone() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getEmail() : "", cellNum++);

						String dob = ExcelUtil.getFormateDate(
								dmsLeadData.getDateOfBirth() != null ? dmsLeadData.getDateOfBirth() : null);
						log.debug("dmsLeadData.getDateOfBirth() " + dmsLeadData.getDateOfBirth() + ", DOB " + dob);
						writeIntoCell(detailsRow, dob, cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? ExcelUtil.getFormateDate(
								dmsLeadData.getAnniversaryDate() != null ? dmsLeadData.getAnniversaryDate() : null)
								: "", cellNum++);
						cellNum = addAddress(addressList, detailsRow, cellNum);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getModel() : "", cellNum++);

						cellNum = addDmsLeadProducts(dmsLeadProductDtoList, detailsRow, cellNum);
						String customerType = getCustomerTypeByLeadId(leadId);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getEnquirySegment() : "", cellNum++);
						writeIntoCell(detailsRow, customerType, cellNum++);
						writeIntoCell(detailsRow, getSource(dmsLeadDto != null ? dmsLeadDto.getSourceOfEnquiry() : 0),
								cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSubSource() : "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadData != null ? dmsLeadData.getCompany() : "", cellNum++);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getEnquiryCategory() : "", cellNum++);  //Enquiry Category
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getLeadStatus() : "", cellNum++);

						cellNum = addEventDetails(detailsRow, cellNum, leadId, dmsLeadDto.getEventCode());

						writeIntoCell(detailsRow,dmsLeadDto != null ? ExcelUtil.getFormateDate(dmsLeadDto.getDmsExpectedDeliveryDate()): "",cellNum++);   //Expected Delivery date

						cellNum = addDmsFinanceDetails(dmsFinanceDetailsList, detailsRow, cellNum);
						// writeIntoCell(detailsRow, "", cellNum++);
						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getBuyerType() : "", cellNum++);
						// writeIntoCell(detailsRow, dmsLeadDto!=null?dmsLeadDto.getBuyerType():"",
						// cellNum++);

						cellNum = addDmsExchangeDetails(dmsExchagedetailsList, detailsRow, cellNum);

						String evalName = "";
						Timestamp evalDate = null;
						String evalNumber = "";
						String evalStatus = "";
						String evalOfferPrice = "";
						String custExpectedPrice = "";
						String approviedPrice = "";
						final List<Map<String, Object>> evalutionList = buildEvaluation(dmsLeadDto.getCrmUniversalId());

						for (Map<String, Object> m : evalutionList) {

							evalName = "" + (Integer) m.get("evalutor_id");
							evalDate = (Timestamp) m.get("updated_date");
							evalNumber = (String) m.get("mobile_num");
							evalStatus = m.get("evalution_status") != null ? (String) m.get("evalution_status") : "";
							// BigDecimal evalOfferPricetmp = new Bi
							if (m.get("evaluator_offer_price") != null) {
								BigDecimal evalOfferPricetmp = (BigDecimal) m.get("evaluator_offer_price");
								evalOfferPrice = String.valueOf(evalOfferPricetmp);
							}

							if (m.get("cust_expected_price") != null) {
								BigDecimal evalOfferPricetmp = (BigDecimal) m.get("cust_expected_price");
								custExpectedPrice = String.valueOf(evalOfferPricetmp);
							}
							if (m.get("manager_offer_price") != null) {
								BigDecimal evalOfferPricetmp = (BigDecimal) m.get("manager_offer_price");
								approviedPrice = String.valueOf(evalOfferPricetmp);
							}

						}

						log.debug("evalName:::" + evalName + ",evalDate:" + evalDate);
						writeIntoCell(detailsRow, evalStatus, cellNum++); // Eval Status
						writeIntoCell(detailsRow, evalName, cellNum++); // Evaluator Name
						writeIntoCell(detailsRow, evalDate != null ? evalDate.toString() : "", cellNum++); // Evaluation
																											// Date
						writeIntoCell(detailsRow, evalNumber, cellNum++); // Evaluation number
						writeIntoCell(detailsRow, evalOfferPrice, cellNum++); // Offered Price
						writeIntoCell(detailsRow, custExpectedPrice, cellNum++); // Customer Exp. Price
						writeIntoCell(detailsRow, approviedPrice, cellNum++); // Approved price

						cellNum = addTestDriveDetails(dmsLeadDto.getCrmUniversalId(), detailsRow, cellNum);

						cellNum = addHomeVisitDetails(dmsLeadDto.getCrmUniversalId(), detailsRow, cellNum);

						String sd = ExcelUtil.getDateFormatV3(leadRef.getStartDate());
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					   
						String ed =   java.time.LocalDate.now().toString();
						
						log.info("StartDate ::"+sd+":: endDate "+ed);
						long days = findDaysBetweenDates(sd,ed);
						writeIntoCell(detailsRow, dmsLeadDto != null ? days: "", cellNum++); 
						writeIntoCell(detailsRow, dmsAccountDto != null ? dmsAccountDto.getKmsTravelledInMonth() : "",
								cellNum++);
						writeIntoCell(detailsRow, dmsAccountDto != null ? dmsAccountDto.getWhoDrives() : "", cellNum++);
						writeIntoCell(detailsRow, dmsAccountDto != null ? dmsAccountDto.getMembersInFamily() : "",
								cellNum++);
						writeIntoCell(detailsRow,
								dmsAccountDto != null ? dmsAccountDto.getPrimeExpectationFromCar() : "", cellNum++);

						cellNum = addLookingForAnotherDetails(dmsLeadScoreDTOList, detailsRow, cellNum);

						if (dmsLeadDto.getLeadStage().equalsIgnoreCase(DROPPED)) {
							List<DmsLeadDrop> dropList = dmsLeadDropDao.getByLeadId(leadId);
							if (dropList != null && !dropList.isEmpty()) {
								DmsLeadDrop droppedLead = dropList.get(0);
								writeIntoCell(detailsRow, droppedLead.getCreatedDateTime(), cellNum++); // Enquiry Lost
																										// date
								writeIntoCell(detailsRow, droppedLead.getLostReason(), cellNum++); // Enquiry Lost
																									// Reason

							} else {
								writeIntoCell(detailsRow, "", cellNum++);
								writeIntoCell(detailsRow, "", cellNum++);
							}

						} else {
							writeIntoCell(detailsRow, "", cellNum++);
							writeIntoCell(detailsRow, "", cellNum++);
						}
						cellNum = addLookingForAnotherDetailsV2(dmsLeadScoreDTOList, detailsRow, cellNum);

						writeIntoCell(detailsRow, dmsLeadDto != null ? dmsLeadDto.getSalesConsultant() : "", cellNum++);
						String empID = getEmpName(dmsLeadDto.getSalesConsultant());
						writeIntoCell(detailsRow, empID, cellNum++);
						String teamLeadName = getTeamLead(empID);
						writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
						writeIntoCell(detailsRow, getManager(teamLeadName), cellNum++); // Manager
						//cellNum = addRemarks(dmsLeadScoreDTOList, detailsRow, cellNum);// Last Remarks
						writeIntoCell(detailsRow, getTaskRemarks(dmsLeadDto.getCrmUniversalId(),"Enquiry Follow Up"), cellNum++); // Manager

					}
				}
			}
			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * private DmsLeadData buildDmsLeadData(String universalId) {
	 * log.debug("Calling buildDmsLeadData method,universalId:"+universalId);
	 * DmsLeadData lead = new DmsLeadData(); try {
	 * 
	 * Query q1 = entityManager.
	 * createNativeQuery("SELECT dms_account_id from dms_lead where ID="+universalId
	 * ); Object obj1 = q1.getSingleResult(); if(obj1!=null) { Integer id =
	 * (Integer)obj1; log.debug("DMS Account is not null and ID is ::::"+id);
	 * Optional<DmsAccount> opt = dmsAccountDao.findById(id); if(opt.isPresent()) {
	 * DmsAccount obj = opt.get(); lead = modelMapper.map(obj, DmsLeadData.class); }
	 * } else { Query q2 = entityManager.
	 * createNativeQuery("SELECT dms_contact_id from dms_lead where ID="+universalId
	 * ); Object obj2 = q2.getSingleResult(); if(obj2!=null) { Integer id =
	 * (Integer)obj2; log.debug("DMS Contact is not null and ID is ::::"+id);
	 * Optional<DmsContact> opt = dmsContactDao.findById(id); if(opt.isPresent()) {
	 * DmsContact obj = opt.get(); lead = modelMapper.map(obj, DmsLeadData.class); }
	 * } } log.debug("DmsLeadData output "+lead); }catch(Exception e) {
	 * e.printStackTrace(); } return lead; }
	 */

	private String getTaskRemarks(String crmUniversalId, String tasks) {
		log.debug("Inside getRemarks method ");
		String res="";
		try {
			res  = dmsWfTaskDao.getRemarksByUniversalIdAndStage(crmUniversalId,tasks);
			
		}catch(Exception e) {
			e.printStackTrace();
			log.error("Exception in getRemarks ",e);
			
		}
		return res;
	}

	private int addLookingForAnotherDetailsV2(List<DmsLeadScoreCardDto> dmsLeadScoreDTOList, Row detailsRow,
			int cellNum) {
		try {
			if (null != dmsLeadScoreDTOList && !dmsLeadScoreDTOList.isEmpty()) {
				DmsLeadScoreCardDto dto = dmsLeadScoreDTOList.get(0);
				if (dto != null) {
					writeIntoCell(detailsRow, dto.getDealershipName(), cellNum++); // Lost to Co-delaer Name
					writeIntoCell(detailsRow, dto.getDealershipLocation(), cellNum++); // Lost to Co-dealer Location
					writeIntoCell(detailsRow, "Yes", cellNum++); // Lost to Competetor
					writeIntoCell(detailsRow, dto.getModel(), cellNum++); // Lost to Competetor
				} else {
					writeIntoCell(detailsRow, "", cellNum++); // Lost to Co-delaer Name
					writeIntoCell(detailsRow, "", cellNum++); // Lost to Co-dealer Location
					writeIntoCell(detailsRow, "", cellNum++); // Lost to Competetor
					writeIntoCell(detailsRow, "", cellNum++); // Lost to Competetor
				}
			} else {
				writeIntoCell(detailsRow, "", cellNum++); // Lost to Co-delaer Name
				writeIntoCell(detailsRow, "", cellNum++); // Lost to Co-dealer Location
				writeIntoCell(detailsRow, "", cellNum++); // Lost to Competetor
				writeIntoCell(detailsRow, "", cellNum++); // Lost to Competetor
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellNum;

	}

	private int addLookingForAnotherDetails(List<DmsLeadScoreCardDto> dmsLeadScoreDTOList, Row detailsRow,
			int cellNum) {

		try {
			if (null != dmsLeadScoreDTOList && !dmsLeadScoreDTOList.isEmpty()) {
				DmsLeadScoreCardDto dto = dmsLeadScoreDTOList.get(0);
				if (dto != null) {
					if (dto.getLookingForAnyOtherBrand() != null && dto.getLookingForAnyOtherBrand()) {
						writeIntoCell(detailsRow, String.valueOf("yes"), cellNum++);
					} else {
						writeIntoCell(detailsRow, "", cellNum++); // Looking for another make
					}
					writeIntoCell(detailsRow, dto.getModel(), cellNum++); // Looking for another model
					writeIntoCell(detailsRow, dto.getVariant(), cellNum++); // Looking for another Variant
					writeIntoCell(detailsRow, dto.getDealershipName(), cellNum++); // CO-Dealership/Competetor Name
					writeIntoCell(detailsRow, dto.getDealershipLocation(), cellNum++); // CO-Dealership/Competetor
																						// Location
				} else {
					writeIntoCell(detailsRow, "", cellNum++); // Looking for another make
					writeIntoCell(detailsRow, "", cellNum++); // Looking for another model
					writeIntoCell(detailsRow, "", cellNum++); // Looking for another Variant
					writeIntoCell(detailsRow, "", cellNum++); // CO-Dealership/Competetor Name
					writeIntoCell(detailsRow, "", cellNum++); // CO-Dealership/Competetor Location
				}
			} else {
				writeIntoCell(detailsRow, "", cellNum++); // Looking for another make
				writeIntoCell(detailsRow, "", cellNum++); // Looking for another model
				writeIntoCell(detailsRow, "", cellNum++); // Looking for another Variant
				writeIntoCell(detailsRow, "", cellNum++); // CO-Dealership/Competetor Name
				writeIntoCell(detailsRow, "", cellNum++); // CO-Dealership/Competetor Location
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error("exception ", e);
		}
		return cellNum;

	}

	private int addRemarks(List<DmsLeadScoreCardDto> dmsLeadScoreDTOList, Row detailsRow, int cellNum) {

		try {
			if (null != dmsLeadScoreDTOList && !dmsLeadScoreDTOList.isEmpty()) {
				DmsLeadScoreCardDto dto = dmsLeadScoreDTOList.get(0);
				if (dto != null) {
					writeIntoCell(detailsRow, dto.getVoiceofCustomerRemarks(), cellNum++); // Looking for another make

				} else {
					writeIntoCell(detailsRow, "", cellNum++); // Looking for another make

				}
			} else {
				writeIntoCell(detailsRow, "", cellNum++); // Looking for another make

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellNum;

	}

	private DmsLeadData buildDmsLeadData(DmsEntity dmsEntity) {
		log.debug("Calling buildDmsLeadData method");
		DmsLeadData lead = new DmsLeadData();
		try {
			DmsAccountDto acc = dmsEntity.getDmsAccountDto();
			DmsContactDto con = dmsEntity.getDmsContactDto();

			if (acc != null) {
				lead = modelMapper.map(acc, DmsLeadData.class);
			} else if (con != null) {
				lead = modelMapper.map(con, DmsLeadData.class);
			}

			log.debug("DmsLeadData output " + lead);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lead;

	}

	private int addTestDriveDetails(String crmUniversalId, Row detailsRow, int cellNum) {

		try {

			List<DmsWFTask> dmsWFTaskListHomeVisit = dmsWfTaskDao.getWfTaskByUniversalIdandTaskV2(crmUniversalId,TEST_DRIVE);
			if (null != dmsWFTaskListHomeVisit && !dmsWFTaskListHomeVisit.isEmpty()) {
				DmsWFTask wfTask = dmsWFTaskListHomeVisit.get(0);
				if (wfTask.getTaskExceptedStartTime() != null) {
					writeIntoCell(detailsRow, "Yes", cellNum++);
				} else {
					writeIntoCell(detailsRow, "No", cellNum++);
				}
				writeIntoCell(detailsRow, wfTask.getTaskId(), cellNum++);
				writeIntoCell(detailsRow, ExcelUtil.getDateFormatV2(Timestamp.valueOf(wfTask.getTaskActualStartTime())),
						cellNum++);
			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellNum;
	}

	private int addHomeVisitDetails(String crmUniversalId, Row detailsRow, int cellNum) {
		try {
			List<DmsWFTask> dmsWFTaskListHomeVisit = dmsWfTaskDao.getWfTaskByUniversalIdandTaskV2(crmUniversalId,
					HOME_VISIT);
			if (null != dmsWFTaskListHomeVisit && !dmsWFTaskListHomeVisit.isEmpty()) {
				DmsWFTask wfTask = dmsWFTaskListHomeVisit.get(0);
				writeIntoCell(detailsRow, wfTask.getTaskStatus() != null ? "DONE" : "NO", cellNum++);
				writeIntoCell(detailsRow, ExcelUtil.getDateFormatV2(Timestamp.valueOf(wfTask.getTaskActualStartTime())),
						cellNum++);
			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cellNum;
	}

	private int addDmsExchangeDetails(List<DmsExchangeBuyerDto> dmsExchagedetailsList, Row detailsRow, int cellNum) {
		if (dmsExchagedetailsList != null && !dmsExchagedetailsList.isEmpty()) {

			DmsExchangeBuyerDto a1 = dmsExchagedetailsList.get(0);
			if (null != a1) {
				writeIntoCell(detailsRow, a1.getRegNo(), cellNum++);
				writeIntoCell(detailsRow, a1.getBrand(), cellNum++);
				writeIntoCell(detailsRow, a1.getModel(), cellNum++);
				writeIntoCell(detailsRow, a1.getVarient(), cellNum++);
				writeIntoCell(detailsRow, a1.getColor(), cellNum++);
				writeIntoCell(detailsRow, a1.getFuelType(), cellNum++);
				writeIntoCell(detailsRow, a1.getTransmission(), cellNum++);
				writeIntoCell(detailsRow, a1.getYearofManufacture(), cellNum++);

			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);

			}

		} else {
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
		}
		return cellNum;
	}

	private int addDmsFinanceDetails(List<DmsFinanceDetailsDto> dmsFinanceDetailsList, Row detailsRow, int cellNum) {
		log.debug("Inside addDmsFinanceDetails method,dmsFinanceDetailsList " + dmsFinanceDetailsList);

		if (dmsFinanceDetailsList != null && !dmsFinanceDetailsList.isEmpty()) {

			DmsFinanceDetailsDto a1 = dmsFinanceDetailsList.get(0);
			log.debug("a1.getFinanceCategory():::" + a1.getFinanceCategory());
			if (null != a1) {
				writeIntoCell(detailsRow, a1.getFinanceType(), cellNum++);
				writeIntoCell(detailsRow, a1.getFinanceCategory(), cellNum++);
				writeIntoCell(detailsRow, a1.getDownPayment(), cellNum++);
				writeIntoCell(detailsRow, a1.getLoanAmount(), cellNum++);
				writeIntoCell(detailsRow, a1.getFinanceCompany(), cellNum++);
				writeIntoCell(detailsRow, a1.getRateOfInterest(), cellNum++);
				writeIntoCell(detailsRow, a1.getExpectedTenureYears(), cellNum++);
				writeIntoCell(detailsRow, a1.getAnnualIncome(), cellNum++);
				writeIntoCell(detailsRow, a1.getFinanceCompany(), cellNum++);

			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}

		} else {
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
		}
		return cellNum;
	}

	private int addDmsLeadProducts(List<DmsLeadProductDto> dmsLeadProductDtoList, Row detailsRow, int cellNum) {
		if (dmsLeadProductDtoList != null && !dmsLeadProductDtoList.isEmpty()) {

			DmsLeadProductDto a1 = dmsLeadProductDtoList.get(0);
			if (null != a1) {
				writeIntoCell(detailsRow, a1.getVariant(), cellNum++);
				writeIntoCell(detailsRow, a1.getColor(), cellNum++);
				writeIntoCell(detailsRow, a1.getFuel(), cellNum++);
				writeIntoCell(detailsRow, a1.getTransimmisionType(), cellNum++);

			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);

			}

		} else {

			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);

		}
		return cellNum;
	}

	private int addSingleAddress(List<DmsAddress> addressList, Row detailsRow, int cellNum) {
		if (addressList != null && !addressList.isEmpty()) {

			DmsAddress a1 = addressList.get(0);
			if (null != a1) {
				writeIntoCell(detailsRow, a1.getHouseNo()+","+a1.getStreet()+","+a1.getVillage(), cellNum++);
				writeIntoCell(detailsRow, a1.getCity(), cellNum++);
				writeIntoCell(detailsRow, a1.getDistrict(), cellNum++);
				writeIntoCell(detailsRow, a1.getState(), cellNum++);
				writeIntoCell(detailsRow, a1.getPincode(), cellNum++);
			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}

		} else {
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
		}
		return cellNum;
	}

	private int addAddress(List<DmsAddress> addressList, Row detailsRow, int cellNum) {
		if (addressList != null && !addressList.isEmpty()) {

			DmsAddress a1 = addressList.get(0);
			if (null != a1) {
				String add = a1.getHouseNo()+","+a1.getStreet()+","+a1.getVillage();
				writeIntoCell(detailsRow, add, cellNum++);
				writeIntoCell(detailsRow, a1.getCity(), cellNum++);
				writeIntoCell(detailsRow, a1.getDistrict(), cellNum++);
				writeIntoCell(detailsRow, a1.getState(), cellNum++);
				writeIntoCell(detailsRow, a1.getPincode(), cellNum++);
			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}

			DmsAddress a2 = addressList.get(1);
			if (null != a2) {
				String add = a1.getHouseNo()+","+a1.getStreet()+","+a1.getVillage();	
				writeIntoCell(detailsRow, add, cellNum++);
				writeIntoCell(detailsRow, a2.getCity(), cellNum++);
				writeIntoCell(detailsRow, a2.getDistrict(), cellNum++);
				writeIntoCell(detailsRow, a2.getState(), cellNum++);
				writeIntoCell(detailsRow, a2.getPincode(), cellNum++);
			} else {
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
				writeIntoCell(detailsRow, "", cellNum++);
			}
		} else {
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
			writeIntoCell(detailsRow, "", cellNum++);
		}
		return cellNum;
	}

	private void genearateExcelForPreEnq(List<ETVPreEnquiry> etvList, String fileName) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Pre Enquiry");
			int rowNum = 0;
			Row row = sheet.createRow(rowNum++);
			List<String> rowHeaders = getPreEnqRowHeaders();
			int cellNum = 0;
			for (String rowHeader : rowHeaders) {
				Cell cell = row.createCell(cellNum);
				cell.setCellValue(rowHeader);
				cellNum++;
			}

			for (ETVPreEnquiry pre : etvList) {
				Row detailsRow = sheet.createRow(rowNum++);
				cellNum = 0;
				writeIntoCell(detailsRow, pre.getLocation(), cellNum++);
				writeIntoCell(detailsRow, pre.getDealerCode(), cellNum++);
				writeIntoCell(detailsRow, pre.getPreEnqId(), cellNum++);
				writeIntoCell(detailsRow, pre.getPreEnqDate().split(" ")[0], cellNum++);
				writeIntoCell(detailsRow, pre.getPreEnqDate().split(" ")[1], cellNum++);
				writeIntoCell(detailsRow, ExcelUtil.formatDate(pre.getPreEnqMonthYear()), cellNum++);
				writeIntoCell(detailsRow, pre.getFirstName(), cellNum++);
				writeIntoCell(detailsRow, pre.getLastName(), cellNum++);
				writeIntoCell(detailsRow, pre.getMobileNo(), cellNum++);
				writeIntoCell(detailsRow, pre.getEmailId(), cellNum++);
				writeIntoCell(detailsRow, pre.getModel(), cellNum++);
				writeIntoCell(detailsRow, pre.getEnqSegment(), cellNum++);
				writeIntoCell(detailsRow, pre.getCustomerType(), cellNum++);
				writeIntoCell(detailsRow, pre.getSourceOfPreEnquiry(), cellNum++);
				writeIntoCell(detailsRow, pre.getSubSoruceOfPreEnquiry(), cellNum++);
				writeIntoCell(detailsRow, pre.getPincode(), cellNum++);
				writeIntoCell(detailsRow,
						ExcelUtil.getDateFormat(
								pre != null && pre.getDropDate() != null ? Timestamp.valueOf(pre.getDropDate()) : null),
						cellNum++);
				writeIntoCell(detailsRow, pre.getDropReason(), cellNum++);
				writeIntoCell(detailsRow, pre.getSubDropReason(), cellNum++);
				writeIntoCell(detailsRow, pre.getAssignedBy(), cellNum++);
				writeIntoCell(detailsRow, pre.getSalesExecutive(), cellNum++);

				// String empName = getEmpNameById(pre.getSalesExecutive());
				// log.debug("SalesExecutive ID:" + pre.getSalesExecutive() + " and Name :" +
				// empName);
				writeIntoCell(detailsRow, pre.getSalesExecutiveEmpId(), cellNum++);
				String teamLeadName = getTeamLead(pre.getSalesExecutiveEmpId());
				writeIntoCell(detailsRow, teamLeadName, cellNum++); // Team Leader
				writeIntoCell(detailsRow, getManager(teamLeadName), cellNum++); // Manager

				writeIntoCell(detailsRow, getTaskRemarks(pre.getUniversalId(),"Pre Enquiry Follow Up"), cellNum++);

			}

			FileOutputStream out = new FileOutputStream(new File(fileName)); // file name with path
			workbook.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getEmpNameById(String empId) {
		// TODO Auto-generated method stub
		return dmsEmployeeRepo.findEmpNameById(empId);
	}

	private String getEmpIdByName(String salesExecutive) {
		// TODO Auto-generated method stub
		return dmsEmployeeRepo.findEmpIdByName(salesExecutive)!=null?dmsEmployeeRepo.findEmpIdByName(salesExecutive).get(0):"";

	}

	private void writeIntoCell(Row row, Object value, int cellNum) {
		Cell cell = row.createCell(cellNum++);

		if (value instanceof String) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((String) value);
		} else if (value instanceof Long) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Long) value);
		} else if (value instanceof Integer) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Integer) value);
		} else if (value instanceof Double) {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Double) value);
		}

	}

	private List<String> getPreEnqRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Pre enquiry ID");
		list.add("Pre enquiry Date");
		list.add("Pre enquiry Time");
		list.add("Pre enquiry Month & Year");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Model");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of pre enquiry");
		list.add("Subsource of Pre enquiry");
		list.add("Pincode");
		list.add("Drop date");
		list.add("Drop Reason");
		list.add("Sub Drop Reason");
		list.add("Assigned by");
		list.add("Sales Executive");
		list.add("Sales Executive EMP ID");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Last remarks");
		return list;
	}

	private List<ETVPreEnquiry> buildPreEnqList(List<LeadStageRefEntity> leadRefDBList, List<DmsLead> leadDBList) {
		List<ETVPreEnquiry> list = new ArrayList<>();
		for (DmsLead lead : leadDBList) {
			log.debug("LEAD ID ::::::::::::::" + lead.getCrmUniversalId());

			ETVPreEnquiry pre = new ETVPreEnquiry();

			List<LeadStageRefEntity> tmpleadRefList = leadRefDBList.stream()
					.filter(x -> x.getLeadId() != null && x.getLeadId().equals(lead.getId()))
					.collect(Collectors.toList());
			LeadStageRefEntity leadRef = tmpleadRefList.get(0);
			pre.setUniversalId(leadRef.getUniversalId());
			
			pre.setLocation("");
			Optional<DmsBranch> optBranch = dmsBranchDao.findById(leadRef.getBranchId());
			if (optBranch.isPresent()) {
				DmsBranch branch = optBranch.get();
				pre.setDealerCode(branch.getName());
				pre.setLocation(getLocationNameFromBranch(branch.getName()));
			}
			
			
			pre.setPreEnqId(leadRef.getRefNo());
			pre.setPreEnqDate(ExcelUtil.getDateFormat(leadRef.getStartDate()));
			pre.setPreEnqMonthYear(getMonthAndYear(leadRef.getStartDate()));
			pre.setFirstName(lead.getFirstName());
			pre.setLastName(lead.getLastName());
			pre.setMobileNo(lead.getPhone());

			pre.setModel(lead.getModel());
			pre.setEnqSegment(lead.getEnquirySegment());
			pre.setSubSoruceOfPreEnquiry(lead.getSubSource()); // blocker
			String pincode = "";
			try {
				if (lead.getDmsAddresses() != null && !lead.getDmsAddresses().isEmpty()) {
					pincode = lead.getDmsAddresses().get(0).getPincode();
					pre.setPincode(pincode);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("exception ", e);
			}
			String customerType = getCustomerTypeByLeadId(lead.getId());
			String emailId = getEmailByLeadId(lead.getId());
			pre.setEmailId(emailId);
			pre.setCustomerType(customerType);
			pre.setSourceOfPreEnquiry(lead.getDmsSourceOfEnquiry().getName());
			pre.setSubSoruceOfPreEnquiry(lead.getSubSource());
			pre.setAssignedBy(lead.getCreatedBy());
			if (lead.getLeadStage().equalsIgnoreCase(DROPPED)) {
				List<DmsLeadDrop> dropList = dmsLeadDropDao.getByLeadId(lead.getId());
				if (dropList != null && !dropList.isEmpty()) {
					DmsLeadDrop droppedLead = dropList.get(0);

					pre.setDropDate(droppedLead.getCreatedDateTime());
					pre.setDropReason(droppedLead.getLostReason());
					pre.setSubDropReason(droppedLead.getLostSubReason());
				}

			}

			pre.setSalesExecutive(lead.getSalesConsultant());
			pre.setSalesExecutiveEmpId(getEmpName(lead.getSalesConsultant()));
			pre.setRemarks(lead.getRemarks());
			list.add(pre);
		}

		return list;
	}

	private String getEmailByLeadId(int id) {
		log.debug("Calling getEmailByLeadId method");
		Query q1 = entityManager.createNativeQuery("SELECT dms_account_id from dms_lead where ID=" + id);
		Object obj1 = q1.getSingleResult();

		String emailId = "";
		if (obj1 != null) {
			emailId = getEmailIDFromDmsAccount((Integer) obj1);
		} else {
			Query q2 = entityManager.createNativeQuery("SELECT dms_contact_id from dms_lead where ID=" + id);
			Object obj2 = q2.getSingleResult();
			emailId = getEmailIdFromDmsContact((Integer) obj2);
		}
		return emailId;
	}

	private String getEmailIdFromDmsContact(Integer id) {
		String customerType = "";
		Query innerQuery = entityManager.createNativeQuery("SELECT email from dms_contact where ID=" + id);
		Object innerObj = innerQuery.getSingleResult();
		if (innerObj != null) {
			customerType = (String) innerObj;
		}
		return customerType;
	}

	private String getEmailIDFromDmsAccount(Integer id) {

		String customerType = "";
		Query innerQuery = entityManager.createNativeQuery("SELECT email from dms_account where ID=" + id);
		Object innerObj = innerQuery.getSingleResult();
		if (innerObj != null) {
			customerType = (String) innerObj;
		}
		return customerType;

	}

	private String getCustomerTypeByLeadId(int id) {
		log.debug("Calling getCustomerTypeByLeadId method");
		Query q1 = entityManager.createNativeQuery("SELECT dms_account_id from dms_lead where ID=" + id);
		Object obj1 = q1.getSingleResult();

		String customerType = "";
		if (obj1 != null) {
			customerType = getCustomerTypeNameFromDmsAccount((Integer) obj1);
		} else {
			Query q2 = entityManager.createNativeQuery("SELECT dms_contact_id from dms_lead where ID=" + id);
			Object obj2 = q2.getSingleResult();
			customerType = getCustomerTypeNameFromDmsContact((Integer) obj2);
		}
		return customerType;
	}

	private String getCustomerTypeNameFromDmsContact(Integer id) {
		String customerType = "";
		Query innerQuery = entityManager.createNativeQuery("SELECT customer_type from dms_contact where ID=" + id);
		Object innerObj = innerQuery.getSingleResult();
		if (innerObj != null) {
			customerType = (String) innerObj;
		}
		return customerType;
	}

	private String getCustomerTypeNameFromDmsAccount(Integer id) {
		String customerType = "";
		Query innerQuery = entityManager.createNativeQuery("SELECT customer_type from dms_account where ID=" + id);
		Object innerObj = innerQuery.getSingleResult();
		if (innerObj != null) {
			customerType = (String) innerObj;
		}
		return customerType;
	}

	private String getLocationNameFromBranch(String name) {

		if (name != null && name.contains("-")) {
			return name.split("-")[1];
		} else {
			return name;
		}

	}

	/*
	private String getEmpName(String salesConsultant) {
		String empId = "";
		empId = dmsEmployeeRepo.findEmpIdByName(salesConsultant);
		return empId;

	}*/
	
	private String getEmpName(String salesConsultant) {
		String empId = null;
		List<String> list = dmsEmployeeRepo.findEmpIdByName(salesConsultant);
		if(list!=null && !list.isEmpty()) {
			empId = list.get(0);
		}
		return empId;

	}

	private String getMonthAndYear(Timestamp timestamp) {

		String tmp = "";
		if (null != timestamp) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(timestamp.getTime());
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			tmp = month + " - " + year;
		}

		return tmp;
	}

	private List<LeadStageRefEntity> getLeadRefDBList(String orgId, String startDate, String endDate, String stageName,
			List<String> branchIdList,String leadStatus) {
		if (null != branchIdList && branchIdList.isEmpty()) {
			log.debug("branchIdList is empty");
			return leadStageRefDao.getLeadsBasedOnStageV2(orgId, startDate, endDate, stageName,leadStatus);
		} else {
			log.debug("branchIdList is not empty");
			return leadStageRefDao.getLeadsBasedOnStageBranchV2(orgId, startDate, endDate, stageName, branchIdList,leadStatus);
		}

	}
	
	private List<LeadStageRefEntity> getLeadRefDBList(String orgId, String startDate, String endDate, String stageName,
			List<String> branchIdList) {
		if (null != branchIdList && branchIdList.isEmpty()) {
			log.debug("branchIdList is empty");
			return leadStageRefDao.getLeadsBasedOnStage(orgId, startDate, endDate, stageName);
		} else {
			log.debug("branchIdList is not empty");
			return leadStageRefDao.getLeadsBasedOnStageBranch(orgId, startDate, endDate, stageName, branchIdList);
		}

	}
	

	private List<String> getEnquiryRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Univeral ID");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Date of birth");
		list.add("Date of aniversary");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Permenent Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Enquiry segment");
		list.add("Customer Type");
		list.add("Source of enquiry");
		list.add("Sub source");
		list.add("Company/Institution");
		list.add("Enquiry Category");
		list.add("Enquiry Status");
		list.add("Event Name");
		list.add("Event ID");
		list.add("Event Start date");
		list.add("Event End date");
		list.add("Event Category");
		list.add("Expected Delivery date");
		list.add("Retail Finance");
		list.add("Finance Category");
		list.add("Down Payment");
		list.add("Loan Amount");
		list.add("Bank/Financier Name");
		list.add("Rate of intrest");
		list.add("Loan tenure");
		list.add("Approx annual Income");
		list.add("Leasing Name");
		list.add("Buyer type");
		list.add("Old car Reg.Number");
		list.add("Old car Make");
		list.add("Old car Model");
		list.add("Old car Variant");
		list.add("Old car Colour");
		list.add("Old car Fuel");
		list.add("Old car Transmission");
		list.add("Old car Month & Year of manufacture");
		list.add("Eval Status");
		list.add("Evaluator Name");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Offered Price");
		list.add("Customer Exp. Price");
		list.add("Approved price");
		list.add("Test Drive Given");
		list.add("Test drive ID");
		list.add("Test Drive Date");
		list.add("Home Visit Status");
		list.add("Home visit date");
		list.add("Enq Ageing");
		list.add("KM's Travelled in Month");
		list.add("Who drives");
		list.add("How many members in home");
		list.add("Pimary expectation");
		list.add("Looking for another make");
		list.add("Looking for another model");
		list.add("Looking for another Variant");
		list.add("CO-Dealership/Competetor Name ");
		list.add("CO-Dealership/Competetor Location");
		list.add("Enquiry Lost date");
		list.add("Enquiry Lost Reason");
		list.add("Lost to Co-delaer Name");
		list.add("Lost to Co-dealer Location");
		list.add("Lost to Competetor");
		list.add("Competetor Model");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Last Remarks");
		return list;
	}

	//////////////////////////////////////////

	// Boking

	private List<String> getBookingRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Univeral ID");
		list.add("Booking ID");
		list.add("Booking Date");
		list.add("Booking Month & Year");
		list.add("Customer ID");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile Number");
		list.add("Email id");
		list.add("Date of birth");
		list.add("Booking address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Pan Number");
		list.add("Aadhar Number");
		list.add("GST Number");
		list.add("Selected Insurance Type");
		list.add("Selected Add on-Covers");
		list.add("Selected Ex-warranty Type");
		list.add("Paid accessories amount");
		list.add("Foc accessories amount");
		list.add("Cash Discount");
		list.add("Ex-Showroom Price");
		list.add("On Road price");
		list.add("Booking Status");
		list.add("Booking payment Mode");
		list.add("Booking Amount");
		list.add("Total Payment received");
		list.add("Pending Amount");
		list.add("Retail Finance");
		list.add("Vehicle Allocation Date");
		list.add("Vehicle Allocation Age");
		list.add("Allocated VIN Number");
		list.add("Preferred Delivery date");
		list.add("Promissed delivery date");
		list.add("Delivery Location");
		list.add("Enquiry to booking Conversion days");
		list.add("Booking age");
		list.add("Enquiry Date");
		list.add("Enquiry Number");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of Enquiry");
		list.add("Subsource of Enquiry");
		list.add("Corporate Name");
		list.add("Event Name");
		list.add("Event Category");
		list.add("Buyer type");
		list.add("Old Car Reg.Number");
		list.add("Old car Model");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Evaluator Name");
		list.add("Eval Status");
		list.add("Customer Exp. Price");
		list.add("Offered Price");
		list.add("Approved Price");
		list.add("Exchnage price Approval status");
		list.add("Booking Cancel date");
		list.add("Booking Cancel Reason");
		list.add("Booking Cancel Sub Reason");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Finance Executive");
		list.add("Last Remarks");
		return list;
	}
	//////////////////////////////////////////////////////////////////
	// Retail

	private List<String> getRetailRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Univeral ID");
		list.add("Invoice ID");
		list.add("Invoice Date");
		list.add("Invoice Month & Year");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile Number");
		list.add("Email id");
		list.add("Confirm billing address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("VIN Number");
		list.add("Chassis Number");
		list.add("Engine Number");
		list.add("Pan Number");
		list.add("Aadhar Number");
		list.add("GST Number");
		list.add("Booking ID");
		list.add("Booking date");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of Enquiry");
		list.add("Subsource of Enquiry");
		list.add("Corporate Name");
		list.add("Event Name");
		list.add("Event ID");
		list.add("Event Start date");
		list.add("Event End date");
		list.add("Event Category");
		list.add("Booking to Retail Conversion days");
		list.add("Exshowroom Price");
		list.add("GST Type");
		list.add("CGST % + SGST %");
	
		list.add("IGST %");
		list.add("UTGST %");
		list.add("CESS %");
		list.add("Total GST %");
		list.add("TCS %");
		list.add("TCS Amount");
		list.add("Consumer offer");
		list.add("Exchange Offer");
		list.add("Corporate Offer");
		list.add("Special Offer");
		list.add("Additional offer 1");
		list.add("Additional offer 2");
		list.add("Cash Discount");
		list.add("FOC accessories amount");
		list.add("Invoice amount");
		list.add("Retail Finance");
		list.add("Finance Name");
		list.add("Buyer Type");
		list.add("Old Car Reg.number");
		list.add("Old car Make");
		list.add("Old car Model");
		list.add("Old car Variant");
		list.add("Old car approved Price");
		list.add("Relationship with customer");
		list.add("Invoice cancel date");
		list.add("Invoice Cancel Reason");
		list.add("Invoice cancel Sub Lost reason");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Finance Executive");
		list.add("Evaluator Name");
		return list;
	}

	////////////////////////////

	// Delivery

	private List<String> getDeliveryRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Univeral ID");
		list.add("Delivery Challan ID");
		list.add("Delivery Date");
		list.add("Delivery Month & Year");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile Number");
		list.add("Email id");
		list.add("Confirm billing address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Engine CC");
		list.add("VIN Number");
		list.add("Chassis Number");
		list.add("Engine Number");
		list.add("Pan Number");
		list.add("Aadhar Number");
		list.add("GST Number");
		list.add("Booking ID");
		list.add("Booking date");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of Enquiry");
		list.add("Subsource of Enquiry");
		list.add("Corporate Name");
		list.add("Event Name");
		list.add("Event ID");
		list.add("Event Start date");
		list.add("Event End date");
		list.add("Event Category");
		list.add("Retail to Delivery Conversion days");
		list.add("Exshowroom Price");
		list.add("GST Type");
		list.add("CGST % + SGST %");
		list.add("IGST %");
		list.add("UTGST %");
		list.add("CESS %");
		list.add("Total GST %");
		list.add("TCS %");
		list.add("TCS Amount");
		list.add("Consumer offer");
		list.add("Exchange Offer");
		list.add("Corporate Offer");
		list.add("Special Offer");
		list.add("Additional offer 1");
		list.add("Additional offer 2");
		list.add("Cash Discount");
		list.add("FOC accessories amount");
		list.add("Invoice amount");
		list.add("Vehicle Purchase date");
		list.add("Vehicle Purchase amount");
		list.add("Exchange status");
		list.add("Buyer Type");
		list.add("Evaluation ID");
		list.add("Evaluation date");
		list.add("Old Car Reg.number");
		list.add("Old car Make");
		list.add("Old car Model");
		list.add("Old car Variant");
		list.add("Old car approved Price");
		list.add("Relationship with customer");
		list.add("EW status");
		list.add("EW Number");
		list.add("EW Start date");
		list.add("EW End date");
		list.add("Insurance Type");
		list.add("Insurane Number");
		list.add("Insurance Company");
		list.add("Insurance Start date");
		list.add("Insurance End date");
		list.add("Insurance premium");
		list.add("Paid accessories amount");
		list.add("Accessories fitting pening parts amount");
		list.add("Retail Finance");
		list.add("Financier Name");
		list.add("Financier branch");
		list.add("Loan amount");
		list.add("Rate of intrest");
		list.add("Loan tenure");
		list.add("EMI Amount");
		list.add("Payout %");
		list.add("Net payout");
		list.add("Disbursed date");
		list.add("Disbursed amount");
		list.add("Payment ref number");
		list.add("Delivery challan cancel date");
		list.add("Delivery challan Cancel Reason");
		list.add("Delivery challan cancel Sub Lost reason");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Finance Executive");
		list.add("Evaluator Name");
		return list;
	}
	///////////////////////////////////
	// Evalution

	private List<String> getEvalutionRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Evaluation Id");
		list.add("Evaluation date");
		list.add("Evaluation Month & Year");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("Evaluation Status");
		list.add("Customer Name");
		list.add("Mobile No");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("VehicleType");
		list.add("Old car Reg.Number");
		list.add("Old car Make");
		list.add("Old car Model");
		list.add("Old car Variant");
		list.add("Old car Colour");
		list.add("Old car Fuel");
		list.add("Old car Transmission");
		list.add("Vin No");
		list.add("Make Year");
		list.add("Old car Month & Year of manufacture");
		list.add("Registartion expiry date");
		list.add("Kms driven");
		list.add("Expected Price");
		list.add("Offered Price");
		list.add("Gap amount");
		list.add("Lead Stage");
		list.add("Enquiry Category");
		list.add("Buyer type");
		list.add("Enq Ageing");
		list.add("Evaluator Name");
		list.add("Evaluation Manager");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Last Remarks");
		return list;
	}

	////////////////////////////////////

	// TestDrive

	private List<String> getTestDriveRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Test Drive ID");
		
		list.add("Test drive Date");
		list.add("Pre enquiry ID");
		list.add("Pre enquiry Date");
		list.add("Pre enquiry Month & Year");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Enquiry Source");
		list.add("Sub source");
		list.add("Test Drive At");
		list.add("Test drive status");
		
		list.add("Test drive Model");
		list.add("Test drive Variant");
		list.add("Driver Name");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Test drive remarks");
		return list;
	}
	//////////////////////////////////////////////////////////////////////////

	// HomeVisit

	private List<String> getHomeVisitRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Home Visit ID");
		list.add("Home visit date");
		list.add("Pre enquiry ID");
		list.add("Pre enquiry Date");
		list.add("Pre enquiry Month & Year");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("Customer Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Enquiry Source");
		list.add("Sub source");
		list.add("Home Visit status");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Home visit remarks");
		return list;
	}
	//////////////////////////////////////////////////////////

	// LiveEnquiry

	private List<String> getLiveEnquiryRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Date of birth");
		list.add("Date of aniversary");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Permenent Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Enquiry segment");
		list.add("Customer Type");
		list.add("Source of enquiry");
		list.add("Sub source");
		list.add("Company/Institution");
		list.add("Enquiry Category");
		list.add("Enquiry Status");
		list.add("Event Name");
		list.add("Event ID");
		list.add("Event Start date");
		list.add("Event End date");
		list.add("Event Category");
		list.add("Expected Delivery date");
		list.add("Retail Finance");
		list.add("Finance Category");
		list.add("Down Payment");
		list.add("Loan Amount");
		list.add("Bank/Financier Name");
		list.add("Rate of intrest");
		list.add("Loan tenure");
		list.add("Approx annual Income");
		list.add("Leasing Name");
		list.add("Buyer type");
		list.add("Old car Reg.Number");
		list.add("Old car Make");
		list.add("Old car Model");
		list.add("Old car Variant");
		list.add("Old car Colour");
		list.add("Old car Fuel");
		list.add("Old car Transmission");
		list.add("Old car Month & Year of manufacture");
		list.add("Eval Status");
		list.add("Evaluator Name");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Offered Price");
		list.add("Customer Exp. Price");
		list.add("Approved price");
		list.add("Test Drive Given");
		list.add("Test drive ID");
		list.add("Test Drive Date");
		list.add("Home Visit Status");
		list.add("Home visit date");
		list.add("Enq Ageing");
		list.add("KM's Travelled in Month");
		list.add("Who drives");
		list.add("How many members in home");
		list.add("Pimary expectation");
		list.add("Looking for another make");
		list.add("Looking for another model");
		list.add("Looking for another Variant");
		list.add("CO-Dealership/Competetor Name ");
		list.add("CO-Dealership/Competetor Location");
		list.add("Enquiry Lost date");
		list.add("Enquiry Lost Reason");
		list.add("Lost to Co-delaer Name");
		list.add("Lost to Co-dealer Location");
		list.add("Lost to Competetor");
		list.add("Competetor Model");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Last Remarks");
		return list;
	}
	////////////////////////////
	/// Live Booking

	private List<String> getLiveBookingRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Booking ID");
		list.add("Booking Date");
		list.add("Booking Month & Year");
		list.add("Customer ID");
		list.add("Salutation");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile Number");
		list.add("Email id");
		list.add("Date of birth");
		list.add("Booking address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Pan Number");
		list.add("Aadhar Number");
		list.add("GST Number");
		list.add("Selected Insurance Type");
		list.add("Selected Add on-Covers");
		list.add("Selected Ex-warranty Type");
		list.add("Paid accessories amount");
		list.add("Foc accessories amount");
		list.add("Cash Discount");
		list.add("Ex-Showroom Price");
		list.add("On Road price");
		list.add("Booking Status");
		list.add("Booking payment Mode");
		list.add("Booking Amount");
		list.add("Total Payment received");
		list.add("Pending Amount");
		list.add("Retail Finance");
		list.add("Vehicle Allocation Date");
		list.add("Vehicle Allocation Age");
		list.add("Allocated VIN Number");
		list.add("Preferred Delivery date");
		list.add("Promissed delivery date");
		list.add("Delivery Location");
		list.add("Enquiry to booking Conversion days");
		list.add("Booking age");
		list.add("Enquiry Date");
		list.add("Enquiry Number");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of Enquiry");
		list.add("Subsource of Enquiry");
		list.add("Corporate Name");
		list.add("Event Name");
		list.add("Event Category");
		list.add("Buyer type");
		list.add("Old Car Reg.Number");
		list.add("Old car Model");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Evaluator Name");
		list.add("Eval Status");
		list.add("Customer Exp. Price");
		list.add("Offered Price");
		list.add("Approved Price");
		list.add("Exchnage price Approval status");
		list.add("Booking Cancel date");
		list.add("Booking Cancel Reason");
		list.add("Booking Cancel Sub Reason");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Finance Executive");
		list.add("Last Remarks");
		return list;
	}

	////////////////////////////

	// EnquiryLost

	private List<String> getEnquiryLostRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Enquiry Lost Date");
		list.add("Enquiry Lost Month & Year");
		list.add("Enquiry ID");
		list.add("Enquiry Date");
		list.add("Enquiry Month & Year");
		list.add("First Name");
		list.add("Last Name");
		list.add("Mobile No");
		list.add("Email Id");
		list.add("Communication Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Permenent Address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Enquiry segment");
		list.add("Customer Type");
		list.add("Source of enquiry");
		list.add("Sub source");
		list.add("Company/Institution");
		list.add("Enquiry Category");
		list.add("Event Name");
		list.add("Event ID");
		list.add("Event Start date");
		list.add("Event End date");
		list.add("Event Category");
		list.add("Expected Delivery date");
		list.add("Retail Finance");
		list.add("Finance Category");
		list.add("Buyer type");
		list.add("Old car Reg.Number");
		list.add("Eval Status");
		list.add("Evaluator Name");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Offered Price");
		list.add("Customer Exp. Price");
		list.add("Approved price");
		list.add("Test Drive Given");
		list.add("Test drive ID");
		list.add("Test Drive Date");
		list.add("Home Visit");
		list.add("Home visit date");
		list.add("Enq Ageing");
		list.add("Lost reason");
		list.add("Sub lost reason");
		list.add("Lost to co-delaer Name");
		list.add("Lost to co-delaer Location");
		list.add("Lost to model");
		list.add("Lost to variant");
		list.add("Lost to compitetor Name");
		list.add("Lost to compitetor Location");
		list.add("Lost to model");
		list.add("Lost to variant");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Last Remarks");
		return list;
	}

	////////////////////////////////////

	// BookingLost

	private List<String> getBookingLostRowHeaders() {
		List<String> list = new ArrayList<>();
		list.add("Location");
		list.add("Dealer Code");
		list.add("Booking Cancel Date");
		list.add("Booking Lost Month & Year");
		list.add("Booking ID");
		list.add("Booking Date");
		list.add("Booking Month & Year");
		list.add("Customer ID");
		list.add("Customer Name");
		list.add("Mobile No");
		list.add("Email id");
		list.add("Booking address");
		list.add("City");
		list.add("District");
		list.add("State");
		list.add("Pincode");
		list.add("Model");
		list.add("Variant");
		list.add("Colour");
		list.add("Fuel");
		list.add("Transmission");
		list.add("Pan Number");
		list.add("Aadhar Number");
		list.add("GST Number");
		list.add("Selected Insurance Type");
		list.add("Selected Add on-Covers");
		list.add("Selected Ex-warranty Type");
		list.add("Retail Finance");
		list.add("Paid accessories amount");
		list.add("Foc accessories amount");
		list.add("Cash Discount");
		list.add("On Road price");
		list.add("Booking Status");
		list.add("Booking payment Mode");
		list.add("Booking Amount");
		list.add("Total Payment received");
		list.add("Pending Amount");
		list.add("Preferred Delivery date");
		list.add("Promissed delivery date");
		list.add("Delivery Location");
		list.add("Booking age");
		list.add("Enquiry Date");
		list.add("Enquiry Number");
		list.add("Enquiry Segment");
		list.add("Customer Type");
		list.add("Source of pre enquiry");
		list.add("Subsource of Pre enquiry");
		list.add("Corporate Name");
		list.add("Event Name");
		list.add("Event Category");
		list.add("Buyer type");
		list.add("Old Car Reg.Number");
		list.add("Old car Model");
		list.add("Evaluation Date");
		list.add("Evaluation number");
		list.add("Evaluator Name");
		list.add("Eval Status");
		list.add("Customer Exp. Price");
		list.add("Offered Price");
		list.add("Sales Executive");
		list.add("Sales Executive Emp Id");
		list.add("Reporting Manager 1");
		list.add("Reporting Manager 2");
		list.add("Finance Executive");
		list.add("Last Remarks");
		return list;
	}

	@Override
	public String generateWizardView(WizardReq request) throws DynamicFormsServiceException {
		log.debug("Inside generateWizardView(){}");
		String res = null;
		try {

			String primaryKeyColumnName = request.getPrimaryKeyColumnName();
			String primaryKeyColumnValue = request.getPrimaryKeyColumnValue();

			String tableName = request.getTableName();
			String bulkUploadedColumnValue = request.getBulkUploadedColumnValue();
			String bulkUploadColName = request.getBulkUploadColName();
			log.debug("tableName " + tableName);
			if (null != tableName && !tableName.isEmpty()) {
				StringBuilder query = new StringBuilder();
				query.append("SELECT * FROM ");
				query.append(tableName);
				query.append(" WHERE ");
				query.append(primaryKeyColumnName + "=" + primaryKeyColumnValue);
				query.append(" AND ");
				query.append(bulkUploadColName + "=" + quote(bulkUploadedColumnValue));
				log.debug("query::" + query);
				List<Object[]> colnHeadersList = new ArrayList<>();
				colnHeadersList = entityManager.createNativeQuery("DESCRIBE " + tableName).getResultList();
				log.debug("colnHeadersList " + colnHeadersList);
				List<String> headers = new ArrayList<>();

				for (Object[] arr : colnHeadersList) {
					String colName = (String) arr[0];
					if (StringUtils.containsIgnoreCase(colName, " as ")) {
						colName = colName.replaceAll("\"", "");
						colName = colName.replaceAll("\'", "");
						colName = colName.substring(StringUtils.indexOfIgnoreCase(colName, " AS") + 3,
								colName.length());
						colName = colName.trim();
						headers.add(colName);
					}
					headers.add(colName);
				}

				log.debug("Coln Headers ::" + headers);

				final List<Map<String, Object>> jObjList = new ArrayList<>();

				Query q = entityManager.createNativeQuery(query.toString());
				List<Object[]> queryResults = q.getResultList();
				log.debug("ColnHeader size " + headers.size() + ",queryResults size " + queryResults.size());
				for (int i = 0; i < queryResults.size(); i++) {
					Object[] objArr = queryResults.get(i);
					Map<String, Object> map = new LinkedHashMap<>();
					for (int j = 0; j < objArr.length; j++) {
						String colName = headers.get(j);
						map.put(colName, objArr[j]);
					}
					jObjList.add(map);
				}
				log.debug("jObjList size " + jObjList.size());

				Map<String, Object> finalMap = new LinkedHashMap<>();
				List<Map<String, Object>> filterdList = new ArrayList<>();
				if (!jObjList.isEmpty()) {
					finalMap.put("pageNo", 0);
					finalMap.put("size", jObjList.size());
					filterdList = jObjList;

				}

				finalMap.put("data", filterdList);
				res = objectMapper.writeValueAsString(finalMap);

			} else {
				throw new DynamicFormsServiceException("Invalid TableName or Request", HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DynamicFormsServiceException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res;
	}

	@Autowired
	WizardDao wizardDao;
	@Autowired
	OrgnizationDao orgnizationDao;

	@Override
	public List<WizardEntity> generateWizardViewPage(String orgId) throws DynamicFormsServiceException {
		return wizardDao.findAll();
	}

	private String getSource(int source) {
		log.info("Within the getSource method");
		String description = "";
		Optional<DmsSourceOfEnquiry> optional = dmsSourceOfEnquiryDao.findById(source);
		if (optional.isPresent()) {
			DmsSourceOfEnquiry dmsSourceOfEnquiry = optional.get();
			description = dmsSourceOfEnquiry.getDescription();
		}
		log.info("sourceOfEnquiry description:{}", description);
		return description;

	}

	@Override
	public List<DmsOrganizationWizard> generateWizardOrgViewPage(String isBulkUpload)
			throws DynamicFormsServiceException {
		return orgnizationDao.findAll();
	}

	@Override
	public List<WizardEntity> generateWizardViewAllPage() throws DynamicFormsServiceException {
		return wizardDao.findAll();
	}
	
	@Override
	public List<DmsOrganizationWizard> getQrCode(int orgId) throws DynamicFormsServiceException {
		List<DmsOrganizationWizard> res =null;
		try {
			res = orgnizationDao.getQrCode(orgId);
		}
		catch (DataAccessException e) {
			throw new DynamicFormsServiceException(env.getProperty("INTERNAL_SERVER_ERROR"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res ;
	}
	
	
	@Override
	public List<EmployeeEntity> getEmpPic(int empId) throws DynamicFormsServiceException {
		List<EmployeeEntity> res =null;
		try {
			res = dmsEmployeeDao.getEmpPic(empId);
		}
		catch (DataAccessException e) {
			throw new DynamicFormsServiceException(env.getProperty("INTERNAL_SERVER_ERROR"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res ;
	}

	@Override
	public List<FollowupReasonsEntity> getfollowupReasons(String orgId,String stageName) throws DynamicFormsServiceException {
		List<FollowupReasonsEntity> res =null;
		try {
			res = followupReasons.getfollowupReasons(orgId,stageName);
		}
		catch (DataAccessException e) {
			throw new DynamicFormsServiceException(env.getProperty("INTERNAL_SERVER_ERROR"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return res ;
	}

	@Override
	public BulkUploadResponse processBulkExcelForOtherMaker(MultipartFile bulkExcel, Integer empId, Integer orgId) throws Exception {
		Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
		Path tmpDir = Files.createTempDirectory("temp");
		Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
		Files.write(tempFilePath, bulkExcel.getBytes());
		String fileName = bulkExcel.getOriginalFilename();
		fileName = fileName.substring(0, fileName.indexOf("."));
		return processBulkExcelOtherMakerDetails(tempFilePath.toString(), empId,orgId);
	}
	
	public BulkUploadResponse processBulkExcelOtherMakerDetails(String inputFilePath, Integer empId, Integer orgId)
			throws Exception {
		Workbook workbook = null;
		Sheet sheet = null;
		List<OtherMaker> makerList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					OtherMaker makerDetails = new OtherMaker();
					if (orgId != null) {
						makerDetails.setOrgId(String.valueOf(orgId));
					} else {
						throw new Exception("OriganistionId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							makerDetails.setOtherMaker(getCellValueBasedOnCellType(row, 0));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Maker cannot be blank");
						}
					} else {
						throw new Exception("Maker cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						try {
							makerDetails.setVehicleSegment(getCellValueBasedOnCellType(row, 1));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Segment cannot be blank");
						}
					} else {
						throw new Exception("Segment cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
						try {
							makerDetails.setStatus(getCellValueBasedOnCellType(row, 2));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Segment cannot be blank");
						}
					} else {
						throw new Exception("Segment cannot be blank");
					}
					makerDetails.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
					makerDetails.setCreatedBy(String.valueOf(empId));
					makerList.add(makerDetails);
				}
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}
		}
		
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		for (OtherMaker maker : makerList) {
			try {
			j++;
			OtherMaker makerdata = otherMakerRepository.save(maker);
			SuccessCount++;
			}catch(DataAccessException e) {
				String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}catch(Exception e) {
				String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}	
		}
		FailedCount=TotalCount-SuccessCount;
		res.setFailedCount(FailedCount);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(SuccessCount);
		res.setTotalCount(TotalCount);
		return res;
	}
	
	private Workbook getWorkBook(File fileName) {
		Workbook workbook = null;
		try {
			String myFileName = fileName.getName();
			String extension = myFileName.substring(myFileName.lastIndexOf("."));
			if (extension.equalsIgnoreCase(".xls")) {
				workbook = new HSSFWorkbook(new FileInputStream(fileName));
			} else if (extension.equalsIgnoreCase(".xlsx")) {
				workbook = new XSSFWorkbook(new FileInputStream(fileName));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return workbook;
	}
	
	private String getCellValueBasedOnCellType(Row rowData, int columnPosition) {
		String cellValue = null;
		Cell cell = rowData.getCell(columnPosition);
		if (cell != null) {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String inputCellValue = cell.getStringCellValue();
				if (inputCellValue.endsWith(".0")) {
					inputCellValue = inputCellValue.substring(0, inputCellValue.length() - 2);
				}
				cellValue = inputCellValue;
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {

					DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
					java.util.Date today = cell.getDateCellValue();
					cellValue = df.format(today);

				} else {
					Integer doubleVal = (int) cell.getNumericCellValue();
					cellValue = Integer.toString(doubleVal);
				}
			}

		}
		return cellValue;
	}

	@Override
	public BulkUploadResponse processBulkExcelForOtherModel(MultipartFile bulkExcel, Integer empId, Integer orgId) throws Exception{
	Resource file = null;
	if (bulkExcel.isEmpty()) {
		BulkUploadResponse res = new BulkUploadResponse();
		List<String> FailedRecords =new ArrayList<>();
		String resonForFailure = "File not found";
		FailedRecords.add(resonForFailure);
		res.setFailedCount(0);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(0);
		res.setTotalCount(0);
		return res;
	}
	Path tmpDir = Files.createTempDirectory("temp");
	Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
	Files.write(tempFilePath, bulkExcel.getBytes());
	String fileName = bulkExcel.getOriginalFilename();
	fileName = fileName.substring(0, fileName.indexOf("."));
	return processBulkExcelOtherMakerModel(tempFilePath.toString(), empId,orgId);
	}
	
	public BulkUploadResponse processBulkExcelOtherMakerModel(String inputFilePath, Integer empId, Integer orgId) throws Exception{
		Workbook workbook = null;
		Sheet sheet = null;
		List<OtherModel> modelList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					OtherModel otherModelDetails = new OtherModel();
					if (orgId != null) {
						otherModelDetails.setOrgId(String.valueOf(orgId));
						} 
					else {
						throw new Exception("OriganistionId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							otherModelDetails.setOtherMaker(getCellValueBasedOnCellType(row, 0));
						} catch (IllegalArgumentException ex) {
							throw new Exception("maker can not be blank");
						}
					} else {
						throw new Exception("maker can not be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						try {
							otherModelDetails.setOtherModel(getCellValueBasedOnCellType(row, 1));
						} catch (IllegalArgumentException ex) {
							throw new Exception("model can not be blank");
						}
					} else {
						throw new Exception("model can not be blank");
					}

					if (StringUtils.isNoneBlank(getCellValueBasedOnCellType(row, 2))) {
						try {
							otherModelDetails.setOthermakerId(Integer.valueOf(getCellValueBasedOnCellType(row, 2)));
						} catch (IllegalArgumentException ex) {
							throw new Exception("OthermakerId can not be blank");
						}
					} else {
						throw new Exception("Othermakerid can not be blank");
					}
					if (StringUtils.isNoneBlank(getCellValueBasedOnCellType(row, 3))) {
						try {
							otherModelDetails.setStatus(getCellValueBasedOnCellType(row, 3));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Status can not be blank");
						}
					} else {
						throw new Exception("Status can not be blank");
					}
					otherModelDetails.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
					otherModelDetails.setCreatedBy(String.valueOf(empId));
					modelList.add(otherModelDetails);
				}	
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}
			
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		for (OtherModel model : modelList) {
			try {
			j++;
			OtherModel modeldata = othermakermodelrepo.save(model);
			SuccessCount++;
			}catch(DataAccessException e) {
				String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}catch(Exception e) {
				String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}	
		}
		FailedCount=TotalCount-SuccessCount;
		res.setFailedCount(FailedCount);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(SuccessCount);
		res.setTotalCount(TotalCount);
		return res;
	}

	@Override
	public BulkUploadResponse processBulkExcelForFollowupReason(MultipartFile bulkExcel, Integer empId,
			Integer orgId) throws Exception {
		Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
		Path tmpDir = Files.createTempDirectory("temp");
		Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
		Files.write(tempFilePath, bulkExcel.getBytes());
		String fileName = bulkExcel.getOriginalFilename();
		fileName = fileName.substring(0, fileName.indexOf("."));
		return bulkExcelFollowUpReason(tempFilePath.toString(), orgId,empId);
	}
	
	public BulkUploadResponse bulkExcelFollowUpReason(String inputFilePath,
			Integer orgId,Integer empId) throws Exception {
		List<FollowupReasonsEntity> response = new ArrayList<>();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		Workbook workbook = null;
		Sheet sheet = null;
		List<FollowupReasonsEntity> followUpList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		BulkUploadResponse res = new BulkUploadResponse();
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					FollowupReasonsEntity followupDetails = new FollowupReasonsEntity();
					if (orgId != null) {
						followupDetails.setOrgId(String.valueOf(orgId));
						} else {
						throw new Exception("OrganizationId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							followupDetails.setStageName(getCellValueBasedOnCellType(row, 0));
						} catch (IllegalArgumentException ex) {
							throw new Exception("stageName cannot be blank");
						}
					} else {
						throw new Exception("StageName  cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						try {
							followupDetails.setReason(getCellValueBasedOnCellType(row, 1));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Reason cannot be blank");
						}

					} else {
						throw new Exception("Reason cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 2))) {
						try {

							followupDetails.setStatus(getCellValueBasedOnCellType(row, 2));
						} catch (IllegalArgumentException ex) {
							throw new Exception("Status field cannot be blank");
						}
					} else {
						throw new Exception("Status field cannot be blank");
					}
					followupDetails.setIsBulkUpload("true");
					followupDetails.setCreatedBy(String.valueOf(empId));
					followupDetails.setCreatedAt(new Timestamp(System.currentTimeMillis()).toString());
					followUpList.add(followupDetails);
				}
				}catch(Exception e) {
					String resonForFailure = e.getMessage();
					System.out.println(resonForFailure);
					FailedRecords.add(resonForFailure);
					continue;
				}
			}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		for (FollowupReasonsEntity followup : followUpList) {
		try {
			j++;
			FollowupReasonsEntity followdata = followupReasons.save(followup);
			SuccessCount++;
			response.add(followdata);
		}catch(DataAccessException e) {
			String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}catch(Exception e) {
			String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}	
	}
	FailedCount=TotalCount-SuccessCount;
	res.setFailedCount(FailedCount);
	res.setFailedRecords(FailedRecords);
	res.setSuccessCount(SuccessCount);
	res.setTotalCount(TotalCount);
	return res;
}

	@Override
	public BulkUploadResponse  processBulkUploadForDeliveryCheckList(MultipartFile bulkExcel, Integer empId,
			Integer orgId) throws Exception {
		Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
		Path tmpDir = Files.createTempDirectory("temp");
		Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
		Files.write(tempFilePath, bulkExcel.getBytes());
		String fileName = bulkExcel.getOriginalFilename();
		fileName = fileName.substring(0, fileName.indexOf("."));
		return bulkUploadForDeliveryCheckList(tempFilePath.toString(), orgId,empId);
	}
	
	public BulkUploadResponse  bulkUploadForDeliveryCheckList(String inputFilePath, Integer orgId, Integer empId)
			throws Exception {
		List<DmsDeliveryCheckList> response = new ArrayList<>();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		BulkUploadResponse res = new BulkUploadResponse();
		Workbook workbook = null;
		Sheet sheet = null;
		List<DmsDeliveryCheckList> checkList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				if (row.getRowNum() != 0) {
					emptyCheck++;
					DmsDeliveryCheckList deliveryChekList = new DmsDeliveryCheckList();
					if (orgId != null) {
						deliveryChekList.setOrgId(String.valueOf(orgId));
					} else {
						throw new Exception("OriganistionId not present");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							deliveryChekList.setItemName(getCellValueBasedOnCellType(row, 0));
						} catch (Exception ex) {
							throw new Exception("ItemName field cannot be blank");
						}
					} else {
						throw new Exception("ItemName field cannot be blank");
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						if (getCellValueBasedOnCellType(row, 1).equals("active")
								|| getCellValueBasedOnCellType(row, 1).equals("Active")) {
							deliveryChekList.setStatus(getCellValueBasedOnCellType(row, 1));
						} else {
							deliveryChekList.setStatus(getCellValueBasedOnCellType(row, 1));
						}
					} else {
						throw new Exception("Status must be in Active,Inactive");
					}
					deliveryChekList.setCreatedDateTime(new Timestamp(System.currentTimeMillis()));
					deliveryChekList.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					checkList.add(deliveryChekList);
				}
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}	
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		for (DmsDeliveryCheckList checkListDetails : checkList) {
		try {
			j++;
			DmsDeliveryCheckList chekListDelivery = deliveryCheckListRepo.save(checkListDetails);
			SuccessCount++;
			response.add(chekListDelivery);
		}catch(DataAccessException e) {
			String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}catch(Exception e) {
			String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
			continue;
		}	
	}
	FailedCount=TotalCount-SuccessCount;
	res.setFailedCount(FailedCount);
	res.setFailedRecords(FailedRecords);
	res.setSuccessCount(SuccessCount);
	res.setTotalCount(TotalCount);
	return res;
	}

	@Override
	public BulkUploadResponse processBulkUploadForInsurenceCompanyName(MultipartFile bulkExcel, Integer empId,
			Integer orgId) throws Exception {
		Resource file = null;
		if (bulkExcel.isEmpty()) {
			BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = "File not found";
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return res;
		}
		Path tmpDir = Files.createTempDirectory("temp");
		Path tempFilePath = tmpDir.resolve(bulkExcel.getOriginalFilename());
		Files.write(tempFilePath, bulkExcel.getBytes());
		String fileName = bulkExcel.getOriginalFilename();
		fileName = fileName.substring(0, fileName.indexOf("."));
		return bulkUploadInsurenceCompanyName(tempFilePath.toString(), orgId,empId);
	}
	
	public BulkUploadResponse bulkUploadInsurenceCompanyName(String inputFilePath, Integer orgId,
			Integer empId) throws Exception {
		List<DmsInsurenceCompanyMd> response = new ArrayList<>();
		List<String> FailedRecords =new ArrayList<>();
		int TotalCount =-1;
		int SuccessCount=0;
		int FailedCount=0;
		int emptyCheck=0;
		Workbook workbook = null;
		Sheet sheet = null;
		List<DmsInsurenceCompanyMd> checkList = new ArrayList<>();
		workbook = getWorkBook(new File(inputFilePath));
		sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		BulkUploadResponse res = new BulkUploadResponse();
		System.out.println(rowIterator.hasNext());
		while (rowIterator.hasNext()) {
			TotalCount++;
			Row row = rowIterator.next();
			try {
				System.out.println(row.getRowNum());
				if (row.getRowNum() != 0) {
					emptyCheck++;
					DmsInsurenceCompanyMd dmsInsurenceCompanyMd = new DmsInsurenceCompanyMd();
					if (orgId != null) {
						dmsInsurenceCompanyMd.setOrgId(String.valueOf(orgId));
					} else {
						throw new Exception("OriganistionId not present"+"in "+row.getRowNum()+1);
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 0))) {
						try {
							dmsInsurenceCompanyMd.setCompanyName(getCellValueBasedOnCellType(row, 0));
						} catch (Exception ex) {
							throw new Exception("CompanyName field cannot be blank"+"in "+row.getRowNum()+1);
						}
					} else {
						throw new Exception("CompanyName field cannot be blank"+"in "+row.getRowNum()+1);
					}
					if (StringUtils.isNotBlank(getCellValueBasedOnCellType(row, 1))) {
						if (getCellValueBasedOnCellType(row, 1).equals("active")
								|| getCellValueBasedOnCellType(row, 1).equals("Active")) {
							dmsInsurenceCompanyMd.setStatus(Status.Active);
						} else {
							dmsInsurenceCompanyMd.setStatus(Status.Inactive);
						}
					} else {
						throw new Exception("Status must be in Active,Inactive"+"in "+row.getRowNum()+1);
					}
					dmsInsurenceCompanyMd.setCreatedDatetime(new Timestamp(System.currentTimeMillis()));
					checkList.add(dmsInsurenceCompanyMd);
				}	
			}catch(Exception e) {
				String resonForFailure = e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}
		}
		if(emptyCheck==0) {
			String resonForFailure = "DATA NOT FOUND";
			System.out.println(resonForFailure);
			FailedRecords.add(resonForFailure);
		}
		int j=0;
		for (DmsInsurenceCompanyMd checkListDetails : checkList) {
			try {
				j++;
				DmsInsurenceCompanyMd chekListDelivery = dmsInsurenceCompanyMdRepo.save(checkListDetails);
				SuccessCount++;
				response.add(chekListDelivery);
			}catch(DataAccessException e) {
				String resonForFailure = "DUPLICATE ENTRY IN "+j+" ROW FOUND";
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}catch(Exception e) {
				String resonForFailure = "ERROR IN SAVEING DATA FOR "+j+" ROW "+e.getMessage();
				System.out.println(resonForFailure);
				FailedRecords.add(resonForFailure);
				continue;
			}	
		}
		FailedCount=TotalCount-SuccessCount;
		res.setFailedCount(FailedCount);
		res.setFailedRecords(FailedRecords);
		res.setSuccessCount(SuccessCount);
		res.setTotalCount(TotalCount);
		return res;
	}
}
