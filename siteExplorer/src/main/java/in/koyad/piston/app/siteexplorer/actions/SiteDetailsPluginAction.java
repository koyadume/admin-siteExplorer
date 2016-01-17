/*
 * Copyright (c) 2012-2016 Shailendra Singh <shailendra_01@outlook.com>
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

import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.PopulateFormUtil;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.RequestContextUtil;

import org.koyad.piston.core.model.Site;

@AnnoPluginAction(
	name = SiteDetailsPluginAction.ACTION_NAME
)
public class SiteDetailsPluginAction extends PluginAction {
	
	public static final String ACTION_NAME = "siteDetails";

	private static final LogUtil LOGGER = LogUtil.getLogger(SiteDetailsPluginAction.class);
	
	@Override
	public String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		String siteId = RequestContextUtil.getParameter("id");
		RequestContextUtil.setRequestAttribute("frames", PistonModelCache.frames.values());
		if(null != siteId) {
			SiteDetailsPluginForm siteForm = new SiteDetailsPluginForm();
			Site site = PistonModelCache.sites.get(siteId);
			PopulateFormUtil.populateSiteDetails(siteForm, site);
			RequestContextUtil.setRequestAttribute(SiteDetailsPluginForm.FORM_NAME, siteForm);
		}
		
		LOGGER.exitMethod("execute");
		return "/pages/siteDetails.xml";
	}

}
