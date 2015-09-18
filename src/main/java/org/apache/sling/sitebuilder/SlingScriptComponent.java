package org.apache.sling.sitebuilder;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ModifiableValueMapDecorator;
import org.apache.sling.sitebuilder.api.DefaultFrontendComponent;
import org.apache.sling.sitebuilder.api.ModifyServletOperation;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;

public abstract class SlingScriptComponent extends DefaultFrontendComponent{

	protected abstract String getPrototypeScriptPath();

	protected void copyScriptPrototype(ResourceResolver resourceResolver, String fileName) throws PersistenceException {
		String scriptResourcePathString = getParameterValueString("script_resource_path");
		Resource scriptResourcePath = getOrCreateParentResource(resourceResolver, scriptResourcePathString, null);
		
		Resource prototypeScriptResource = resourceResolver.getResource(getPrototypeScriptPath());
		Map<String,Object> prototypeScriptResourceProperties = prototypeScriptResource.adaptTo(ValueMap.class);
		Resource htmlJSP = resourceResolver.create(scriptResourcePath, fileName, prototypeScriptResourceProperties);
		
		String fileContentNodeName = "jcr:content";
		Resource fileContentNode = prototypeScriptResource.getChild(fileContentNodeName);
		ValueMap fileContentNodeProperties = fileContentNode.adaptTo(ValueMap.class);
		Map<String, Object> fileContentNodeMap = new HashMap<String, Object>(fileContentNodeProperties);
		fileContentNodeMap.remove("jcr:uuid");
		ValueMap fileContentNodePropertiesWithoutUuid = new ModifiableValueMapDecorator(fileContentNodeMap);
		resourceResolver.create(htmlJSP, fileContentNodeName, fileContentNodePropertiesWithoutUuid);
	}

	protected void getOrCreateDomainDataResource(ResourceResolver resourceResolver) {
		String resourcePathString = getParameterValueString("resource_path");
		String resourceType = getParameterValueString("resource_type");
		getOrCreateParentResource(resourceResolver, resourcePathString, resourceType);
	}

	protected Resource getOrCreateParentResource(ResourceResolver resourceResolver, String resourcePath, String resourceType) {
		Resource parentResource = resourceResolver.getResource("/");
		int lastSlashIndex = resourcePath.lastIndexOf("/");
		boolean hasExtension = resourcePath.substring(lastSlashIndex).contains(".");
		if (!resourcePath.endsWith("/")){
			if (hasExtension) {
				// cut off the characters beginning with the last '/'
				resourcePath = resourcePath.substring(0, lastSlashIndex);
			} else {
				resourcePath += "/";
			}
		}
		if (resourcePath.startsWith("/")){
			resourcePath = resourcePath.substring(1);
			String[] resourcePathElements = resourcePath.split("/");
			StringBuilder currentPath = new StringBuilder();
			for (int i=0; i<resourcePathElements.length; i++){
				currentPath.append("/");
				currentPath.append(resourcePathElements[i]);
				Resource childResource = resourceResolver.getResource(currentPath.toString());
				if (childResource == null) {
					try {
						Map<String,Object> targetResourceProperties = new HashMap<String,Object>();
						targetResourceProperties.put("jcr:primaryType", "nt:unstructured");
						if (i == resourcePathElements.length-1 && resourceType != null){
							targetResourceProperties.put("sling:resourceType", resourceType);
						}
						parentResource = resourceResolver.create(parentResource, resourcePathElements[i], targetResourceProperties);
					} catch (PersistenceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					parentResource = childResource;
				}
			}
		} else {
			throw new IllegalArgumentException("The resourcePath should start with a '/'.");
		}
		return parentResource;
	}

}
