package com.funday.test;

import java.io.IOException;

import com.funday.util.CouponUtil;

import net.sf.json.JSONObject;

public class UploadImg {

	private static final String UPLOAD_IMG_URL = "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN";
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/*
		String filePath = "../../workspace/Funday/WebContent/files/logo.jpg";
		String token = WeixinUtil.getExitAccessToken().getToken();
		String result = WeixinUtil.uploadImg(filePath, token);
		System.out.println(result);
		*/
		
		CouponUtil.getCoupon();
		
		
	}
	
	
}
