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
package in.koyad.piston.app.siteexplorer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.koyad.piston.business.model.Frame;
import org.koyad.piston.business.model.Members;
import org.koyad.piston.business.model.Page;
import org.koyad.piston.business.model.Resource;
import org.koyad.piston.business.model.SecurityAcl;
import org.koyad.piston.business.model.Site;
import org.koyad.piston.business.model.embedded.PageMetadata;
import org.koyad.piston.business.model.embedded.SiteMetadata;
import org.koyad.piston.business.model.enums.RoleType;

import in.koyad.piston.app.siteexplorer.forms.FrameDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.forms.ResourcePluginForm;
import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.cache.store.PortalDynamicCache;
import in.koyad.piston.client.api.PortalUserStoreClient;
import in.koyad.piston.common.basic.StringUtil;
import in.koyad.piston.common.basic.exception.FrameworkException;
import in.koyad.piston.common.util.BeanPropertyUtils;
import in.koyad.piston.core.sdk.impl.PortalUserStoreClientImpl;

public class ModelGenerator {
	
	private static final PortalUserStoreClient portalUserStoreClient = PortalUserStoreClientImpl.getInstance();

	public static Site getSite(SiteDetailsPluginForm form) throws FrameworkException {
		SiteMetadata metadata = new SiteMetadata();
		//title
		BeanPropertyUtils.copyProperties(metadata, form);
		
		Site site = new Site();
		site.setMetadata(metadata);
		
		//id, name
		BeanPropertyUtils.copyProperties(site, form);
		
		//frame
		site.setFrameId(form.getFrameId());
		
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
		
		//id, name, siteId
		BeanPropertyUtils.copyProperties(page, form);
		
		Site site = null;
		if(StringUtil.isEmpty(form.getId())) {
			//This means its a new page and so site id must be present in the form.
			site = PortalDynamicCache.sites.get(form.getSiteId());
		} else {
			site = PortalDynamicCache.pages.get(form.getId()).getSite();
		}
		page.setSite(site);
		
		if(StringUtil.isEmpty(form.getId())) {
			//This means its a new page and so position should be set.
			if(StringUtil.isEmpty(metadata.getParentId())) {
				metadata.setPosition(PortalDynamicCache.sites.get(form.getSiteId()).getRootPages().size() + 1);
			} else {
				metadata.setPosition(PortalDynamicCache.pages.get(metadata.getParentId()).getChildren().size() + 1);
			}
		}
		
		page.setAcls(getAcls(page, form));
		
		return page;
	}
	
	private static List<SecurityAcl> getAcls(Resource res, ResourcePluginForm form) throws FrameworkException {
		List<SecurityAcl> acls = new ArrayList<>();
		if(null != form.getManager()) {
			acls.add(getAcl(res, RoleType.MANAGER, form.getManager()));
		}
		
		if(null != form.getEditor()) {
			acls.add(getAcl(res, RoleType.EDITOR, form.getEditor()));
		}
		
		if(null != form.getUser()) {
			acls.add(getAcl(res, RoleType.USER, form.getUser()));
		}

		return acls;
	}
	
	private static SecurityAcl getAcl(Resource res, RoleType role, String[] typeAndExternalIds) throws FrameworkException {
		SecurityAcl acl = new SecurityAcl();
		acl.setRole(role);
		List<String> users = new ArrayList<>();
		List<String> groups = new ArrayList<>();
		for(String typeAndExternalId : typeAndExternalIds) {
			if(typeAndExternalId.startsWith("user:")) {
				users.add(typeAndExternalId.split(Pattern.quote(":"))[1]);
			} else if(typeAndExternalId.startsWith("group:")) {
				groups.add(typeAndExternalId.split(Pattern.quote(":"))[1]);
			}
		}
		Members members = new Members(users, groups);
		acl.setMembers(members);
		
		return acl;
	}
	
	public static Frame getFrame(FrameDetailsPluginForm form) {
		Frame frame = new Frame();
		BeanPropertyUtils.copyProperties(frame, form);
		return frame;
	}
}
