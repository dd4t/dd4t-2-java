package org.dd4t.providers.impl;

import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.impl.CacheElementImpl;
import org.dd4t.providers.CacheProvider;

/**
 * CacheProvider which doesn't cache anything.
 * 
 * @author rooudsho
 *
 */
public class NoCacheProvider implements CacheProvider {

	@Override
	public <T> void storeInItemCache(String key, CacheElement<T> cacheElement) {
		
	}

	@Override
	public <T> void storeInItemCache(String key, CacheElement<T> cacheElement,
			int dependingPublicationId, int dependingItemId) {
		
	}

	@Override
	public <T> CacheElement<T> loadFromLocalCache(String key) {
		return new CacheElementImpl<T>(null, true);
	}

}
