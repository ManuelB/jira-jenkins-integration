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

package com.marvelution.jira.plugins.hudson.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.admin.ApplicationPropertiesService;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.IssueManager;
import com.google.common.base.Preconditions;

/**
 * ChangeLog annotator helper
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 *
 * @since 4.5.0
 */
public class ChangelogAnnotator {

	private static final Logger LOGGER = Logger.getLogger(ChangelogAnnotator.class);

	private final ApplicationPropertiesService applicationPropertiesService;
	private final IssueManager issueManager;

	private ApplicationProperties applicationProperties;

	/**
	 * Constructor
	 *
	 * @param applicationPropertiesService the {@link ApplicationPropertiesService} implementation
	 * @param issueManager the {@link IssueManager} implementation
	 */
	public ChangelogAnnotator(ApplicationPropertiesService applicationPropertiesService, IssueManager issueManager) {
		this.applicationPropertiesService =
			Preconditions.checkNotNull(applicationPropertiesService, "applicationPropertiesService");
		this.issueManager = Preconditions.checkNotNull(issueManager, "issueManager");
	}

	/**
	 * Annotate the Changelog given
	 * 
	 * @param changelog the changelog to annotate
	 * @return the annotated changelog
	 */
	public String annotate(String changelog) {
		String baseUrl = "";
		try {
			baseUrl = getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		} catch (Exception e) {
		}
		return annotate(baseUrl, changelog);
	}

	/**
	 * Annotate the Changelog given
	 * 
	 * @param jiraBaseUrl the JIRA base URL
	 * @param changelog the changelog to annotate
	 * @return the annotated changelog
	 */
	public String annotate(String jiraBaseUrl, String changelog) {
		Pattern pattern =
			Pattern.compile("\\b((" + getProperty(APKeys.JIRA_PROJECTKEY_PATTERN) + ")-([1-9][0-9]*))\\b");
		LOGGER.debug("Searching for: " + pattern.pattern());
		Matcher matcher = pattern.matcher(changelog);
		while (matcher.find()) {
			String issueKey = matcher.group();
			LOGGER.error("Found issue key: " + issueKey);
			if (issueManager.getIssueObject(issueKey) != null) {
				changelog =
					StringUtils.replace(changelog, issueKey,
						String.format("<a href=\"%1$s/browse/%2$s\">%2$s</a>", jiraBaseUrl, issueKey));
			}
		}
		return changelog;
	}

	/**
	 * Getter for the current value of an editable application property
	 * 
	 * @param key the key of the property to get
	 * @return the current value of the property
	 */
	private String getProperty(String key) {
		return applicationPropertiesService.getApplicationProperty(key).getCurrentValue();
	}

	/**
	 * Getter of the {@link ApplicationProperties}
	 * 
	 * @return the {@link ApplicationProperties}
	 */
	private ApplicationProperties getApplicationProperties() {
		if (applicationProperties == null) {
			applicationProperties = ComponentManager.getComponentInstanceOfType(ApplicationProperties.class);
		}
		return applicationProperties;
	}

}
