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

AJS.$.namespace("AJS.gadget.hudson.status");

/**
 * Generate the Status Overview
 * 
 * @param gadget the Gadget to generate the status overview for
 * @param server he server the builds are configured on
 * @param builds the builds on the server
 */
AJS.gadget.hudson.status.generateStatusOverview = function(gadget, server, builds) {
	AJS.$.each(builds, function (i, build) {
		gadget.getView().append(AJS.gadget.hudson.status.generateBuildOverview(gadget, server, build));
	});
	gadget.getView().append(AJS.gadget.hudson.common.generateGadgetFooter(gadget, server));
}

/**
 * Generate a Build Overview div
 * 
 * @param gadget the Gadget the div will be added to
 * @param server the server that executed the build
 * @param build the build data
 * @return the build overview div
 */
AJS.gadget.hudson.status.generateBuildOverview = function(gadget, server, build) {
	var buildResult = AJS.$("<div/>").addClass("build-result").addClass("build-result-" + build.result);
	AJS.$("<div/>").addClass("build-info")
		.append(
			AJS.$("<a/>").attr({
				href: server.url + "/" + build.projectUrl,
				target: "_parent"
			}).text(build.project)
		).append(" #")
		.append(
			AJS.$("<a/>").attr({
				href: server.url + "/" + build.projectUrl + build.number,
				target: "_parent"
			}).text(build.number)
		).appendTo(buildResult);
	AJS.$("<div/>").addClass("build-details")
		.append(AJS.format(gadget.getMsg("hudson.gadget.common.ran"), build.timespan))
		.append(" | ")
		.append(build.trigger)
		.appendTo(buildResult);
	AJS.$("<div/>").addClass("build-details")
		.append(AJS.format(gadget.getMsg("hudson.gadget.common.duration"), build.duration))
		.appendTo(buildResult);
	return buildResult;
}