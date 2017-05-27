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

import org.koyad.piston.business.model.Frame;

import in.koyad.piston.app.api.annotation.AnnoPluginAction;
import in.koyad.piston.app.api.model.Request;
import in.koyad.piston.app.api.plugin.BasePluginAction;
import in.koyad.piston.app.siteexplorer.forms.FrameDetailsPluginForm;
import in.koyad.piston.client.api.PortalClient;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.util.BeanPropertyUtils;
import in.koyad.piston.common.util.LogUtil;
import in.koyad.piston.core.sdk.impl.PortalClientImpl;

@AnnoPluginAction(
	name = FrameDetailsPluginAction.ACTION_NAME
)
public class FrameDetailsPluginAction extends BasePluginAction {
	
	private final PortalClient portalClient = PortalClientImpl.getInstance();
	
	public static final String ACTION_NAME = "frameDetails";

	private static final LogUtil LOGGER = LogUtil.getLogger(FrameDetailsPluginAction.class);
	
	@Override
	public String execute(Request req) throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		String frameId = req.getParameter("id");
		
		if(null != frameId) {
			Frame frame = portalClient.getFrame(frameId);

			FrameDetailsPluginForm frameForm = new FrameDetailsPluginForm();
			BeanPropertyUtils.copyProperties(frameForm, frame);
			
			req.setAttribute(FrameDetailsPluginForm.FORM_NAME, frameForm);
		}
			
		LOGGER.exitMethod("execute");
		return "/frameDetails.xml";
	}

}
