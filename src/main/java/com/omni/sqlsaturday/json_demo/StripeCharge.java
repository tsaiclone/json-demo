package com.omni.sqlsaturday.json_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONObject;

public class StripeCharge extends JSONObject {

	private static final long serialVersionUID = 1L;
	
	private final Random random = new Random();

	@SuppressWarnings("unchecked")
	public StripeCharge(JSONObject customer) {
		JSONObject user = (JSONObject)customer.get("user");
		String firstName = ((JSONObject)user.get("name")).get("first").toString();
    	String lastName = ((JSONObject)user.get("name")).get("last").toString();
    	String address1 = ((JSONObject)user.get("location")).get("street").toString();
    	String city = ((JSONObject)user.get("location")).get("city").toString();
    	String state = ((JSONObject)user.get("location")).get("state").toString();
    	String zip = ((JSONObject)user.get("location")).get("zip").toString();
//    	String imageUrl = ((JSONObject)((JSONObject)customer.get("user")).get("picture")).get("medium").toString();
    	String username = user.get("username").toString();
    	
		put("amount", getRandomBetween(1, Integer.MAX_VALUE)); // in pennies
		put("currency", "USD");
		put("description", "Payment for " + firstName + " " + lastName);
		put("statement_descriptor", "Monthly payment for " + firstName + " " + lastName);
		put("username", username);
		
		JSONObject source = new JSONObject();
		source.put("name", firstName + " " + lastName);
		source.put("exp_month", String.valueOf(getRandomBetween(1, 12)));
		source.put("exp_year", String.valueOf(getRandomBetween(1990, 2025)));
    	source.put("number", String.valueOf(getRandomCardNumber()));
    	source.put("object", "card");
    	source.put("cvc", String.valueOf(getRandomBetween(100, 999)));
    	source.put("address_line1", address1);
    	source.put("address_city", city);
    	source.put("address_state", StateUtil.getByName(state));
    	source.put("address_zip", zip);
    	source.put("address_country", "USA");
    		
		put("source", source);
	}
	
	private Integer getRandomBetween(Integer min, Integer max) {
    	return random.nextInt(max - min + 1) + min;
    }
    
    private final List<String> cardNumbers = new ArrayList<>();
    private String getRandomCardNumber() {
    	if(cardNumbers.isEmpty()) {
    		cardNumbers.add("4242424242424242");	//Visa
    		cardNumbers.add("4012888888881881");	//Visa
    		cardNumbers.add("4000056655665556");	//Visa (debit)
    		cardNumbers.add("5555555555554444");	//MasterCard
    		cardNumbers.add("5200828282828210");	//MasterCard (debit)
    		cardNumbers.add("5105105105105100");	//MasterCard (prepaid)
    		cardNumbers.add("378282246310005");	//American Express
    		cardNumbers.add("371449635398431");	//American Express
    		cardNumbers.add("6011111111111117");	//Discover
    		cardNumbers.add("6011000990139424");	//Discover
    		cardNumbers.add("30569309025904");	//Diners Club
    		cardNumbers.add("38520000023237");	//Diners Club
    		cardNumbers.add("3530111333300000");	//JCB
    		cardNumbers.add("3566002020360505");	//JCB
    		cardNumbers.add("4000000000000077");	//Charge will succeed and funds will be added directly to your available balance (bypassing your pending balance).
    		cardNumbers.add("4000000000000093");	//Charge will succeed and domestic pricing will be used (other test cards use international pricing). This card is only significant in countries with split pricing.
    		cardNumbers.add("4000000000000010");	//With default account settings, charge will succeed but address_line1_check and address_zip_check will both fail.
    		cardNumbers.add("4000000000000028");	//With default account settings, charge will succeed but address_line1_check will fail.
    		cardNumbers.add("4000000000000036");	//With default account settings, charge will succeed but address_zip_check will fail.
    		cardNumbers.add("4000000000000044");	//With default account settings, charge will succeed but address_zip_check and address_line1_check will both be unavailable.
    		cardNumbers.add("4000000000000101");	//With default account settings, charge will succeed unless a CVC is entered, in which case cvc_check will fail and the charge will be declined.
    		cardNumbers.add("4000000000000341");	//Attaching this card to a Customer object will succeed, but attempts to charge the customer will fail.
    		cardNumbers.add("4000000000000002");	//Charge will be declined with a card_declined code.
    		cardNumbers.add("4100000000000019");	//Charge will be declined with a card_declined code and a fraudulent reason.
    		cardNumbers.add("4000000000000127");	//Charge will be declined with an incorrect_cvc code.
    		cardNumbers.add("4000000000000069");	//Charge will be declined with an expired_card code.
    		cardNumbers.add("4000000000000119");	//Charge will be declined with a processing_error code.
    	}
    	
    	return cardNumbers.get(getRandomBetween(0,  cardNumbers.size() - 1));
    }
}
