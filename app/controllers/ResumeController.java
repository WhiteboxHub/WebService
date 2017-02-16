package controllers;

import static gate.Utils.stringFor;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.util.GateException;
import gate.util.Out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import play.Configuration;
import play.Play;

public class ResumeController {
	public static File parseToHTMLUsingApacheTikka(String file)
			throws IOException, SAXException, TikaException {
		// determine extension
		
		String ext = FilenameUtils.getExtension(file);
		String outputFileFormat = ".html";
		// ContentHandler handler;
		
		System.out.println("hello");
			
	/*	if (ext.equalsIgnoreCase("html") | ext.equalsIgnoreCase("pdf")
				| ext.equalsIgnoreCase("doc") | ext.equalsIgnoreCase("docx")) {
			outputFileFormat = ".html";
			// handler = new ToXMLContentHandler();
		} else if (ext.equalsIgnoreCase("txt") | ext.equalsIgnoreCase("rtf")) {
			outputFileFormat = ".txt";
		} else {
			System.out.println("Input format of the file " + file + " is not supported.");
			return null;
		}*/
		String OUTPUT_FILE_NAME = FilenameUtils.removeExtension(file)
				+ outputFileFormat;
		ContentHandler handler = new ToXMLContentHandler();
		// ContentHandler handler = new BodyContentHandler();
		// ContentHandler handler = new BodyContentHandler(
		// new ToXMLContentHandler());
		InputStream stream = new FileInputStream(file);
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		try {
			System.out.println("hai222");
			parser.parse(stream, handler, metadata);
			FileWriter htmlFileWriter = new FileWriter(OUTPUT_FILE_NAME);
			htmlFileWriter.write(handler.toString());
			htmlFileWriter.flush();
			htmlFileWriter.close();
			return new File(OUTPUT_FILE_NAME);
		} finally {
			System.out.println("hai222");

			stream.close();
			System.out.println(OUTPUT_FILE_NAME);

		}
	}
	public static JSONObject loadGateAndAnnie(File file) throws GateException,
			IOException, NullPointerException {
		Out.prln("Initialising basic system...");
		Gate.init();
		Out.prln("...basic system initialised");

		// initialise ANNIE (this may take several minutes)
		Annie annie = new Annie();
		annie.initAnnie();

		// create a GATE corpus and add a document for each command-line
		// argument
		Corpus corpus = Factory.newCorpus("Annie corpus");
		String current = new File(".").getAbsolutePath();
		URL u = file.toURI().toURL();
		System.out.println(u);
		FeatureMap params = Factory.newFeatureMap();
		params.put("sourceUrl", u);
		params.put("preserveOriginalContent", new Boolean(true));
		params.put("collectRepositioningInfo", new Boolean(true));
		Out.prln("Creating doc for " + u);
		Document resume = (Document) Factory.createResource(
				"gate.corpora.DocumentImpl", params);
		corpus.add(resume);

		// tell the pipeline about the corpus and run it
		annie.setCorpus(corpus);
		annie.execute();

		Iterator iter = corpus.iterator();
		JSONObject parsedJSON = new JSONObject();
		Out.prln("Started parsing...");
		// while (iter.hasNext()) {
		if (iter.hasNext()) { // should technically be while but I am just
								// dealing with one document
			JSONObject profileJSON = new JSONObject();
			Document doc = (Document) iter.next();
			AnnotationSet defaultAnnotSet = doc.getAnnotations();

			AnnotationSet curAnnSet;
			Iterator it;
			Annotation currAnnot;

			// Name
			curAnnSet = defaultAnnotSet.get("NameFinder");
			if (curAnnSet.iterator().hasNext()) 
			{
				currAnnot = (Annotation) curAnnSet.iterator().next();
				JSONObject nameJson = new JSONObject();
				String[] nameFeatures = new String[] { "firstName",
						"middleName", "surname" };
				for (String feature : nameFeatures) {
					String s = (String) currAnnot.getFeatures().get(feature);
					if (s != null && s.length() > 0) {
						nameJson.put(feature, s);
					}
				} 
				profileJSON.put("name", nameJson);
			} 
			curAnnSet = defaultAnnotSet.get("TitleFinder");
			if (curAnnSet.iterator().hasNext()) { // only one title will be
													// found.
				currAnnot = (Annotation) curAnnSet.iterator().next();
				String title = stringFor(doc, currAnnot);
				if (title != null && title.length() > 0) {
					profileJSON.put("title", title);
				}
			}// title

			// email,address,phone,url
			String[] annSections = new String[] { "EmailFinder",
					"AddressFinder", "PhoneFinder", "URLFinder" };
			String[] annKeys = new String[] { "email", "address", "phone",
					"url" };
			for (short i = 0; i < annSections.length; i++) {
				String annSection = annSections[i];
				curAnnSet = defaultAnnotSet.get(annSection);
				it = curAnnSet.iterator();
				JSONArray sectionArray = new JSONArray();
				while (it.hasNext()) { // extract all values for each
										// address,email,phone etc..
					currAnnot = (Annotation) it.next();
					String s = stringFor(doc, currAnnot);
					if (s != null && s.length() > 0) {
						sectionArray.add(s);
					}
				}
				if (sectionArray.size() > 0) {
					profileJSON.put(annKeys[i], sectionArray);
				}
			}
			if (!profileJSON.isEmpty()) {
				parsedJSON.put("basics", profileJSON);
			}

			// awards,credibility,education_and_training,extracurricular,misc,skills,summary
			String[] otherSections = new String[] { "summary",
					"education_and_training", "skills", "accomplishments",
					"awards", "credibility", "extracurricular", "misc" };
			for (String otherSection : otherSections) {
				curAnnSet = defaultAnnotSet.get(otherSection);
				it = curAnnSet.iterator();
				JSONArray subSections = new JSONArray();
				while (it.hasNext()) {
					JSONObject subSection = new JSONObject();
					currAnnot = (Annotation) it.next();
					String key = (String) currAnnot.getFeatures().get(
							"sectionHeading");
					String value = stringFor(doc, currAnnot);
					if (!StringUtils.isBlank(key)
							&& !StringUtils.isBlank(value)) {
						subSection.put(key, value);
					}
					if (!subSection.isEmpty()) {
						subSections.add(subSection);
					}
				}
				if (!subSections.isEmpty()) {
					parsedJSON.put(otherSection, subSections);
				}
			}

			// work_experience
			curAnnSet = defaultAnnotSet.get("work_experience");
			it = curAnnSet.iterator();
			JSONArray workExperiences = new JSONArray();
			while (it.hasNext()) {
				JSONObject workExperience = new JSONObject();
				currAnnot = (Annotation) it.next();
				String key = (String) currAnnot.getFeatures().get(
						"sectionHeading");
				if (key.equals("work_experience_marker")) {
					// JSONObject details = new JSONObject();
					String[] annotations = new String[] { "date_start","date_end", "jobtitle", "organization" };
					for (String annotation : annotations) {
						String v = (String) currAnnot.getFeatures().get(
								annotation);
						if (!StringUtils.isBlank(v)) {
							// details.put(annotation, v);
							workExperience.put(annotation, v);
						}
					}
					// if (!details.isEmpty()) {
					// workExperience.put("work_details", details);
					// }
					key = "text";

				}
				String value = stringFor(doc, currAnnot);
				if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
					workExperience.put(key, value);
				}
				if (!workExperience.isEmpty()) {
					workExperiences.add(workExperience);
				}

			}
			if (!workExperiences.isEmpty()) {
				parsedJSON.put("work_experience", workExperiences);
			}

		}// if
		Out.prln("Completed parsing...");
		return parsedJSON;
	}

public JSONObject transducer(String inputFileName) throws GateException, IOException, SAXException, TikaException {
		System.out.println();
	    Configuration conf = Play.application().configuration();
		System.setProperty("gate.home", "C:\\Program Files\\GATE_Developer_8.2");
		conf.getString("gate.home");
		//String inputFileName = file.getName();
		//System.out.println(inputFileName);
		//File destination = new File(System.getProperty("user.dir")+ "\\json\\" + inputFileName);
		//String outputFileName ="resume.json"; 
		
		//String inputFileName = "E:\\resume.docx"; 
		String outputFileName = "E:\\resume.json";
		System.out.println("I'm outside try");
		JSONObject returnJSON = new JSONObject();
		 
		System.out.println("I'm outside  try");
				
			File tikkaConvertedFile = parseToHTMLUsingApacheTikka(inputFileName);
			System.out.println("I'm inside try");
			if (tikkaConvertedFile != null) {
				System.out.println("I'm inside if");
				JSONObject parsedJSON = loadGateAndAnnie(tikkaConvertedFile);
				System.out.println("I crossed parsedJson");
				JSONObject resultJSON = resultJSON(parsedJSON);
			//	returnJSON = resultJSON;
				System.out.println("I crossed resultJson");
				Out.prln("Writing to output...");
				FileWriter jsonFileWriter = new FileWriter(outputFileName);
				jsonFileWriter.write(resultJSON.toJSONString());
				returnJSON = resultJSON;
				jsonFileWriter.flush();
				jsonFileWriter.close();
				
				Out.prln("Output written to file " +outputFileName);
			}
		//} catch (Exception e) {
			// TODO Auto-generated catch block
			//System.out.println("Sad Face :( .Something went wrong.");
			//e.printStackTrace();
		//}
		return returnJSON;	
	}
	public JSONObject resultJSON(JSONObject obj)
	{
		  JSONObject resultJSON = new JSONObject();
		  resultJSON.put("basics",basicJSON(obj));
		  //Out.prln(resultJSON);
		  resultJSON.put("work",workJSON(obj));
		  JSONArray array = interestsJSON(obj);
		  if(!array.isEmpty()){
		  resultJSON.put("interests",array);
		  }
		  resultJSON.put("education",educationJSON(obj));
		  resultJSON.put("skills",skillsJSON(obj));
		  return resultJSON;
	}
	public JSONObject basicJSON(JSONObject obj)
	{
	  JSONObject basicJSON = new JSONObject();
		  if(obj.containsKey("basics"))
		  {
		  			 //Out.prln(obj.get(key));
			  JSONObject subObj =(JSONObject)obj.get("basics");
			  String[] basics = {"name","title","email","phone","url"};
			  String[] summary = {"summary"};
			  for(String key1:basics)
			  	{
				  if(subObj.containsKey(key1))
				  {
					  if(key1=="title")
						  basicJSON.put("label", subObj.get(key1));
					  else if(key1=="url")
						  basicJSON.put("website", subObj.get(key1));
					  else if(key1=="name")
					  {
						String s="";
						JSONObject nameObj = (JSONObject) subObj.get("name");
						String names[] = {"firstName","middleName","surname"};
						for(String key:names)  
						{
						if(nameObj.containsKey(key))
						  {
							  String name = (String) nameObj.get(key);
							  s=s+name+" ";
								//Out.prln(s);
						  }
						}
						  basicJSON.put(key1,s);
					  }

					  else
						  basicJSON.put(key1, subObj.get(key1));
				  }
			  	}
		  	}
		  //JSONArray summaryJSON = new JSONArray();
		  if(obj.containsKey("summary"))
		  {
			  JSONArray arr = (JSONArray)obj.get("summary");
			  String[] summary = {"Summary","SUMMARY","PROFESSIONAL SUMMARY","CAREER OBJECTIVE","PROFESSIONAL EXPERIENCE","Profile","PROFILE","Career Summary"};
			  for(int i=0;i<arr.size();i++)
			  {
				  JSONObject json = (JSONObject)arr.get(i);
				  JSONObject sum = new JSONObject();
				  for(String key:summary)
				  {
					  if(json.containsKey(key))
					  {
						  basicJSON.put("summary",json.get(key));
					  }
				  }
				  //basicJSON.put("summary",sum);  
			  }
			  //basicJSON.put("summary",summaryJSON);
		  }	  //basicsJSON.put("basics", basicJSON);
	  return basicJSON;
	}
	public JSONArray workJSON(JSONObject obj)
	{
		  JSONArray workJSON = new JSONArray();
			  if(obj.containsKey("work_experience"))
			  {
				  JSONArray arr = (JSONArray)obj.get("work_experience");
				  String[] works = {"organization","jobtitle","url","date_start","date_end","text","Projects"};
				  for(int i=0;i<arr.size();i++)
				  {
					  JSONObject json = (JSONObject)arr.get(i);
					  JSONObject work = new JSONObject();
					  for(String key:works)
					  {
						  if((json.containsKey(key)))
						  {
							  if(key=="organization")
								  work.put("company",json.get(key));
							  else if(key=="jobtitle")
							  work.put("position",json.get(key));
							  else if(key=="url")
								  work.put("website",json.get(key));
							  else if(key=="date_start")
								  work.put("startDate",json.get(key));
							  else if(key=="date_end")
								  work.put("endDate",json.get(key));
							  else if(key=="text")
								  work.put("summary",json.get(key));
							  else
								  work.put("summary",json.get(key));
						  }
					  }
					  if(!work.isEmpty()){
					  workJSON.add(work);
					  }
				  }
			  }
		/*	  JSONArray resultJSON = new JSONArray();
		for(int i=0;i<workJSON.size();i++)	  
		{
		if (!workJSON.get(i).equals(null))
			resultJSON.add(workJSON.get(i));
		}*/
		return workJSON;
	}
	public JSONArray interestsJSON(JSONObject obj)
{
	JSONObject interestsJSON = new JSONObject();
	JSONArray  arr = new JSONArray();
		  if(obj.containsKey("misc"))
		  {
			  
			  arr = (JSONArray)obj.get("misc");
		  }
	  return arr;
}
	public JSONArray educationJSON(JSONObject obj)
{
	JSONArray  arr = new JSONArray();
	if(obj.containsKey("basics"))
	{
		  if(obj.containsKey("education_and_training"))
		  {
			  arr = (JSONArray)obj.get("education_and_training");
		  }
	}
	return arr;
}
	public JSONArray skillsJSON(JSONObject obj)
{
	JSONArray  skillsArr = new JSONArray();
	if(obj.containsKey("basics"))
	{
		  if(obj.containsKey("skills"))
		  {
			  JSONArray  arr = new JSONArray();
			 
			  arr = (JSONArray)obj.get("skills");
			  if(arr!=null)
				  skillsArr=arr;
		  }
	}
	return skillsArr;
}
}