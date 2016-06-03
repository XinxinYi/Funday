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
			String eventType = map.get("Event");
			String message = null;
			
			MatchUser matchUser = new MatchUser();
			MatchData matchData = new MatchData();
			matchUser = matchData.selectMatchUser(fromUserName);
			String toOpenid = null;
			SqlConn sc = new SqlConn();	
			//�жϸ��û��Ƿ���������
			if(!matchUser.isInMatch()){	
			if(MessageUtil.MESSAGE_TEXT.equals(msgType)){				
				//message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.sorryText());				
				if("�Ż�ȯ".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, CouponUtil.getCoupon());
				}else if("���Կͷ��ӿ�".equals(content)){
					for(int i=0;i<500002;i++){
					String mess1 = MessageUtil.getCusContent(fromUserName, "����"+i+"��");
					WeixinUtil.customSend(fromUserName, mess1);
					System.out.println("�����ˣ�"+i+"��");
					try{
					    Thread thread = Thread.currentThread();
					    thread.sleep(150);//��ͣ1.5���������ִ��
					}catch (InterruptedException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
					}
					}
				}
				
				else if("��������".equals(content)){
					message = MessageUtil.initText(toUserName, fromUserName, "ϵͳ��Ϣ\n���������ˣ�������ɵ�����������졯");
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
					//����û����е�������Ϣ
					sc.deleteUser(fromUserName);
					
					//�����Ա��е������Ϣ
					toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
					matchData.cleanMatch(fromUserName, toOpenid);
					matchData.deleteMatchUser(fromUserName);
				}else if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					String key = map.get("EventKey");
					if(key.equals("21_qiandao")){
						User user = new User();						
						user = sc.selectUser(fromUserName);
						//�����û�ͷ���ǳƣ��û�������ʱ�޸�
						user.setHeadimgurl(WeixinUtil.getUser(fromUserName).getHeadimgurl());
						user.setNickname(WeixinUtil.getUser(fromUserName).getNickname());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						if(user.isTodaySign()){
							//������ǩ��
							message = MessageUtil.initText(toUserName, fromUserName, "��������ǩ����");	
							System.out.println(fromUserName);
						}else {
							if(WeixinUtil.isYtdaySign(user.getLastSignTime(),sdf.format(new Date()))){
								//����ǩ��
								user.setSignCount(user.getSignCount()+1);								
								//message = MessageUtil.initText(toUserName, fromUserName, "����ǩ��"+user.getSignCount()+"��");								
							}else{
								//û������ǩ��
								user.setSignCount(1);							
								//message = MessageUtil.initText(toUserName, fromUserName, "������û��ǩ������������ǩ��1�죡");
							}
							user.setTodaySign(true);
							user.setSignAllCount(user.getSignAllCount()+1);
							user.setLastSignTime(sdf.format(new Date()));
							user.setPoints(user.getPoints() + WeixinUtil.getPoints(user.getSignCount()));
							//System.out.println("����points=" + WeixinUtil.getPoints(user.getSignCount()));
							sc.updateUser(user);
							message = MessageUtil.signNewsMessage(toUserName, fromUserName);
						}													
						
					}
					//��ȡ��Ц����
					else if(key.equals("11_getone")){
						//message = MessageUtil.initText(toUserName, fromUserName, "ɨ��ɹ�");						
						message = MessageUtil.getOneNews(toUserName, fromUserName);
						//System.out.println(message);
					}else if(key.equals("31_matchOpen")){						
						matchUser.setInMatch(true);
						matchUser.setOpenid(fromUserName);						
						//����Ա��в����û����Է�ȥ�����칦�ܺ��ټ�������
						matchData.insertMatchUser(fromUserName);						
						
						toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
						System.out.println(toOpenid.length());
						//�������,���ݿ���Ӧ�����˫�������ݾ��޸�
						if(toOpenid.length() == 0){
							toOpenid = matchData.matchUser(fromUserName);
							if(toOpenid.length() == 0){
								toOpenid = matchData.matchUser(fromUserName);
							}
							if(toOpenid.length() == 0){
								String mess = "�Խ�ʧ�ܣ�Ҫ������һ�ΰɣ�";
								message = MessageUtil.initText(toUserName, fromUserName, mess);
							}
							
							String mess1 = MessageUtil.getCusContent(toOpenid, "ϵͳ��Ϣ\n���쿪ʼ�����ڿ�ʼ�����͵��κ�������Ϣ��ֱ�����͸�Ta");
							WeixinUtil.customSend(toOpenid, mess1);
							String mess = "ϵͳ��Ϣ\n���쿪ʼ�����ڿ�ʼ�����͵��κ�������Ϣ��ֱ�����͸��Է���";
							message = MessageUtil.initText(toUserName, fromUserName, mess);
						}else{
							String mess1 = MessageUtil.getCusContent(fromUserName, "ϵͳ��Ϣ\n���쿪ʼ�����ڿ�ʼ�����͵��κ�������Ϣ��ֱ�����͸�Ta");
							WeixinUtil.customSend(fromUserName, mess1);
							String mess = "ϵͳ��Ϣ\n���쿪ʼ�����ڿ�ʼ�����͵��κ�������Ϣ��ֱ�����͸�Ta";
							message = MessageUtil.initText(toUserName, fromUserName, mess);
						}
						matchUser.setToOpenid(toOpenid);
						//�������˫��������
						matchData.updateMatchUser(fromUserName, toOpenid);
						matchData.openMatch(fromUserName,toOpenid);
						/*
						//�����˫���ֱ��������Ϣ
						User user1 = sc.selectUser(fromUserName);
						User user2 = sc.selectUser(toOpenid);
			
						String mess1 = MessageUtil.getCusContent(fromUserName, "ϵͳ��Ϣ\n"+user2.getNickname()+"���ں������죬Ta����"+user2.getCity());
						WeixinUtil.customSend(fromUserName, mess1);
						String mess2 = MessageUtil.getCusContent(toOpenid, "ϵͳ��Ϣ\n"+user1.getNickname()+"���ں������죬Ta����"+user1.getCity());
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
			//��������������û������������򲻻ظ��κΡ�
			else{
				message = "";
			}
			out.print(message);
			}
			else{
				System.out.println("�����Ѿ�����Ի����̣�");
				System.out.println(content);
				//MatchData matchData = new MatchData();
				//MatchUser matchUser = new MatchUser();
				String result = "";
				//��ԶԷ���openid
				toOpenid = matchData.selectMatchUser(fromUserName).getToOpenid();
				if(MessageUtil.MESSAGE_CLICK.equals(eventType)){
					String key = map.get("EventKey");
					if(key.equals("31_matchClose")){
						matchData.closeMatch(fromUserName,toOpenid);
						//matchData.deleteMatchUser(fromUserName);						
						result = "ϵͳ��Ϣ\n���Ѿ��˳������������ˣ�";
						message = MessageUtil.initText(toUserName, fromUserName,result);
						String toMess = MessageUtil.getCusContent(toOpenid, "ϵͳ��Ϣ\n�Է����˳������������ˣ�");
						WeixinUtil.customSend(toOpenid, toMess);
					}else if(key.equals("31_matchChange")){
						matchData.cleanMatch(fromUserName, toOpenid);
						result = "ϵͳ��Ϣ\n������������졯���¿�ʼһ���ó̰ɣ�";
						message = MessageUtil.initText(toUserName, fromUserName,result);
						String toMess = MessageUtil.getCusContent(toOpenid, "ϵͳ��Ϣ\n�Է����˳������������ˣ�");
						WeixinUtil.customSend(toOpenid, toMess);
					}else{
						result = "ϵͳ��Ϣ\n��ʼ�����˾�ר�ĵ��";
						message = MessageUtil.initText(toUserName, fromUserName,result);
					}				
				}
				//��������г���ȡ����ע�����
				else if(MessageUtil.MESSAGE_EVENT.equals(msgType)){
					if(MessageUtil.MESSAGE_UNSUBSCRIBE.equals(eventType)){
						//����û����е�������Ϣ
						sc.deleteUser(fromUserName);						
						//�����Ա��е������Ϣ
						matchData.cleanMatch(fromUserName, toOpenid);
						matchData.deleteMatchUser(fromUserName);
						//֪ͨ�Է��û������˳�����
						String toMess = MessageUtil.getCusContent(toOpenid, "ϵͳ��Ϣ\n�Է����˳������������ˣ�");
						WeixinUtil.customSend(toOpenid, toMess);
					}
				}
				//����������̣������������������ܣ�ֻ�ܷ����ı���Ϣ
				else if(!MessageUtil.MESSAGE_TEXT.equals(msgType)){
					result = "ϵͳ��Ϣ\n�ݲ�֧�������������Ϣ��ʽ��";
					message = MessageUtil.initText(toUserName, fromUserName,result);
				}else{					
						matchData.updateMessage(fromUserName, content);											
						matchUser = matchData.selectMatchUser(fromUserName);						
						//message = MessageUtil.initText(toUserName, fromUserName, content);
						MatchUser toUser = matchData.selectMatchUser(matchUser.getToOpenid());
						String response = toUser.getMessage();
						//message = MessageUtil.initText(toUserName, matchUser.getToOpenid(),content);							 						
						String toMess = MessageUtil.getCusContent(matchUser.getToOpenid(), content);
						WeixinUtil.customSend(matchUser.getToOpenid(), toMess);
						message = "";
																	
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
