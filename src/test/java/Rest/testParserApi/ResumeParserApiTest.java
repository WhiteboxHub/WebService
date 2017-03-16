package Rest.testParserApi;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;import org.testng.annotations.Test;

public class ResumeParserApiTest<Workbook> {


	String POST_URI = "http://localhost:9000/api/v1/utils/resumes";
	String FILE_NAME = System.getProperty("user.dir") + "\\ResumeS\\" + "resource";
	HttpResponse response;

	HttpClient client;
	HttpPost postRequest;
	


	@BeforeClass
	public void beforeClass() {
		client = new DefaultHttpClient();
		postRequest = new HttpPost(POST_URI);
		
	}

	@Test
	public <Sheet> void apiTest() {

		try {
			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
			//to-do send different valid file names through testng data provider
			File file = new File("C://Users//Innovapath//Desktop//");
			 reqEntity.addBinaryBody("resume", file);
			postRequest.setEntity(reqEntity.build());
			response = client.execute(postRequest);
			assertNotNull(response);

			// to-do log statements instead of print statments
			System.out.println("status code:" + response.getStatusLine().getStatusCode());
			assertEquals(200, response.getStatusLine().getStatusCode());

			// to-do- content type, content length
			   
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}

	@Test
	public void actualContentInResume() {
		try {

			JSONObject json = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
			
			JSONArray skillsArray = json.getJSONArray("skills");
			assertNotNull(skillsArray);

			assertEquals(true, skillsArray.length() >= 1);
			
			assertNotNull(json.get("basics"));
			
			JSONArray worksArray = json.getJSONArray("work");

			assertNotNull(worksArray);
			assertEquals(true, worksArray.length() >= 1);

			for (int i = 0; i <= worksArray.length(); i++) {

				JSONObject worksJson = worksArray.getJSONObject(i);
				assertNotNull(worksJson.get("summary "));
				assertNotNull(worksJson.get("company"));
				assertNotNull(worksJson.get("startDate"));
				assertNotNull(worksJson.get("endDate"));

			}
			
			JSONArray educationsArray = json.getJSONArray("education");

			assertNotNull(educationsArray);
			assertEquals(true, educationsArray.length() >= 1);

		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (IOException e11) {
			e11.printStackTrace();
		}
	}


	@Test
	public void noFile(){
		
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		File file = new File("C:/Users/Innovapath/ Desktop");
		
		
		reqEntity.addBinaryBody("resume", file);
		postRequest.setEntity(reqEntity.build());
		
	try {
			response = client.execute(postRequest);
			
			System.out.println("status code:" + response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}

	assertEquals(404, response.getStatusLine().getStatusCode());}
			

	
	@Test
	public void testInvalidFiles(){
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
		//to-do send different invalid file names through testng data provider
		File file = new File("C:/Users/Innovapath/Desktop");
		  
		
		reqEntity.addBinaryBody("resume", file);
		postRequest.setEntity(reqEntity.build());
		try {
			response = client.execute(postRequest);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(400, response.getStatusLine().getStatusCode());
	}
	
	
	@Test
	public void moreThantwoFiles(int TestId,String TestDescription,int Expected,int Actual,String Result){
		MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();	
		File file = new File("C:/Users/Innovapath/ Desktop");
	  JSONObject obj=new JSONObject();
	  obj.put("TestDEscription",TestDescription );
	
		System.out.println("hiiiii");
	  reqEntity.addBinaryBody("C:/Users/Innovapath/RestWorkSpace/TestApi/ResumeParserApiAutomation/ResumeS/Ang.docx", file);
		postRequest.setEntity(reqEntity.build());
		try {
			response = client.execute(postRequest);
			
			System.out.println("status code:" + response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
		 assertEquals(403, response.getStatusLine().getStatusCode());	
	}


@Test
public void contentNotmatches(){
	
	MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	File file = new File("C:/Users/Innovapath/ Desktop");
	
	reqEntity.addBinaryBody("resume", file);
	postRequest.setEntity(reqEntity.build());
	
try {
		response = client.execute(postRequest);
		
		System.out.println("status code:" + response.getStatusLine().getStatusCode());
	} catch (NullPointerException e) {
	
		e.printStackTrace();
	} catch (IOException e) {
	
		e.printStackTrace();
	}

assertEquals(400, response.getStatusLine().getStatusCode());}
	
	

@Test

   public void noNameResume() throws ClientProtocolException, IOException{
	
	MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	File file = new File("C:/Users/Innovapath/ Desktop/rest-test.xlsx");
    reqEntity.addBinaryBody("resume", file);
	postRequest.setEntity(reqEntity.build());
	try {
		response = client.execute(postRequest);
		
		System.out.println("status code:" + response.getStatusLine().getStatusCode());
	} catch (NullPointerException e) {
	
		e.printStackTrace();
	} catch (IOException e) {
	
		e.printStackTrace();
	}
   assertEquals(405, response.getStatusLine().getStatusCode());
 

}


@Test
public void malFormedscript() throws ClientProtocolException, IOException {
	

	MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();	
	File file = new File("C:/Users/Innovapath/ Desktop");
	
	 reqEntity.addBinaryBody("resume", file);
		postRequest.setEntity(reqEntity.build());
		try {
			response = client.execute(postRequest);
			
			System.out.println("status code:" + response.getStatusLine().getStatusCode());
		} catch (NullPointerException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		 assertEquals(404, response.getStatusLine().getStatusCode());	 

}
}

