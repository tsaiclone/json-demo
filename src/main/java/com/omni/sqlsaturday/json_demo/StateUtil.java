package com.omni.sqlsaturday.json_demo;

import java.util.HashMap;
import java.util.Map;

public class StateUtil {
	private static final Map<String, String> stateMap = new HashMap<String, String>();
	
	public static String getByName(String stateName) {
		if(stateMap.isEmpty()) {
			stateMap.put("alabama", "AL");
			stateMap.put("alaska", "AK");
			stateMap.put("arizona", "AZ");
			stateMap.put("arkansas", "AR");
			stateMap.put("california", "CA");
			stateMap.put("colorado", "CO");
			stateMap.put("connecticut", "CT");
			stateMap.put("delaware", "DE");
			stateMap.put("florida", "FL");
			stateMap.put("georgia", "GA");
			stateMap.put("hawaii", "HI");
			stateMap.put("idaho", "ID");
			stateMap.put("illinois", "IL");
			stateMap.put("indiana", "IN");
			stateMap.put("iowa", "IA");
			stateMap.put("kansas", "KS");
			stateMap.put("kentucky", "KY");
			stateMap.put("louisiana", "LA");
			stateMap.put("maine", "ME");
			stateMap.put("maryland", "MD");
			stateMap.put("massachusetts", "MA");
			stateMap.put("michigan", "MI");
			stateMap.put("minnesota", "MN");
			stateMap.put("mississippi", "MS");
			stateMap.put("missouri", "MO");
			stateMap.put("montana", "MT");
			stateMap.put(",ebraska", "NE");
			stateMap.put("nevada", "NV");
			stateMap.put("new hampshire", "NH");
			stateMap.put("new jersey", "NJ");
			stateMap.put("new mexico", "NM");
			stateMap.put("new york", "NY");
			stateMap.put("north carolina", "NC");
			stateMap.put("north dakota", "ND");
			stateMap.put("ohio", "OH");
			stateMap.put("oklahoma", "OK");
			stateMap.put("oregon", "OR");
			stateMap.put("pennsylvania", "PA");
			stateMap.put("rhode Island", "RI");
			stateMap.put("south carolina", "SC");
			stateMap.put("south dakota", "SD");
			stateMap.put("tennessee", "TN");
			stateMap.put("texas", "TX");
			stateMap.put("utah", "UT");
			stateMap.put("vermont", "VT");
			stateMap.put("virginia", "VA");
			stateMap.put("washington", "WA");
			stateMap.put("west virginia", "WV");
			stateMap.put("wisconsin", "WI");
			stateMap.put("wyoming", "WY");
		}
		return stateMap.get(stateName).toLowerCase();
	}
}
