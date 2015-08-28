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
package in.koyad.piston.app.siteexplorer.utils;

import java.util.HashSet;
import java.util.Set;

import org.koyad.piston.core.model.Frame;
import org.koyad.piston.core.model.Page;
import org.koyad.piston.core.model.PageMetadata;
import org.koyad.piston.core.model.Resource;
import org.koyad.piston.core.model.SecurityAcl;
import org.koyad.piston.core.model.Site;
import org.koyad.piston.core.model.SiteMetadata;
import org.koyad.piston.core.model.enums.Role;

import in.koyad.piston.app.siteexplorer.forms.FrameDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.forms.ResourcePluginForm;
import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.common.exceptions.FrameworkException;
import in.koyad.piston.common.utils.BeanPropertyUtils;
import in.koyad.piston.common.utils.ServiceManager;
import in.koyad.piston.common.utils.StringUtil;
import in.koyad.piston.core.sdk.api.PortalUserService;
import in.koyad.piston.servicedelegate.model.PistonModelCache;

public class ModelGenerator {
	
	private static final PortalUserService portalUserService = ServiceManager.getService(PortalUserService.class);

	public static Site getSite(SiteDetailsPluginForm form) throws FrameworkException {
		SiteMetadata metadata = new SiteMetadata();
		//title
		BeanPropertyUtils.copyProperties(metadata, form);
		
		Site site = new Site();
		site.setMetadata(metadata);
		
		//id, name
		BeanPropertyUtils.copyProperties(site, form);
		
		//frame
		site.setFrame(PistonModelCache.frames.get(form.getFrame()));
		
		//acls
		site.setAcls(getAcls(site, form));
		return site;
	}
	
	public static Page getPage(PageDetailsPluginForm form) throws FrameworkException {
		PageMetadata metadata = new PageMetadata();
		//title
		BeanPropertyUtils.copyProperties(metadata, form);
		
		Page page = new Page();
		page.setMetadata(metadata);
		
		//id, name
		BeanPropertyUtils.copyProperties(page, form);
		
		Site site = null;
		if(StringUtil.isEmpty(form.getId())) {
			//This means its a new paage and so site id must be present in the form.
			site = PistonModelCache.sites.get(form.getSiteId());
		} else {
			site = PistonModelCache.pages.get(form.getId()).getSite();
		}
		page.setSite(site);
		
		if(StringUtil.isEmpty(form.getId())) {
			//This means its a new page and so position should be set.
			if(StringUtil.isEmpty(metadata.getParentId())) {
				metadata.setPosition(site.getRootPages().size() + 1);
			} else {
				metadata.setPosition(PistonModelCache.pages.get(metadata.getParentId()).getChildren().size() + 1);
			}
		}
		
		page.setAcls(getAcls(page, form));
		
		return page;
	}
	
	private static Set<SecurityAcl> getAcls(Resource res, ResourcePluginForm form) throws FrameworkException {
		Set<SecurityAcl> acls = new HashSet<>();
		if(null != form.getManager()) {
			acls.add(getAcl(res, Role.MANAGER, form.getManager()));
		}
		
		if(null != form.getEditor()) {
			acls.add(getAcl(res, Role.EDITOR, form.getEditor()));
		}
		
		if(null != form.getUser()) {
			acls.add(getAcl(res, Role.USER, form.getUser()));
		}

		return acls;
	}
	
	private static SecurityAcl getAcl(Resource res, Role role, String[] typeAndExternalIds) throws FrameworkException {
		SecurityAcl acl = new SecurityAcl();
		acl.setResource(res);
		acl.setRole(role);
		acl.setMembers(portalUserService.getPrincipals(typeAndExternalIds));
		
		return acl;
	}
	
	public static Frame getFrame(FrameDetailsPluginForm form) {
		Frame frame = new Frame();
		BeanPropertyUtils.copyProperties(frame, form);
		return frame;
	}
}
