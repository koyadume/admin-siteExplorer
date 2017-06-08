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

import org.koyad.piston.business.model.Frame;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.FrameDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.utils.ModelGenerator;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.PortalClient;
import in.koyad.piston.common.basic.StringUtil;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.constants.Messages;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.common.util.Message;
import in.koyad.piston.core.sdk.impl.PortalClientImpl;

/**
 * This action is used to update frame details.
 */
@AnnoPluginAction(
	name = SaveFramePluginAction.ACTION_NAME
)
public class SaveFramePluginAction extends BasePluginAction {
	
	private final PortalClient portalClient = PortalClientImpl.getInstance();

	public static final String ACTION_NAME = "saveFrame";	
	
	private static final LogUtil LOGGER = LogUtil.getLogger(SaveFramePluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		FrameDetailsPluginForm form = null;
		try {
			//update data in db
			form = req.getPluginForm(FrameDetailsPluginForm.class);
			
//			boolean update = true;
//			if(StringUtil.isEmpty(form.getId())) {
//				update = false;
//			}
			
			Frame updatedData = ModelGenerator.getFrame(form);
			Frame dbData = portalClient.saveFrame(updatedData);
			
			//update version in form
			form.setVersion(dbData.getVersion());
			
			//update data in cache if it is update operation
//			if(update) {
//				Frame oldData = PortalDynamicCache.frames.get(dbData.getId());
//				oldData.refresh(dbData);
//			} else {
				PortalDynamicCache.frames.remove(dbData.getId());
//			}
			
			// create operation
			if(form.getId().isEmpty()) {
				form.setId(updatedData.getId());
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_CREATED_SUCCESSFULLY, "Frame")));
			
			// update operation
			} else {
				req.setAttribute("msg", new Message(MsgType.INFO, MessageFormat.format(Messages.RESOURCE_UPDATED_SUCCESSFULLY, "Frame")));
			}
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while updating frame details."));
		}
		
		req.setAttribute(FrameDetailsPluginForm.FORM_NAME, form);
		
		LOGGER.exitMethod("execute");
		return "/frameDetails.xml";
	}

}
