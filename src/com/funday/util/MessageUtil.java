package com.funday.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.funday.data.GetItems;
import com.funday.po.Article;
import com.funday.po.Image;
import com.funday.po.ImageMessage;
import com.funday.po.Music;
import com.funday.po.MusicMessage;
import com.funday.po.NewsMessage;
import com.funday.po.TextMessage;
import com.funday.spam.ArticleText;
import com.funday.spam.ArticleTextArr;
import com.funday.spam.Filter;
import com.funday.spam.Items;
import com.funday.spam.Mpnews;
import com.funday.spam.NewsSpam;
import com.funday.spam.Text;
import com.funday.spam.TextSpam;
import com.thoughtworks.xstream.XStream;

public class MessageUtil {
	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_IMAGE = "image";
	public static final String MESSAGE_VOICE = "voice";
	public static final String MESSAGE_VIDEO = "video";
	public static final String MESSAGE_MUSIC = "music";
	public static final String MESSAGE_NEWS = "news";	
	public static final String MESSAGE_LINK = "link";
	public static final String MESSAGE_LOCATION = "location";
	public static final String MESSAGE_SHORTVIDEW = "shortvideo";
	public static final String MESSAGE_EVENT = "event";
	public static final String MESSAGE_SUBSCRIBE = "subscribe";
	public static final String MESSAGE_UNSUBSCRIBE = "unsubscribe";
	public static final String MESSAGE_CLICK = "CLICK";
	public static final String MESSAGE_VIEW = "VIEW";
	public static final String MESSAGE_SCAN = "scancode_push";
	
	private static final String SIGN_URL = "http://tongyuan.tunnel.qydev.com/Funday/SignCount.jsp";
	private static final String ARTICLE_URL = "http://tongyuan.tunnel.qydev.com/Funday/Article.jsp";
	/*
	 * xml转为map集合
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	
	public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException{
		Map<String,String> map = new HashMap<String,String>();
		SAXReader reader = new SAXReader();
		
		InputStream ins = request.getInputStream();
		Document doc = reader.read(ins);
		
		Element root = doc.getRootElement();
		
		List<Element> list = root.elements();
		
		for(Element e:list){
			map.put(e.getName(), e.getText());
		}
		ins.close();
		return map;
	}
	
	/*
	 * 将文本对象转换为xml
	 */
	public static String textMessageToXml(TextMessage textMessage){
		XStream xstream = new XStream();
		xstream.alias("xml", textMessage.getClass());
		
		return xstream.toXML(textMessage);
	}
	/*
	 * 图文消息转换为XML	
	 */
	public static String newsMessageToXml(NewsMessage newsMessage){
		XStream xstream = new XStream();
		xstream.alias("xml", newsMessage.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(newsMessage);
	}
	/* 
	 * 图片消息转换为XML	
	 */
	public static String imageMessageToXml(ImageMessage imageMessage){
		XStream xstream = new XStream();
		xstream.alias("xml", imageMessage.getClass());
		xstream.alias("image", new Image().getClass());
		return xstream.toXML(imageMessage);
	}
	/* 
	 * 音乐消息转换为XML	
	 */
	public static String musicMessageToXml(MusicMessage musicMessage){
		XStream xstream = new XStream();
		xstream.alias("xml", musicMessage.getClass());
		xstream.alias("Music", new Music().getClass());
		return xstream.toXML(musicMessage);
	}
	/*
	 * 拼接文本消息
	 */
	public static String initText(String toUserName,String fromUserName,String content){
		TextMessage text = new TextMessage();
		text.setFromUserName(toUserName);
		text.setToUserName(fromUserName);
		text.setMsgType(MessageUtil.MESSAGE_TEXT);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		 
		text.setCreateTime(sdf.format(new Date()));
		text.setContent(content);
		return textMessageToXml(text);
		
	}
	/*
	 * 关注时回复的文本消息
	 */
	public static String subscribeText(String nickName){
		StringBuffer sb = new StringBuffer();
		sb.append(nickName);
		sb.append("，恭喜你终于找到组织啦！\n");
		sb.append("这里有你想看的段子、图片、视频，让你每天笑到飞起来！");
		return sb.toString();
	}

	/*
	 * 主菜单 
	 */
	public static String sorryText(){
		StringBuffer sb = new StringBuffer();
		sb.append("抱歉，'搞笑日常'正在建设中");
		sb.append("");
		return sb.toString();
	}
	/*
	 * 点击搞笑一则，随机获取图文消息
	 */
	public static String getOneNews(String toUserName,String fromUserName) throws IOException{
		String message = null;
		List<Article> articleList = new ArrayList<Article>();
		NewsMessage newsMessage = new NewsMessage();
		Items[] items = GetItems.getItems();
		int random =(int) Math.round(Math.random()*20);
		System.out.println(random);
		for(int i = 0;i<10;i++){
			Article article = new Article();
			article.setTitle(items[random].getHeader());
			article.setDescription(items[random].getContent());
			article.setPicUrl(items[random].getThumbnail());				
			article.setUrl(ARTICLE_URL + "?articleId="+ items[random].getArticalId());
			
			articleList.add(article);
			++random;
		}
		
		newsMessage.setFromUserName(toUserName);
		newsMessage.setToUserName(fromUserName);
		newsMessage.setArticles(articleList);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		newsMessage.setCreateTime(sdf.format(new Date()));
		newsMessage.setArticleCount(articleList.size());
		newsMessage.setMsgType(MESSAGE_NEWS);
		message = MessageUtil.newsMessageToXml(newsMessage);		
		return message;
	}
	/*
	 * 签到时返回的图文消息
	 */
	public static String signNewsMessage(String toUserName,String fromUserName){
		String message = null;
		List<Article> articleList = new ArrayList<Article>();
		NewsMessage newsMessage = new NewsMessage();

		Article article = new Article();
		article.setTitle("打卡成功！");
		article.setDescription("↓↓↓戳我，就现在");
		article.setPicUrl("http://imgsrc.baidu.com/forum/w%3D580/sign=5fac7d37253fb80e0cd161df06d12ffb/bf19852397dda144f3711db4b6b7d0a20df4868e.jpg");				
		article.setUrl(SIGN_URL+"?openid="+fromUserName);
		//article.setUrl("H7t11zWvCnnGk6Cg-v-IO2JfBwEAYWGLaNUWssLNPq1YcDts5V5SGwM9-q01SIgP");
		
		articleList.add(article);		
		
		newsMessage.setFromUserName(toUserName);
		newsMessage.setToUserName(fromUserName);
		newsMessage.setArticles(articleList);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		newsMessage.setCreateTime(sdf.format(new Date()));
		newsMessage.setArticleCount(articleList.size());
		newsMessage.setMsgType(MESSAGE_NEWS);
		
		message = MessageUtil.newsMessageToXml(newsMessage);
		return message;
	}
	/*
	 * 图片消息
	 */
	public static String initImageMessage(String toUserName,String fromUserName){
		String message = null;
		Image image = new Image();
		image.setMediaId("JiF1AlIFT4TgrALeExilEKTh7kNeukc3SLwS215UrTFMufsUlXx57Sn2HmZ0XVO7");
		ImageMessage im = new ImageMessage();
		im.setFromUserName(toUserName);
		im.setToUserName(fromUserName);
		im.setCreateTime("2016-3-9");
		im.setMsgType(MessageUtil.MESSAGE_IMAGE);
		im.setImage(image);
		
		message = MessageUtil.imageMessageToXml(im);
		return message;
		
	}
	/*
	 * 音频、音乐消息
	 */
	public static String initMusicMessage(String toUserName,String fromUserName){
		String message = null;
		Music music = new Music();
		music.setTitle("test Music");
		music.setDescription("test description");
		music.setThumbMediaId("ABLgKOOwgusANliiMCrOfX55a5VabN0oTdx89MG6E4gCJJAYjrMSvA1PIUwMBmJx");
		music.setMusicUrl("http://tongyuan.tunnel.qydev.com/Weixin/resource/Sleep Away.mp3");
		music.setHQMusicUrl("http://tongyuan.tunnel.qydev.com/Weixin/resource/Sleep Away.mp3");
		
		MusicMessage mm = new MusicMessage();
		mm.setFromUserName(toUserName);
		mm.setToUserName(fromUserName);
		mm.setCreateTime("2016-3-9");
		mm.setMsgType(MessageUtil.MESSAGE_MUSIC);
		mm.setMusic(music);

		message = MessageUtil.musicMessageToXml(mm);
		return message;
		
	}
	/*
	 * 自动群发的图文消息
	 */
	public static ArticleTextArr makeNews(Items[] items,String imgId) throws IOException{	
		ArticleTextArr atArr = new ArticleTextArr();
		//String filePath = "../../111.jpg";
		String accessToken = WeixinUtil.getExitAccessToken().getToken();
		//String thumb_media_id = "http://mmbiz.qpic.cn/mmbiz/joqDZc3wChsj5vGhCguzn52oMmZvtic1Kf20ZagTXicsT4A39DhmFuXx7JvV6ELMBuxSVGic2PxJcq7UC9eCemicAA/0";
		//String imgId = "http://mmbiz.qpic.cn/mmbiz/joqDZc3wChsx5G4ySPOtibK0fL5iaCYicNYMpmLvZ1V8zlpPdJhZ70RDQwzMzm4ZGSLBRc2px3hQx6hicNOg5c7TzQ/0";
		List<ArticleText> articleList = new ArrayList<ArticleText>();
		for(int i=0;i<3;i++){
			if(i>3) break;
			ArticleText at = new ArticleText();
			at.setAuthor("小二");
			at.setContent(items[i].getContent());
			//String uploadImgId = SpamUtil.uploadImg(items[i].getThumbnail(), accessToken);
			at.setThumb_media_id(imgId);
			//at.setThumb_media_id(thumb_media_id );
			at.setTitle(items[0].getHeader());
			at.setContent_source_url(items[i].getFullTextUrl());
			at.setShow_cover_pic(1);
			articleList.add(at);
		}	
		atArr.setArticles(articleList);				
		return atArr;

	}
	/*
	 * 根据分组进行群发，拼接POST群发文本消息
	 */
	public static TextSpam makeSpamText(String str){
		TextSpam ts = new TextSpam();
		Filter filter = new Filter();
		filter.setIs_to_all(true);
		Text text = new Text();
		text.setContent(str);
		ts.setFilter(filter);
		ts.setText(text);
		ts.setMsgtype(MessageUtil.MESSAGE_TEXT);
		return ts;
	}
	/*
	 * 根据分组进行群发，拼接POST群发图文消息
	 */
	public static NewsSpam makeSpamNews(String media_id){
		if(media_id.length() > 5){
			NewsSpam ns = new NewsSpam();
			Filter filter = new Filter();
			filter.setIs_to_all(true);
			Mpnews mn = new Mpnews();
			mn.setMedia_id(media_id);
			ns.setFilter(filter);
			ns.setMpnews(mn);
			ns.setMsgtype("mpnews");
			return ns;
		}else{
			return null;
		}
		
	}
	

	
}
