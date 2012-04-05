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

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.marvelution.hudson.plugins.apiv2.APIv2Plugin;
import com.marvelution.hudson.plugins.apiv2.cache.activity.ActivityCache;
import com.marvelution.hudson.plugins.apiv2.cache.activity.ActivityCachePredicates;
import com.marvelution.hudson.plugins.apiv2.cache.activity.BuildActivityCache;
import com.marvelution.hudson.plugins.apiv2.utils.BuildUtils;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AsyncPeriodicWork;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.TaskListener;

/**
 * {@link AsyncPeriodicWork} implementation to (re)index all known JIRA issue keys
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
@Extension
public class ActivityIndexingThread extends AsyncPeriodicWork {

	private static final Logger LOGGER = Logger.getLogger(ActivityIndexingThread.class.getSimpleName());
	private static ActivityIndexingThread INSTANCE;

	/**
	 * Constructor
	 */
	public ActivityIndexingThread() {
		super("Activity Indexing");
		INSTANCE = this;
	}

	/**
	 * Execute the {@link ActivityIndexingThread}
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
		List<ActivityCache> outdated = Lists.newArrayList();
		outdated.addAll(Collections2.filter(APIv2Plugin.getActivitiesCache(),
			ActivityCachePredicates.isBuildActivity()));
		List<ActivityCache> updated = Lists.newArrayList();
		// Check all the JobActivities
		for (ActivityCache cache : Collections2.filter(APIv2Plugin.getActivitiesCache(),
				ActivityCachePredicates.isJobActivity())) {
			if (Hudson.getInstance().getItemByFullName(cache.getJob()) == null) {
				outdated.add(cache);
			}
		}
		// Check all the BuildActivities
		for (Job<?, ? extends AbstractBuild<?, ?>> job : Hudson.getInstance().getAllItems(Job.class)) {
			LOGGER.info("Processing job: " + job.getFullName());
			for (AbstractBuild<?, ?> build : job.getBuilds()) {
				BuildActivityCache newCache = new BuildActivityCache(build.getTimeInMillis(), job.getFullName(), build.getNumber());
				newCache.setCulprit(BuildUtils.getCulpritFromRunnable(build));
				updated.add(newCache);
			}
		}
		// First remove the outdated cache
		APIv2Plugin.getActivitiesCache().removeAll(outdated);
		// And then add the updated cache
		APIv2Plugin.getActivitiesCache().addAll(updated);
	}

}
