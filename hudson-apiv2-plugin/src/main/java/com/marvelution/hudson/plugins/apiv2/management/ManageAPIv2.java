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

package com.marvelution.hudson.plugins.apiv2.management;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.apache.commons.lang.time.FastDateFormat;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.marvelution.hudson.plugins.apiv2.APIv2Plugin;
import com.marvelution.hudson.plugins.apiv2.cache.activity.ActivitiesCache;
import com.marvelution.hudson.plugins.apiv2.cache.issue.BuildId;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueCache;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueKey;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssuesCache;
import com.marvelution.hudson.plugins.apiv2.resources.model.HudsonSystem;
import com.marvelution.hudson.plugins.apiv2.threads.ActivityIndexingThread;
import com.marvelution.hudson.plugins.apiv2.threads.IssueIndexingThread;
import com.marvelution.hudson.plugins.apiv2.utils.HudsonPluginUtils;

import hudson.Extension;
import hudson.Util;
import hudson.model.ManagementLink;
import hudson.model.Hudson;
import hudson.model.User;

/**
 * {@link ManagementLink} implementation for Cache management
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
@Extension
public class ManageAPIv2 extends ManagementLink {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIconFileName() {
		return "gear2.png";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUrlName() {
		return getUrl();
	}

	public static String getUrl() {
		return "manageAPIv2";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayName() {
		return "Manage APIv2 Caches";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "Clear, rebuild the caches maintained by the APIv2 plugin";
	}

	/**
	 * Getter for the {@link FastDateFormat}
	 * 
	 * @return the {@link FastDateFormat}
	 */
	public FastDateFormat getDateFormatter() {
		return Util.RFC822_DATETIME_FORMATTER;
	}

	/**
	 * Check if the given username is an actual user
	 * 
	 * @param username the username to check
	 * @return <code>true</code> if the user is not the system user and if the user exists in Hudson,
	 *         <code>false</code> otherwise
	 */
	public boolean isActualUser(String username) {
		return !getSystem().getSystemUser().getUserId().equals(username) && User.get(username, false) != null;
	}

	/**
	 * Getter for the current {@link HudsonSystem}
	 * 
	 * @return the current {@link HudsonSystem}
	 */
	public HudsonSystem getSystem() {
		return HudsonPluginUtils.getHudsonSystem();
	}

	/**
	 * Getter for the {@link IssuesCache}
	 * 
	 * @return the {@link IssuesCache}
	 */
	public Map<BuildId, Set<IssueKey>> getIssuesCache() {
		Map<BuildId, Set<IssueKey>> issues = Maps.newHashMap();
		for (IssueCache cache : APIv2Plugin.getIssuesCache()) {
			if (issues.containsKey(cache.getBuildId())) {
				issues.get(cache.getBuildId()).add(cache.getIssueKey());
			} else {
				issues.put(cache.getBuildId(), Sets.newHashSet(cache.getIssueKey()));
			}
		}
		return issues;
	}

	/**
	 * Getter for the {@link ActivitiesCache}
	 * 
	 * @return the {@link ActivitiesCache}
	 */
	public ActivitiesCache getActivitiesCache() {
		return APIv2Plugin.getActivitiesCache();
	}

	/**
	 * Web method to trigger a full index
	 * 
	 * @param req the {@link StaplerRequest}
	 * @param rsp the {@link StaplerResponse}
	 * @throws IOException in case of IO errors
	 * @throws ServletException in case of Servlet errors
	 */
	public void doIssueIndex(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		IssueIndexingThread.invoke();
		rsp.sendRedirect2(".");
	}

	/**
	 * Web method to clear the Issues Cache
	 * 
	 * @param req the {@link StaplerRequest}
	 * @param rsp the {@link StaplerResponse}
	 * @throws IOException in case of IO errors
	 * @throws ServletException in case of Servlet errors
	 */
	public void doClearIssueCache(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		APIv2Plugin.getIssuesCache().clear();
		rsp.sendRedirect2(".");
	}

	/**
	 * Web method to trigger a full index of the Activity cache
	 * 
	 * @param req the {@link StaplerRequest}
	 * @param rsp the {@link StaplerResponse}
	 * @throws IOException in case of IO errors
	 * @throws ServletException in case of Servlet errors
	 */
	public void doActivityIndex(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		ActivityIndexingThread.invoke();
		rsp.sendRedirect2("./activities");
	}

	/**
	 * Web method to clear the Activities Cache
	 * 
	 * @param req the {@link StaplerRequest}
	 * @param rsp the {@link StaplerResponse}
	 * @throws IOException in case of IO errors
	 * @throws ServletException in case of Servlet errors
	 */
	public void doClearActivityCache(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
		APIv2Plugin.getActivitiesCache().clear();
		rsp.sendRedirect2("./activities");
	}

}
