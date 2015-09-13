/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.sitebuilder.internal.resource;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceWrapper;

/**
 * This ResourceWrapper adds a marker to the ResourceMetadata object that the {@link ScriptnameResourceDecorator}
 * uses.
 */
class ResourceTypeResourceWrapper extends ResourceWrapper {

	private static final String RESOURCE_TYPE = "sling/pageEditor";

	public ResourceTypeResourceWrapper(Resource resource) {
		super(resource);
	}

	@Override
	public ResourceMetadata getResourceMetadata() {
		// Overwriting to get rid of the error "JcrNodeResourceMetadata is locked (500)"
		ResourceMetadata newResourceMetadata = new ResourceMetadata();
		newResourceMetadata.putAll(super.getResourceMetadata());
		newResourceMetadata.put(PageEditorResourceProvider.PAGE_EDITOR_PROVIDER_RESOURCE, null);
		newResourceMetadata.put(PageEditorResourceProvider.PAGE_EDITOR_ORIGINAL_RESOURCE_TYPE, super.getResourceType());
		return newResourceMetadata;
	}
	
	@Override
	public String getResourceType(){
		return ResourceTypeResourceWrapper.RESOURCE_TYPE;
	}

	@Override
    public Iterator<Resource> listChildren() {
		return new ArrayList<Resource>().iterator();
	}
}
