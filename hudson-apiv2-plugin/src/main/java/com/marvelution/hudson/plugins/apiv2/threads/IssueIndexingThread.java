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

package com.marvelution.hudson.plugins.apiv2.threads;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Lists;
import com.marvelution.hudson.plugins.apiv2.APIv2Plugin;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueCache;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueKey;
import com.marvelution.hudson.plugins.apiv2.utils.JiraKeyUtils;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AsyncPeriodicWork;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

/**
 * {@link AsyncPeriodicWork} implementation to (re)index all known JIRA issue keys
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
@Extension
public class IssueIndexingThread extends AsyncPeriodicWork {

	private static final Logger LOGGER = Logger.getLogger(IssueIndexingThread.class.getSimpleName());
	private static IssueIndexingThread INSTANCE;

	/**
	 * Constructor
	 */
	public IssueIndexingThread() {
		super("Issue Indexing");
		INSTANCE = this;
	}

	/**
	 * Execute the {@link IssueIndexingThread}
	 */
	public static void invoke() {
		INSTANCE.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getRecurrencePeriod() {
		return DAY * 7;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute(TaskListener taskListener) throws IOException, InterruptedException {
		List<IssueCache> newCache = Lists.newArrayList();
		for (Job<?, ? extends AbstractBuild<?, ?>> job : Hudson.getInstance().getAllItems(Job.class)) {
			LOGGER.info("Processing job: " + job.getFullName());
			for (AbstractBuild<?, ?> build : job.getBuilds()) {
				for (Entry entry : (ChangeLogSet<? extends Entry>) build.getChangeSet()) {
					for (String key : JiraKeyUtils.getJiraIssueKeysFromText(entry.getMsg(),
							APIv2Plugin.getIssuesCache().getIssueKeyPattern())) {
						try {
							newCache.add(new IssueCache(IssueKey.getIssueKey(key), job.getFullName(),
								build.getNumber()));
						} catch (Exception e) {
							// Ignore this
						}
					}
				}
			}
		}
		// First clear the current cache
		APIv2Plugin.getIssuesCache().clear();
		// And then add the new cache
		APIv2Plugin.getIssuesCache().addAll(newCache);
	}

}
