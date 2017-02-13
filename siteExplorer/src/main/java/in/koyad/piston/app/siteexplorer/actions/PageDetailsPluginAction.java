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

import org.koyad.piston.core.model.Page;

import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.PopulateFormUtil;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = PageDetailsPluginAction.ACTION_NAME
)
public class PageDetailsPluginAction extends PluginAction {
	
	public static final String ACTION_NAME = "pageDetails";
	
	private final SiteService siteService = SiteImpl.getInstance();

	private static final LogUtil LOGGER = LogUtil.getLogger(PageDetailsPluginAction.class);
	
	@Override
	public String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		String pageId = RequestContextUtil.getParameter("id");
		
		if(null != pageId) {
			PageDetailsPluginForm pageform = new PageDetailsPluginForm();
			Page page = PistonModelCache.pages.get(pageId);
			PopulateFormUtil.populatePageDetails(pageform, page);
			RequestContextUtil.setRequestAttribute(PageDetailsPluginForm.FORM_NAME, pageform);
		}
		
		RequestContextUtil.setRequestAttribute("sites", siteService.getSites());
		
		LOGGER.exitMethod("execute");
		return "/pages/pageDetails.xml";
	}

}
