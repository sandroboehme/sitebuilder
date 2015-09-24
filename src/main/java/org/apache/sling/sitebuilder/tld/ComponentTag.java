package org.apache.sling.sitebuilder.tld;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;

public class ComponentTag extends BodyTagSupport {

	/*
	 * A short and good custom tag docu:
	 * http://java.boot.by/wcd-guide/ch10.html
	 */
	private String componentId = null;
	private static final long serialVersionUID = 7945120677517592501L;
	private Map<String, Object> pageContextAttributes = new HashMap<String, Object>();
	private static final String JSP_PAGE_ATTRIB_PREFIX = "javax.servlet.jsp";
	private static final String ATTRIBUTE_COMPONENT_RESOURCE_PATH = "componentResourcePath";
	private static final String ATTRIBUTE_SCRIPT_STACK = "scriptStack";
	private static final String ATTRIBUTE_COMPONENT_RESOURCE = "componentResource";
	private static final String ATTRIBUTE_COMPONENT_ID = "componentIdAttribute";
	
	private String[] attributesToKeep = new String[]{
			"sling", 
			"resource", 
			"resourceResolver", 
			"slingResponse", 
			"slingRequest",
			"bindings", 
			"currentNode", 
			"log",
			ATTRIBUTE_COMPONENT_RESOURCE_PATH,
			ATTRIBUTE_COMPONENT_RESOURCE,
			ATTRIBUTE_SCRIPT_STACK
	};
	
	@Override
	public int doStartTag() throws JspException { 
		
		savePageContextAttributes();

		removePageContextAttributesExceptSlingAndJSP();

		setSitebuilderAttributes();

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() {
		removeAllPageContextAttributes();
		restorePageContextAttributes();
		return EVAL_PAGE;
	}
		
	private Resource getResource(){
		return (Resource) this.pageContext.getAttribute("resource");
	}

	protected ResourceResolver getResourceResolver() {
		final SlingBindings bindings = (SlingBindings) pageContext.getRequest()
				.getAttribute(SlingBindings.class.getName());
		final SlingScriptHelper scriptHelper = bindings.getSling();
		final ResourceResolver resolver = scriptHelper.getRequest()
				.getResourceResolver();
		return resolver;
	}

	private void setSitebuilderAttributes() {
		String componentResourcePath = this.getResource().getPath()+"/"+this.componentId;
		this.pageContext.setAttribute(ATTRIBUTE_COMPONENT_ID, "data-component-id=\""+this.componentId+"\"");
		this.pageContext.setAttribute(ATTRIBUTE_COMPONENT_RESOURCE_PATH, componentResourcePath);
		Resource componentResource = getResourceResolver().getResource(componentResourcePath);
		this.pageContext.setAttribute(ATTRIBUTE_COMPONENT_RESOURCE,componentResource);
	}

	private void removePageContextAttributesExceptSlingAndJSP() {
		Set<String> attributesToKeepSet = new HashSet<String>();
		attributesToKeepSet.addAll(Arrays.asList(attributesToKeep));
		Set<String> attributes = this.pageContextAttributes.keySet();
		for (String attributeName : attributes) {
			boolean noJSPAttrib = !attributeName.startsWith(JSP_PAGE_ATTRIB_PREFIX);
			boolean noSlingAttrib = !attributesToKeepSet.contains(attributeName);
			if (noJSPAttrib && noSlingAttrib){
				this.pageContext.removeAttribute(attributeName);
			}
		}
	}

	private void removeAllPageContextAttributes() {
		Set<String> attributeNamesSet = new HashSet<String>();
		Enumeration<?> attributeNames = this.pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
		while (attributeNames.hasMoreElements()) {
			String attributeName = (String) attributeNames.nextElement();
			attributeNamesSet.add(attributeName);
		}
		for (String attributeName : attributeNamesSet) {
			this.pageContext.removeAttribute(attributeName);
		}
	}

	private void savePageContextAttributes() {
		Enumeration<?> attributeNames = this.pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);
		while (attributeNames.hasMoreElements()) {
			String attributeName = (String) attributeNames.nextElement();
			pageContextAttributes.put(attributeName, this.pageContext.getAttribute(attributeName));
		}
	}

	private void restorePageContextAttributes() {
		Set<String> pageContextAttributeNames = this.pageContextAttributes.keySet();
		for (String pageContextAttributeName : pageContextAttributeNames) {
			this.pageContext.setAttribute(pageContextAttributeName, this.pageContextAttributes.get(pageContextAttributeName));
		}
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
}
