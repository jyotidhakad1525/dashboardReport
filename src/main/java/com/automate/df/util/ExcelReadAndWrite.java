package com.automate.df.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

public class ExcelReadAndWrite {

    public static void main(String[] args) throws IOException {
        ExcelReadAndWrite excel = new ExcelReadAndWrite();
        //excel.process("D:\\automatenda\\upload\\ENQ_ETVBRL_1650859211559.xlsx");
        excel.mergeExcelFiles();
    }

   public String cloneTemplateFile() {
	   String fn = null;
	   FileSystem system = FileSystems.getDefault();
       Path original = system.getPath("C:\\programs\\my.xlsx");
       Path target = system.getPath("C:\\programs\\my2.xlsx");

       try {
           // Throws an exception if the original file is not found.
           Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
       } catch (IOException ex) {
           System.out.println("ERROR");
       }
	   return fn;
   }
    public void mergeExcelFiles() throws IOException {
    	String fn = "ETVBRLD - FormulatedSheet.xlsx";
	List<String> fileNamesList = new ArrayList<>();
	String tmpPath = "D:\\automatenda\\upload\\";
	fileNamesList.add(tmpPath+"PRE_ETVBRL_1650977417627.xlsx");
	
	try {
        Workbook newBook = new Workbook();
		Worksheet enqSheet = null;
		newBook.loadFromFile(tmpPath+fn);
		for (Object obj : newBook.getWorksheets()) {
			String sheetName = ((Worksheet) obj).getName();
			System.out.println(sheetName);
			if (sheetName.equalsIgnoreCase("Pre Enquiry")) {
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
					// enqSheet.addCopy(sheet, WorksheetCopyType.CopyAll);
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
    
    
    public void process(String fileName) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(bis);
        
        BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream("D:\\automatenda\\upload\\ETVBRL_TEMPLATE.xlsx"));
        XSSFWorkbook myWorkBook = new XSSFWorkbook(bis2);
        XSSFSheet sheet = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        XSSFSheet mySheet = null;
        XSSFRow myRow = null;
        XSSFCell myCell = null;
        int sheets = workbook.getNumberOfSheets();
        int fCell = 0;
        int lCell = 0;
        int fRow = 0;
        int lRow = 0;
        for (int iSheet = 0; iSheet < sheets; iSheet++) {
            sheet = workbook.getSheetAt(iSheet);
            if (sheet != null && sheet.getSheetName().equalsIgnoreCase("Enquiry")) {
                //mySheet = myWorkBook.createSheet(sheet.getSheetName());
                mySheet = myWorkBook.getSheet("Enquiry");
                fRow = sheet.getFirstRowNum();
                lRow = sheet.getLastRowNum();
                for (int iRow = fRow; iRow <= lRow; iRow++) {
                    row = sheet.getRow(iRow);
                    myRow = mySheet.createRow(iRow);
                    if (row != null) {
                        fCell = row.getFirstCellNum();
                        lCell = row.getLastCellNum();
                        for (int iCell = fCell; iCell < lCell; iCell++) {
                            cell = row.getCell(iCell);
                            myCell = myRow.createCell(iCell);
                            if (cell != null) {
                                myCell.setCellType(cell.getCellType());
                                switch (cell.getCellType()) {
                                case XSSFCell.CELL_TYPE_BLANK:
                                    myCell.setCellValue("");
                                    break;

                                case XSSFCell.CELL_TYPE_BOOLEAN:
                                    myCell.setCellValue(cell.getBooleanCellValue());
                                    break;

                                case XSSFCell.CELL_TYPE_ERROR:
                                    myCell.setCellErrorValue(cell.getErrorCellValue());
                                    break;

                                case XSSFCell.CELL_TYPE_FORMULA:
                                    myCell.setCellFormula(cell.getCellFormula());
                                    break;

                                case XSSFCell.CELL_TYPE_NUMERIC:
                                    myCell.setCellValue(cell.getNumericCellValue());
                                    break;

                                case XSSFCell.CELL_TYPE_STRING:
                                    myCell.setCellValue(cell.getStringCellValue());
                                    break;
                                default:
                                    myCell.setCellFormula(cell.getCellFormula());
                                }
                            }
                        }
                    }
                }
            }
        }
        bis.close();
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream("D:\\automatenda\\upload\\ETVBRL_TEMPLATE.xlsx", true));
        myWorkBook.write(bos);
        bos.close();
    }
}
