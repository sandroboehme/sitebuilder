/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.sitebuilder.internal;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainerResolverIfc;

/**
 * Streams the content of the property specified by the request parameter
 * 'property' to the response of the request.
 */
@Component
@Service(Servlet.class)
@Properties({
		@Property(name = "service.description", value = "Servlet for returning resource paths for the script stack provided in the selector"),
		@Property(name = "service.vendor", value = "The Apache Software Foundation"),
		@Property(name = "sling.servlet.extensions", value = ScriptContainerServlet.EXTENSION_SCRIPTCONTAINER),
		@Property(name = "sling.servlet.resourceTypes", value = "sling/web-page")
})
@References( {
    @Reference(name = "ScriptContainerResolver", referenceInterface = ScriptContainerResolverIfc.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC, bind = "setScriptContainerResolver", unbind = "unsetScriptContainerResolver")
})

public class ScriptContainerServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = -1L;
    public static final String EXTENSION_SCRIPTCONTAINER = "scriptcontainer";
	private ScriptContainerResolverIfc scriptContainerResolver = null;

    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        
		String[] scriptIdStackArr = request.getParameterValues("scriptIdStack");
		
        PrintWriter writer = response.getWriter();
        writer.print("[");
        for (int i=0; i < scriptIdStackArr.length; i++){
        	String scriptIdStack = scriptIdStackArr[i];
        	ScriptContainer resolvedScriptContainer = scriptContainerResolver.resolve(scriptIdStack, request, true);
            writer.printf("{\"scriptStack\": \"%s\", \"resolvedScriptStack\":{\"resourcePath\":\"%s\", \"scriptPath\":\"%s\"}}", scriptIdStack, resolvedScriptContainer.getResource().getPath(), resolvedScriptContainer.getScriptResource().getPath());
            if (i+1 < scriptIdStackArr.length){
            	writer.print(",");
            }
		}
        writer.print("]");
	}

	public void unsetScriptContainerResolver(final ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = null;
	}

	public void setScriptContainerResolver(ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = scriptContainerResolver;
	}
    
}
