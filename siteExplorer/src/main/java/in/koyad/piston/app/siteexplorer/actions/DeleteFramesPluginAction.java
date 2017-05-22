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
import in.koyad.piston.app.siteexplorer.forms.DeleteFramesPluginForm;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.PortalClient;
import in.koyad.piston.common.basic.constant.FrameworkConstants;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.constants.MsgType;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.common.util.Message;
import in.koyad.piston.core.sdk.impl.PortalClientImpl;

@AnnoPluginAction(
	name = DeleteFramesPluginAction.ACTION_NAME
)
public class DeleteFramesPluginAction extends BasePluginAction {
	
	private final PortalClient portalClient = PortalClientImpl.getInstance();
	
	public static final String ACTION_NAME = "deleteFrames";	
	
	private static final LogUtil LOGGER = LogUtil.getLogger(DeleteFramesPluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		DeleteFramesPluginForm form = null;
		try {
			//update in db
			form = req.getPluginForm(DeleteFramesPluginForm.class); 
			String[] frameIds = form.getFrameIds();
			portalClient.deleteFrames(Arrays.asList(frameIds));
			
			//udpate data in cache
			PortalDynamicCache.frames.removeAll(frameIds);
			
			req.setAttribute("msg", new Message(MsgType.INFO, "Frame(s) deleted successfully."));
		} catch(FrameworkException ex) {
			LOGGER.logException(ex);
			req.setAttribute("msg", new Message(MsgType.ERROR, "Error occured while deleting frames."));
			
			req.setAttribute(DeleteFramesPluginForm.FORM_NAME, form);
		}
		LOGGER.exitMethod("execute");
		return FrameworkConstants.PREFIX_FORWARD + ListFramesPluginAction.ACTION_NAME;
	}

}
