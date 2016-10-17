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
import java.util.Calendar;
import java.util.HashMap;
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
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.servlets.post.AbstractPostOperation;
import org.apache.sling.servlets.post.JSONResponse;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.servlets.post.ModificationType;
import org.apache.sling.servlets.post.PostResponse;
import org.apache.sling.sitebuilder.SlingCallComponent;
import org.apache.sling.sitebuilder.SlingIncludeComponent;
import org.apache.sling.sitebuilder.api.DefaultFrontendComponent;
import org.apache.sling.sitebuilder.api.FrontendComponent;
import org.apache.sling.sitebuilder.api.ModifyServletOperation;
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
	private static final String COMPONENTS_PATH = "/apps/sling/sitebuilder/components";
	private Pattern notEmptyPattern = Pattern.compile(".*");
	private Pattern componentPattern = Pattern.compile(".*component.*");
	private ScriptContainerResolverIfc scriptContainerResolver = null;
	private static Map<String, FrontendComponent> componentRegistry = new HashMap<String, FrontendComponent>();
	static {
		componentRegistry.put(SlingIncludeComponent.TYPE_NAME, new SlingIncludeComponent());
		componentRegistry.put(SlingCallComponent.TYPE_NAME, new SlingCallComponent());
	}
	
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
		String addedComponentDataString = request.getParameter("addedComponentData");
		final String componentType = request.getParameter("componentType");
		
		final ResourceResolver resourceResolver = request.getResourceResolver();
		
		ScriptContainer resolvedIdScriptContainer = null;
		Resource idScriptResource = null; 
		InputStream idInputStream = null;
		Source idJspSource = null;
		OutputDocument idOutputDocument=null;
		Element sourceElement = null; 
		try {
			if (operation.isAddOperation()){
				if (componentType != null && componentType.startsWith("/")) {// for old toolbar
					//resolvedIdScriptContainer = scriptContainerResolver.resolve(request, COMPONENTS_PATH+componentType+"/content", COMPONENTS_PATH+componentType);
					resolvedIdScriptContainer = new ScriptContainer(){
						public Resource getResource() {
							String toolbarItemName = componentType.substring(componentType.lastIndexOf("/")+1);
							return resourceResolver.getResource(COMPONENTS_PATH+componentType+"/"+toolbarItemName+"-content");
						}
						public Resource getScriptResource() {
							return null;
						}
					};
				} else {
					resolvedIdScriptContainer = new ScriptContainer(){
						public Resource getResource() {
							return resourceResolver.getResource(COMPONENT_STOCK_DOMAIN_CONTENT_PATH);
						}
						public Resource getScriptResource() {
							return resourceResolver.getResource(COMPONENT_STOCK_SCRIPT_PATH);
						}
					};
				}
			} else {
				resolvedIdScriptContainer = scriptContainerResolver.resolve(scriptIdStackString, request, false);
				idScriptResource = resolvedIdScriptContainer.getScriptResource();
				idInputStream = idScriptResource.adaptTo(InputStream.class);
				idJspSource = new Source(idInputStream);
				idOutputDocument=new OutputDocument(idJspSource);
				sourceElement = getComponentElement(id, idJspSource);
			}
	
			ScriptContainer resolvedReferenceIdScriptContainer = null;
			Resource referenceIdScriptToChange = null;
			boolean sameScriptResources = false;
			boolean sameResources = true;
			InputStream referenceIdInputStream = null;
			
			Source referenceIdJspSource = null;
			OutputDocument referenceIdOutputDocument= null;
			
			OutputDocument sourceElementOutput = null;
			Element targetElement = null;
			int newComponentId = 0;
			if (operation.isAddOperation() || operation.isOrderOperation()){
				newComponentId = Integer.parseInt(lastIdString)+1;
				resolvedReferenceIdScriptContainer = scriptContainerResolver.resolve(referenceScriptIdStackString, request, false);
				referenceIdScriptToChange = resolvedReferenceIdScriptContainer.getScriptResource();
				if (operation.isOrderOperation()){
					sameScriptResources = idScriptResource.getPath().equals(referenceIdScriptToChange.getPath());
					sameResources = resolvedIdScriptContainer.getResource().getPath().equals(resolvedReferenceIdScriptContainer.getResource().getPath());
					referenceIdInputStream = sameScriptResources ? idInputStream : referenceIdScriptToChange.adaptTo(InputStream.class);
					referenceIdJspSource = sameScriptResources ? idJspSource : new Source(referenceIdInputStream);
					referenceIdOutputDocument= sameScriptResources ? idOutputDocument : new OutputDocument(referenceIdJspSource);
					sourceElementOutput = handleComponentId(newComponentId, sourceElement, sameResources, operation);
					targetElement = getComponentElement(referenceElementId, referenceIdJspSource);
					idOutputDocument.remove(sourceElement);
				}
				if (operation.isAddOperation()){
					String includeString = "<sb2:component resourceType=\""+COMPONENTS_PATH+componentType+"\" componentId=\""+newComponentId+"\"/>";
					Source source = new Source(includeString);
					sourceElementOutput = new OutputDocument(source);
					referenceIdInputStream = referenceIdScriptToChange.adaptTo(InputStream.class);
					referenceIdJspSource = new Source(referenceIdInputStream);
					referenceIdOutputDocument= new OutputDocument(referenceIdJspSource);
					targetElement = getComponentElement(referenceElementId, referenceIdJspSource);
//					try {
//						//TODO: make available not only for the HTML tag but for custom tag as well
//						String dataComponentType = sourceElement.getAttributeValue("data-component-type");
//						FrontendComponent frontendComponent = componentRegistry.get(dataComponentType);
//						if (frontendComponent != null) {
//							frontendComponent.setClientParameters(new JSONArray(addedComponentDataString));
//							frontendComponent.processServerScript(operation, sourceElement, sourceElementOutput, id, targetElement, referenceElementId);
//						}
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
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

//			String dataComponentType = sourceElement.getAttributeValue("data-component-type");
			FrontendComponent frontendComponent = null;//componentRegistry.get(dataComponentType);
			if (frontendComponent == null) {
				frontendComponent = new DefaultFrontendComponent();
			}
			frontendComponent.processResources(operation, resourceResolver, newComponentId, id, resolvedIdScriptContainer, resolvedReferenceIdScriptContainer, sameResources);

			
			writeOutputDocuments(idScriptResource, referenceIdScriptToChange, sameScriptResources, idJspSource,
					referenceIdJspSource, idOutputDocument, referenceIdOutputDocument, operation);
			
			resourceResolver.commit();
			String sourceScriptPath = idScriptResource == null ? COMPONENTS_PATH+componentType : idScriptResource.getPath();
			Modification modification = new Modification(ModificationType.MODIFY, sourceScriptPath, referenceIdScriptToChange==null ? "" : referenceIdScriptToChange.getPath());
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
	if (idScriptResource!=null){
		File idScriptFile = idScriptResource.adaptTo(File.class);
		if (idScriptFile != null) {
			idOutputDocument.writeTo(new FileWriter(idScriptFile));
		} else {
			CharArrayWriter idWriter = new CharArrayWriter();
			idOutputDocument.writeTo(idWriter); //idInputStream
			writeOutputToScript(idOutputDocument, idScriptResource, idWriter.toString(), idJspSource.getEncoding());
		}
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
		properties.put("jcr:lastModified", Calendar.getInstance());
	}

	public void unsetScriptContainerResolver(final ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = null;
	}

	public void setScriptContainerResolver(ScriptContainerResolverIfc scriptContainerResolver) {
		this.scriptContainerResolver = scriptContainerResolver;
	}

}