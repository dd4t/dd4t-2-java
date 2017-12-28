<%@page import="org.dd4t.contentmodel.ComponentPresentation" %>
<%@page import="org.dd4t.contentmodel.Field" %>
<%@page import="org.dd4t.core.resolvers.LinkResolver" %>
<%@page import="org.dd4t.springmvc.constants.Constants" %>
<%@page import="org.dd4t.springmvc.siteedit.SiteEditService" %>
<%@ page
        language="java" contentType="text/html; charset=UTF-8"
        import="org.dd4t.springmvc.util.ApplicationContextProvider,
                java.util.Map"
        pageEncoding="UTF-8" %>

<%
    ComponentPresentation cp = (ComponentPresentation) request.getAttribute(Constants.COMPONENT_PRESENTATION_KEY);
    Map<String, Field> cont = cp.getComponent().getContent();
%>
<h3>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("title")) %>
    <%=cont.get("title").getValues().get(0) %>
</h3>

<%
    LinkResolver resolver = (LinkResolver) ApplicationContextProvider.getBean("LinkResolver");
    String url = resolver.resolve(cp);
%>
<a href="<%=url %>">Read more</a>
	