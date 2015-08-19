package org.dd4t.providers.impl;

import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.storage.RelatedKeyword;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BrokerTaxonomyProviderTest {

    private TestableBrokerTaxonomyProvider provider;
    private com.tridion.taxonomies.Keyword keyword;
    private List<RelatedKeyword> relatedItems;

    private class TestableBrokerTaxonomyProvider extends BrokerTaxonomyProvider {
        @Override
        public com.tridion.taxonomies.Keyword getTaxonomy(String taxonomyURI) throws StorageException {
            return keyword;
        }

        @Override
        public List<RelatedKeyword> getRelatedItems(String taxonomyURI, int itemType) throws ParseException, StorageException {
            return relatedItems;
        }
    }

    @Before
    public void initialise() {
        provider = new TestableBrokerTaxonomyProvider();
    }

    @Test public void
    get_keyword_from_taxonomy_uri_without_related_content() throws SerializationException, ItemNotFoundException {
        final String taxonomyURI = "tcm:1-2-3";
        final String keywordURI = "keywordURI";
        keyword = new com.tridion.taxonomies.Keyword(taxonomyURI, keywordURI);

        Keyword keyword = provider.getTaxonomyByURI(taxonomyURI, false);

        assertThat(keyword.getTaxonomyId(), is(taxonomyURI));
        assertThat(keyword.getId(), is(keywordURI));
    }

    @Test public void
    get_keyword_from_taxonomy_uri_with_related_content() throws SerializationException, ItemNotFoundException {
        final String taxonomyURI = "tcm:1-2-3";
        final String keywordURI = "keywordURI";
        keyword = new com.tridion.taxonomies.Keyword(taxonomyURI, keywordURI);
        relatedItems = new ArrayList<>();
        RelatedKeyword relatedKeyword = new RelatedKeyword();
        relatedKeyword.setPublicationId(1);
        relatedKeyword.setItemId(2);
        relatedItems.add(relatedKeyword);

        Keyword keyword = provider.getTaxonomyByURI(taxonomyURI, true);

        assertThat(keyword.getTaxonomyId(), is(taxonomyURI));
        assertThat(keyword.getId(), is(keywordURI));
    }

    @Test public void
    add_elements_to_map() {
        Map<String, Set<org.dd4t.core.util.TCMURI>> elements = new HashMap<>();

        List<RelatedKeyword> relatedKeywords = new ArrayList<>();
        relatedKeywords.add(createRelatedKeyword(1, 1, 10));
        relatedKeywords.add(createRelatedKeyword(2, 2, 20));

        provider.mergeRelatedItems(relatedKeywords, elements, ItemTypes.COMPONENT);

        assertThat(elements.size(), is(2));
        assertThat(relatedKeywords.size(), is(2));
    }

    @Test public void
    add_unique_elements_to_map() {
        Map<String, Set<org.dd4t.core.util.TCMURI>> elements = new HashMap<>();

        List<RelatedKeyword> relatedKeywords = new ArrayList<>();
        relatedKeywords.add(createRelatedKeyword(1, 1, 10));
        relatedKeywords.add(createRelatedKeyword(1, 1, 10));
        relatedKeywords.add(createRelatedKeyword(3, 2, 20));

        BrokerTaxonomyProvider provider = new BrokerTaxonomyProvider();
        provider.mergeRelatedItems(relatedKeywords, elements, ItemTypes.COMPONENT);

        assertThat(elements.size(), is(2));
        assertThat(relatedKeywords.size(), is(3));
    }

    private RelatedKeyword createRelatedKeyword(int keywordId, int pubId, int itemId) {
        RelatedKeyword relatedKeyword = new RelatedKeyword();
        relatedKeyword.setKeywordId(keywordId);
        relatedKeyword.setPublicationId(pubId);
        relatedKeyword.setItemId(itemId);
        return relatedKeyword;
    }


}