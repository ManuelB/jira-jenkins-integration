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

package com.marvelution.jira.plugins.hudson.web.action.admin;

import java.util.Arrays;
import java.util.Collection;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.marvelution.jira.plugins.hudson.services.configuration.HudsonConfigurationManager;
import com.marvelution.jira.plugins.hudson.services.servers.HudsonServerManager;

/**
 * {@link AbstractHudsonAdminWebActionSupport} implementation for the administrative page ConfigureHudsonPlugin.jspa
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 */
public class ConfigurePlugin extends AbstractHudsonAdminWebActionSupport {

	private static final long serialVersionUID = 1L;
	private final HudsonConfigurationManager configurationManager;
	private final GroupManager groupManager;
	
	/**
	 * Constructor
	 * 
	 * @param configurationManager the {@link HudsonConfigurationManager} implementation
	 * @param serverManager the {@link HudsonServerManager} implementation
	 * @param groupManager the {@link GroupManager} implementation
	 */
	public ConfigurePlugin(HudsonConfigurationManager configurationManager, HudsonServerManager serverManager, GroupManager groupManager) {
		super(serverManager);
		this.configurationManager = configurationManager;
		this.groupManager = groupManager;
	}

	/**
	 * Getter for all the {@link ProjectRole} objects within JIRA
	 * 
	 * @return {@link Collection} of {@link ProjectRole} objects
	 */
	public Collection<ProjectRole> getProjectRoles() {
		return getProjectRoleManager().getProjectRoles();
	}

	/**
	 * Getter for all the {@link Group} objects within JIRA
	 * 
	 * @return {@link Collection} of {@link Group} objects
	 */
	public Collection<Group> getGroups() {
		return groupManager.getAllGroups();
	}

	/**
	 * Getter for all the {@link IssueType} objects in a {@link Collection}
	 * 
	 * @return the {@link Collection} with all the {@link IssueType}
	 */
	public Collection<IssueType> getIssueTypes() {
		return getConstantsManager().getAllIssueTypeObjects();
	}

	/**
	 * Getter for hideUnassociatedHudsonTabs
	 * 
	 * @return the hideUnassociatedHudsonTabs
	 */
	public Boolean getHideUnassociatedHudsonTabs() {
		return configurationManager.isHideUnassociatedHudsonTabs();
	}

	/**
	 * Setter for hideUnassociatedHudsonTabs
	 * 
	 * @param hideUnassociatedHudsonTabs the hideUnassociatedHudsonTabs to set
	 */
	public void setHideUnassociatedHudsonTabs(Boolean hideUnassociatedHudsonTabs) {
		configurationManager.setHideUnassociatedHudsonTabs(hideUnassociatedHudsonTabs);
	}

	/**
	 * Getter for filterHudsonBuilds
	 * 
	 * @return the filterHudsonBuilds
	 */
	public Boolean getFilterHudsonBuilds() {
		return configurationManager.isFilterHudsonBuilds();
	}

	/**
	 * Setter for filterHudsonBuilds
	 * 
	 * @param filterHudsonBuilds the filterHudsonBuilds to set
	 */
	public void setFilterHudsonBuilds(Boolean filterHudsonBuilds) {
		configurationManager.setFilterHudsonBuilds(filterHudsonBuilds);
	}

	/**
	 * Getter for timePastDateStrings
	 * 
	 * @return the timePastDateStrings
	 */
	public Boolean getTimePastDateStrings() {
		return configurationManager.isTimePastDateStrings();
	}

	/**
	 * Setter for timePastDateStrings
	 * 
	 * @param timePastDateStrings the timePastDateStrings to set
	 */
	public void setTimePastDateStrings(Boolean timePastDateStrings) {
		configurationManager.setTimePastDateStrings(timePastDateStrings);
	}

	/**
	 * Getter for showIfIssueOfIssuetype
	 * 
	 * @return the showIfIssueOfIssuetype
	 */
	public String[] getShowIfIssueOfIssuetype() {
		return getShowIfIssueOfIssuetypeAsCollection().toArray(new String[]{});
	}

	/**
	 * Getter for the showIfIssueOfIssuetype as Collection
	 * 
	 * @return the showIfIssueOfIssuetype
	 */
	public Collection<String> getShowIfIssueOfIssuetypeAsCollection() {
		return configurationManager.getShowIfIssueOfIssueType();
	}

	/**
	 * Setter for showIfIssueOfIssuetype
	 * 
	 * @param showIfIssueOfIssuetype the showIfIssueOfIssuetype to set
	 */
	public void setShowIfIssueOfIssuetype(String[] showIfIssueOfIssuetype) {
		configurationManager.setShowIfIssueOfIssueType(Arrays.asList(showIfIssueOfIssuetype));
	}

	/**
	 * Getter for showIfUserMemberOfUsergroup
	 * 
	 * @return the showIfUserMemberOfUsergroup
	 */
	public String[] getShowIfUserMemberOfUsergroup() {
		return getShowIfUserMemberOfUsergroupAsCollection().toArray(new String[]{});
	}

	/**
	 * Getter for the showIfUserMemberOfUsergroup as Collection
	 * 
	 * @return the showIfUserMemberOfUsergroup
	 */
	public Collection<String> getShowIfUserMemberOfUsergroupAsCollection() {
		return configurationManager.getShowIfUserMemberOfUsergroup();
	}

	/**
	 * Setter for showIfUserMemberOfUsergroup
	 * 
	 * @param showIfUserMemberOfUsergroup the showIfUserMemberOfUsergroup to set
	 */
	public void setShowIfUserMemberOfUsergroup(String[] showIfUserMemberOfUsergroup) {
		configurationManager.setShowIfUserMemberOfUsergroup(Arrays.asList(showIfUserMemberOfUsergroup));
	}

	/**
	 * Getter for showIfUserMemberOfProjectRole
	 * 
	 * @return the showIfUserMemberOfProjectRole
	 */
	public String[] getShowIfUserMemberOfProjectRole() {
		return getShowIfUserMemberOfProjectRoleAsCollection().toArray(new String[]{});
	}

	/**
	 * Getter for the showIfUserMemberOfProjectRole as Collection
	 * 
	 * @return the showIfUserMemberOfProjectRole
	 */
	public Collection<String> getShowIfUserMemberOfProjectRoleAsCollection() {
		return configurationManager.getShowIfUserMemberOfProjectRole();
	}

	/**
	 * Setter for showIfUserMemberOfProjectRole
	 * 
	 * @param showIfUserMemberOfProjectRole the showIfUserMemberOfProjectRole to set
	 */
	public void setShowIfUserMemberOfProjectRole(String[] showIfUserMemberOfProjectRole) {
		configurationManager.setShowIfUserMemberOfProjectRole(Arrays.asList(showIfUserMemberOfProjectRole));
	}

	/**
	 * Getter for the maxBuildsPerRequest
	 * 
	 * @return the maxBuildsPerRequest
	 * @see com.marvelution.jira.plugins.hudson.services.configuration.HudsonConfigurationManager#getMaxBuildsPerRequest()
	 * @since 4.5.0
	 */
	public int getMaxBuildsPerRequest() {
		return configurationManager.getMaxBuildsPerRequest();
	}

	/**
	 * Setter for the maxBuildsPerRequest
	 * 
	 * @param maxBuildsPerRequest the maxBuildsPerRequest to set
	 * @see com.marvelution.jira.plugins.hudson.services.configuration.HudsonConfigurationManager#setMaxBuildsPerRequest(int)
	 * @since 4.5.0
	 */
	public void setMaxBuildsPerRequest(int maxBuildsPerRequest) {
		configurationManager.setMaxBuildsPerRequest(maxBuildsPerRequest);
	}

}
