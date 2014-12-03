package com.taobao.hsf.test.service;

import java.util.Map;
import java.util.HashMap;

/**
 * 服务实现类，根据name查相关信息。
 * 
 * @author bogao <bogao.zx@alibaba-inc.com>
 * @version 1.0.0 <2014-11-17 14:17>
 * @since JDK 1.6  
 */
public class PseudoHTaoServiceImpl implements PseudoHTaoService {
	
	Map<String, String> infoMap = new HashMap<String, String>();
	
	public PseudoHTaoServiceImpl() {
		infoMap.put("zx", "123");
		infoMap.put("mry", "345");
	}

	@Override
	public String queryInfo(String name) {
		// TODO Auto-generated method stub
		if(infoMap.containsKey(name)) {
			return infoMap.get(name);
		}
		return "null";
	}

}
