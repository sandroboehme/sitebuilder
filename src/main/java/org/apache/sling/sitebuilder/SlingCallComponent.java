package org.apache.sling.sitebuilder;

import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.sitebuilder.api.ModifyServletOperation;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;

public class SlingCallComponent extends SlingScriptComponent{

	public static final String TYPE_NAME = "sling-call"; 
	public static final String PATH_PROTOTYPE_SCRIPT = "/apps/sling/pageEditor/prototype/call/call-prototype.jsp";
	
	public String getTypeName(){
		return TYPE_NAME;
	}

	@Override
	public void processServerScript(ModifyServletOperation operation, Element sourceElement, OutputDocument sourceElementOutput,
			String id, Element targetElement, String referenceElementId) {
		List<Element> allElements = sourceElement.getAllElements();
		for (Element element : allElements) {
			if (element.getName().endsWith(":call")){
				OutputDocument includeElementOutput = new OutputDocument(element);
				Map<String, String> attributeMap = includeElementOutput.replace(element.getAttributes(), false);
				attributeMap.put("script", getScriptFileName());
				sourceElementOutput.replace(element, includeElementOutput.toString());
			}
		}
	}

	@Override
	public void processResources(ModifyServletOperation operation, ResourceResolver resourceResolver, int newComponentId,
			String sourceComponentId, ScriptContainer resolvedIdScriptContainer,
			ScriptContainer resolvedReferenceIdScriptContainer, boolean sameResources) throws PersistenceException {

		if (operation.isAddOperation()){
			String copyValueString = getParameterValueString("copy");
			boolean copyValue = "on".equalsIgnoreCase(copyValueString);
			if (copyValue){
				String fileName = getScriptFileName();
				copyScriptPrototype(resourceResolver, fileName);
			}
		} else {
			super.processResources(operation, resourceResolver, newComponentId, sourceComponentId, resolvedIdScriptContainer, resolvedReferenceIdScriptContainer, sameResources);
		}
		
	}

	private String getScriptFileName() {
		String scriptResourcePath = getParameterValueString("script_resource_path");
		int lastSlash = scriptResourcePath.lastIndexOf("/");
		String fileName = scriptResourcePath.substring(lastSlash+1);
		return fileName;
	}

	@Override
	protected String getPrototypeScriptPath() {
		return PATH_PROTOTYPE_SCRIPT;
	}

}
