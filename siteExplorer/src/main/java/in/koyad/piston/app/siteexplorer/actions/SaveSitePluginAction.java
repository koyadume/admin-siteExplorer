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

import org.koyad.piston.business.model.Site;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.model.Response;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
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
 * This action is used to update site metadata and its permissions. 
 */
@AnnoPluginAction(
	name = SaveSitePluginAction.ACTION_NAME
)
public class SaveSitePluginAction extends BasePluginAction {
	
	private final SiteClient siteClient = SiteClientImpl.getInstance();
	
	public static final String ACTION_NAME = "saveSite";

	private static final LogUtil LOGGER = LogUtil.getLogger(SaveSitePluginAction.class);
	
	@Override
	public String execute(Request req, Response resp) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		SiteDetailsPluginForm form = null;
		try {
			//save data in db
			form = req.getPluginForm(SiteDetailsPluginForm.class);
			String siteId = form.getId();
			Site newData = ModelGenerator.getSite(form);
			Site response = siteClient.saveSite(newData);
			
			//clear cache
			if(!StringUtil.isEmpty(siteId)) {
				PortalDynamicCache.sites.remove(form.getId());
			}
			
			//update id, version in form
			form.setId(response.getId());
			form.setVersion(response.getVersion());
			
			if(StringUtil.isEmpty(siteId)) {
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Site")));
				return FrameworkConstants.PREFIX_FORWARD.concat(ListSitesPluginAction.ACTION_NAME);
			} else {
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Site")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating site details."));
		}
		
		req.setAttribute("frames", PortalDynamicCache.frames.values());
		req.setAttribute(SiteDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return "/siteDetails.xml";
	}

}
