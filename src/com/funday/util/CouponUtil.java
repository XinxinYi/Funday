package com.funday.util;

import java.io.IOException;

import com.funday.coupon.Base_info;
import com.funday.coupon.Card;
import com.funday.coupon.Cards;
import com.funday.coupon.Date_info;
import com.funday.coupon.Groupon;
import com.funday.coupon.Sku;

import net.sf.json.JSONObject;

public class CouponUtil extends WeixinUtil{
	private static final String UPLOAD_COUPON = "https://api.weixin.qq.com/card/create?access_token=ACCESS_TOKEN";
	private static final String GET_CODE = "https://api.weixin.qq.com/card/qrcode/create?access_token=ACCESS_TOKEN";
	private static final String SET_WHITE_URL = "https://api.weixin.qq.com/card/testwhitelist/set?access_token=ACCESS_TOKEN";
	
	public static String getCoupon() throws IOException{				
		String filePath = "../../workspace/Funday/WebContent/files/logo.jpg";
		String token = WeixinUtil.getExitAccessToken().getToken();
		
		String setReturn = setWhiteList(token);
		System.out.println("********setReturn********");
		System.out.println(setReturn);
		
		System.out.print(token);
		String logo_url = WeixinUtil.uploadImg(filePath, token);
		System.out.println("********logo_url********");
		System.out.println(logo_url);
		
		String cardJson = makeCards(logo_url);
		System.out.println("********cardJson********");
		System.out.println(cardJson);
		
		JSONObject cardReturn = WeixinUtil.doPostStr(UPLOAD_COUPON.replace("ACCESS_TOKEN", token), cardJson);
		System.out.println("********cardReturn********");
		System.out.println(cardReturn);
		
		String card_id = cardReturn.getString("card_id");
		String urlReturn = makeCode(card_id,token);
		System.out.println("********urlReturn********");
		System.out.println(urlReturn);
		
		//String code_id = WeixinUtil.upload(urlReturn, token, "image");
		//System.out.println("code_id");
		
		return urlReturn;
		
	}
	
	//上传图片
	
	//创建卡券
	public static String makeCards(String logo_url){
		Cards cards = new Cards();
		Card card = new Card();
		card.setCard_type("GROUPON");
		
		Groupon groupon = new Groupon();
		String deal_detail = "以下锅底2选1（有菌王锅、麻辣锅、大骨锅、番茄锅、清补凉锅、酸菜鱼锅可选）：\n大锅1份 12元\n小锅2份 16元";
		groupon.setDeal_detail(deal_detail);
		
		Base_info base_info = new Base_info();
		base_info.setLogo_url(logo_url);
		base_info.setBrand_name("搞笑客餐厅");
		base_info.setCode_type("CODE_TYPE_TEXT");
		base_info.setTitle("168元双人火锅套餐");
		base_info.setSub_title("五一假期狂欢必备");
		base_info.setColor("Color100");
		base_info.setNotice("使用时请向服务员出示此券");
		base_info.setService_phone("010-68799999");
		base_info.setDescription("不可与其他优惠同享\n如需团购券发票，请在消费时向商户提出\n店内均可使用，仅限堂食");
		
		Date_info date_info = new Date_info();
		date_info.setType("DATE_TYPE_FIX_TERM");
		date_info.setFixed_term(15);
		date_info.setFixed_begin_term(0);
		base_info.setDate_info(date_info);
		
		Sku sku = new Sku();
		sku.setQuantity(50000);
		base_info.setSku(sku);
		
		base_info.setGet_limit(3);
		base_info.setUse_custom_code(false);
		base_info.setCan_share(false);
		base_info.setCan_give_friend(true);
		base_info.setLocation_id_list("[123, 12321, 345345]");
		base_info.setCustom_url_name("立即使用");
		base_info.setCustom_url("http://www.qq.com");
		base_info.setCustom_url_sub_title("6个汉字tips");
		base_info.setPromotion_url_name("更多优惠");
		base_info.setPromotion_url("http://www.baidu.com");
		
		groupon.setBase_info(base_info);
		
		card.setGroupon(groupon);
		cards.setCard(card);
		String result = JSONObject.fromObject(cards).toString();		
		return result;
	}
	
	//创建二维码投放json
	public static String makeCode(String card_id,String token){
		String result = null;
		String jsonStr = "{\"action_name\": \"QR_CARD\", \"action_info\": {\"card\": {\"card_id\": \"CARD_ID\"}}}";
		System.out.println("********card_id-json********");
		System.out.println(jsonStr.replace("CARD_ID", card_id));
		JSONObject jsonObject = WeixinUtil.doPostStr(GET_CODE.replace("ACCESS_TOKEN", token), jsonStr.replace("CARD_ID", card_id));
		result = jsonObject.getString("show_qrcode_url");
		return result;				
	}
	
	//显示二维码
	
	//设置白名单
	public static String setWhiteList(String token){
		String jsonStr = "{\"username\": [\"gh_33d9c18a084d\"]}";
		JSONObject jsonObject = WeixinUtil.doPostStr(SET_WHITE_URL.replace("ACCESS_TOKEN", token), jsonStr);
		String result = jsonObject.toString();
		return result;
		
	}
	
}
