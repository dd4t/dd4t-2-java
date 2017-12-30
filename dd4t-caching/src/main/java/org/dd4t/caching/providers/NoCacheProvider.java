/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.caching.providers;

import org.dd4t.caching.Cachable;
import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.impl.CacheElementImpl;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PayloadCacheProvider;

import java.util.Collection;
import java.util.List;

/**
 * CacheProvider which doesn't cache anything.
 *
 * @author rooudsho
 */
public class NoCacheProvider implements PayloadCacheProvider, CacheProvider {

    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement) {
        // Do nothing
    }

    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, int dependingPublicationId, int
            dependingItemId) {
        // Do nothing
    }

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache(String key) {
        return new CacheElementImpl<T>(null, true);
    }

    @Override
    public void storeInCache(String key, Cachable ob, Collection<Cachable> deps) {
        // Do nothing
    }

    @Override
    public void storeInItemCache(String key, Object ob, int dependingPublicationId, int dependingItemId) {
        // Do nothing
    }

    @Override
    public void storeInComponentPresentationCache(String key, Object ob, int dependingPublicationId, int
            dependingCompId, int dependingTemplateId) {
        // Do nothing
    }

    @Override
    public void storeInKeywordCache(String key, Object ob, int dependingPublicationId, int dependingItemId) {
        // Do nothing
    }

    @Override
    public Object loadFromLocalCache(String key) {
        return null;
    }

    @Override
    public <T> void storeInItemCache(String key, CacheElement<T> cacheElement, List<CacheDependency> dependencies) {
        // Nothing to do.
    }

    @Override
    public void addDependency(String cacheKey, String dependencyKey) {
        // Nothing to do.
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void storeInItemCache(String key, Object ob, List<CacheDependency> dependencies) {
        // Nothing to do.
    }
}
