/*
 * Copyright 2007-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.rose.web.portal.controllers.demo;

import net.paoding.rose.web.portal.Portal;
import net.paoding.rose.web.portal.PortalSetting;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public class PortalController {

    @PortalSetting(timeout = 100)
    public String home(Portal portal) throws Exception {
        portal.addWindow("content1", "/test/uri?abc=asd");
        portal.addWindow("content2", "/test/uri?abc=asd");
        portal.addWindow("content3", "/test/uri?abc=asd");
        portal.addWindow("content4", "/test/uri?abc=asd").forRender(false);
        return "portal-home";
    }
}