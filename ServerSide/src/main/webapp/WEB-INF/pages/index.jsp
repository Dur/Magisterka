<%@ page import="java.util.*" %>
<html>
<body>
<h1>Hello World!</h1>
 
<h2>Message : ${message}</h2>
<h2><%= new java.util.Date() %></h2>
<%!
    Date theDate = new Date();
    Date getDate()
    {
        return theDate;
    }
%>
<%
	for(int a = 0; a < 5; a++){
	%>
		<h2>Message : ${message}</h2>
<% 
	}
%>
<TABLE BORDER=2>
<%
    for ( int i = 0; i < 10; i++ ) {
        %>
        <TR>
        <TD>Number <%= "" + i %></TD>
        <TD><%= i+1 %></TD>
        <TD><%=getDate()%></TD>
        </TR>
        <%
    }
%>
</TABLE>	
</body>
</html>