package org.apache.sling.sitebuilder.internal.scriptstackresolver;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;

public class ScriptContainerResolvingPathInfo implements RequestPathInfo {

	private String suffix;
	private Resource includeResource;
	private String resourcePath;
	private String selectorString;
	private String[] selectors;
	private RequestPathInfo rpi;
	private boolean removeExtension;
	private String originalExtension;

	public ScriptContainerResolvingPathInfo(SlingHttpServletRequest request, boolean removeExtension){
		this.removeExtension = removeExtension;
		init(request);
	}
	
	public ScriptContainerResolvingPathInfo(SlingHttpServletRequest request, String resourcePath, boolean removeExtension){
		this.removeExtension = removeExtension;
		this.resourcePath = resourcePath;
		init(request);
	}
	
	public ScriptContainerResolvingPathInfo(SlingHttpServletRequest request, Resource includeResource, String replaceSelectors, String addSelectors, String replaceSuffix, boolean removeExtension) {
		this.removeExtension = removeExtension;
		this.includeResource = includeResource;
		
		// see SlingRequestDispatcher.getMergedRequestPathInfo() and public SlingRequestPathInfo(Resource r)
		resourcePath = includeResource.getResourceMetadata().getResolutionPath();

		// see SlingRequestPathInfo.merge(options)
		if (replaceSelectors != null) {
			this.selectorString = replaceSelectors;
		}
		
        if (addSelectors != null) {
            if (this.selectorString != null) {
            	this.selectorString += "." + addSelectors;
            } else {
            	this.selectorString = addSelectors;
            }
        }
        
        if (replaceSuffix != null) {
            this.suffix = replaceSuffix;
        }
        
        this.selectors = (selectorString != null) ? selectorString.split("\\.") : new String[0];
        init(request);
	}
	
	private void init(SlingHttpServletRequest request) {
		this.rpi = request.getRequestPathInfo();
		if (this.removeExtension){
	    	String url = request.getRequestURI();
	    	int lastDot = url.lastIndexOf(".");
	    	int dotBeforeLast = url.lastIndexOf(".", lastDot-1);
	    	originalExtension = url.substring(dotBeforeLast+1, lastDot);
		}
	}

	@Override
	public String getSelectorString() {
		return this.selectorString != null ? this.selectorString : rpi.getSelectorString();
	}

	@Override
	public String[] getSelectors() {
		return this.selectors != null ? this.selectors : rpi.getSelectors();
	}

	@Override
	public String getSuffix() {
		return this.suffix != null ? this.suffix : rpi.getSuffix();
	}

	@Override
    public Resource getSuffixResource() {
		// see SlingRequestPathInfo.getSuffixResource();
        if (this.suffix != null) {
            return includeResource.getResourceResolver().getResource(this.suffix);
        }
        return null;
    }
	
	@Override
	public String getResourcePath() {
		return this.resourcePath != null ? this.resourcePath : rpi.getResourcePath();
	}
	
	@Override
	public String getExtension() {
		return this.removeExtension ? this.originalExtension : this.rpi.getExtension();
	}
}
