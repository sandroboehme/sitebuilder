
package org.apache.sling.sitebuilder.internal.scriptstackresolver;

import org.apache.sling.api.resource.Resource;

public class ScriptContainer {
	
	private Resource resource;
	private Resource scriptResource;
	
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public Resource getScriptResource() {
		return scriptResource;
	}
	public void setScriptResource(Resource scriptResource) {
		this.scriptResource = scriptResource;
	}

}
