package com.ijpay;

import com.jpay.unionpay.CertUtil;
import com.jpay.unionpay.SDKConfig;
import com.jpay.unionpay.UnionPayApi;

public class Test {
	public static void main(String[] args) {
		SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
		String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
		System.out.println(requestAppUrl);
		System.out.println(CertUtil.getSignCertId());
		
		System.out.println(UnionPayApi.getAllAreas());
		System.out.println(UnionPayApi.getAllCategories());
	}
}
