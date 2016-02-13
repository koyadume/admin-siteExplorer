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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.koyad.piston.core.model.Page;
import org.koyad.piston.core.model.Site;

import in.koyad.piston.app.siteexplorer.forms.DeletePagesPluginForm;
import in.koyad.piston.app.siteexplorer.forms.GetSitePageChildrenPluginForm;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.common.utils.StringUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.FormUtils;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = GetSitePageChildrenPluginAction.ACTION_NAME
)
public class GetSitePageChildrenPluginAction extends PluginAction {
	
	public static final String ACTION_NAME = "getSitePageChildren";
	
	private final SiteService siteService = SiteImpl.getInstance();

	private static final LogUtil LOGGER = LogUtil.getLogger(GetSitePageChildrenPluginAction.class);
	
	@Override
	public String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		List<Site> sites = siteService.getSites();
		RequestContextUtil.getRequest().setAttribute("sites", sites);
		
		GetSitePageChildrenPluginForm form = FormUtils.createFormWithReqParams(GetSitePageChildrenPluginForm.class);		
		
		String siteId = form.getSiteId();
		if(!StringUtil.isEmpty(siteId)) {
			DeletePagesPluginForm deletePagesForm = new DeletePagesPluginForm();
			deletePagesForm.setSiteId(siteId);
			RequestContextUtil.setRequestAttribute(DeletePagesPluginForm.FORM_NAME, deletePagesForm);
			
			Site site = PistonModelCache.sites.get(siteId);
			RequestContextUtil.setRequestAttribute("site", site);
			
			String parentId = form.getParentId();
			List<Page> children = null;
	
			if(StringUtil.isEmpty(parentId)) {
				children = site.getRootPages();
			} else {
				List<Page> parents = new LinkedList<>();
				
				Page parent = PistonModelCache.pages.get(parentId);
				children = parent.getChildren();
				Page tmpParent = parent;
				do {
					parents.add(tmpParent);
					tmpParent = tmpParent.getParent();
				} while(null != tmpParent);
				
				Collections.reverse(parents);
				RequestContextUtil.setRequestAttribute("parents", parents);
			}
			
			RequestContextUtil.setRequestAttribute("children", children);
		}
		
		String view = "/pages/siteNodes.xml"; 
//		String scenario = RequestContextUtil.getParameter(FrameworkConstants.SCENARIO);
//		if(null != scenario) {
//			switch(scenario){
//				case "createUpdatePage":
//					view = "/ajax/sitePageChildren.xml";
//					break;
//			}
//		} else {
//			view = "/pages/siteNodes.xml"; 
//		}
		
		LOGGER.exitMethod("execute");
		return view;
	}

}
