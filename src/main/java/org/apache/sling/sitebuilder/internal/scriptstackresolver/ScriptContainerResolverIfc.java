package org.apache.sling.sitebuilder.internal.scriptstackresolver;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 *
 */
public interface ScriptContainerResolverIfc {

	/**
	 * @param scriptIdStackString
	 * @param request
	 * @param removeExtension
	 * @return
	 */
	public ScriptContainer resolve(String scriptIdStackString, SlingHttpServletRequest request, boolean removeExtension);

	/**
	 * @param request
	 * @param removeExtension
	 * @return
	 */
	public String getGETMethodScriptPath(SlingHttpServletRequest request, boolean removeExtension);

}