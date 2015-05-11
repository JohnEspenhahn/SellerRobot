package com.hahn.sellerrobot.model;

import java.util.Map;

import com.hahn.sellerrobot.model.ParamFactory.Param;
import com.hahn.sellerrobot.model.Procedure.Action;
import com.hahn.sellerrobot.util.MapUtils;
import com.hahn.sellerrobot.util.exceptions.MissingArgumentException;

public class ActionVerifier {
	
	public static void verify(Action a) throws MissingArgumentException, IllegalArgumentException {
		String title = a.getAction().toString();
		Map<String, Object> defparams = a.getParams();
		
		switch (a.getAction()) {
		case read:
			verifyParams(title, defparams, ParamFactory.get(String.class, "type"), ParamFactory.get(String.class, "filename"));
			break;
		case determinant:
			verifyParams(title, defparams, ParamFactory.get(String.class, "type"), ParamFactory.get(String.class, "filename"));
			break;
		case click:
			verifyParams(title, defparams, ParamFactory.get(Integer.class, "x"), ParamFactory.get(Integer.class, "y"));
			break;
		case type:
			verifyParams(title, defparams, ParamFactory.get(Object.class, "text"));
			break;
		case focus:
			verifyParams(title, defparams, ParamFactory.get(String.class, "name"));
			break;
		case sleep:
			verifyParams(title, defparams, ParamFactory.get(Integer.class, "ms"));
			break;
		case wheel:
			verifyParams(title, defparams, ParamFactory.get(Integer.class, "amount"));
			break;
		}
	}
	
	public static void verifyParams(String title, Map<?,?> defparams, Param... params) throws MissingArgumentException {
		// Printable version of the map
		String mapstr = title + ":" + MapUtils.map2str(defparams);
		
		// Verify parameters
		for (Param param: params) {
			if (!defparams.containsKey(param.getName())) {
				throw new MissingArgumentException(String.format("The action %s is missing the parameter [%s]", mapstr, param));
			} else if (!param.isInstance(defparams.get(param.getName()))) {
				throw new IllegalArgumentException(String.format("The parameter %s in action %s should be [%s], but got [%s]", param.getName(), mapstr, param.getClazz(), defparams.get(param.getName()).getClass()));
			}
		}
	}
}