/*
 * Licensed to Marvelution under one or more contributor license 
 * agreements.  See the NOTICE file distributed with this work 
 * for additional information regarding copyright ownership.
 * Marvelution licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.marvelution.jira.plugins.hudson.servlets.query;

import java.net.URI;

import com.marvelution.hudson.plugins.apiv2.client.services.Query;
import com.marvelution.hudson.plugins.apiv2.client.services.QueryType;

/**
 * Wrapper for the {@link Query}
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 */
public class QueryWrapper implements Query<WrapperModel> {

	private final URI uri;
	private String acceptHeader;

	/**
	 * Constructor
	 * 
	 * @param uri the {@link URI}
	 */
	public QueryWrapper(URI uri) {
		this.uri = uri;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUrl() {
		return uri.getPath() + "?" + (uri.getQuery() != null ? uri.getQuery() : "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<WrapperModel> getModelClass() {
		return WrapperModel.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T getData() {
		// Default implementation for the Queries. Most don't need this as they are GET Queries
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryType getQueryType() {
		return QueryType.GET;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAcceptHeader() {
		return acceptHeader;
	}

	/**
	 * Setter for the Accept Header
	 * 
	 * @param acceptHeader the Accept Header
	 */
	public void setAcceptHeader(String acceptHeader) {
		this.acceptHeader = acceptHeader;
	}

}
