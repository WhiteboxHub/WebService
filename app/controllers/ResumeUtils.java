package controllers;
import gate.util.GateException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.json.simple.JSONObject;
import org.xml.sax.SAXException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.util.List;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
public class ResumeUtils extends  Controller   
{
	public static Result resumes() throws GateException, IOException, SAXException, TikaException
	{   
	    
		ResumeController rc = new ResumeController();
		  
		  MultipartFormData  body = request().body().asMultipartFormData();
		  FilePart resume = body.getFile("resume");
	      File file = resume.getFile();
	      Calendar calendar = Calendar.getInstance();		  
		  String fileName = resume.getFilename();
		  String ext = FilenameUtils.getExtension(fileName);
	      fileName  =  FilenameUtils.removeExtension(fileName);
		  System.out.println("FileNAme "+fileName);
		  String originalFile=fileName;
		  fileName = "resume_" + calendar.getTimeInMillis() +"."+ext;
		  System.out.println(fileName);		    	
		  File destination = new File(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName);
		  InputStream inStream = null;
		  OutputStream outStream = null;
		  inStream = new FileInputStream(file);
		  outStream = new FileOutputStream(destination);
		  byte[] buffer = new byte[1024];
		  int length;
		    while ((length = inStream.read(buffer)) > 0)
		    	    {
		    	    	outStream.write(buffer, 0, length);
		    	    }
		    	    inStream.close();
		    	    outStream.close();
		    if (ext.equalsIgnoreCase("html") | ext.equalsIgnoreCase("txt")
							| ext.equalsIgnoreCase("doc"))
					 
				  {	   		    	 
	
		             JSONObject s = rc.transducer(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName);	
		              return ok(Json.toJson(s));
		          }
		    else if (ext.equalsIgnoreCase("pdf")) 
			 
		          {
			    	   		    	 
			    	System.out.println("hai");
				 		   
				 	  FileWriter fwv=new FileWriter(System.getProperty("user.dir")+ "\\pdf\\" + fileName);
				 	  BufferedWriter bw=new BufferedWriter(fwv);
				 	  PdfReader pr=new PdfReader(System.getProperty("user.dir")+ "\\pdf\\" + originalFile);
				 	  int pNum=pr.getNumberOfPages();
				 	  for(int page=1;page<=pNum;page++)
				 	     {
				 		    String text=PdfTextExtractor.getTextFromPage(pr, page);
				 		    bw.write(text);
				 		    bw.newLine();
				 		  
				 		    System.out.println();
				 		 }
				 		   bw.flush();
				 		   bw.close();

					  JSONObject s = rc.transducer(System.getProperty("user.dir")+ "\\pdf\\" + fileName);	
		

			   return ok(Json.toJson(s));
		                  }
		  
		   else if (ext.equalsIgnoreCase("docx"))  
		   {
			   XWPFDocument docx = new XWPFDocument(new  FileInputStream(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName ));
		        List<XWPFParagraph> paragraphList =  docx.getParagraphs();

		        XWPFDocument document= new XWPFDocument(); 
		        FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName ));
		        XWPFParagraph n = document.createParagraph();
		        XWPFRun run=n.createRun();

		        for (XWPFParagraph paragraph: paragraphList)
		        { 
		            run.setText(paragraph.getText());              
		            run.addCarriageReturn();
		        }
		        document.write(out); 
		        document.close();   
		        out.close();
		        JSONObject s = rc.transducer(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName);
		        return ok(Json.toJson(s)); 
		                  }
		    else  {
				System.out.println("Input format of the file " + file + " is not supported.");
		   		return null;
		   }
           }
           }