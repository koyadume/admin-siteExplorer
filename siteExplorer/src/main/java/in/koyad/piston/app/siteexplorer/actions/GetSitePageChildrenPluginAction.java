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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.koyad.piston.business.model.Page;
import org.koyad.piston.business.model.Site;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.DeletePagesPluginForm;
import in.koyad.piston.app.siteexplorer.forms.GetSitePageChildrenPluginForm;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.SiteClient;
import in.koyad.piston.common.basic.StringUtil;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.core.sdk.impl.SiteClientImpl;

@AnnoPluginAction(
	name = GetSitePageChildrenPluginAction.ACTION_NAME
)
public class GetSitePageChildrenPluginAction extends BasePluginAction {
	
	public static final String ACTION_NAME = "getSitePageChildren";
	
	private final SiteClient siteClient = SiteClientImpl.getInstance();

	private static final LogUtil LOGGER = LogUtil.getLogger(GetSitePageChildrenPluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		List<Site> sites = siteClient.getSites();
		req.setAttribute("sites", sites);
		
		GetSitePageChildrenPluginForm form = req.getPluginForm(GetSitePageChildrenPluginForm.class);		
		
		String siteId = form.getSiteId();
		if(!StringUtil.isEmpty(siteId)) {
			DeletePagesPluginForm deletePagesForm = new DeletePagesPluginForm();
			deletePagesForm.setSiteId(siteId);
			req.setAttribute(DeletePagesPluginForm.FORM_NAME, deletePagesForm);
			
			Site site = PortalDynamicCache.sites.get(siteId);
			req.setAttribute("site", site);
			
			String parentId = form.getParentId();
			List<Page> children = null;
	
			if(StringUtil.isEmpty(parentId)) {
				children = site.getRootPages();
			} else {
				List<Page> parents = new LinkedList<>();
				
				Page parent = PortalDynamicCache.pages.get(parentId);
				children = parent.getChildren();
				Page tmpParent = parent;
				do {
					parents.add(tmpParent);
					tmpParent = tmpParent.getParent();
				} while(null != tmpParent);
				
				Collections.reverse(parents);
				req.setAttribute("parents", parents);
			}
			
			req.setAttribute("children", children);
		}
		
		String view = "/siteNodes.xml"; 
//		String scenario = req.getParameter(FrameworkConstants.SCENARIO);
//		if(null != scenario) {
//			switch(scenario){
//				case "createUpdatePage":
//					view = "/ajax/sitePageChildren.xml";
//					break;
//			}
//		} else {
//			view = "/siteNodes.xml"; 
//		}
		
		LOGGER.exitMethod("execute");
		return view;
	}

}
