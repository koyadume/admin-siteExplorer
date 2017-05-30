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

import java.util.Arrays;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.DeleteSitesPluginForm;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.SiteClient;
import in.koyad.piston.common.basic.constant.FrameworkConstants;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.common.util.Message;
import in.koyad.piston.core.sdk.impl.SiteClientImpl;

@AnnoPluginAction(
	name = DeleteSitesPluginAction.ACTION_NAME
)
public class DeleteSitesPluginAction extends BasePluginAction {
	
	private final SiteClient siteClient = SiteClientImpl.getInstance();
	
	public static final String ACTION_NAME = "deleteSites";

	private static final LogUtil LOGGER = LogUtil.getLogger(DeleteSitesPluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		DeleteSitesPluginForm form = null;
		try {
			//update data in db
			form = req.getPluginForm(DeleteSitesPluginForm.class);
			siteClient.deleteSites(Arrays.asList(form.getSiteIds()));
			
			//update cache
			PortalDynamicCache.sites.removeAll(form.getSiteIds());
			
			req.setAttribute("msg", new Message(MsgType.INFO, "Site(s) deleted successfully."));
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while deleting sites."));
			
			req.setAttribute(DeleteSitesPluginForm.FORM_NAME, form);
		}
		
		LOGGER.exitMethod("execute");
		return FrameworkConstants.PREFIX_FORWARD + ListSitesPluginAction.ACTION_NAME;
	}

}
