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

import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.AbstractPostOperation;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.PostResponse;

/**
 * Streams the content of the property specified by the request parameter
 * 'property' to the response of the request.
 */
@Component
@Service(org.apache.sling.servlets.post.PostOperation.class)
@Properties({
		@Property(name = "service.description", value = "Servlet for editing the resource type for a resource"),
		@Property(name = "service.vendor", value = "The Apache Software Foundation"),
		@Property(name = "sling.post.operation", value = "edit-resource-type")

})
public class ResourceTypeEditorServlet extends AbstractPostOperation {
	
	@Override
	protected void doRun(SlingHttpServletRequest request, PostResponse postResponse, List<Modification> modifications) throws RepositoryException {
		ResourceResolver resourceResolver = request.getResourceResolver();
		String resourceType = request.getParameter("resourceType");
		Resource resourceTypeResource;
		try {
			String targetResourceTypePath = "/apps/"+resourceType;
			resourceTypeResource = getOrCreateResource(resourceResolver, targetResourceTypePath );
			String selectedScriptFilePath = request.getParameter("selectedScriptFilePath");
			selectedScriptFilePath = "/apps/"+selectedScriptFilePath;
			String scriptName = selectedScriptFilePath.substring(selectedScriptFilePath.lastIndexOf("/")+1);
			Resource targetScriptResource = resourceResolver.resolve(targetResourceTypePath+"/"+scriptName);
			if (!(targetScriptResource instanceof NonExistingResource)) {
				resourceResolver.delete(targetScriptResource);
			}
			ResourceUtils.copy(resourceResolver, selectedScriptFilePath, targetResourceTypePath, scriptName);
//			resourceResolver.copy(selectedScriptFilePath, targetResourceTypePath);
			String resourceSuperType = request.getParameter("resourceSuperType");
//			resourceTypeResource.getValueMap().put("sling:resourceSuperType", resourceSuperType);
			resourceTypeResource.adaptTo(ModifiableValueMap.class).put("sling:resourceSuperType", resourceSuperType);
			request.getResource().adaptTo(ModifiableValueMap.class).put("sling:resourceType", resourceType);
			resourceResolver.commit();
			postResponse.setLocation(request.getRequestURL().toString());
			postResponse.setStatus(HttpStatus.SC_MOVED_TEMPORARILY, "redirect to target");
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Resource getOrCreateResource(ResourceResolver resourceResolver, String resourceType) throws PersistenceException {
		resourceType = resourceType.endsWith("/") ? resourceType.substring(0, resourceType.length()-2) : resourceType;
		Resource resourceTypeResource = resourceResolver.resolve(resourceType);
		if (!(resourceTypeResource instanceof NonExistingResource)){
			return resourceTypeResource;
		} else {
			int lastSlash = resourceType.lastIndexOf("/");
			String parentResourceType = resourceType.substring(0, lastSlash);
			Resource parentResource = getOrCreateResource(resourceResolver, parentResourceType);
			String resourceTypeLastElementName = resourceType.substring(lastSlash+1);
			return resourceResolver.create(parentResource, resourceTypeLastElementName, null);
		}
	}

}