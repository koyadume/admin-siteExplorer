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

import java.util.List;

import org.koyad.piston.core.model.Frame;

import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.LogUtil;
import in.koyad.piston.controller.plugin.PluginAction;
import in.koyad.piston.controller.plugin.annotations.AnnoPluginAction;
import in.koyad.piston.core.sdk.api.PortalService;
import in.koyad.piston.core.sdk.impl.PortalImpl;
import in.koyad.piston.ui.utils.RequestContextUtil;

@AnnoPluginAction(
	name = ListFramesPluginAction.ACTION_NAME
)
public class ListFramesPluginAction extends PluginAction {
	
	private final PortalService portalService = PortalImpl.getInstance();

	public static final String ACTION_NAME = "listFrames";
	
	private static final LogUtil LOGGER = LogUtil.getLogger(ListFramesPluginAction.class);
	
	@Override
	protected String execute() throws FrameworkException {
		LOGGER.enterMethod("execute");
		
		List<Frame> frames = portalService.getFrames();
		RequestContextUtil.getRequest().setAttribute("frames", frames);
		
		LOGGER.exitMethod("execute");
		return "/pages/frames.xml";
	}

}
