<%@ page language="java" import="java.util.*,self.wonder.search.*"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<HEAD>
<title>万度搜索-Wonder,生活有你更精彩</title>
<META content=IE=7 http-equiv=X-UA-Compatible>
<META content=text/html;charset=utf-8 http-equiv=content-type>
<link rel="stylesheet" href="css/style.css" type="text/css" />
<SCRIPT>
	function h(obj, url) {
		obj.style.behavior = 'url(#default#homepage)';
		obj.setHomePage(url);
	}
</SCRIPT>
<META name=GENERATOR content="MSHTML 8.00.6001.18904">
<%
	List<PostBO> postList = (List<PostBO>) request.getAttribute("postList");
	int totalRecord = (Integer) request.getAttribute("totalRecord");
	int currBeginRecord = (Integer) request.getAttribute("currBeginRecord");
	int currEndRecord = (Integer) request.getAttribute("currEndRecord");
	String searchTimes = (String) request.getAttribute("searchTimes");
	String searchKey = (String) request.getAttribute("searchKey");
%>

</HEAD>
<BODY link=#0000cc>

	<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center height=58>
		<TBODY>
			<TR vAlign=center>
				<TD
					style="PADDING-BOTTOM: 0px; PADDING-LEFT: 8px; WIDTH: 137px; PADDING-RIGHT: 0px; PADDING-TOP: 4px"
					vAlign=top width="100%" noWrap><A href="index.jsp"><IMG
						border=0 alt=主页 src="images/logo.gif" width=137 height=46>
				</A>
				</TD>
				<TD>&nbsp;&nbsp;&nbsp;</TD>
				<TD vAlign=top width="100%">
					<TABLE cellSpacing=0 cellPadding=0>
						<TBODY>
							<TR>&nbsp;&nbsp;&nbsp;
							</TR>
						</TBODY>
						<TBODY>
							<TR>
								<TD vAlign=top noWrap>
									<FORM name=f action="./PostInfoSearch" method="post">
										<INPUT value=8 type=hidden name=f> <INPUT id=kw
											class=i value=<%=searchKey%> maxLength=100 size=46
											name=searchKey> <INPUT class=btn value=开始搜索
											type=submit>
									</FORM>
								</TD>
								<TD vAlign=center noWrap></TD>
							</TR>
						</TBODY>
					</TABLE>
				</TD>
				<TD></TD>
			</TR>
		</TBODY>
	</TABLE>

	<TABLE class=bi border=0 cellSpacing=0 cellPadding=0 width="100%"
		align=center>
		<TBODY>
			<TR>
				<TD noWrap>&nbsp;&nbsp;&nbsp;<STRONG>推荐&nbsp;:&nbsp;</STRONG><A
					style="COLOR: #000000"
					onclick="h(this,'http://localhost:8081/luceneTest/searchjsp/index.jsp')"
					href="">设为主页 </A>
				</TD>
				<TD noWrap align=right>找到相关记录<%=totalRecord%>条，当前第<%=currBeginRecord%>-<%=currEndRecord %>条记录，用时<%=searchTimes%>秒</TD>
			</TR>
		</TBODY>
	</TABLE>
	<TABLE border=0 cellSpacing=0 cellPadding=0 width="30%" align=right>
		<TBODY>
			<TR>
				<TD style="PADDING-RIGHT: 10px" align=left>
					<DIV
						style="BORDER-LEFT: #e1e1e1 1px solid; PADDING-LEFT: 10px; WORD-WRAP: break-word; WORD-BREAK: break-all">
						<DIV class=r>
							<DIV
								style="PADDING-BOTTOM: 0px; LINE-HEIGHT: 29px; PADDING-LEFT: 3px; PADDING-RIGHT: 3px; BACKGROUND: #eff2fa; HEIGHT: 30px; OVERFLOW: hidden; WORD-BREAK: normal; PADDING-TOP: 0px">
								<A style="FONT-SIZE: 16px" href="index.jsp" target=_blank>来万度推广您的产品</A>
							</DIV>
						</DIV>
						<BR>
					</DIV>
					<BR>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
	<%
		for (PostBO post : postList) {
	%>
	<TABLE id=2 border=0 cellSpacing=0 cellPadding=0>
		<TBODY>
			<TR>
				<TD class=f><FONT size=3><%=post.getID()%>&nbsp;&nbsp;&nbsp;<%=post.getName()%></FONT>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<FONT size=-1><%=post.getArea()%></FONT>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<FONT size=-1><%=post.getWorkYear()%></FONT><BR>
						<FONT color=#008000 size=-1> 
							<%
					 			String postDesciption = post.getDescription();
							    postDesciption = StringHelper.replaceHtml(postDesciption);
							 	//temppost = temppost.substring(0, 200);
							 %> <%=postDesciption%> 
						<BR>
				</FONT>
				<%-- 
					<p class="c overflow-all">
						<cite>职位描述：</cite><q id="job<%=i %>">
							<script type="text/javascript">showDescript("job<%=i %>","<%=description%>")</script>
						</q>
					</p>
					<script type="text/javascript">
						function showDescript(uid,texts)
						{
							var src = getTop5Row(texts);
							
							$('#'+uid).html(src);
							
						}
			
						/*计算有多少行*/
						function getRowCount(src){
							var arr = src.split("<br/>");
							return arr.length;
						}
			
						/*取得前3行的HTML数据流*/
						function getTop5Row(src){
							if(getRowCount(src) > 3){
								var arr = src.split("<br/>");
								var result = arr[0];
								for(var i=1; i < 3; i++){
									result = result +"<br/>"+ arr[i];
								}
								return result + " ......";
							}else{
								//如果没有换行符，则截取前120个字符显示
								var len = src.length;
								if(len > 120)
								{
									return src.substring(0,120) + " ......";
								}
								else
								{
									return src;
								}
							}
						}
						</script>
				 --%>
				</TD>
			</TR>
		</TBODY>
	</TABLE>
	<BR>
	<%
		}
	%>
	<DIV class=p>
		<pg:pager url="./PostInfoSearch" items="${totalRecord}"
			export="currentPageNumber=pageNumber" maxPageItems="10">
			<pg:param name="qc" value="<%=searchKey %>" />
			<pg:first>
				<a href="${pageUrl}">首页</a>
			</pg:first>
			<pg:prev>
				<a href="${pageUrl }">上一页</a>
			</pg:prev>
			<pg:pages>
				<c:choose>
					<c:when test="${currentPageNumber eq pageNumber}">
						<font color="red">${pageNumber }</font>
					</c:when>
					<c:otherwise>
						<a href="${pageUrl }">${pageNumber }</a>
					</c:otherwise>
				</c:choose>
			</pg:pages>
			<pg:next>
				<a href="${pageUrl }">下一页</a>
			</pg:next>
			<pg:last>
				<a href="${pageUrl }">尾页</a>
			</pg:last>
		</pg:pager>
	</DIV>
	<BR>
</BODY>
</HTML>