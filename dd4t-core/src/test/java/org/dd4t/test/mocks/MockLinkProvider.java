package org.dd4t.test.mocks;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.LinkProvider;

/**
 * MockLinkProvider.
 */
public class MockLinkProvider implements LinkProvider{
    @Override
    public String resolveComponent(final String targetComponentUri) throws ItemNotFoundException, SerializationException {
        return "/resolved/"+targetComponentUri;
    }

    @Override
    public String resolveComponentFromPage(final String targetComponentUri, final String sourcePageUri) throws ItemNotFoundException, SerializationException {
        return "/resolved/"+targetComponentUri;
    }

    @Override
    public String resolveComponent(final String targetComponentUri, final String excludeComponentTemplateUri) throws ItemNotFoundException, SerializationException {
        return "/resolved/"+targetComponentUri;
    }
}
