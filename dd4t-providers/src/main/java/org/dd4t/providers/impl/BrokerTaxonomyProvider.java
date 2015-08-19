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

package org.dd4t.providers.impl;

import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.storage.RelatedKeyword;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BaseDAO;
import com.tridion.storage.dao.WrappableDAO;
import com.tridion.storage.persistence.JPABaseDAO;
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import com.tridion.util.TCMURI;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.providers.TaxonomyProvider;
import org.dd4t.providers.serializer.KeywordBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Provides access to taxonomies published to a Content Delivery database. It returns keywords with all their parent/
 * children relationships resolved. It also provides the capability to retrieve related items (i.e. Tridion items that
 * use each Keyword in the taxonomy directly).
 *
 * @author Mihai Cadariu
 */
public class BrokerTaxonomyProvider extends BaseBrokerProvider implements TaxonomyProvider {
    public static final String SELECT_RELATED_KEYWORDS = "select distinct(rk) from RelatedKeyword rk, ItemMeta im" +
            " where rk.publicationId = :publicationId and rk.taxonomyId = :taxonomyId and im.itemType = :itemType and rk.itemId = im.itemId and rk.publicationId = im.publicationId";

    private static final String SELECT_RELATED_COMPONENTS_BY_SCHEMA = "select distinct(rk) from RelatedKeyword rk, ItemMeta im, ComponentMeta cm" +
            " where rk.publicationId = :publicationId and rk.taxonomyId = :taxonomyId and im.itemType = 16 and rk.itemId = im.itemId and rk.publicationId = im.publicationId and" +
            " cm.publicationId = im.publicationId and cm.itemId = im.itemId and cm.schemaId = :schemaId";

    /**
     * Retrieves the Keyword object model with all its Parent/Children relationships resolved.
     *
     * @param taxonomyURI String representing the taxonomy TCMURI
     * @return Keyword the resolved taxonomy with its parent/children relationships
     * @throws StorageException if something went wrong during accessing the CD DB
     */
    public Keyword getTaxonomy(String taxonomyURI) throws StorageException {
        TaxonomyFactory factory = new TaxonomyFactory();
        return factory.getTaxonomyKeywords(taxonomyURI);
    }

    /**
     * Returns a list of RelatedKeyword objects representing the usage of each Keyword under the given taxonomy URI
     * and its direct using item. Returned items are only of the given type.
     *
     * @param taxonomyURI String representing the root taxonomy Keyword TCMURI
     * @param itemType    int representing the item type id to retrieve
     * @return List a list of RelatedKeyword objects holding item usage information
     * @throws ParseException   if the given Keyword URI does not represent a valid TCMURI
     * @throws StorageException if something went wrong during accessing the CD DB
     */

    public List<RelatedKeyword> getRelatedItems(String taxonomyURI, int itemType) throws ParseException, StorageException {
        TCMURI taxonomyTcmUri = new TCMURI(taxonomyURI);
        int publicationId = taxonomyTcmUri.getPublicationId();

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("publicationId", publicationId);
        queryParams.put("taxonomyId", taxonomyTcmUri.getItemId());
        queryParams.put("itemType", itemType);

        return getJPADAO(publicationId).executeQueryListResult(SELECT_RELATED_KEYWORDS, queryParams);
    }

    /**
     * Returns a list of RelatedKeyword objects representing the usage of each Keyword under the given taxonomy URI
     * and its direct using item. Returned items are only Components based on the given Schema URI.
     *
     * @param taxonomyURI String representing the root taxonomy Keyword TCMURI
     * @param schemaURI   String representing the filter for classified related Components to return for each Keyword
     * @return List a list of RelatedKeyword objects holding item usage information
     * @throws ParseException   if the given Keyword URI does not represent a valid TCMURI
     * @throws StorageException if something went wrong during accessing the CD DB
     */
    public List<RelatedKeyword> getRelatedComponentsBySchema(String taxonomyURI, String schemaURI)
            throws ParseException, StorageException {
        TCMURI taxonomyTcmUri = new TCMURI(taxonomyURI);
        TCMURI schemaTcmUri = new TCMURI(schemaURI);
        int publicationId = taxonomyTcmUri.getPublicationId();

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("publicationId", publicationId);
        queryParams.put("taxonomyId", taxonomyTcmUri.getItemId());
        queryParams.put("schemaId", schemaTcmUri.getItemId());

        return getJPADAO(publicationId).executeQueryListResult(SELECT_RELATED_COMPONENTS_BY_SCHEMA, queryParams);
    }

    /*
    Looks for the JPABaseDAO defined in the storage config. If XPM wrappers are used, it searches inside the wrapper.
     */
    private JPABaseDAO getJPADAO(int publicationId) throws StorageException {
        BaseDAO baseDAO = StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.ITEM_META);
        boolean loop = true;

        while (loop) {
            if (baseDAO instanceof WrappableDAO) {
                WrappableDAO wrappableDAO = (WrappableDAO) baseDAO;
                baseDAO = wrappableDAO.getWrapped();
            } else {
                loop = false;
            }

            if (baseDAO instanceof JPABaseDAO) {
                return (JPABaseDAO) baseDAO;
            }
        }
        throw new StorageException("Cannot find JPABaseDAO. Please check your storage bindings.");
    }

    @Override
    public org.dd4t.contentmodel.Keyword getTaxonomyByURI(final String taxonomyURI, final boolean resolveContent) throws ItemNotFoundException, SerializationException {
        try {
            com.tridion.taxonomies.Keyword keyword = getTaxonomy(taxonomyURI);
            if (keyword == null) {
                throw new ItemNotFoundException("Taxonomy with URI " + taxonomyURI + " was not found");
            }

            KeywordBuilder keywordBuilder = new KeywordBuilder();
            if (resolveContent) {
                keywordBuilder.with(getRelatedContent(taxonomyURI));
            }

            return keywordBuilder.build(keyword);
        } catch (StorageException | ParseException e) {
            throw new SerializationException(e);
        }
    }

    /**
     * Retrieves a map having Keyword TCMURIs as keys and as values a set of TCMURIs representing the items that make
     * direct 'use' the the Keyword in the key.
     *
     * @param taxonomyURI String representing the root taxonomy Keyword TCMURI
     * @return Map of items that use each keyword directly
     * @throws IOException if something went wrong during accessing the CD DB
     */
    private Map<String, Set<org.dd4t.core.util.TCMURI>> getRelatedContent(final String taxonomyURI) throws ParseException, StorageException {
        Map<String, Set<org.dd4t.core.util.TCMURI>> result = new HashMap<>();

        mergeRelatedItems(getRelatedItems(taxonomyURI, ItemTypes.COMPONENT),
                result, ItemTypes.COMPONENT);
        mergeRelatedItems(getRelatedItems(taxonomyURI, ItemTypes.PAGE),
                result, ItemTypes.PAGE);

        return result;
    }

    void mergeRelatedItems(List<RelatedKeyword> relatedKeywords, Map<String, Set<org.dd4t.core.util.TCMURI>> result, int itemType) {
        for (RelatedKeyword keyword : relatedKeywords) {
            int publicationId = keyword.getPublicationId();
            String key = String.format("tcm:%d-%d-1024", publicationId, keyword.getKeywordId());
            Set<org.dd4t.core.util.TCMURI> itemList = result.get(key);

            if (itemList == null) {
                itemList = new HashSet<>();
                result.put(key, itemList);
            }

            org.dd4t.core.util.TCMURI itemURI = new org.dd4t.core.util.TCMURI.Builder(publicationId, keyword.getItemId())
                    .itemType(itemType)
                    .version(0)
                    .create();
            itemList.add(itemURI);
        }
    }

    @Override public String getTaxonomyFilterBySchema(final String taxonomyURI, final String schemaURI) throws ItemNotFoundException, SerializationException {
        return null;
    }
}