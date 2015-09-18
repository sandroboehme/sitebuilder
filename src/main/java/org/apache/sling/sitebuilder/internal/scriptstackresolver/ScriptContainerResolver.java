package org.apache.sling.sitebuilder.internal.scriptstackresolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.Servlet;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolver;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.osgi.framework.Constants;


@Component(immediate = true, metatype = true, label = "ScriptContainerResolver", description = "Resolves data nessecary for script containers")
@Service(value={ScriptContainerResolverIfc.class})
@Properties( {
    @Property(name = Constants.SERVICE_VENDOR, value = "The Apache Software Foundation"),
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Sling Servlet")
})
@References( {
    @Reference(name = "ServletResolver", referenceInterface = ServletResolver.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC, bind = "setServletResolver", unbind = "unsetServletResolver")
})
public class ScriptContainerResolver implements ScriptContainerResolverIfc  {
	private ServletResolver servletResolver;
	
	/* (non-Javadoc)
	 * @see org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainerResolverIfc#resolve(java.lang.String, org.apache.sling.api.SlingHttpServletRequest, boolean)
	 */
	@Override
	public ScriptContainer resolve(String scriptIdStackString, SlingHttpServletRequest request, boolean removeExtension) {
		ScriptContainer resolvedScriptContainer = new ScriptContainer();
        ResourceResolver resourceResolver = request.getResourceResolver();
        
		String mainScriptPath = getGETMethodScriptPath(request, removeExtension);
        Resource scriptResource = resourceResolver.getResource(mainScriptPath);
        
        resolvedScriptContainer.setScriptResource(scriptResource);
        Resource resource = request.getResource();
		if (StringUtils.isNotBlank(scriptIdStackString)){
			String[] scriptIdStackArray = scriptIdStackString.split("_");
			Iterator<String> scriptIdStack = Arrays.asList(scriptIdStackArray).iterator();
			while (scriptIdStack.hasNext()) {
				InputStream scriptResourceInputStream = scriptResource.adaptTo(InputStream.class);
				Source jspSource;
				try {
					jspSource = new Source(scriptResourceInputStream);
					Servlet servlet = null;
					String nextId = scriptIdStack.next();
					Element sourceElement = jspSource.getFirstElement("data-component-id", nextId, true);
					String scriptContainerType = sourceElement.getAttributeValue("data-component-type");
					if ("sling-call".equals(scriptContainerType)) {
						Element slingCallElement = sourceElement.getFirstElement("sling:call");
						String scriptName = slingCallElement.getAttributeValue("script");
						servlet = servletResolver.resolveServlet(resource, scriptName);
				        resolvedScriptContainer.setResource(resource);
					} else if ("sling-include".equals(scriptContainerType)){
						Element slingIncludeElement = sourceElement.getFirstElement("sling:include");
						String path = slingIncludeElement.getAttributeValue("path");
						String resourceType = slingIncludeElement.getAttributeValue("resourceType");
						String replaceSelectors = slingIncludeElement.getAttributeValue("replaceSelectors");
						String addSelectors = slingIncludeElement.getAttributeValue("addSelectors");
						String replaceSuffix = slingIncludeElement.getAttributeValue("replaceSuffix");

						// see SlingRequestDispatcher.dispatch()
						String absPath = getAbsolutePath(request, path);
			            Resource includeResource = resourceResolver.resolve(absPath);
				        resolvedScriptContainer.setResource(includeResource);

				        SlingHttpServletRequest scriptResolvingRequest = null;
				        if (StringUtils.isNotBlank(resourceType)){
				        	scriptResolvingRequest = new ScriptContainerResolvingRequestWrapper(request, includeResource, resourceType, removeExtension);
				        } else {
				        	scriptResolvingRequest = new ScriptContainerResolvingRequestWrapper(request, includeResource, new ScriptContainerResolvingPathInfo(request, includeResource, replaceSelectors, addSelectors, replaceSuffix, removeExtension));				        	
				        }
				        // see SlingRequestProcessorImpl.dispatchRequest()
				        servlet = servletResolver.resolveServlet(scriptResolvingRequest);
				        
				        // The script on the next level of the scriptId stack will belong to the `includeResource`
				        resource = includeResource;
					}
					if (servlet != null) {
						String scriptPath = RequestUtil.getServletName(servlet);
						scriptResource = resourceResolver.getResource(scriptPath);
				        resolvedScriptContainer.setScriptResource(scriptResource);
					} else {
						// TODO handle error
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return resolvedScriptContainer;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.sling.sitebuilder.internal.scriptstackresolver.ScriptContainerResolverIfc#getGETMethodScriptPath(org.apache.sling.api.SlingHttpServletRequest, boolean)
	 */
	@Override
	public String getGETMethodScriptPath(final SlingHttpServletRequest request, boolean removeExtension) {
		SlingHttpServletRequestWrapper scriptResolvingRequest = new ScriptContainerResolvingRequestWrapper(request, removeExtension);
		
		String scriptPath = null;
        Servlet servlet = servletResolver.resolveServlet(scriptResolvingRequest);
        if (servlet != null) {
        	scriptPath = RequestUtil.getServletName(servlet);
        }
        return scriptPath;
	}

	/*
	 * Copied from SlingRequestDispatcher
	 */
    private String getAbsolutePath(SlingHttpServletRequest request, String path) {
        // path is already absolute
        if (path.startsWith("/")) {
            return path;
        }

        // get parent of current request
        String uri = request.getResource().getPath();
        int lastSlash = uri.lastIndexOf('/');
        if (lastSlash >= 0) {
            uri = uri.substring(0, lastSlash);
        }

        // append relative path to parent
        return uri + '/' + path;
    }
    
    protected void setServletResolver(final ServletResolver servletResolver) {
		this.servletResolver = servletResolver;
    }

    protected void unsetServletResolver(final ServletResolver servletResolver) {
		this.servletResolver = null;
    }
}
