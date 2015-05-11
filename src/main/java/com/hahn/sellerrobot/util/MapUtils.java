package com.hahn.sellerrobot.util;

import java.util.Map;

public class MapUtils {

	public static String map2str(Map<?,?> map) {
		// Build parameters to a string
		String mapstr = "";
		mapstr += "{";
		
		boolean first = true;
		for (Object key : map.keySet()) {
			if (first) first = false;
			else mapstr += ", ";
			
			mapstr += String.format("%s: %s", key.toString(), map.get(key).toString());
		}
		mapstr += "}";

		return mapstr;
	}
	
}
