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
package net.paoding.rose.jade.jadeinterface.impl.scanner;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.io.Resource;

/**
 * @author zhiliang.wang 王志亮 [qieqie.wang@gmail.com]
 */
public class ResourceInfo {

    private Resource resource;

    private String[] modifiers;

    public ResourceInfo(Resource resource, String[] modifiers) {
        this.resource = resource;
        this.modifiers = modifiers;
    }

    public Resource getResource() {
        return resource;
    }

    public String[] getModifiers() {
        return modifiers;
    }

    public boolean hasModifier(String modifier) {
        return ArrayUtils.contains(modifiers, "*") || ArrayUtils.contains(modifiers, modifier);
    }

    @Override
    public String toString() {
        try {
            return resource.getURL() + Arrays.toString(modifiers);
        } catch (IOException e) {
            return resource + Arrays.toString(modifiers);
        }
    }
}