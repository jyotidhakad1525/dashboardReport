package com.automate.df.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.automate.df.dao.DmsOrganizationDao;
import com.automate.df.entity.sales.DmsOrganization;
import com.automate.df.exception.DynamicFormsServiceException;
import com.spire.xls.ExcelVersion;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.spire.xls.WorksheetCopyType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExcelUtil {

	@Value("${tmp.path}")
	String tmpPath;
	
	@Value("${etvbrl.template.filename}")
	String etvbrlTmpltNm;
	
	@Value("${etvbrl.template.fileloc}")
	String etvbrlTmplLoc;
	
	@Autowired
	DmsOrganizationDao dmsOrganizationDao;

	public String mergeFiles(List<String> fileNamesList) {
		String fn = "ETVBRL_" + System.currentTimeMillis() + ".xlsx";
		fileNamesList = fileNamesList.stream().distinct().collect(Collectors.toList());
		try {
			Workbook newBook = new Workbook();
			newBook.getWorksheets().clear();
			Workbook tempBook = new Workbook();
			for (String file : fileNamesList) {
				File f = new File(file);
				if (null != f && f.exists()) {
					tempBook.loadFromFile(file);
					for (Worksheet sheet : (Iterable<Worksheet>) tempBook.getWorksheets()) {
						newBook.getWorksheets().addCopy(sheet, WorksheetCopyType.CopyAll);
					}
				}
			}
			newBook.saveToFile(tmpPath + fn, ExcelVersion.Version2013);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception while merging excel sheets ", e);
		}
		return fn;
	}

	public static String formatDate(String str) {
		String st[] = str.split("-");
		String formatdDate = "";
		if (st.length == 2) {
			String s1 = st[0].trim();
			String s2 = st[1].trim();
			try {
				int month = Integer.parseInt(s1);
				switch (month) {
				case 0:
					formatdDate = "January";
					break;
				case 1:
					formatdDate = "February";
					break;
				case 2:
					formatdDate = "March";
					break;
				case 3:
					formatdDate = "April";
					break;
				case 4:
					formatdDate = "May";
					break;
				case 5:
					formatdDate = "June";
					break;
				case 6:
					formatdDate = "July";
					break;
				case 7:
					formatdDate = "August";
					break;
				case 8:
					formatdDate = "September";
					break;
				case 9:
					formatdDate = "October";
					break;
				case 10:
					formatdDate = "November";
					break;
				case 11:
					formatdDate = "December";
					break;

				}
				formatdDate = formatdDate.concat(" - " + s2);
			} catch (Exception e) {
				log.error("Exception occurred during conversition from string to int:", e);
				formatdDate = str;
			}
		}
		
		return formatdDate;
	}

	public static String getDateFormat(Timestamp timeStamp) {
		String myDate = "";
		if (timeStamp != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			myDate = simpleDateFormat.format(timeStamp);
		}
		
		return myDate;
	}
	public static String getDateFormatV2(Timestamp timeStamp) {
		String myDate = "";
		if (timeStamp != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			myDate = simpleDateFormat.format(timeStamp);
		}
		
		return myDate;
	}
	public static String getDateFormatV2(Date timeStamp) {
		String myDate = "";
		if (timeStamp != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			myDate = simpleDateFormat.format(timeStamp);
		}
		
		return myDate;
	}
	
	public static String getDateFormatV3(Timestamp timeStamp) {
		String myDate = "";
		if (timeStamp != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			myDate = simpleDateFormat.format(timeStamp);
		}
		
		return myDate;
	}
	
	public static String getDateFormatV2OnlyDate(Date timeStamp) {
		String myDate = "";
		if (timeStamp != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			myDate = simpleDateFormat.format(timeStamp);
		}
		
		return myDate;
	}

	public static String getFormateDate(Date date) {
		String myDate = "";
		if (date != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			myDate = simpleDateFormat.format(date);
		}
		
		return myDate;
	}
	
    public void mergeExcelFiles() throws IOException {
    	String fn = "ETVBRL_TEMPLATE.xlsx";
	List<String> fileNamesList = new ArrayList<>();
	String tmpPath = "D:\\automatenda\\upload\\";
	fileNamesList.add(tmpPath+"ENQ_ETVBRL_1650859211559.xlsx");
	
	try {
        Workbook newBook = new Workbook();
		Worksheet enqSheet = null;
		newBook.loadFromFile(tmpPath+fn);
		 for (Object obj: newBook.getWorksheets()) {
			 String sheetName = ((Worksheet) obj).getName();
	         System.out.println(sheetName);
	         if(sheetName.equalsIgnoreCase("Enquiry")) {
	        	 enqSheet = ((Worksheet) obj);
	         }
	      }
		 System.out.println("enqSheet "+enqSheet);
		//newBook.getWorksheets().clear();
		Workbook tempBook = new Workbook();
		for (String file : fileNamesList) {
			File f = new File(file);
			if (null != f && f.exists()) {
				tempBook.loadFromFile(file);
				for (Worksheet sheet : (Iterable<Worksheet>) tempBook.getWorksheets()) {
					//enqSheet.addCopy(sheet, WorksheetCopyType.CopyAll);
					enqSheet.copyFrom(sheet);
				}
			}
		}
		//newBook.savet
	//	newBook.saveToFile(tmpPath + fn, ExcelVersion.Version2016);
		newBook.save();

	} catch (Exception e) {
		e.printStackTrace();
		//log.error("Exception while merging excel sheets ", e);
	}
	//return fn;
	}
    
    
    public String cloneTemplateFile() {
 	    String fn = tmpPath+"ETVBRL_" + System.currentTimeMillis() + ".xlsx";
 	    Path original = Path.of(etvbrlTmplLoc+etvbrlTmpltNm);
        Path target = Path.of(fn);
        try {
            Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("ERROR::"+ex);
            ex.printStackTrace();
        }
 	   return fn;
    }
    
    public boolean verifyTmpltFile() {
    	boolean flag= false;
    	try {
    		File f = new File(etvbrlTmplLoc+etvbrlTmpltNm);
    		flag =f.exists();
    	}catch(Exception e) {
    		flag =false;
    		e.printStackTrace();
    		log.error("Exception "+e);
    		
    	}
    	return flag;
    }
    
	public String generateExcelFromTemplate(List<String> fileNamesList,String orgId)
			throws DynamicFormsServiceException {
		log.debug("inside generateExcelFromTemplate::::::(){},orgId "+orgId);
		log.info("inside generateExcelFromTemplate::::::(){}");
		String tmplFileNm = null;
		String resFileNm=null;
		try {
			// tmplFileNm = cloneTemplateFile();
			String orgTmpltFileNm = dmsOrganizationDao.getTemplateFileName(orgId);
			log.debug("orgTmpltFileNm:::"+orgTmpltFileNm);
			resFileNm="ETVBRL_" + System.currentTimeMillis() + ".xlsx";
			tmplFileNm = tmpPath + resFileNm;
			
			Path original =null;
			
			if(null!=orgTmpltFileNm && orgTmpltFileNm.length()>0) {
				orgTmpltFileNm = orgTmpltFileNm+".xlsx";
				original = Path.of(etvbrlTmplLoc + orgTmpltFileNm);
			}else {
				original = Path.of(etvbrlTmplLoc + etvbrlTmpltNm);
			}
			
			
			
			
			Path target = Path.of(tmplFileNm);
			try {
				Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				log.error("Exception whle copying template file in generateExcelFromTemplate::" + ex);
				ex.printStackTrace();
			}

			Map<String, String> fileMap = generateMap(fileNamesList);
			log.debug("fileMap ::" + fileMap);
			Set<Map.Entry<String, String>> set = fileMap.entrySet();
			Workbook tmpltBook = new Workbook();
			tmpltBook.loadFromFile(tmplFileNm);
			for (Map.Entry<String, String> e : set) {
				String fn = e.getKey();
				String sn = e.getValue();
				log.debug("Adding sheet to template file ,file name " + fn + " and sheet name :" + sn);
				Worksheet sheet = null;
				for (Object obj : tmpltBook.getWorksheets()) {
					String sheetName = ((Worksheet) obj).getName();
					if (sheetName.equalsIgnoreCase(sn)) {
						sheet = ((Worksheet) obj);
					}
				}
				if (null != sheet) {
					Workbook tempBook = new Workbook();
					File f = new File(fn);
					if (null != f && f.exists()) {
						tempBook.loadFromFile(fn);
						for (Worksheet fnsheet : (Iterable<Worksheet>) tempBook.getWorksheets()) {
							sheet.copyFrom(fnsheet);
						}
					}
					tmpltBook.calculateAllValue();
					tmpltBook.save();

				}
			}
			tmpltBook.calculateAllValue();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception occured in generateExcelFromTemplate method of ExcelUtils ",e);
			throw new DynamicFormsServiceException(e.getMessage());
		}
		return resFileNm;
	}

	private Map<String, String> generateMap(List<String> fileNamesList) {
		Map<String, String> map = new HashMap<>();
		for(String s : fileNamesList) {
			
			if(s.contains("PRE_ETVBRL")) {
				map.put(s, "Pre Enquiry");
			}else if(s.contains("ENQ_LOST_")) {
				map.put(s, "Lost Enquiry");
			}else if(s.contains("ENQ_LIVE_")) {
				map.put(s, "Live Enquiry");
			}else if(s.contains("ENQ_ETVBRL")) {
				map.put(s, "Enquiry");
			}else if(s.contains("BOOKING_ETVBRL_")) {
				map.put(s, "Booking");
				
			}else if(s.contains("BOOKING_LOSTETVBRL")) {
				map.put(s, "Lost Booking");
				
			}else if(s.contains("BOOKING_LIVE_ETVBRL_")) {
				map.put(s, "Live Booking");
			}else if(s.contains("RETAIL_ETVBRL_")) {
				map.put(s, "Invoice");
			}else if(s.contains("HV_ETVBRL")) {
				map.put(s, "Home Visit");
			}else if(s.contains("TD_ETVBRL_")) {
				map.put(s, "Test Drive");
			}else if(s.contains("DELIVERY_ETVBRL_")) {
				map.put(s, "Delivery");
			}
			else if(s.contains("EVALUATION_LOSTETVBRL_")) {
				map.put(s, "Evaluation");
			}
		}
		return map;
	}
    
    

}

