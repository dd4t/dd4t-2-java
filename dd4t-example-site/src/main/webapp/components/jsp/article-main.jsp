<%@page import="org.dd4t.contentmodel.ComponentPresentation" %>
<%@page import="org.dd4t.contentmodel.Field" %>
<%@page import="org.dd4t.springmvc.constants.Constants" %>
<%@ page
        language="java" contentType="text/html; charset=UTF-8"
        import="org.dd4t.springmvc.siteedit.SiteEditService,
                java.util.Map"
        pageEncoding="UTF-8" %>

<%
    ComponentPresentation cp = (ComponentPresentation) request.getAttribute(Constants.COMPONENT_PRESENTATION_KEY);
    Map<String, Field> cont = cp.getComponent().getContent();
%>
<h2>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("title")) %>
    <%=cont.get("title").getValues().get(0) %>
</h2>

<p><strong>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("introduction")) %>
    <%=cont.get("introduction").getValues().get(0) %>
</strong></p>

<div>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("paragraph")) %>
    <%=cont.get("paragraph").getValues().get(0) %>
</div>
	
	