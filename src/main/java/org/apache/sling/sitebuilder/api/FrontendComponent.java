package org.apache.sling.sitebuilder.api;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainer;

public interface FrontendComponent {

	public JSONArray getClientParameters();

	public void setClientParameters(JSONArray clientParameters);
	
	public String getTypeName();

	public void processServerScript(ModifyServletOperation operation, Element sourceElement, OutputDocument sourceElementOutput, String id, Element targetElement,
			String referenceElementId);

	void processResources(ModifyServletOperation operation, ResourceResolver resourceResolver, int newComponentId,
			String sourceComponentId, ScriptContainer resolvedIdScriptContainer, ScriptContainer resolvedReferenceIdScriptContainer,
			boolean sameResources) throws PersistenceException;
}
