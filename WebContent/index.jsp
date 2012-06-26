<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>万度搜索-Wonder,生活有你更精彩</title>
	<link rel="stylesheet" href="css/index.css" type="text/css" />
	<META name=GENERATOR content="MSHTML 8.00.6001.18904">
  </head>
 <body bgcolor="#ffffff">
 <% request.setCharacterEncoding("UTF-8");  %>
  <% response.setCharacterEncoding("UTF-8");  %>
<center></center>
<br>
<br>
<br>
 <font face="华文中宋" color="#3399FF" size=18>万</font><font face="华文中宋" color="#c60a00" size=2>Wonder</font><font face="华文中宋" color="#3399FF" size=18>度</font>
 <br>
 <br>
 <!-- 
  <form action="./PostInfoIndex" method="post" >
   <input type="submit" name="submit" value="创建索引">
   </form>
    -->
	<form action="./PostInfoSearch" method="post" >
    请输入关键字:  <input type="text" name="searchKey" size="40">
   <input type="submit" name="submit" value="开始搜索">
  </form>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <div><P id=cp>&copy;2010 Wonder <A href="">使用万度前必读</A> 
  <A href="" target=_blank>粤DL证6-312</A>
  </P></DIV>
</body>
</html>
