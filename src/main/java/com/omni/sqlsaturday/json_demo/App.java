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
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Output SQL Server insert statements for random users.
 */
public class App implements Runnable {
	private static final String URL_RANDOMUSER = "https://randomuser.me/api/?nat=us&results=%d";
	private static final String STRIPE_PUBLIC_KEY = "sk_test_RxszT1p17bDnzd6CQPwM3Q8J";
	private static final String STRIPE_CHARGE_URL = "https://api.stripe.com/v1/charges";
	private static final String UTF8 = "UTF-8";
	private static final Charset UTF8_CH = Charset.forName(UTF8);
	private static final String LINE_BREAK = "\n";
	private static final String SQL_INSERT_USER_ACCOUNT =
			"INSERT INTO dbo.user_account(stripe_id, username, first_name, last_name, display_name, address1, address2, city, state, zip, image_url)"
			+ " VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
	private static final String SQL_INSERT_STRIPE_LOG =
			"INSERT INTO dbo.stripe_log(request_date, request, response_date, response)"
			+ " VALUES (GETDATE(), '%s', GETDATE(), '%s');";
	private Integer amountOfUsersToCreate;
	private String filePrefix;
	
    public static void main( String[] args ) throws IOException {
    	//parse arguments
    	int amountOfUsersToCreate = 5000;
    	int numThreads = 10;
    	if (args != null) {
    		if (args.length >= 1) {
    			amountOfUsersToCreate = Integer.valueOf(args[0]);
    		}
    	}
    	
    	ExecutorService threadPool = Executors.newCachedThreadPool();
    	for(int x = 0; x < numThreads; x++) {
    		threadPool.execute(new App(amountOfUsersToCreate/numThreads, "thread" + x));
    	}
    	threadPool.shutdown();
    	try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public App(Integer amountOfUsersToCreate, String filePrefix) {
    	this.amountOfUsersToCreate = amountOfUsersToCreate;
    	this.filePrefix = filePrefix;
    }
    
    public void run() {
        System.out.println("Creating " + amountOfUsersToCreate + " random users.");
        
        //create file for output
        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd-hh-mm").format(new Date());
        String outputFileName = filePrefix + "_random-users_" + currentTimestamp + "_" + amountOfUsersToCreate + ".sql";
        Path outputFilePath = Paths.get("sql", outputFileName);

        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, UTF8_CH, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
        	
        	JSONArray users = httpRandomuser();
        	for(Object obj: users) {
        		JSONObject user = (JSONObject)((JSONObject)obj).get("user");
        		
        		((JSONObject)user.get("location")).put("state", StateUtil.getByName(((JSONObject)user.get("location")).get("state").toString()));
	        	//create random user
        			//http request to randomuser
	        
	        	/** call to stripe to create charge **/
        		// capture JSON of request
        		StripeCharge stripeRequest = new StripeCharge(user);
		        // http request to stripe
        		JSONObject stripeResponse = chargeCard(stripeRequest);
        		stripeRequest.put("username", user.get("username"));
        		
        		//use JSONObjects to create inserts to output
        		//use JSON response from randomuser and strip to create user_account insert
        		String userAccountInsert = createUserAccountInsert(user, stripeResponse);
	        		//output user_account insert
        		if (StringUtils.isEmpty(userAccountInsert)) {
        			System.out.println("ERROR: No user_account insert generated for user " + user.get("username"));
        			continue;
        		} else {
        			writer.write(userAccountInsert + LINE_BREAK);
        		}
        		
        			//use JSON request and response to create stripe_log insert (add status property to JSON response)
        		String stripeLogInsert = createStripeLogInsert(stripeRequest, stripeResponse);
        			//output strip_log insert
        		if (StringUtils.isEmpty(stripeLogInsert)) {
        			System.out.println("ERROR: No stripe_log insert generated for user " + user.get("username"));
        			continue;
        		} else {
        			writer.write(stripeLogInsert + LINE_BREAK);
        		}
        	}
        
	        System.out.println("Finished " + amountOfUsersToCreate + " random users to " + outputFileName);
        } catch (Exception e) {
        	e.printStackTrace();
		}
    }
    
    private JSONArray httpRandomuser() {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
        	String url = String.format(URL_RANDOMUSER, amountOfUsersToCreate);
        	HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);

            String json = EntityUtils.toString(result.getEntity(), UTF8);
            System.out.println(filePrefix + " Random user response: " + json);
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                return (JSONArray)((JSONObject)resultObject).get("results");
            } catch (Exception e) {
            	e.printStackTrace();
            }

        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        return null;
    }
    
    private String createUserAccountInsert(JSONObject user, JSONObject stripeResponse) {
    	if (user == null || stripeResponse == null) {
    		return null;
    	}
    	
    	JSONObject name = (JSONObject)user.get("name");
    	JSONObject location = (JSONObject)user.get("location");
    	JSONObject picture = (JSONObject)user.get("picture");
    	
    	String stripe_id = (String)stripeResponse.get("id");
    	String username = (String)user.get("username");
    	String first_name = WordUtils.capitalize((String)name.get("first"));
    	String last_name = WordUtils.capitalize((String)name.get("last"));
    	String display_name = WordUtils.capitalize((String)name.get("title")) + ". " + first_name + " " + last_name;
    	String address1 = WordUtils.capitalize((String)location.get("street"));
    	String address2 = "";
    	String city = WordUtils.capitalize((String)location.get("city"));
    	String state = (String)location.get("state");
    	String zip = String.format("%05d", (Long)location.get("zip"));
    	String image_url = (String)picture.get("medium");
    	
    	return String.format(SQL_INSERT_USER_ACCOUNT,
    			stripe_id, username, first_name, last_name, display_name, address1, address2, city, state, zip, image_url);
    }
    
    private String createStripeLogInsert(JSONObject stripeRequest, JSONObject stripeResponse) {
    	if (stripeRequest == null || stripeResponse == null) {
    		return null;
    	}
    	
    	return String.format(SQL_INSERT_STRIPE_LOG, stripeRequest.toJSONString().replaceAll("'", "''"), stripeResponse.toJSONString().replaceAll("'", "''"));
    }
    
    
    /**
     * Converts the given <code>customer</code> into a StripeCharge and POSTs it to Stripe
     * @param customer
     * @return the charge object created by Stripe in response to the charge request, or a Stripe error object if an error card number was used
     */
    @SuppressWarnings("unchecked")
	private JSONObject chargeCard(StripeCharge charge) {
    	HttpPost request = new HttpPost(STRIPE_CHARGE_URL);
    	request.addHeader("Content-Type", "application/x-www-form-urlencoded");
    	
    	CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
        		AuthScope.ANY,
                new UsernamePasswordCredentials(STRIPE_PUBLIC_KEY, null));
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

    	try {
    		String username = charge.get("username").toString();
    		// I am a terrible person
    		charge.remove("username");
    		
    		StringEntity params = new StringEntity(toFormData(charge));
    		request.setEntity(params);
    		
            HttpResponse result = httpClient.execute(request);
            int httpStatus = result.getStatusLine().getStatusCode();

            String json = EntityUtils.toString(result.getEntity(), UTF8);
            try {
                JSONParser parser = new JSONParser();
                JSONObject resultObject = (JSONObject)parser.parse(json);
                resultObject.put("httpStatus", httpStatus);
                resultObject.put("username", username);
                return resultObject;
            } catch (Exception e) {
            	e.printStackTrace();
            }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    	return null;
    }
    
    private String toFormData(JSONObject obj) {
    	String format = "%s=%s&";
    	String subObjectFormat = "%s[%s]=%s&";
    	StringBuilder sb = new StringBuilder();
    	for(Object key: obj.keySet()) {
    		Object value = obj.get(key);
    		if(value instanceof Map) {
    			// This is a sub-object, which has a different syntax
    			Map map = (Map)value;
    			for(Object subKey: map.keySet()) {
    				Object subValue = map.get(subKey);
    				try {
    					sb.append(String.format(subObjectFormat, key.toString(), subKey.toString(), subValue.toString()));
    				} catch(NullPointerException e) {
    					System.out.println("key: " + key + ", subKey: " + subKey + ", subValue: " + subValue);
    				}
    			}
    		} else {
    			sb.append(String.format(format, key.toString(), value.toString()));
    		}
    	}
    	
    	String result = sb.toString();
    	if(result.endsWith("&")) result = result.substring(0, result.length() - 1);
    	System.out.println("Form data: " + result);
    	return result;
    }
    
}
