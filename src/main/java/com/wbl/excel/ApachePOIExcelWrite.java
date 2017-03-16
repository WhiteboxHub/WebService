package com.wbl.excel;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApachePOIExcelWrite {

    private static final String FILE_NAME = "C:/Users/Innovapath/ Desktop";

    public static void main(String[] args) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Datatypes in Java");
        Object[][] datatypes = {
        		{"Testid", "Test description", "Teststeps","testData","Expected","Actual","Result"},
                {1, "validfiles","","",200,200,"success"},
                {2, "Nofile","","",400,400,"badrequest"},
                {3, "InvalidFileFormat","",404,404,"notfound" },
                {4, "MoreTHan ONE FileUPloaded","","C:/Users/Innovapath/RestWorkSpaceTestApi/ResumeParserApiAutomation/ResumeS",403,403,"access Forbidden"},
                {5, "contentTYpe-Length-Responsetime","",""},
                {6,"Uploadvalidafile-Nocontent-Resume","",401,401,"Unathourized"},
                {7,"ResumewithoutName","",400,400,"badRequest"},
                {8,"MalFormedScriptinsideFile","",500,500,"internal Server Error"},
        
              
        };
        int rowNum = 0;
        System.out.println("Creating excel");

        for (Object[] datatype : datatypes) {
            XSSFRow row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                XSSFCell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                	 cell.setCellValue((Integer) field);
                }
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }
}
