package org.dd4t.caching.providers;

import java.io.Serializable;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public abstract class AbstractEHCacheProvider {
    /**
     * The name of the EHCache that contains the cached items for this
     * application
     */
    protected static final String CACHE_NAME = "DD4T-Objects";
    protected static final String CACHE_NAME_DEPENDENCY = "DD4T-Dependencies";
    protected static final String DEPENDENT_KEY_FORMAT = "%s:%s";
    protected static final int ADJUST_TTL = 2;

    protected boolean checkForPreview = false;

    protected static String getKey (Serializable key) {
        String[] parts = ((String) key).split(":");
        switch (parts.length) {
            case 0:
                return "";
            case 1:
                return String.format(DEPENDENT_KEY_FORMAT, parts[0], "");
            default:
                return String.format(DEPENDENT_KEY_FORMAT, parts[0], parts[1]);
        }
    }

    protected static String getKey(int publicationId, int itemId) {
        return String.format(DEPENDENT_KEY_FORMAT, publicationId, itemId);
    }

    protected boolean doCheckForPreview () {
        return checkForPreview;
    }

    protected void setCheckForPreview (boolean breakOnPreview) {
        this.checkForPreview = breakOnPreview;
    }
}
