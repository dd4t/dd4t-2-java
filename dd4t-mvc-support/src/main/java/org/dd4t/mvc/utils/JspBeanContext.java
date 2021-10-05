/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.mvc.utils;

import org.dd4t.core.util.HttpUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public final class JspBeanContext {

    private JspBeanContext() {
    }

    public static <T> T getBean(Class<T> type) {
        return getWebApplicationContext().getBean(type);
    }

    public static Object getBean(String beanName) {
        return getWebApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return getWebApplicationContext().getBean(beanName, type);
    }

    private static WebApplicationContext getWebApplicationContext() {
        // Spring 5 has findWebApplicationContext, Spring 4 < has getWebApplicationContext.
        // Both do the same.


        return findWebApplicationContext(HttpUtils.getCurrentRequest(), null);
    }

    /**
     * Look for the WebApplicationContext associated with the DispatcherServlet
     * that has initiated request processing, and for the global context if none
     * was found associated with the current request. The global context will
     * be found via the ServletContext or via ContextLoader's current context.
     * <p>NOTE: This variant remains compatible with Servlet 2.5, explicitly
     * checking a given ServletContext instead of deriving it from the request.
     *
     * @param request        current HTTP request
     * @param servletContext current servlet context
     * @return the request-specific WebApplicationContext, or the global one
     * if no request-specific context has been found, or {@code null} if none
     * @see org.springframework.web.servlet.DispatcherServlet#WEB_APPLICATION_CONTEXT_ATTRIBUTE
     * @see org.springframework.web.context.support.WebApplicationContextUtils#getWebApplicationContext(javax.servlet.ServletContext)
     * @see org.springframework.web.context.ContextLoader#getCurrentWebApplicationContext()
     * @since 4.2.1
     */
    @Nullable
    public static WebApplicationContext findWebApplicationContext(HttpServletRequest request,
                                                                  @Nullable ServletContext servletContext) {

        WebApplicationContext webApplicationContext =
                (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            if (servletContext != null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            }
            if (webApplicationContext == null) {
                webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            }
        }
        return webApplicationContext;
    }
}
