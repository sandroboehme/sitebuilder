package org.apache.sling.sitebuilder.internal;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.AccessDeniedException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

public class ResourceUtils {

	public static void copy(ResourceResolver resourceResolver, String sourceResourcePath, String targetParentResourcePath,
			String targetResourceName) throws PersistenceException {
		Resource sourceResource = resourceResolver.getResource(sourceResourcePath);
		Resource targetParentResource = resourceResolver.getResource(targetParentResourcePath);
		ResourceUtils.copy(resourceResolver, sourceResource, targetParentResource, targetResourceName);
	}
		

	public static void copy(ResourceResolver resourceResolver, Resource sourceResource, Resource targetParentResource,
			String targetResourceName) throws PersistenceException {
		try {
			Map<String, Object> sourceResourceProperties = sourceResource.adaptTo(ValueMap.class);
			Map<String, Object> newResourceProperties = new HashMap<>(sourceResourceProperties);
			//TODO: Copy only non-protected properties.
			newResourceProperties.remove("jcr:uuid");
			resourceResolver.create(targetParentResource, targetResourceName, newResourceProperties);
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
}
