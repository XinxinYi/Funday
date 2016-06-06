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
			String mediaId = map.get("MediaId");
			String msgId = map.get("MsgId ");
			String eventType = map.get("Event");
			String message = null;
			
			MatchUser matchUser = new MatchUser();
			MatchData matchData = new MatchData();
			matchUser = matchData.selectMatchUser(fromUserName);
			String toOpenid = null;
			SqlConn sc = new SqlConn();	
			//判断该用户是否处于聊天中
			if(!matchUser.isInMatch()){	
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){				
				//message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.sorryText());				
				if("优惠券".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, CouponUtil.getCoupon());
				}else if("测试客服接口".equals(content)){
					for(int i=0;i<500002;i++){
					String mess1 = MessageUtil.getCusText(fromUserName, "测试"+i+"次");
					WeixinUtil.customSend(fromUserName, mess1);
					System.out.println("测试了："+i+"次");
					try{
					    Thread thread = Thread.currentThread();
					    thread.sleep(150);//暂停1.5秒后程序继续执行
					}catch (InterruptedException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					}
					}
				}
				
				else if("不再聊天".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, "系统消息\n不再聊天了，想回来可点击‘开启聊天’");
					toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
					matchData.cleanMatch(fromUserName, toOpenid);
					matchData.deleteMatchUser(fromUserName);
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
				if(MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)){					
					String nickName = WeixinUtil.getUser(fromUserName).getNickname();
					message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.subscribeText(nickName));						 
					User user = new User();
					user = WeixinUtil.getUser(fromUserName);											
					sc.insertUser(user);
					//matchData.insertMatchUser(fromUserName);
					
				}else if(MessageUtil.MESSAGE_UNSUBSCRIBE.equals(eventType)){
					//清除用户表中的所有信息
					sc.deleteUser(fromUserName);
					
					//清除配对表中的配对信息
					toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
					matchData.cleanMatch(fromUserName, toOpenid);
					matchData.deleteMatchUser(fromUserName);
				}else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					String key = map.get("EventKey");
					if(key.equals("21_qiandao")){
						User user = new User();						
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
						
					}
					//获取搞笑集锦
					else if(key.equals("11_getone")){
						//message = MessageUtil.initText(toUserName, fromUserName, "扫码成功");						
						message = MessageUtil.getOneNews(toUserName, fromUserName);
						//System.out.println(message);
					}else if(key.equals("31_matchOpen")){						
						matchUser.setInMatch(true);
						matchUser.setOpenid(fromUserName);						
						//在配对表中插入用户，以防去掉聊天功能后，再加入的情况
						matchData.insertMatchUser(fromUserName);						
						
						toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
						System.out.println(toOpenid.length());
						//进行配对,数据库中应将配对双方的数据均修改
						if(toOpenid.length() == 0){
							toOpenid = matchData.matchUser(fromUserName);
							if(toOpenid.length() == 0){
								toOpenid = matchData.matchUser(fromUserName);
							}
							if(toOpenid.length() == 0){
								String mess = "对接失败！要不再来一次吧！";
								message = MessageUtil.initText(toUserName, fromUserName, mess);
							}
							
							String mess1 = MessageUtil.getCusText(toOpenid, "系统消息\n聊天开始，现在开始您发送的任何文字消息将直接推送给Ta");
							WeixinUtil.customSend(toOpenid, mess1);
							String mess = "系统消息\n聊天开始，现在开始您发送的任何文字消息将直接推送给对方！";
							message = MessageUtil.initText(toUserName, fromUserName, mess);
						}else{
							String mess1 = MessageUtil.getCusText(fromUserName, "系统消息\n聊天开始，现在开始您发送的任何文字消息将直接推送给Ta");
							WeixinUtil.customSend(fromUserName, mess1);
							String mess = "系统消息\n聊天开始，现在开始您发送的任何文字消息将直接推送给Ta";
							message = MessageUtil.initText(toUserName, fromUserName, mess);
						}
						matchUser.setToOpenid(toOpenid);
						//更新配对双方的数据
						matchData.updateMatchUser(fromUserName, toOpenid);
						matchData.openMatch(fromUserName,toOpenid);
						/*
						//给配对双方分别发送配对信息
						User user1 = sc.selectUser(fromUserName);
						User user2 = sc.selectUser(toOpenid);
			
						String mess1 = MessageUtil.getCusContent(fromUserName, "系统消息\n"+user2.getNickname()+"正在和你聊天，Ta来自"+user2.getCity());
						WeixinUtil.customSend(fromUserName, mess1);
						String mess2 = MessageUtil.getCusContent(toOpenid, "系统消息\n"+user1.getNickname()+"正在和你聊天，Ta来自"+user1.getCity());
						WeixinUtil.customSend(toOpenid, mess2);											
						*/												
						
					}else{
						message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.helpText());
					}
					
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
			//除了以上情况，用户的其他动作则不回复任何。
			else{
				message = "";
			}
			out.print(message);
			}
			else{
				System.out.println("现在已经进入对话流程！");
				System.out.println(content);
				//MatchData matchData = new MatchData();
				//MatchUser matchUser = new MatchUser();
				String result = "";
				//配对对方的openid
				toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
				if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					String key = map.get("EventKey");
					if(key.equals("31_matchClose")){
						matchData.closeMatch(fromUserName,toOpenid);
						//matchData.deleteMatchUser(fromUserName);						
						result = "系统消息\n您已经退出随机配对聊天了！";
						message = MessageUtil.initText(toUserName, fromUserName,result);
						String toMess = MessageUtil.getCusText(toOpenid, "系统消息\n对方已退出随机配对聊天了！");
						WeixinUtil.customSend(toOpenid, toMess);
					}else if(key.equals("31_matchChange")){
						matchData.cleanMatch(fromUserName, toOpenid);
						result = "系统消息\n点击‘开启聊天’重新开始一段旅程吧！";
						message = MessageUtil.initText(toUserName, fromUserName,result);
						String toMess = MessageUtil.getCusText(toOpenid, "系统消息\n对方已退出随机配对聊天了！");
						WeixinUtil.customSend(toOpenid, toMess);
					}else{
						result = "系统消息\n开始聊天了就专心的嘛！";
						message = MessageUtil.initText(toUserName, fromUserName,result);
					}				
				}
				//聊天过程中出现取消关注的情况
				else if(MessageUtil.MESSAGE_EVENT.equals(msgType)){
					if(MessageUtil.MESSAGE_UNSUBSCRIBE.equals(eventType)){
						//清除用户表中的所有信息
						sc.deleteUser(fromUserName);						
						//清除配对表中的配对信息
						matchData.cleanMatch(fromUserName, toOpenid);
						matchData.deleteMatchUser(fromUserName);
						//通知对方用户，已退出聊天
						String toMess = MessageUtil.getCusText(toOpenid, "系统消息\n对方已退出随机配对聊天了！");
						WeixinUtil.customSend(toOpenid, toMess);
					}
				}
				//发送图片消息给对方
				else if(MessageUtil.MESSAGE_IMAGE.equals(msgType)){
					
					String postContent = MessageUtil.getCusImage(toOpenid, mediaId);
					WeixinUtil.customSend(toOpenid, postContent);
					message ="";
				}
				//发送语音消息给对方
				else if(MessageUtil.MESSAGE_VOICE.equals(msgType)){
					String postContent = MessageUtil.getCusVoice(toOpenid, mediaId);
					WeixinUtil.customSend(toOpenid, postContent);
					message ="";
				}				
				//发送文本消息给对方
				else if(MessageUtil.MESSAGE_TEXT.equals(msgType)){					
						matchData.updateMessage(fromUserName, content);											
						matchUser = matchData.selectMatchUser(fromUserName);						
						//message = MessageUtil.initText(toUserName, fromUserName, content);
						MatchUser toUser = matchData.selectMatchUser(matchUser.getToOpenid());
						//message = MessageUtil.initText(toUserName, matchUser.getToOpenid(),content);							 						
						String toMess = MessageUtil.getCusText(toOpenid, content);
						WeixinUtil.customSend(matchUser.getToOpenid(), toMess);
						message = "";
																	
				}else{
					result = "系统消息\n暂不支持文字以外的消息形式！";
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
