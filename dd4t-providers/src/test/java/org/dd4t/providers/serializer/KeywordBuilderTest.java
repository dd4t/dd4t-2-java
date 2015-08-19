package org.dd4t.providers.serializer;

import com.tridion.broker.StorageException;
import com.tridion.taxonomies.Keyword;
import org.dd4t.core.util.TCMURI;
import org.junit.Test;

import java.text.ParseException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KeywordBuilderTest {

    @Test public void
    build_a_keyword() throws StorageException {
        final String taxonomyURI = "tcm:1-2-3";
        final String keywordURI = "keywordURI";
        Keyword keyword = new Keyword(taxonomyURI, keywordURI);

        org.dd4t.contentmodel.Keyword result = new KeywordBuilder().build(keyword);

        assertThat(result.getTaxonomyId(), is(taxonomyURI));
        assertThat(result.getId(), is(keywordURI));
    }

    @Test public void
    build_related_items() throws ParseException {
        final String taxonomyURI = "tcm:1-2-3";
        final String keywordURI = "keywordURI";
        Keyword keyword = new Keyword(taxonomyURI, keywordURI);

        Set<TCMURI> tcmUris = new HashSet<>();
        tcmUris.add(new TCMURI.Builder(taxonomyURI).create());
        Map<String, Set<TCMURI>> relatedItems = new HashMap<>();
        relatedItems.put(keywordURI, tcmUris);

        List<TCMURI> elements = new KeywordBuilder().buildClassifiedItems(keyword, relatedItems);
        assertThat(elements.size(), is(1));
    }

    @Test public void
    build_related_items_when_null() throws ParseException {
        final String taxonomyURI = "tcm:1-2-3";
        final String keywordURI = "keywordURI";
        Keyword keyword = new Keyword(taxonomyURI, keywordURI);

        Map<String, Set<TCMURI>> relatedItems = new HashMap<>();

        List<TCMURI> elements = new KeywordBuilder().buildClassifiedItems(keyword, relatedItems);
        assertThat(elements.size(), is(0));
    }






}
