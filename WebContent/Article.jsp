<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.funday.data.GetItems"
		import="com.funday.spam.SourceArticle"
		import="com.funday.data.ModifyHtml"
		import="java.util.ArrayList"
		import="java.util.List"
		import="com.funday.data.SaveImg"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta id="viewport" name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=2.0;" />
<%
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
		String articleId = request.getParameter("articleId");//用request得到
		//String articleId = "2022581";
		SourceArticle sa = GetItems.getSourceArticle(articleId);			
%>

<title><%out.println(sa.getHeader()); %></title>
<style>
body{background:#fffff0; max-width:400px;margin:0 auto;font-family:"微软雅黑";font-size:20px; margin:0 12px}
img{max-width:370px; max-height:320px; margin-bottom:8px;text-align:center}
.header{margin:0 auto;}
.title{font-size:26px;}
.time{font-size:16px;color:#666;}
.source{font-size:16px;color:#666;}

.imgDiv{max-width:370px; max-height:320px; margin-bottom:8px;text-align:center;}
.img{max-width:370px; max-height:320px;}

.content{line-height:38px;text-indent: 1em;font-size:18px;}

.code{text-align:center}
.saoma{color:#007799;font-size:20px;}
.imgCode{width:150px;height:150px;}

</style>

</head>
<body>
	
	<div class="header">
	<p class="title"><%out.println(sa.getHeader());%></p>
	<p class="time"><%out.println(sa.getDate().substring(0, 10)); %></p>
	 <p class="source"><%out.println(sa.getSourc()); %><%out.println(sa.getEditor());%></p>
	</div>
	<div class="imgDiv">
		<img class="img" alt="" src="<%=sa.getPicture_src()%>">
	</div>

	<div class="content">
	<%
		String content = ModifyHtml.getString(sa.getContent(), "style");
		out.println(content);
		System.out.println(content);
		//保存图片貌似没有意义		
		//List pics = ModifyHtml.getImgStr(content);
		//pics = ModifyHtml.getImgStr(content);
		//System.out.println(pics.toString());
		/*
		for(int i=0;i<pics.size();i++){
			String fileName = i+".jpg";
			if(SaveImg.isFileExist(fileName)){
				
			}else{
				String saveUrl = SaveImg.saveImg(pics.get(i).toString(),fileName);			
				//System.out.println(saveUrl);
			}
			String imgUrl = "http://tongyuan.tunnel.qydev.com/Funday/files/article/";
		}*/
		//System.out.println(content);
	%>
	<!--  <img alt="" src="http://tongyuan.tunnel.qydev.com/Funday/files/22.gif">-->
	<img alt=" " src="http://tongyuan.tunnel.qydev.com/Funday/files/0.gif">
		</div>
	
	<div class="code">
		<p class="saoma">扫描二维码，关注我们！</p>
		<img class="imgCode" alt="" src="files/code.jpg">
	</div>
	
</body>
</html>