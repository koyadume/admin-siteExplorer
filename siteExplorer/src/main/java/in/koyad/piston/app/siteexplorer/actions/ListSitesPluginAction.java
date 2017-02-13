/*
 * Copyright (c) 2012-2017 Shailendra Singh <shailendra_01@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.koyad.piston.app.siteexplorer.actions;

import java.util.List;

import org.koyad.piston.core.model.Site;

import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = ListSitesPluginAction.ACTION_NAME
)
public class ListSitesPluginAction extends PluginAction {
	
	private final SiteService siteService = SiteImpl.getInstance();

	public static final String ACTION_NAME = "listSites";
	
	private static final LogUtil LOGGER = LogUtil.getLogger(ListSitesPluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		List<Site> sites = siteService.getSites();
		RequestContextUtil.getRequest().setAttribute("sites", sites);
		
		LOGGER.exitMethod("execute");
		return "/pages/sites.xml";
	}

}
