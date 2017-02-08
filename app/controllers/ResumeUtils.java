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
import org.apache.pdfbox.PDFReader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

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
		  System.out.println("Hey there....!!!");
		  MultipartFormData  body = request().body().asMultipartFormData();
		  FilePart resume = body.getFile("resume");
	      File file = resume.getFile();
	      Calendar calendar = Calendar.getInstance();		  
		  String fileName = resume.getFilename();
	      String extension = FilenameUtils.getExtension(fileName);
		  System.out.println("Extension "+extension);
		      fileName  =  FilenameUtils.removeExtension(fileName);
		      System.out.println("FileNAme "+fileName);
				
		    
		      
		        
			  fileName = "resume_" + calendar.getTimeInMillis() +"."+extension;
			  System.out.println(fileName);		    
				//File destination = new File("C:\\play\\Rest\\ResumeS\\"+fileName);
			  File destination = new File(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName);
			  InputStream inStream = null;
			  OutputStream outStream = null;
		    	try
		    	{
		    	    inStream = new FileInputStream(file);
		    	    outStream = new FileOutputStream(destination);
		    	    byte[] buffer = new byte[1024];
		    	    int length;
		    	    //copy the file content in bytes
		    	    while ((length = inStream.read(buffer)) > 0)
		    	    {
		    	    	outStream.write(buffer, 0, length);
		    	    }
		    	    inStream.close();
		    	    outStream.close();	    	    
		    	 }
		    	catch(IOException e)
		    	{
		    	    e.printStackTrace();
		    	}   
			  
		     JSONObject s = rc.transducer(System.getProperty("user.dir")+ "\\ResumeS\\" + fileName);
		   return ok(Json.toJson(s));
		
	    	
		  }
	
}
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
