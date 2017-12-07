<%@page import="org.dd4t.contentmodel.Component" %>
<%@page import="org.dd4t.contentmodel.ComponentPresentation" %>
<%@page import="org.dd4t.contentmodel.Field" %>
<%@page import="org.dd4t.core.resolvers.LinkResolver" %>
<%@page import="org.dd4t.springmvc.constants.Constants" %>
<%@ page
        language="java" contentType="text/html; charset=UTF-8"
        import="org.dd4t.springmvc.siteedit.SiteEditService,
                org.dd4t.springmvc.util.ApplicationContextProvider,
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

<div>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("paragraph")) %>

    <%=cont.get("paragraph").getValues().get(0) %>
</div>

<h3>Related links</h3>
<ul>
    <%=SiteEditService.generateSiteEditFieldMarking(cont.get("related")) %>

    <%
        for (Object ob : cont.get("related").getValues()) {
            Component linkedComp = (Component) ob;

            // since the news main template is setup with linklevels == 2, the linked component content is loaded inside

            String title = (String) linkedComp.getContent().get("title").getValues().get(0);
            LinkResolver linkResolver = (LinkResolver) ApplicationContextProvider.getBean("LinkResolver");
            String href = linkResolver.resolve(linkedComp);

    %>
    <li><a href="<%=href %>"><%=title %>
    </a></li>
    <%
        }
    %>
</ul>
	
	
	