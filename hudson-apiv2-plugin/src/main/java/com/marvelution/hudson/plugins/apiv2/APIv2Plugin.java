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

package com.marvelution.hudson.plugins.apiv2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.collect.Lists;
import com.marvelution.hudson.plugins.apiv2.cache.activity.ActivitiesCache;
import com.marvelution.hudson.plugins.apiv2.cache.activity.BuildActivityCache;
import com.marvelution.hudson.plugins.apiv2.cache.activity.JobActivityCache;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueCache;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssueKey;
import com.marvelution.hudson.plugins.apiv2.cache.issue.IssuesCache;
import com.marvelution.hudson.plugins.apiv2.servlet.filter.HudsonAPIV2ServletFilter;
import com.marvelution.hudson.plugins.apiv2.utils.JiraKeyUtils;
import com.thoughtworks.xstream.XStream;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.model.Descriptor.FormException;
import hudson.util.CopyOnWriteList;
import hudson.util.FormValidation;
import hudson.util.PluginServletFilter;

/**
 * Main Plugin implementation of the Hudson REST plugin.
 * This class is responsible for the registration of the
 * <a href="http://incubator.apache.org/wink/">Wink Application</a> implementation
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 * @plugin
 */
public class APIv2Plugin extends Plugin {

	private static final Logger LOGGER = Logger.getLogger(APIv2Plugin.class.getName());
	private static final XStream XSTREAM = new XStream();
	private static final String APIV2_DIRECTORY_NAME = "APIv2";
	private static final String ACTIVITIES_CACHE_FILE = "activities-cache.xml";
	private static final String ISSUES_CACHE_FILE = "issues-cache.xml";
	private static final String APIV2_ISSUE_KEY_PATTERN = "apiv2.issue.key.pattern";
	private static final String APIV2_PATTERN_KEY = "apiv2.pattern";

	private static APIv2Plugin plugin;

	private transient List<Filter> filters = Lists.newArrayList();
	private transient ActivitiesCache activitiesCache = new ActivitiesCache();
	private transient IssuesCache issuesCache = new IssuesCache();
	private final CopyOnWriteList<String> patterns = new CopyOnWriteList<String>();

	/**
	 * {@inheritDoc}
	 */
	public void start() throws Exception {
		plugin = this;
		load();
		LOGGER.info("Adding the APIv2 Filters");
		filters.add(new HudsonAPIV2ServletFilter());
		for (Filter filter : filters) {
			PluginServletFilter.addFilter(filter);
		}
		LOGGER.info("Loading the Activity Cache");
		File activityCacheFile = getFile(ACTIVITIES_CACHE_FILE);
		if (activityCacheFile.exists()) {
			try {
				activitiesCache = (ActivitiesCache) XSTREAM.fromXML(new FileInputStream(activityCacheFile));
			} catch (Exception e) {
				LOGGER
					.severe("Failed to load the activity-cache.xml and will thus not be available for the REST APIs");
			}
		}
		LOGGER.info("Loading the Issues Cache");
		File issuesCacheFile = getFile(ISSUES_CACHE_FILE);
		if (issuesCacheFile.exists()) {
			try {
				issuesCache = (IssuesCache) XSTREAM.fromXML(new FileInputStream(issuesCacheFile));
			} catch (Exception e) {
				LOGGER.severe("Failed to load the issue-cache.xml and will thus not be available for the REST APIs");
			}
		}
		if (StringUtils.isBlank(issuesCache.getIssueKeyRegex())) {
			// Make sure the pattern is set
			issuesCache.setIssueKeyRegex(JiraKeyUtils.DEFAULT_JIRA_ISSUE_KEY_PATTERN.pattern());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() throws Exception {
		LOGGER.info("Removing the APIv2 Filters");
		for (Filter filter : filters) {
			PluginServletFilter.removeFilter(filter);
		}
		filters.clear();
		LOGGER.info("Storing the Activity Cache");
		XSTREAM.toXML(activitiesCache, new FileOutputStream(getFile(ACTIVITIES_CACHE_FILE)));
		LOGGER.info("Storing the Issue Cache");
		XSTREAM.toXML(issuesCache, new FileOutputStream(getFile(ISSUES_CACHE_FILE)));
		save();
		plugin = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException,
					FormException {
		if (req.hasParameter(APIV2_ISSUE_KEY_PATTERN)
				&& StringUtils.isNotBlank(req.getParameter(APIV2_ISSUE_KEY_PATTERN))) {
			issuesCache.setIssueKeyRegex(req.getParameter(APIV2_ISSUE_KEY_PATTERN));
			// TODO Also trigger a full index rescan
		}
		String[] newPatterns = req.getParameterValues(APIV2_PATTERN_KEY);
		if (newPatterns != null && !Arrays.equals(newPatterns, patterns.toArray(new String[patterns.size()]))) {
			// TODO Also trigger a full index rescan
			patterns.replaceBy(newPatterns);
			save();
		}
	}

	/**
	 * Web Method to validate a given {@link Pattern}
	 * 
	 * @param value the {@link Pattern} to validate
	 * @return validation result
	 * @throws IOException in case of errors
	 * @throws ServletException in case of errors
	 */
	public FormValidation doCheckPattern(@QueryParameter final String value) throws IOException, ServletException {
		if (!Hudson.getInstance().hasPermission(Hudson.ADMINISTER) || StringUtils.isBlank(value)) {
			return FormValidation.ok();
		}
		try {
			Pattern.compile(value);
			return FormValidation.ok();
		} catch (PatternSyntaxException e) {
			StringBuilder builder = new StringBuilder(e.getDescription());
			if (e.getIndex() >= 0) {
				builder.append(" near index ").append(e.getIndex());
			}
			builder.append("<pre>");
			builder.append(e.getPattern()).append(System.getProperty("line.separator"));
			if (e.getIndex() >= 0) {
				for (int i = 0; i < e.getIndex(); ++i) {
					builder.append(' ');
				}
				builder.append('^');
			}
			builder.append("</pre>");
			return FormValidation.errorWithMarkup(builder.toString());
		}
	}

	/**
	 * Getter for patterns
	 *
	 * @return the patterns
	 */
	public String[] getPatterns() {
		return patterns.toArray(new String[patterns.size()]);
	}

	/**
	 * Getter for the pattern used to index issue key
	 * 
	 * @return the Issue Key pattern
	 * @since 4.5.0
	 */
	public String getIssueKeyPattern() {
		return issuesCache.getIssueKeyRegex();
	}

	/**
	 * Getter for the {@link ActivitiesCache}
	 * 
	 * @return the {@link ActivitiesCache}
	 */
	public static ActivitiesCache getActivitiesCache() {
		return plugin.activitiesCache;
	}

	/**
	 * Getter for the {@link IssuesCache}
	 * 
	 * @return the {@link IssuesCache}
	 */
	public static IssuesCache getIssuesCache() {
		return plugin.issuesCache;
	}

	/**
	 * Getter for a {@link File} by name
	 * 
	 * @param filename the file name to get
	 * @return the {@link File}
	 */
	public File getFile(String filename) {
		File dir = new File(getConfigXml().getFile().getParent(), APIV2_DIRECTORY_NAME);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return new File(dir, filename);
	}

	/**
	 * Initialize the {@link XStream} object
	 */
	static {
		XSTREAM.autodetectAnnotations(true);
		XSTREAM.processAnnotations(ActivitiesCache.class);
		XSTREAM.processAnnotations(JobActivityCache.class);
		XSTREAM.processAnnotations(BuildActivityCache.class);
		XSTREAM.processAnnotations(IssuesCache.class);
		XSTREAM.processAnnotations(IssueCache.class);
		XSTREAM.processAnnotations(IssueKey.class);
	}

}
