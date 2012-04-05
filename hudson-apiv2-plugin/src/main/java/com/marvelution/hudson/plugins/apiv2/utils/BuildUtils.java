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

package com.marvelution.hudson.plugins.apiv2.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Run;

import com.marvelution.hudson.plugins.apiv2.dozer.utils.DozerUtils;
import com.marvelution.hudson.plugins.apiv2.resources.model.build.triggers.ProjectTrigger;
import com.marvelution.hudson.plugins.apiv2.resources.model.build.triggers.Trigger;
import com.marvelution.hudson.plugins.apiv2.resources.model.build.triggers.UserTrigger;

/**
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
public class BuildUtils {

	private static final Logger LOGGER = Logger.getLogger(BuildUtils.class.getSimpleName());

	/**
	 * Get the culprit of the execution of the runnable given
	 * 
	 * @param run the runnable to get the culprit from
	 * @return the culprit
	 */
	public static String getCulpritFromRunnable(Run<?, ?> run) {
		if (run != null && run.getCauses() != null) {
			LOGGER.log(Level.FINE, run.toString() + " has causes, locate the UserTrigger or ProjectTrigger");
			for (Object cause : run.getCauses()) {
				Trigger trigger = DozerUtils.getMapper().map(cause, Trigger.class);
				if (trigger != null) {
					if (trigger instanceof UserTrigger) {
						LOGGER.log(Level.FINE, "Located the UserTrigger " + trigger);
						return ((UserTrigger) trigger).getUsername();
					} else if (trigger instanceof ProjectTrigger) {
						LOGGER.log(Level.FINE, "Located the ProjectTrigger " + trigger);
						Job<?, ?> job =
							Hudson.getInstance().getItemByFullName(((ProjectTrigger) trigger).getName(), Job.class);
						if (job != null) {
							return getCulpritFromRunnable(job.getBuildByNumber(((ProjectTrigger) trigger)
								.getBuildNumber()));
						}
					}
				}
			}
		}
		if (run != null && !(run.getParent().getParent() instanceof Hudson)
				&& run.getParent().getParent() instanceof Job) {
			LOGGER.log(Level.FINE, run.toString() + " has no causes try its parent " + run.getParent().getParent().getFullName());
			return getCulpritFromRunnable(((Job<?, ?>) run.getParent().getParent()).getBuildByNumber(run.getNumber()));
		}
		LOGGER.log(Level.FINE, "Unable to locate the culprit for runnable: " + run);
		return HudsonPluginUtils.getHudsonSystem().getSystemUser().getUserId();
	}
}
