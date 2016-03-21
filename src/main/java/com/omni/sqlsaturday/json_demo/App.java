package com.omni.sqlsaturday.json_demo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Output SQL Server insert statements for random users.
 */
public class App {
	private static final String URL_RANDOMUSER = "https://randomuser.me/api/";
	private static final String UTF8 = "UTF-8";
	private static final Charset UTF8_CH = Charset.forName(UTF8);
	private static final String SQL_INSERT_USER_ACCOUNT =
			"INSERT INTO dbo.user_account(stripe_id, username, first_name, last_name, display_name, address1, address2, city, state, zip, image_url)"
			+ " VALUES (...);";
	//INSERT INTO dbo.stripe_log (request_date, request, response_date, response) VALUES(NOW(), ...);
	
	
    public static void main( String[] args ) throws IOException {
    	//parse arguments
    	int amountOfUsersToCreate = 10;
    	if (args != null) {
    		if (args.length >= 1) {
    			amountOfUsersToCreate = Integer.valueOf(args[0]);
    		}
    	}
        System.out.println("Creating " + amountOfUsersToCreate + " random users.");
        
        //create file for output
        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date());
        String outputFileName = "random-users_" + currentTimestamp + "_" + amountOfUsersToCreate + ".txt";
        Path outputFilePath = Paths.get("", outputFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, UTF8_CH, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        	for (int i=0; i < amountOfUsersToCreate; i++) {
	        	//create random user
        			//http request to randomuser
	        
	        	//call to stripe
	        		//capture JSON of request
			        //http request to stripe
	        		//use JSON response from randomuser and strip to create user_account insert
	        		//output user_account insert
		        	//use JSON request and response to create strip_log insert (add status property to JSON response)
	        		//output strip_log insert
        	}
        
	        System.out.println("Finished " + amountOfUsersToCreate + " random users to " + outputFileName);
        } catch (Exception e) {
        	e.printStackTrace();
		}
    }
    
    private static JSONObject httpRandomuser() {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        	HttpGet request = new HttpGet(URL_RANDOMUSER);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);

            String json = EntityUtils.toString(result.getEntity(), UTF8);
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                return (JSONObject)resultObject;
//                    System.out.println(obj.get("example"));
//                    System.out.println(obj.get("fr"));
            } catch (Exception e) {
            	e.printStackTrace();
            }

        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return null;
    }
    
    private static String createUserAccountInsert(JSONObject userJson) {
    	if (userJson == null) {
    		return null;
    	}
    	
    	String insert = null;
    	
    	
    	return insert;
    }
}
