package com.funday.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;

import com.funday.data.SqlConn;
import com.funday.match.MatchData;
import com.funday.semantic.Semantic;
import com.funday.user.MatchUser;
import com.funday.user.User;
import com.funday.util.CheckUtil;
import com.funday.util.CouponUtil;
import com.funday.util.MessageUtil;
import com.funday.util.SemanticUtil;
import com.funday.util.WeixinUtil;

import net.sf.json.JSONObject;



public class WeixinServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException{
		String signature = req.getParameter("signature");
		String timestamp = req.getParameter("timestamp");
		String nonce = req.getParameter("nonce");
		String echostr = req.getParameter("echostr");
		
		PrintWriter out = resp.getWriter();
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			out.print(echostr);				
		}
		
		
	}
	protected void doPost(HttpServletRequest req,HttpServletResponse resp)
		throws ServletException,IOException{
		
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try{							
			Map<String,String> map = MessageUtil.xmlToMap(req);
			String toUserName = map.get("ToUserName");
			String fromUserName = map.get("FromUserName");
			String creatTime = map.get("CreateTime ");
			String msgType = map.get("MsgType");
			String content = map.get("Content");
			String msgId = map.get("MsgId ");
			
			String message = null;
			
			MatchUser matchUser = new MatchUser();
			MatchData matchData = new MatchData();
			matchUser = matchData.selectMatchUser(fromUserName);
			if(!matchUser.isInMatch()){
			
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){				
				//message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.sorryText());				
				if("优惠券".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, CouponUtil.getCoupon());
				}else{									
					Semantic smt = SemanticUtil.makeSemantic(content, fromUserName);
					String smtStr = JSONObject.fromObject(smt).toString();								
					System.out.println(smtStr);
					try {
						String answer = SemanticUtil.postSemtic(WeixinUtil.getExitAccessToken().getToken(), smtStr,smt.getCategory());
						
						message = MessageUtil.initText(toUserName, fromUserName, answer);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}else if(MessageUtil.MESSAGE_EVENT.equals(msgType)){
				String eventType = map.get("Event");				
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){					
					String nickName = WeixinUtil.getUser(fromUserName).getNickname();
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.subscribeText(nickName));						 
					User user = new User();
					user = WeixinUtil.getUser(fromUserName);											
					SqlConn sc = new SqlConn();
					sc.insertUser(user);
					
				}else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					String key = map.get("EventKey");
					if(key.equals("21_qiandao")){
						//message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
						User user = new User();
						SqlConn sc = new SqlConn();
						
						user = sc.selectUser(fromUserName);
						//更新用户头像及昵称，用户可能随时修改
						user.setHeadimgurl(WeixinUtil.getUser(fromUserName).getHeadimgurl());
						user.setNickname(WeixinUtil.getUser(fromUserName).getNickname());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						if(user.isTodaySign()){
							//今天已签到
							message = MessageUtil.initText(toUserName, fromUserName, "您今日已签到！");	
							System.out.println(fromUserName);
						}else {
							if(WeixinUtil.isYtdaySign(user.getLastSignTime(),sdf.format(new Date()))){
								//连续签到
								user.setSignCount(user.getSignCount()+1);								
								//message = MessageUtil.initText(toUserName, fromUserName, "连续签到"+user.getSignCount()+"天");								
							}else{
								//没有连续签到
								user.setSignCount(1);							
								//message = MessageUtil.initText(toUserName, fromUserName, "您昨天没有签到，今日连续签到1天！");
							}
							user.setTodaySign(true);
							user.setSignAllCount(user.getSignAllCount()+1);
							user.setLastSignTime(sdf.format(new Date()));
							user.setPoints(user.getPoints() + WeixinUtil.getPoints(user.getSignCount()));
							//System.out.println("本次points=" + WeixinUtil.getPoints(user.getSignCount()));
							sc.updateUser(user);
							message = MessageUtil.signNewsMessage(toUserName, fromUserName);
						}							
						
						
					}else if(key.equals("11_getone")){
						//message = MessageUtil.initText(toUserName, fromUserName, "扫码成功");						
						message = MessageUtil.getOneNews(toUserName, fromUserName);
						//System.out.println(message);
					}else if(key.equals("31_matchMessage")){
						String mess = "开启你的随机配对聊天之旅啦！";
						message = MessageUtil.initText(toUserName, fromUserName, mess);
						
						matchUser.setInMatch(true);
						matchUser.setOpenid(fromUserName);						
						matchData.insertMatchUser(matchUser);						
						matchData.openMatch(fromUserName);
						
						System.out.println("用户状态：" + matchData.selectMatchUser(fromUserName).isInMatch());
						
					}
				}else if(MessageUtil.MESSAGE_UNSUBSCRIBE.equals(eventType)){
					SqlConn sc = new SqlConn();
					sc.deleteUser(fromUserName);
				}else if(MessageUtil.MESSAGE_VIEW.equals(eventType)){
					String url = map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName, url);
				}else if(MessageUtil.MESSAGE_SCAN.equals(eventType)){
					String key= map.get("EventKey");
					message = MessageUtil.initText(toUserName, fromUserName,key);
				}				
			}else if(MessageUtil.MESSAGE_LOCATION.equals(msgType)){
				String Label = map.get("Label");
				message = MessageUtil.initText(toUserName, fromUserName,Label);
			}
			
			//System.out.println(message);
			out.print(message);
			}
			else{
				System.out.println("现在已经进入Match流程！");
				System.out.println(content);
				//MatchData matchData = new MatchData();
				//MatchUser matchUser = new MatchUser();
				String result = null;
				//进入配对流程，则其他操作均不接受，只能发送文本消息
				if(!MessageUtil.MESSAGE_TEXT.equals(msgType)){
					result = "请发送您的配对消息！";
					message = MessageUtil.initText(toUserName, fromUserName,result);
				}else if("退出".equals(content)){
					matchData.closeMatch(fromUserName);
					matchData.deleteMatchUser(fromUserName);						
					result = "您已经退出随机配对聊天了！";
					message = MessageUtil.initText(toUserName, fromUserName,result);
					
				}else if("清空".equals(content)){
					matchData.setMatchFalse();
					result = "清空所有配对状态！";
					message = MessageUtil.initText(toUserName, fromUserName,result);
				}else if("我的消息".equals(content)){
					MatchUser matchUser22 = matchData.selectMatchUser(fromUserName);
					result = "我发送的消息是：" + matchUser22.getMessage();
					message = MessageUtil.initText(toUserName, fromUserName,result);
				}
				else{
					if(content.indexOf("替换") >= 0){
						matchData.updateMessage(fromUserName, content.substring(3));
						System.out.println("替换的message为：" + content.substring(3));																		
					}else{
						//matchUser.setOpenid(fromUserName);
						//matchUser.setMessage(content);
						//matchUser.setInMatch(true);
						matchData.updateMessage(fromUserName, content);											
					}
						String toOpenid = matchData.matchUser(fromUserName);
						System.out.println("配对的用户id:" + toOpenid);
						matchData.updateMatchUser(fromUserName, toOpenid);
						SqlConn sc = new SqlConn();
						User user2 = new User();
						user2 = sc.selectUser(toOpenid);
						String nickname2 = user2.getNickname();
						String city2 = user2.getCity();
						int sex2 = user2.getSex();
						String sex22 = null;
						if(sex2==1) {
							sex22 = "男士";
						}else {
							sex22 = "女士";
						}
						MatchUser muser2 = new MatchUser();
						muser2 = matchData.selectMatchUser(toOpenid);
						result = "Ta是一位"+sex22+",昵称是"+nickname2+",现在在"+city2+".\n"+"Ta说："+muser2.getMessage();
						message = MessageUtil.initText(toUserName, fromUserName,result);											
				}					
				out.print(message);	
												
			}
		}catch(DocumentException e){
			e.printStackTrace();
		}finally{
			out.close();
		}
		
}
}
