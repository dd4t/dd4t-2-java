package org.dd4t.caching.providers;

import org.dd4t.caching.CacheDependency;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.CacheInvalidator;
import org.dd4t.caching.impl.CacheElementImpl;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.PayloadCacheProvider;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class EHCache3Provider extends AbstractEHCacheProvider implements PayloadCacheProvider, CacheInvalidator {

    private static final Logger LOG = LoggerFactory.getLogger(EHCache3Provider.class);
    private static final String EHCACHE3_DD4T_XML = "/ehcache3-dd4t.xml";

    private final CacheManager cacheManager;
    private final Cache<String, CacheElement> cache;
    private final Cache<String, CacheElement> dependencyCache;

    public EHCache3Provider() {
        URL ehcacheConfig = getClass().getResource(EHCACHE3_DD4T_XML);
        cacheManager = CacheManagerBuilder.newCacheManager(new XmlConfiguration(ehcacheConfig));
        cache = cacheManager.getCache(CACHE_NAME, String.class, CacheElement.class);
        dependencyCache = cacheManager.getCache(CACHE_NAME_DEPENDENCY,
                String.class, CacheElement.class);
    }

    @Override
    public void flush() {
        if (cache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        LOG.info("Expire all items in cache");
        for (Cache.Entry<String, CacheElement> entry : cache) {
            setExpired(entry, 0);
        }

        for (Cache.Entry<String, CacheElement> entry : dependencyCache) {
            setExpired(entry, ADJUST_TTL);
        }
    }

    @Override
    public void invalidate(final String key) {
        if (dependencyCache == null) {
            LOG.error("Cache configuration is invalid! NOT Caching. Check EH Cache configuration.");
            return;
        }
        String dependencyKey = getKey(key);
        CacheElement dependentEntry = dependencyCache.get(dependencyKey);


        if (dependentEntry != null) {
            LOG.info("Expire key: {} from dependency cache", dependencyKey);
//            dependencyCache.remove();
            setExpired(dependentEntry, ADJUST_TTL);
//            ConcurrentSkipListSet<String> cacheSet = ((CacheElement<ConcurrentSkipListSet<String>>) entry.getValue()).getPayload();
//            if (cacheSet != null) {
//                for (String cacheKey : cacheSet) {
//                    LOG.debug("Expire cache key: {} from cache", cacheKey);
//                    CacheElement entry = cache.get(cacheKey);
//                    setExpired(entry, 0);
//                }
//            }
        } else {
            LOG.info("Attempting to expire key {} but not found in dependency cache", dependencyKey);
        }
    }

    @Override
    public <T> CacheElement<T> loadPayloadFromLocalCache(final String key) {
        if (!doCheckForPreview() || (TridionUtils.getSessionPreviewToken() == null && cache != null)) {
            CacheElement<T> currentElement = cache.get(key);
            if (currentElement == null) {
                currentElement = new CacheElementImpl<T>(null);
                setExpired(currentElement, 0);
                CacheElement<T>  oldElement = cache.putIfAbsent(key, currentElement);
                if (oldElement != null) {
                    currentElement = oldElement;
                }
            }

            String dependencyKey = currentElement.getDependentKey();
            if (dependencyKey != null) {
                CacheElement<T> dependencyElement = dependencyCache.get(dependencyKey); // update
                // LRU
                // stats
                if (dependencyElement == null) { // recover evicted dependent
                    // key
                    addDependency(key, dependencyKey);
                }
            }
            return currentElement;
        } else {
            LOG.debug("Disable cache for Preview Session Token: {}", TridionUtils.getSessionPreviewToken());
            return new CacheElementImpl<T>((T) null, true);
        }
    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement) {

    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement, final int
            dependingPublicationId, final int dependingItemId) {

    }

    @Override
    public <T> void storeInItemCache(final String key, final CacheElement<T> cacheElement,
                                     final List<CacheDependency> dependencies) {

    }

    @Override
    public void addDependency(final String cacheKey, final String dependencyKey) {

    }

    public void setExpired(Cache.Entry<String, CacheElement> entry, int adjustTTL) {
        setExpired(entry.getValue(), adjustTTL);
    }

    public void setExpired (CacheElement cacheElement, int adjustTTL) {
        if (cacheElement == null) {
            return;
        }

        if (!cacheElement.isExpired()) {
            cacheElement.setExpired(true);

            // TODO: something totally different.
//            expireElement(entry, adjustTTL);
        }
    }

    private void expireElement (Cache.Entry<String, CacheElement> entry, int adjustTTL) {

//        long lastAccessTime = entry.getValue().getLastAccessTime();
//        long creationTime = element.getCreationTime();
//        // set element eviction to ('now' + expiredTTL) seconds in the future
//        int timeToLive = lastAccessTime == 0 ? expiredTTL : (int) (lastAccessTime - creationTime) / 1000 + expiredTTL;
//        timeToLive += adjustTTL;
//        element.setTimeToLive(timeToLive);
    }
}
