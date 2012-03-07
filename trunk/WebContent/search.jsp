	<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true"%>
	<%@ page import="java.util.List"  trimDirectiveWhitespaces="true"%>
	<%@ page import="sample.dw.paper.lucene.search.*"  trimDirectiveWhitespaces="true"%>
	<!-- 
		1、公用的css、js文件作用定位不明确,造成重复引用、页面性能下降、开发质量下降、开发维护难度提高。
		2、公用全局变量使用不规范，造成变量重复定义，互相交叉引用，耦合度增高，不利维护。
		3、jsp页面引入java代码片段不规范，造成页面文档结构输出混乱,目前该问题在部分老浏览器中造成样式混乱。
		4、js文件引入的时机不合适，在网路带宽紧张的时候，容易照成js脚本阻塞，导致页面错误或者影响页面文档正常加载。
		5、为节省开发工作量,不自觉引入页面使用不到的外部资源(js脚本、css文件等),造成页面加载缓慢，增加static服务器压力,
		     表现在为了使用某个js而引入大量使用不到的资源,目前系统中大量使用到下面的方式引用全局资源.  <%=com.aceona.common.web.Page.include(request)%>
		6、不合适义的引入java类、标签库等,虽然jsp页面是首次编译多次执行的，但是由于导入过多使用不到的java类、或者标签库，最终会表现为服务器在执行jsp编译的servlet的时候去import这些过多的java类，造成服务器内存浪费,GC将会变得频繁，降低服务器响应速度。
		
		针对以上6类问题,提出以下解决方案,希望大家能共勉,将规范变为习惯,这样对于我们代码的产出质量是有极大提高的。

		首先说明下JSP代码编写的基本规范精神:  按需导入，精简代码
		有的java类导入必须在<!DOCTYPE 声明之前结束，图上蓝框中所示红框处所示的不符合规范，禁止使用*号导致整个包
		绿框处的属性配置的作用是消除由于导入java类或使用tag标签照成页面中输出空白，从而导致浏览器解析怪异。
		消除空白的属性配置:    trimDirectiveWhitespaces="true"
		
		head标签中仅用于导入CSS文件以及导入jquery基础库(jquery.js)，禁止导入其他js（防止页面阻塞）
		所有的私有js脚本引入必须紧贴于</body>标签之前。
	 -->
	<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
	<HTML><HEAD><TITLE>Lucene Search Engine Demo Client</TITLE>
	<META http-equiv=Content-Type content="text/html; charset=utf-8">
	<META content="MSHTML 6.00.2800.1543" name=GENERATOR>
	<style type="text/css">
	<!--
	body{margin:0;
	font-family: Arial, Helvetica, sans-serif;
		font-size: 0.8em;
	}
	td,th,div{
			font-size: 0.8em;
	}
	a{
		color: #0066FF;
	}
	a:hover{
		color: #666;
		text-decoration: none;
	}
	
	h1{
	margin:0px;
	height:70px;
	line-height:70px;
	background: #6699CC;
	color: #fff;
	border-bottom:solid 1px #006699;
	}
	
	.search{
	background:#ddeeff;
	padding-top:5px;
	padding-bottom:5px;
	border-bottom:solid 1px #9ABBCB;
	}
	form{margin:0;
	}
	.result{
	width:70%;
		clear:both;
	margin-bottom:20px;
	margin-top:20px;}
	.result h3{
			margin:0px;
			line-height: 25px;
			font-size: 1.3em;
	}
	.linked {
		padding-top: 5px;
		padding-bottom: 5px;
	}
	.linked a{
	
	margin-right: 10px;
	border:solid 1px #CCCCCC;
	padding:3px 10px 3px 10px;
	text-decoration: none;
	}
	.linked a:hover{
	
	margin-right: 10px;
	border:solid 1px #0066FF;
		background: #0066FF;
		color: #fff;
	
	}
	hr{
	
		border: dashed 1px #ddd;
			display: block;
			background:#fff;
			height: 1px;
	}
	.footer{
		font-size:0.8em;
		border-top:solid 1px #ddd;
		padding-top:10px;
	}
	.footer a{color:#666;
	text-decoration: none;}
	.footer a:hover{
		text-decoration: underline;
	}
	-->
	</style>
	</HEAD>
	<BODY>
	
	<CENTER><h1>Lucene Search Engine Demo Client</h1>
	<div class="search">
	  <FORM id=searchForm action=SearchController>
	  <TABLE>
	    <TBODY>
	      <TR>
	        <TD colspan="3">
	          <INPUT name=searchWord id=searchWord type=text size="40"> 
	          <INPUT id=doSearch type=submit value=search> 
	        </TD>
	      </TR>
	    </TBODY>
	  </TABLE>
	  </FORM>
	</div>
 <TABLE class="result">
	  <TBODY>
	  <%
	    List searchResult = (List)request.getAttribute("searchResult");
	    int resultCount = 0;
	    if(null != searchResult){
	    	resultCount = searchResult.size();
	    }%>
	    <TR><TD class="title"><h4>共匹配出<%=resultCount %>条数据</h4></TD></TR>
	    <% 
	    for(int i = 0; i < resultCount; i++){
	    	SearchResultBean resultBean = (SearchResultBean)searchResult.get(i);
	    	String title = resultBean.getHtmlTitle();
	    	String path  = resultBean.getHtmlPath();
	    	path = path.substring(9);
	  %>
	  <TR>
	    <TD class="title"><h3><A href="<%=path %>"><%=title %></A></h3></TD>
	  </TR>
	  <tr><td><hr /></td></tr>
	  <%
	    }
	  %>
	
	</TBODY>
 </TABLE>
	
	</CENTER>
	</BODY></HTML>