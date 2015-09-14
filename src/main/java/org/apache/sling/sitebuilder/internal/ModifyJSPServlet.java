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

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.AbstractPostOperation;
import org.apache.sling.servlets.post.JSONResponse;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.PostResponse;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainerResolverIfc;

/**
 * Streams the content of the property specified by the request parameter
 * 'property' to the response of the request.
 */
@Component
@Service(org.apache.sling.servlets.post.PostOperation.class)
@Properties({
		@Property(name = "service.description", value = "Servlet for modifying a JSP"),
		@Property(name = "service.vendor", value = "The Apache Software Foundation"),
		@Property(name = "sling.post.operation", value = "modify-jsp")

})
@References( {
    @Reference(name = "ScriptContainerResolver", referenceInterface = ScriptContainerResolverIfc.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC, bind = "setScriptContainerResolver", unbind = "unsetScriptContainerResolver")
})
public class ModifyJSPServlet extends AbstractPostOperation {
	private static final long serialVersionUID = -1L;
	private static final String COMPONENT_STOCK_SCRIPT_PATH = "/apps/sling/sitebuilder/component-stock/html.jsp";
	private static final String COMPONENT_STOCK_DOMAIN_CONTENT_PATH = "/sitebuilder/componentStock";
	private Pattern notEmptyPattern = Pattern.compile(".*");
	private Pattern componentPattern = Pattern.compile(".*component.*");
	private ScriptContainerResolverIfc scriptContainerResolver = null;
	
//	TODO: prevent the move of a sling:call into a sling:include 

	@Override
	protected void doRun(SlingHttpServletRequest request, PostResponse postResponse, List<Modification> modifications) throws RepositoryException {
		String operationString = request.getParameter(":modifyServletOperation");
		ModifyServletOperation operation = ModifyServletOperation.lookup(operationString);
		String referenceElementId = request.getParameter("referenceElementId");
		String id = request.getParameter("componentId");
		String lastIdString = request.getParameter("lastId");
		
		String scriptIdStackString = request.getParameter("scriptIdStack");
		String referenceScriptIdStackString = request.getParameter("referenceScriptIdStack");
		
		final ResourceResolver resourceResolver = request.getResourceResolver();
		
		ScriptContainer resolvedIdScriptContainer = null;
		if (operation.isAddOperation()){
			resolvedIdScriptContainer = new ScriptContainer(){
				public Resource getResource() {
					return resourceResolver.getResource(COMPONENT_STOCK_DOMAIN_CONTENT_PATH);
				}
				public Resource getScriptResource() {
					return resourceResolver.getResource(COMPONENT_STOCK_SCRIPT_PATH);
				}
			};
		} else {
			resolvedIdScriptContainer = scriptContainerResolver.resolve(scriptIdStackString, request, false);
		}
		Resource idScriptResource = resolvedIdScriptContainer.getScriptResource();
		InputStream idInputStream = idScriptResource.adaptTo(InputStream.class);

		ScriptContainer resolvedReferenceIdScriptContainer = null;
		Resource referenceIdScriptToChange = null;
		boolean sameScriptResources = false;
		boolean sameResources = true;
		InputStream referenceIdInputStream = null;
		
		if (operation.isAddOperation() || operation.isOrderOperation()){
			resolvedReferenceIdScriptContainer = scriptContainerResolver.resolve(referenceScriptIdStackString, request, false);
			referenceIdScriptToChange = resolvedReferenceIdScriptContainer.getScriptResource();
			sameScriptResources = idScriptResource.getPath().equals(referenceIdScriptToChange.getPath());
			sameResources = resolvedIdScriptContainer.getResource().getPath().equals(resolvedReferenceIdScriptContainer.getResource().getPath());
			referenceIdInputStream = sameScriptResources ? idInputStream : referenceIdScriptToChange.adaptTo(InputStream.class);
		}

		try {
			Source idJspSource = new Source(idInputStream);
			
			OutputDocument idOutputDocument=new OutputDocument(idJspSource);
			Source referenceIdJspSource = null;
			OutputDocument referenceIdOutputDocument= null;
			
			Element sourceElement = getComponentElement(id, idJspSource);
			OutputDocument sourceElementOutput = null;
			Element targetElement = null;
			int newComponentId = 0;
			if (operation.isAddOperation() || operation.isOrderOperation()){
				newComponentId = Integer.parseInt(lastIdString)+1;
				referenceIdJspSource = sameScriptResources ? idJspSource : new Source(referenceIdInputStream);
				referenceIdOutputDocument= sameScriptResources ? idOutputDocument : new OutputDocument(referenceIdJspSource);
				sourceElementOutput = handleComponentId(newComponentId, sourceElement, sameResources, operation);
				targetElement = getComponentElement(referenceElementId, referenceIdJspSource);
			}
			if (operation.isOrderOperation()){
				idOutputDocument.remove(sourceElement);
			}
			
			switch (operation) {
				case ORDERWITHINFIRST: {
					referenceIdOutputDocument.insert(targetElement.getFirstStartTag().getEnd(), "\n"+sourceElementOutput);
					break;
				}
				case ORDERBEFORE: {
					referenceIdOutputDocument.insert(targetElement.getBegin(), sourceElementOutput+"\n");
					break;
				}
				case ORDERAFTER: {
					referenceIdOutputDocument.insert(targetElement.getEnd(), "\n"+sourceElementOutput);
					break;
				}
				case ADDWITHINFIRST: {
					referenceIdOutputDocument.insert(targetElement.getFirstStartTag().getEnd(), "\n"+sourceElementOutput);
					break;
				}
				case ADDBEFORE: {
					referenceIdOutputDocument.insert(targetElement.getBegin(), sourceElementOutput+"\n");
					break;
				}
				case ADDAFTER: {
					referenceIdOutputDocument.insert(targetElement.getEnd(), "\n"+sourceElementOutput);
					break;
				}
				case DELETE: {
					idOutputDocument.remove(sourceElement);
					break;
				}
				default: {
					break;
				}
			}

			Resource IdScriptContainerResource = resolvedIdScriptContainer.getResource();
			Resource sourceResource = resourceResolver.getResource(IdScriptContainerResource.getPath()+"/"+id);
			if (sourceResource != null && (operation.isAddOperation() || operation.isOrderOperation())){
				Resource targetParentResource = resolvedReferenceIdScriptContainer.getResource();
				if (!sameResources || operation.isAddOperation()){
					copy(resourceResolver, sourceResource, targetParentResource, ""+newComponentId);
				}
			}
			if (sourceResource != null && ((!sameResources && operation.isOrderOperation()) || operation == ModifyServletOperation.DELETE)){
				resourceResolver.delete(sourceResource);
			}

			
			writeOutputDocuments(idScriptResource, referenceIdScriptToChange, sameScriptResources, idJspSource,
					referenceIdJspSource, idOutputDocument, referenceIdOutputDocument, operation);
			
			resourceResolver.commit();
			
			Modification modification = new Modification(ModificationType.MODIFY, idScriptResource.getPath(), referenceIdScriptToChange==null ? "" : referenceIdScriptToChange.getPath());
			modifications.add(modification);
			
			if (postResponse instanceof JSONResponse) {
				JSONResponse jsonResponse = (JSONResponse) postResponse;
				jsonResponse.setProperty("newComponentId", newComponentId);
			}
			
		} catch (FileNotFoundException e) {
			throw new RepositoryException(e);
		} catch (IOException e) {
			throw new RepositoryException(e);
		} finally {
		}
		
	}

private void writeOutputDocuments(Resource idScriptResource, Resource referenceIdScriptToChange, boolean sameScriptResources,
		Source idJspSource, Source referenceIdJspSource, OutputDocument idOutputDocument, OutputDocument referenceIdOutputDocument, ModifyServletOperation operation)
		throws IOException, PersistenceException, RepositoryException {
	File idScriptFile = idScriptResource.adaptTo(File.class);
	if (idScriptFile != null) {
		idOutputDocument.writeTo(new FileWriter(idScriptFile));
	} else {
		CharArrayWriter idWriter = new CharArrayWriter();
		idOutputDocument.writeTo(idWriter); //idInputStream
		writeOutputToScript(idOutputDocument, idScriptResource, idWriter.toString(), idJspSource.getEncoding());
	}
	
	if (!sameScriptResources && operation != ModifyServletOperation.DELETE) {
		File referenceIdScriptFile = referenceIdScriptToChange.adaptTo(File.class);
		if (referenceIdScriptFile != null) {
			referenceIdOutputDocument.writeTo(new FileWriter(referenceIdScriptFile)); // referenceIdInputStream
		} else {
			CharArrayWriter referenceIdWriter = new CharArrayWriter();
			referenceIdOutputDocument.writeTo(referenceIdWriter); // referenceIdInputStream
			writeOutputToScript(referenceIdOutputDocument, referenceIdScriptToChange, referenceIdWriter.toString(), referenceIdJspSource.getEncoding());
		}
	}
}
	
private void copy(ResourceResolver resourceResolver, Resource sourceResource, Resource targetParentResource, String targetResourceName) throws PersistenceException {
	try {
		Map<String,Object> sourceResourceProperties = sourceResource.adaptTo(ValueMap.class);
		resourceResolver.create(targetParentResource, targetResourceName, sourceResourceProperties);
		Iterable<Resource> children = sourceResource.getChildren();
		for (Resource child : children) {
			copy(resourceResolver, child, resourceResolver.getResource(targetParentResource, targetResourceName), child.getName());
		}
	} catch (Exception e) {
		// TODO: Check permission in advance and fail soon?
		// See GetEffectiveAclServlet for an example.
		if (e != null && e.getCause() instanceof AccessDeniedException) {
			throw new PersistenceException("Couldn't persist the change. Are you logged in?");
		} else {
			throw e;
		}
	}
}

private Element getComponentElement(String id, Source idJspSource) {
	Element componentElement = null;
	List<StartTag> allCustomStartTags = idJspSource.getAllStartTags("componentId", id, true);
	boolean customTagFound = false;
	for (int i=0; i<allCustomStartTags.size() && !customTagFound; i++){
		StartTag customStartTag = allCustomStartTags.get(i);
		if(customStartTag.getName().endsWith(":component")){
			customTagFound=true;
			componentElement = idJspSource.getFirstElement("componentId", id, true);
		}
	}
	if (!customTagFound) {
		componentElement = idJspSource.getFirstElement("data-component-id", id, true);
	}
	return componentElement;
}

	private OutputDocument handleComponentId(int newId, Element element, boolean sameResources, ModifyServletOperation operation) {
		OutputDocument elementOutput = new OutputDocument(element);
		if (!sameResources) {
			List<StartTag> allCustomStartTags = element.getAllStartTags("componentId", notEmptyPattern);
			for (StartTag customStartTag : allCustomStartTags) {
				if(customStartTag.getName().endsWith(":component")){
					Map<String, String> attributeMap = elementOutput.replace(customStartTag.getAttributes(), false);
					attributeMap.put("componentId", ""+newId++);
				}
			}
			List<StartTag> allComponentTags = element.getAllStartTags("class", this.componentPattern);
			for (StartTag startTag : allComponentTags) {
				Map<String, String> attributeMap = elementOutput.replace(startTag.getAttributes(), false);
				attributeMap.put("data-component-id", ""+newId++);
			}
		}
		return elementOutput;
	}

	private void writeOutputToScript(OutputDocument outputDocument, Resource scriptResource, String fileContent, String encoding) throws PersistenceException, RepositoryException {
		ResourceResolver resourceResolver = scriptResource.getResourceResolver();
		Resource content = resourceResolver.getResource(scriptResource.getPath()+"/jcr:content");
		InputStream is = new ByteArrayInputStream(Charset.forName(encoding).encode(fileContent).array());
		ModifiableValueMap properties = content.adaptTo(ModifiableValueMap.class);
		if (properties == null) throw new RepositoryException("Couldn't persist the change. Are you logged in?");
		properties.put("jcr:data", is);
	}

	public void unsetScriptContainerResolver(final ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = null;
	}

	public void setScriptContainerResolver(ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = scriptContainerResolver;
	}

	enum ModifyServletOperation {
		ORDERBEFORE, ORDERAFTER, ORDERWITHINFIRST, ORDERWITHINLAST, ADDBEFORE, ADDAFTER, ADDWITHINFIRST, ADDWITHINLAST, DELETE;
	
		public static ModifyServletOperation lookup(String anOperation) {
			for (ModifyServletOperation op : ModifyServletOperation.values()) {
				if (op.name().equalsIgnoreCase(anOperation)) {
					return op;
				}
			}
			return null;
		}
		
		public boolean isAddOperation(){
			return this == ADDBEFORE || this == ADDAFTER || this == ADDWITHINFIRST || this == ADDWITHINLAST;
		}
		
		public boolean isOrderOperation(){
			return this == ORDERBEFORE || this == ORDERAFTER || this == ORDERWITHINFIRST || this == ORDERWITHINLAST;
		}
	}
}