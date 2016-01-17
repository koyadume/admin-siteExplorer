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

import java.util.Arrays;

import in.koyad.piston.app.siteexplorer.forms.DeleteSitesPluginForm;
import in.koyad.piston.common.constants.FrameworkConstants;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.common.utils.Message;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.FormUtils;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = DeleteSitesPluginAction.ACTION_NAME
)
public class DeleteSitesPluginAction extends PluginAction {
	
	private final SiteService siteService = new SiteImpl();
	
	public static final String ACTION_NAME = "deleteSites";

	private static final LogUtil LOGGER = LogUtil.getLogger(DeleteSitesPluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		DeleteSitesPluginForm form = null;
		try {
			//update data in db
			form = FormUtils.createFormWithReqParams(DeleteSitesPluginForm.class);
			siteService.deleteSites(Arrays.asList(form.getSiteIds()));
			
			//update cache
			PistonModelCache.sites.removeAll(form.getSiteIds());
			
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, "Sites deleted successfully."));
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.ERROR, "Error occured while deleting sites."));
			
			RequestContextUtil.setRequestAttribute(DeleteSitesPluginForm.FORM_NAME, form);
		}
		
		LOGGER.exitMethod("execute");
		return FrameworkConstants.PREFIX_FORWARD + ListSitesPluginAction.ACTION_NAME;
	}

}
