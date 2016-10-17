package org.apache.sling.sitebuilder.tld;

public class ComponentTag2 extends org.apache.sling.scripting.jsp.taglib.IncludeTagHandler{

	private static final long serialVersionUID = 7945120677517592501L;
	
	public void setComponentId(String componentId) {
    	super.setPath(componentId);
    }
    
}
