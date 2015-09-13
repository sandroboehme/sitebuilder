package org.apache.sling.sitebuilder.internal.scriptstackresolver;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

public class ScriptContainerResolvingRequestWrapper extends SlingHttpServletRequestWrapper {

	private Resource resource;
	private String resourceType;
	private RequestPathInfo rpi = null;

	public ScriptContainerResolvingRequestWrapper(SlingHttpServletRequest wrappedRequest, boolean removeExtension) {
		super(wrappedRequest);
		rpi = new ScriptContainerResolvingPathInfo(wrappedRequest, removeExtension);
	}

	public ScriptContainerResolvingRequestWrapper(SlingHttpServletRequest wrappedRequest, Resource resource, String resourceType, boolean removeExtension) {
		super(wrappedRequest);
		rpi = new ScriptContainerResolvingPathInfo(wrappedRequest, resource.getResourceMetadata().getResolutionPath(), removeExtension);
		this.resource = resource;
		this.resourceType = resourceType;
	}

	public ScriptContainerResolvingRequestWrapper(SlingHttpServletRequest wrappedRequest, Resource resource,
			RequestPathInfo requestPathInfo) {
		super(wrappedRequest);
		rpi = requestPathInfo;
		this.resource = resource;
	}

	public String getMethod() {
		return "GET";
	}

	public Resource getResource() {
		Resource returningResource = null;
		if (this.resourceType != null) {
			// see SlingRequestDispatcher.getMergedRequestPathInfo()
			returningResource = new TypeOverwritingResourceWrapper(this.resource, this.resourceType);
		} else {
			returningResource = getSlingRequest().getResource();
		}
		return returningResource;
	}

	public RequestPathInfo getRequestPathInfo() {
		return rpi;
	}

    
    /*
     * copied from SlingRequestDispatcher.TypeOverwritingResourceWrapper
     */
    private static class TypeOverwritingResourceWrapper extends ResourceWrapper {

        private final String resourceType;

        TypeOverwritingResourceWrapper(Resource delegatee, String resourceType) {
            super(delegatee);
            this.resourceType = resourceType;
        }

        @Override
        public String getResourceType() {
            return resourceType;
        }

        @Override
        public String getResourceSuperType() {
        	/*
        	 * Overwrite this here because the wrapped resource will return null as
        	 * a super type instead of the resource type overwritten here
        	 */
            return null;
        }

        @Override
        public boolean isResourceType(final String resourceType) {
            return this.getResourceResolver().isResourceType(this, resourceType);
        }
    }
}
