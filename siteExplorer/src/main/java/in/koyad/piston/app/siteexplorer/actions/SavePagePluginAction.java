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

import java.text.MessageFormat;

import org.koyad.piston.business.model.Page;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.ModelGenerator;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.SiteClient;
import in.koyad.piston.common.basic.StringUtil;
import in.koyad.piston.common.basic.constant.FrameworkConstants;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.constants.Messages;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.common.util.Message;
import in.koyad.piston.core.sdk.impl.SiteClientImpl;

/**
 * This action is used to update page metadata and its permissions. 
 */
@AnnoPluginAction(
	name = SavePagePluginAction.ACTION_NAME
)
public class SavePagePluginAction extends BasePluginAction {
	
	private final SiteClient siteClient = SiteClientImpl.getInstance();
	
	public static final String ACTION_NAME = "savePage";

	private static final LogUtil LOGGER = LogUtil.getLogger(SavePagePluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		PageDetailsPluginForm form = null;
		try {
			//save data in db
			form = req.getPluginForm(PageDetailsPluginForm.class);
			Page newData = ModelGenerator.getPage(form);
			siteClient.savePage(newData);
			
			//update version in form
			form.setVersion(newData.getVersion());
			
			//update data in cache
			if(!StringUtil.isEmpty(form.getId())) {
				Page oldData = PortalDynamicCache.pages.get(newData.getId());
				oldData.refresh(newData);
			}
			
			//update data in cache
			if(StringUtil.isEmpty(form.getId())) {
				PortalDynamicCache.sites.remove(form.getSiteId());
			} else {
				PortalDynamicCache.sites.remove(PortalDynamicCache.pages.get(form.getId()).getSite().getId());
			}
			
			//invalidate data in computation cache
//			PermissionsUtil.clearSiteTreePermissions(PortalDynamicCache.sites.get(newData.getSite().getId()));
			
			if(StringUtil.isEmpty(form.getId())) {
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Page")));
			} else {
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Page")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating page details."));
		}
		
		req.setAttribute(PageDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return FrameworkConstants.PREFIX_FORWARD + GetSitePageChildrenPluginAction.ACTION_NAME;
	}

}