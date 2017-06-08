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

import org.koyad.piston.business.model.Site;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.PopulateFormUtil;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.PortalClient;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.core.sdk.impl.PortalClientImpl;

@AnnoPluginAction(
	name = SiteDetailsPluginAction.ACTION_NAME
)
public class SiteDetailsPluginAction extends BasePluginAction {
	
	public static final String ACTION_NAME = "siteDetails";

	private static final LogUtil LOGGER = LogUtil.getLogger(SiteDetailsPluginAction.class);
	
	private static final PortalClient portalClient = PortalClientImpl.getInstance();
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		String siteId = req.getParameter("id");
		req.setAttribute("frames", portalClient.getFrames());
		if(null != siteId) {
			SiteDetailsPluginForm siteForm = new SiteDetailsPluginForm();
			Site site = PortalDynamicCache.sites.get(siteId);
			PopulateFormUtil.populateSiteDetails(siteForm, site);
			req.setAttribute(SiteDetailsPluginForm.FORM_NAME, siteForm);
		}
		
		LOGGER.exitMethod("execute");
		return "/siteDetails.xml";
	}

}
