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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.query.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.adapter.annotations.Adaptable;
import org.apache.sling.adapter.annotations.Adapter;
import org.apache.sling.api.resource.AbstractResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

/** A Sling Resource that represents a planet */
@Adaptable(adaptableClass=Resource.class, adapters={
    @Adapter({ValueMap.class})
})
public class IProviderResource extends AbstractResource implements Resource {

    private final String path;
    private final ResourceMetadata metadata;
    private final ResourceResolver resolver;

//    public static final Map<String, Object> DUMMY_VALUEMAP = ;
//    static {
//    	DUMMY_VALUEMAP.put("k1", "v1");
//    	DUMMY_VALUEMAP.put("k2", "v2");
//    	DUMMY_VALUEMAP.put("k3", "v3");
//    }
    
//    public static final String RESOURCE_TYPE = "sling/test-services/planet";
    
//    public IProviderResource(ResourceResolver resolver, String origPath, String path, ValueMap valueMap) {
//		this.path = path;
//                
//        this.valueMap = valueMap;
//        this.resolver = resolver;
//        
//        metadata = new ResourceMetadata();
//        metadata.setResolutionPath(path);
//    }

	public IProviderResource(ResourceResolver resourceResolver, String parentPath, Resource origResource) {
		this.path = parentPath+ "/"+IProviderResource.encodePath(origResource.getPath());
                
        this.resolver = resourceResolver;
        
        metadata = new ResourceMetadata();
        metadata.setResolutionPath(parentPath);
	}

	public IProviderResource(ResourceResolver resourceResolver, String absPath) {
		this.path = absPath;
                
        this.resolver = resourceResolver;
        
        metadata = new ResourceMetadata();
        metadata.setResolutionPath(absPath);
	}

	public static String encodePath(String path) {
		// escape all underscores in the path with a hyphen
		String encodedPath = path.replace("_", "-_");
		
		// reverse the order of the path elements
		String[] encodedPathElements = encodedPath.split("/");
		List<String> encodedPathElementsList = Arrays.asList(encodedPathElements);
		Collections.reverse(encodedPathElementsList);
		encodedPath = StringUtils.join(encodedPathElementsList, "_");
		// change all forward slashes to underscores
		return encodedPath;
	}

	public static String decodePath(String name){
		/*
		 * It's a negative lookbehind.
		 * It matches a underscore that is not preceded by the "-" (escape character). 
		 * It doesn't match "-_", but matches the "_" (and only the "_") especially in "_apps" but also in "apps_". 
		 * 
		 * A negated character class like [^-]_ does not work as it does not mean "_" not preceded by the "-". 
		 * It means an "_" preceded by a character that is not a "-". It does not match "_apps".
		 * see: http://www.regular-expressions.info/lookaround.html
		 */
		String regex = "(?<!-)_";
		String decodedPath = name.replaceAll(regex, "/");
		

		// reverse the order of the path elements
		String[] decodedPathElements = decodedPath.split("/");
		List<String> decodedPathElementsList = Arrays.asList(decodedPathElements);
		Collections.reverse(decodedPathElementsList);
		decodedPath = StringUtils.join(decodedPathElementsList, "/");
		
		// unescape the underscores
		decodedPath = decodedPath.replace("-_", "_");
		decodedPath = name.endsWith("_") ? "/"+decodedPath : decodedPath;
		return decodedPath;
	}
	
    @Override
	public Iterator<Resource> listChildren() {
        if(this.getPath().startsWith(InheritanceResourceProvider.ROOT)) {
//          // Not the most efficient thing...good enough for this example
//          final List<Resource> kids = new ArrayList<Resource>();
//          for(Map.Entry<String, ValueMap> e : PLANETS.entrySet()) {
//              if(parent.getPath().equals(parentPath(e.getKey()))) {
//                  kids.add(new IProviderResource(parent.getResourceResolver(), e.getKey(), e.getValue()));
//              }
//          }
//          return kids.iterator();
      	String resourceSuperType = this.getResourceTypeFromPath();
      	String query = "//element(*, nt:base)[@sling:resourceSuperType=\""+resourceSuperType+"\"]";
      	Iterator<Resource> children = resolver.findResources(query, Query.XPATH);
      	return new IProviderResourceIteratorWrapper(this.resolver, this.getPath(), children);
      } else {
          return null;
      }
	}

	@Override
    public String toString() {
        return getClass().getSimpleName() + " " + path;
    }

	@Override
    public String getPath() {
        return path;
    }

	@Override
    public String getName() {
		String name = this.getPath().substring(this.getPath().lastIndexOf("/")+1);
        return name;
    }

    public String getResourceTypeFromPath() {
    	String originalPath = IProviderResource.decodePath(this.getName());
		String resourceTypeFromPath = originalPath.substring(InheritanceResourceProvider.APPS_PATH.length()+1);
		return resourceTypeFromPath;
    }

	@Override
    public ResourceMetadata getResourceMetadata() {
        return metadata;
    }

    public ResourceResolver getResourceResolver() {
        return resolver;
    }

	@Override
    public String getResourceType() {
//        return RESOURCE_TYPE;
		return getResourceTypeFromPath();
    }
	@Override
	public String getResourceSuperType() {
		// TODO Auto-generated method stub
		return null;
	}
    
    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if(type == ValueMap.class) {
            ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>());
        	String originalPath = IProviderResource.decodePath(this.getName());
        	Resource origResource = this.resolver.getResource(originalPath);
            Iterator<Resource> childResources = origResource.listChildren();
            while (childResources.hasNext()){
            	Resource childResource = childResources.next();
            	if ("nt:file".equals(childResource.getResourceType())){
            		valueMap.put(childResource.getName(), childResource.getName());
            	}
            }
            
            return (AdapterType)valueMap;
        }
        return super.adaptTo(type);
    }

}