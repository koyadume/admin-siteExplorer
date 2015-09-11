/*
 * Copyright (c) 2012-2015 Shailendra Singh <shailendra_01@outlook.com>
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

import org.koyad.piston.core.model.Page;

import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.ModelGenerator;
import in.koyad.piston.common.constants.FrameworkConstants;
import in.koyad.piston.common.constants.Messages;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.common.utils.Message;
import in.koyad.piston.common.utils.StringUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.servicedelegate.model.PermissionsUtil;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.FormUtils;
import in.koyad.piston.ui.utils.RequestContextUtil;

/**
 * This action is used to update page metadata and its permissions. 
 */
@AnnoPluginAction(
	name = SavePagePluginAction.ACTION_NAME
)
public class SavePagePluginAction extends PluginAction {
	
	private final SiteService siteService = new SiteImpl();
	
	public static final String ACTION_NAME = "savePage";

	private static final LogUtil LOGGER = LogUtil.getLogger(SavePagePluginAction.class);
	
	@Override
	public String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		PageDetailsPluginForm form = null;
		try {
			//save data in db
			form = FormUtils.createFormWithReqParams(PageDetailsPluginForm.class);
			Page newData = ModelGenerator.getPage(form);
			siteService.savePage(newData);
			
			//update version in form
			form.setVersion(newData.getVersion());
			
			//update data in cache
			if(!StringUtil.isEmpty(form.getId())) {
				Page oldData = PistonModelCache.pages.get(newData.getId());
				oldData.refresh(newData);
			}
			
			//update data in cache
			if(StringUtil.isEmpty(form.getId())) {
				PistonModelCache.sites.remove(form.getSiteId());
			} else {
				PistonModelCache.sites.remove(PistonModelCache.pages.get(form.getId()).getSiteId());
			}
			
			//invalidate data in computation cache
			PermissionsUtil.clearSiteTreePermissions(PistonModelCache.sites.get(newData.getSiteId()));
			
			if(StringUtil.isEmpty(form.getId())) {
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Page")));
			} else {
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Page")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating page details."));
		}
		
		RequestContextUtil.setRequestAttribute(PageDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return FrameworkConstants.PREFIX_FORWARD + GetSitePageChildrenPluginAction.ACTION_NAME;
	}

}