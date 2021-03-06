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

package com.marvelution.jira.plugins.hudson.web.action.admin.servers;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.marvelution.hudson.plugins.apiv2.client.ClientException;
import com.marvelution.hudson.plugins.apiv2.client.Host;
import com.marvelution.hudson.plugins.apiv2.client.HudsonClient;
import com.marvelution.hudson.plugins.apiv2.client.services.VersionQuery;
import com.marvelution.jira.plugins.hudson.services.servers.HudsonClientFactory;
import com.marvelution.jira.plugins.hudson.services.servers.HudsonServerManager;
import com.marvelution.jira.plugins.hudson.web.action.admin.AbstractHudsonAdminWebActionSupport;
import com.marvelution.jira.plugins.hudson.web.action.admin.KeyValuePair;
import com.marvelution.jira.plugins.hudson.web.action.admin.ModifyActionType;

/**
 * Abstract Modify Server WebAction
 * 
 * @author <a href="mailto:markrekveld@marvelution.com">Mark Rekveld</a>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractModifyServer extends AbstractHudsonAdminWebActionSupport {

	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(AbstractModifyServer.class);

	private String name;
	private String description;
	private String host;
	private String publicHost;
	private String username;
	private String password;
	private boolean includeInStreams;
	private boolean cacheBuilds;

	protected final HudsonClientFactory clientFactory;

	/**
	 * Constructor
	 * 
	 * @param serverManager the {@link HudsonServerManager} implementation
	 * @param clientFactory the {@link HudsonClientFactory} implementation
	 */
	protected AbstractModifyServer(HudsonServerManager serverManager, HudsonClientFactory clientFactory) {
		super(serverManager);
		this.clientFactory = clientFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doValidation() {
		if (StringUtils.isBlank(name)) {
			addError("name", getText("hudson.server.name.required"));
		}
		if (StringUtils.isBlank(host)) {
			addError("host", getText("hudson.server.host.required"));
		} else if ((!host.startsWith("http://")) && (!host.startsWith("https://"))) {
			addError("host", getText("hudson.server.host.invalid"));
		} else {
			HudsonClient client = clientFactory.create(new Host(host, username, password));
			try {
				if (client.find(VersionQuery.createForPluginVersion()) == null) {
					logger.error("Unable to get a response from the /apiv2/plugin/version endpoint");
					addError("host", getText("hudson.server.host.apiv2.failed"));
				}
			} catch (ClientException e) {
				logger.error("Unable to get a response from the server: " + e.getMessage(), e);
				addError("host", getText("hudson.server.host.apiv2.failed"));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String doExecute() throws Exception {
		if (hasAnyErrors()) {
			return INPUT;
		}
		saveServer(name, description, host, publicHost, username, password, includeInStreams, cacheBuilds);
		return getRedirect(ADMINISTER_SERVERS);
	}

	/**
	 * Internal method to save the modification
	 * 
	 * @param name
	 * @param description
	 * @param host
	 * @param publicHost
	 * @param username
	 * @param password
	 * @param includeInStreams
	 * @param cacheBuilds
	 */
	protected abstract void saveServer(String name, String description, String host, String publicHost,
					String username, String password, boolean includeInStreams, boolean cacheBuilds);

	/**
	 * Getter for the action type eg: Add/Update
	 * 
	 * @return the {@link ModifyActionType} action
	 */
	public abstract ModifyActionType getActionType();

	/**
	 * Getter for a {@link Collection} of {@link KeyValuePair} objects to be added to the form in hidden inputs
	 * 
	 * @return the {@link Collection} of {@link KeyValuePair} objects
	 */
	public abstract Collection<KeyValuePair> getExtraHiddenInput();

	/**
	 * Getter for sid
	 *
	 * @return the sid
	 */
	public int getSid() {
		return 0;
	}

	/**
	 * Getter for name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Getter for host
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Setter for host
	 * 
	 * @param host the host to set
	 */
	public void setHost(String host) {
		if (host.endsWith("/")) {
			this.host = host.substring(0, host.length() - 1);
		} else {
			this.host = host;
		}
	}

	/**
	 * Getter for public host
	 * 
	 * @return the public host
	 */
	public String getPublicHost() {
		return publicHost;
	}

	/**
	 * Setter for public host
	 * 
	 * @param publicH the public host to set
	 */
	public void setPublicHost(String publicHost) {
		if (publicHost.endsWith("/")) {
			this.publicHost = publicHost.substring(0, publicHost.length() - 1);
		} else {
			this.publicHost = publicHost;
		}
	}

	/**
	 * Getter for username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Setter for username
	 * 
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Getter for password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Setter for password
	 * 
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Getter for includeInStreams
	 *
	 * @return the includeInStreams
	 */
	public boolean isIncludeInStreams() {
		return includeInStreams;
	}

	/**
	 * Setter for includeInStreams
	 *
	 * @param includeInStreams the includeInStreams to set
	 */
	public void setIncludeInStreams(boolean includeInStreams) {
		this.includeInStreams = includeInStreams;
	}

	/**
	 * Getter for cacheBuilds
	 *
	 * @return the cacheBuilds
	 * @since 4.5.0
	 */
	public boolean isCacheBuilds() {
		return cacheBuilds;
	}

	/**
	 * Setter for cacheBuilds
	 *
	 * @param cacheBuilds the cacheBuilds to set
	 * @since 4.5.0
	 */
	public void setCacheBuilds(boolean cacheBuilds) {
		this.cacheBuilds = cacheBuilds;
	}

}
