package org.apache.sling.sitebuilder.api;

import java.util.Map;

import javax.jcr.AccessDeniedException;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;

public class DefaultFrontendComponent implements FrontendComponent {
	
	private JSONArray clientParameters = null;

	@Override
	public JSONArray getClientParameters() {
		return clientParameters;
	}

	@Override
	public void setClientParameters(JSONArray clientParameters) {
		this.clientParameters = clientParameters;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processServerScript(ModifyServletOperation operation, Element sourceElement, OutputDocument sourceElementOutput,
			String id, Element targetElement, String referenceElementId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processResources(ModifyServletOperation operation, ResourceResolver resourceResolver, int newComponentId, String sourceComponentId,
			ScriptContainer resolvedIdScriptContainer, ScriptContainer resolvedReferenceIdScriptContainer, boolean sameResources) throws PersistenceException {
		try {
			Resource IdScriptContainerResource = resolvedIdScriptContainer.getResource();
			Resource sourceResource = resourceResolver.getResource(IdScriptContainerResource.getPath() + "/" + sourceComponentId);
			if (sourceResource != null && (operation.isAddOperation() || operation.isOrderOperation())) {
				Resource targetParentResource = resolvedReferenceIdScriptContainer.getResource();
				if (!sameResources || operation.isAddOperation()) {
					copy(resourceResolver, sourceResource, targetParentResource, "" + newComponentId);
				}
			}
			if (sourceResource != null
					&& ((!sameResources && operation.isOrderOperation()) || operation == ModifyServletOperation.DELETE)) {
				resourceResolver.delete(sourceResource);
			}
		} catch (PersistenceException e) {
			throw e;
		}
	}

	private void copy(ResourceResolver resourceResolver, Resource sourceResource, Resource targetParentResource,
			String targetResourceName) throws PersistenceException {
		try {
			Map<String, Object> sourceResourceProperties = sourceResource.adaptTo(ValueMap.class);
			resourceResolver.create(targetParentResource, targetResourceName, sourceResourceProperties);
			Iterable<Resource> children = sourceResource.getChildren();
			for (Resource child : children) {
				copy(resourceResolver, child, resourceResolver.getResource(targetParentResource, targetResourceName),
						child.getName());
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

	
	protected String getParameterValueString(String parameterName){
		return getParameterString(parameterName, "value");
	}
	
	protected String getParameterString(String parameterName, String valueKey){
		String returnString = null;
		JSONObject objectFound = null;
		try {
			for (int i=0; i<this.clientParameters.length() && objectFound == null; i++){
				JSONObject jsonObject = (JSONObject) this.clientParameters.get(i);
				if (parameterName.equals(jsonObject.get("name"))){
					objectFound = jsonObject;
				}
			}
			if (objectFound != null) {
				returnString = objectFound.getString(valueKey);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnString;
	}
}
