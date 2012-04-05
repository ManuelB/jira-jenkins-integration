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

package com.marvelution.hudson.plugins.apiv2.cache.issue;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
public class BuildId {

	private final String job;
	private final int build;

	/**
	 * Constructor
	 *
	 * @param job
	 * @param build
	 */
	public BuildId(String job, int build) {
		this.job = job;
		this.build = build;
	}

	/**
	 * Getter for job
	 *
	 * @return the job
	 */
	public String getJob() {
		return job;
	}

	/**
	 * Getter for build
	 *
	 * @return the build
	 */
	public int getBuild() {
		return build;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof BuildId) {
			BuildId other = (BuildId) object;
			return getJob().equals(other.getJob()) && getBuild() == other.getBuild();
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getJob() + " #" + getBuild();
	}

}
