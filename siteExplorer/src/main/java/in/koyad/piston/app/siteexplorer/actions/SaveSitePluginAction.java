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

import org.koyad.piston.core.model.Site;

import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.ModelGenerator;
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
 * This action is used to update site metadata and its permissions. 
 */
@AnnoPluginAction(
	name = SaveSitePluginAction.ACTION_NAME
)
public class SaveSitePluginAction extends PluginAction {
	
	private final SiteService siteService = new SiteImpl();
	
	public static final String ACTION_NAME = "saveSite";

	private static final LogUtil LOGGER = LogUtil.getLogger(SaveSitePluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		SiteDetailsPluginForm form = null;
		try {
			//save data in db
			form = FormUtils.createFormWithReqParams(SiteDetailsPluginForm.class);
			Site newData = ModelGenerator.getSite(form);
			siteService.saveSite(newData);
			
			//update version in form
			form.setVersion(newData.getVersion());
			
			//update data in cache
			Site oldData = PistonModelCache.sites.get(newData.getId());
			oldData.refresh(newData);
			
			//invalidate data in computation cache
			PermissionsUtil.clearSiteTreePermissions(newData);
			
			if(StringUtil.isEmpty(form.getId())) {
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Site")));
			} else {
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Site")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating site details."));
		}
		
		RequestContextUtil.setRequestAttribute("frames", PistonModelCache.frames.values());
		RequestContextUtil.setRequestAttribute(SiteDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return "/pages/siteDetails.xml";
	}

}
