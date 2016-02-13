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

import in.koyad.piston.app.siteexplorer.forms.MovePagePluginForm;
import in.koyad.piston.common.constants.FrameworkConstants;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.common.utils.Message;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.SiteService;
import in.koyad.piston.core.sdk.impl.SiteImpl;
import in.koyad.piston.ui.utils.FormUtils;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = MovePagePluginAction.ACTION_NAME
)
public class MovePagePluginAction extends PluginAction {
	
	private final SiteService siteService = SiteImpl.getInstance();
	
	public static final String ACTION_NAME = "movePage";

	private static final LogUtil LOGGER = LogUtil.getLogger(MovePagePluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		try {
			MovePagePluginForm form = FormUtils.createFormWithReqParams(MovePagePluginForm.class);
//			Page node = PistonModelCache.pages.get(form.getPageId());
			if(form.getAction().equalsIgnoreCase("down")) {
				siteService.moveDown(form.getPageId());
			} else if(form.getAction().equalsIgnoreCase("up")) {
				siteService.moveUp(form.getPageId());
			}
			
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, "Page moved successfully."));
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.ERROR, "Error occured while moving page."));
		}
		
		LOGGER.exitMethod("execute");
//		String nextAction = (String)RequestContextUtil.getRequestAttribute(FrameworkConstants.PISTON_FWK_NEXTACTION);
		return FrameworkConstants.PREFIX_FORWARD + GetSitePageChildrenPluginAction.ACTION_NAME;
	}

}
