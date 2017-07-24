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
import in.koyad.piston.app.api.model.Response;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.DeletePagesPluginForm;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.SiteClient;
import in.koyad.piston.common.basic.constant.FrameworkConstants;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.common.util.Message;
import in.koyad.piston.core.sdk.impl.SiteClientImpl;

@AnnoPluginAction(
	name = DeletePagesPluginAction.ACTION_NAME
)
public class DeletePagesPluginAction extends BasePluginAction {
	
	private final SiteClient siteClient = SiteClientImpl.getInstance();
	
	public static final String ACTION_NAME = "deletePages";

	private static final LogUtil LOGGER = LogUtil.getLogger(DeletePagesPluginAction.class);
	
	@Override
	public String execute(Request req, Response resp) throws FrameworkException {
		LOGGER.enterMethod("execute");
	
		DeletePagesPluginForm form = null;
		try {
			//update data in db
			form = req.getPluginForm(DeletePagesPluginForm.class);
			siteClient.deletePages(Arrays.asList(form.getPageIds()));
			
			//update data in cache
			PortalDynamicCache.sites.remove(form.getSiteId());
			
			req.setAttribute("msg", new Message(MsgType.INFO, "Page(s) deleted successfully."));
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while deleting pages."));
			
			req.setAttribute(DeletePagesPluginForm.FORM_NAME, form);
		}
			
		LOGGER.exitMethod("execute");
//		String nextAction = (String)RequestContextUtil.getRequestAttribute(FrameworkConstants.PISTON_FWK_NEXTACTION);
		return FrameworkConstants.PREFIX_FORWARD + GetSitePageChildrenPluginAction.ACTION_NAME;
	}

}
