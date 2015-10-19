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

import org.koyad.piston.core.model.Frame;

import in.koyad.piston.app.siteexplorer.forms.FrameDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.ModelGenerator;
import in.koyad.piston.common.constants.Messages;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.common.utils.Message;
import in.koyad.piston.common.utils.StringUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.PortalService;
import in.koyad.piston.core.sdk.impl.PortalImpl;
import in.koyad.piston.servicedelegate.model.PistonModelCache;
import in.koyad.piston.ui.utils.FormUtils;
import in.koyad.piston.ui.utils.RequestContextUtil;

/**
 * This action is used to update frame details.
 */
@AnnoPluginAction(
	name = SaveFramePluginAction.ACTION_NAME
)
public class SaveFramePluginAction extends PluginAction {
	
	private final PortalService portalService = new PortalImpl();

	public static final String ACTION_NAME = "saveFrame";	
	
	private static final LogUtil LOGGER = LogUtil.getLogger(SaveFramePluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		FrameDetailsPluginForm form = null;
		try {
			//update data in db
			form = FormUtils.createFormWithReqParams(FrameDetailsPluginForm.class);
			
			boolean update = true;
			if(StringUtil.isEmpty(form.getId())) {
				update = false;
			}
			
			Frame newData = ModelGenerator.getFrame(form);
			portalService.saveFrame(newData);
			
			//update version in form
			form.setVersion(newData.getVersion());
			
			//update data in cache if it is update operation
			if(update) {
				Frame oldData = PistonModelCache.frames.get(newData.getId());
				oldData.refresh(newData);
			}
			
			if(!update) {
				form.setId(newData.getId());
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Frame")));
			} else {
				RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Frame")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			RequestContextUtil.setRequestAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating frame details."));
		}
		
		RequestContextUtil.setRequestAttribute(FrameDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return "/pages/frameDetails.xml";
	}

}
