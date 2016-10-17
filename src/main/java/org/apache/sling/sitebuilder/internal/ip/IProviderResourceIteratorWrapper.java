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
package org.apache.sling.sitebuilder.internal.ip;

import java.util.Iterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.ValueMapDecorator;

/**
 * It wraps the resources with the {@link ResourceTypeResourceWrapper} on
 * {@code next()}.
 */
public class IProviderResourceIteratorWrapper implements Iterator<Resource> {

	private Iterator<Resource> iterator;
	private ResourceResolver resourceResolver;
	private String path;

	public IProviderResourceIteratorWrapper(ResourceResolver resourceResolver, String path, Iterator<Resource> iterator) {
		this.resourceResolver = resourceResolver;
		this.path = path;
		this.iterator = iterator;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public Resource next() {
		Resource nextResource = iterator.next();
		return new IProviderResource(resourceResolver, path, nextResource);
	}

	public void remove() {
		iterator.remove();
	}

}
