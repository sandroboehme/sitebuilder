/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.sitebuilder.internal.ip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.query.Query;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.spi.resource.provider.ResolveContext;
import org.apache.sling.spi.resource.provider.ResourceContext;
import org.apache.sling.spi.resource.provider.ResourceProvider;

/** Test/example ResourceProvider that provides info about
 *  the Solar System's planets at /planets. 
 *  Use /planets.tidy.-1.json to GET the whole thing. 
 *  
 *  This uses the new (February 2016) spi.ResourceProvider base class.
 *  If you need an example based on the old ResourceProvider interface,
 *  see this code at svn revision 1727946.
 */
@Component
@Service(value=ResourceProvider.class)
@Properties({ 
    @Property(name = ResourceProvider.PROPERTY_NAME, value = "InheritedResources"),
    @Property(name = ResourceProvider.PROPERTY_ROOT, value=InheritanceResourceProvider.ROOT)
})
public class InheritanceResourceProvider extends ResourceProvider<Object> {

    public static final String APPS_PATH = "/apps";
    public static final String ROOT = "/apps/inheritedresources";
    public static final String SITEBUILDER_BASE_TYPE = "sling/web-page";
    
    @Override
    public Resource getResource(ResolveContext<Object> ctx,
            String path, ResourceContext resourceContext, Resource parent) {
        // Synthetic resource for our root, so that /planets works
        if((ROOT).equals(path)) {
            return new SyntheticResource(ctx.getResourceResolver(), path, InheritanceResourceProvider.SITEBUILDER_BASE_TYPE);
        }

		String resourceName = path.substring(path.lastIndexOf("/")+1);
        String decodedPath = IProviderResource.decodePath(resourceName);
        Resource origResource = ctx.getResourceResolver().getResource(decodedPath);
//        int lastDot = resourcePath.lastIndexOf('.');
//        StringBuilder selectorElements = new StringBuilder();
//        while (origResource == null && lastDot>0) {
//        	String selectorElementToBeRemoved = resourcePath.substring(lastDot);
//        	resourcePath = resourcePath.substring(0, lastDot);
//        	selectorElements.insert(0, selectorElementToBeRemoved);
//            decodedPath = IProviderResource.decodePath(resourcePath);
//            origResource = ctx.getResourceResolver().getResource(decodedPath);
//            lastDot = resourcePath.lastIndexOf('.');
//        } 
//        Resource resolvedResource = null;
//        if (origResource != null) {
//        	resolvedResource = ctx.getResourceResolver().resolve(decodedPath+selectorElements);
//        }
//        String parentPath = path.substring(0, path.lastIndexOf("/"));
        return origResource == null ? null : new IProviderResource(ctx.getResourceResolver(), path);
//      return origResource == null ? null : new IProviderResource(ctx.getResourceResolver(), parentPath, origResource);
//		return resolvedResource;
//		return resolvedResource == null ? null : new IProviderResource(ctx.getResourceResolver(), decodedPath, resolvedResource);
    }
    
    @Override
    public Iterator<Resource> listChildren(ResolveContext<Object> ctx, Resource parent) {
        if(parent.getPath().startsWith(ROOT)) {
//            // Not the most efficient thing...good enough for this example
//            final List<Resource> kids = new ArrayList<Resource>();
//            for(Map.Entry<String, ValueMap> e : PLANETS.entrySet()) {
//                if(parent.getPath().equals(parentPath(e.getKey()))) {
//                    kids.add(new IProviderResource(parent.getResourceResolver(), e.getKey(), e.getValue()));
//                }
//            }
//            return kids.iterator();
        	String resourceSuperType = parent.getResourceSuperType();
        	String query = "//element(*, nt:base)";
//        	String query = "//element(*, "+InheritanceProvider.SITEBUILDER_BASE_TYPE+")";
        	if (ROOT.equals(parent.getPath())){
        		query = query+"[@sling:resourceSuperType=\""+InheritanceResourceProvider.SITEBUILDER_BASE_TYPE+"\"]";
        	} else if (resourceSuperType != null && !resourceSuperType.trim().equals("")){
        		query = query+"[@sling:resourceSuperType=\""+resourceSuperType+"\"]";
        	} else if (parent instanceof IProviderResource){
        		resourceSuperType = ((IProviderResource) parent).getResourceTypeFromPath();
        		query = query+"[@sling:resourceSuperType=\""+resourceSuperType+"\"]";
        	} else {
        		return null;
        	}
        	Iterator<Resource> children = parent.getResourceResolver().findResources(query, Query.XPATH);
        	return new IProviderResourceIteratorWrapper(ctx.getResourceResolver(), parent.getPath(), children);
        } else {
            return null;
        }
    }
    
    private static String parentPath(String path) {
        final int lastSlash = path.lastIndexOf("/");
        return lastSlash > 0 ? path.substring(0, lastSlash) : "";
    }
    
}