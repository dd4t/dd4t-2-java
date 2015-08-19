package org.dd4t.providers.serializer;

import com.tridion.broker.StorageException;
import com.tridion.meta.CustomMeta;
import com.tridion.meta.NameValuePair;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.impl.DateField;
import org.dd4t.contentmodel.impl.KeywordImpl;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.util.DateUtils;
import org.dd4t.core.util.TCMURI;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class KeywordBuilder {

    private Map<String, Set<TCMURI>> relatedItems = new HashMap<>();

    public KeywordBuilder() {
    }

    public KeywordBuilder with(Map<String, Set<TCMURI>> relatedItems) {
        this.relatedItems = relatedItems;
        return this;
    }

    public Keyword build(com.tridion.taxonomies.Keyword keyword) throws StorageException {
        KeywordImpl result = new KeywordImpl();

        result.setId(keyword.getKeywordURI());
        result.setDescription(keyword.getKeywordDescription());
        result.setKey(keyword.getKeywordKey());
        result.setTaxonomyId(keyword.getTaxonomyURI());
        result.setTitle(keyword.getKeywordName());
        result.setMetadata(buildMetadata(keyword.getKeywordMeta()));
        result.setParentKeywords(buildParents(keyword));
        result.setRelatedKeywords(buildRelatedKeywords(keyword));
        result.setPath(setPath(result, keyword));
        result.setChildKeywords(buildChildren(keyword));
        result.setClassifiedItems(buildClassifiedItems(keyword, relatedItems));

        return result;
    }

    List<TCMURI> buildClassifiedItems(com.tridion.taxonomies.Keyword keyword, Map<String, Set<TCMURI>> relatedItems) {
        Set<TCMURI> itemSet = relatedItems.get(keyword.getKeywordURI());
        return itemSet != null ? new ArrayList<>(itemSet) : Collections.<TCMURI>emptyList();
    }

    private String setPath(Keyword k, com.tridion.taxonomies.Keyword keyword) {
        String result = k.hasParents() ? k.getParentKeywords().get(0).getPath() : "";
        result += "/" + keyword.getKeywordName();

        return result;
    }

    private Map<String, Field> buildMetadata(CustomMeta meta) {
        Map<String, Field> result = new HashMap<>();

        for (Map.Entry<String, NameValuePair> entry : meta.getNameValues().entrySet()) {
            String key = entry.getKey();
            NameValuePair value = entry.getValue();

            switch (value.getMetadataType()) {
                case DATE:
                    DateField dateField = new DateField();
                    dateField.setName(key);
                    List<String> dateValues = new ArrayList<>();
                    for (Object objectValue : value.getMultipleValues()) {
                        dateValues.add(DateUtils.convertSqlTimestampToString((Timestamp) objectValue));
                    }
                    dateField.setDateTimeValues(dateValues);
                    result.put(key, dateField);
                    break;
                case FLOAT:
                    NumericField numericField = new NumericField();
                    numericField.setName(key);
                    List<Double> numericValues = new ArrayList<>();
                    numericField.setNumericValues(numericValues);
                    for (Object objectValue : value.getMultipleValues()) {
                        numericValues.add(Double.valueOf((Float) objectValue));
                    }
                    result.put(key, numericField);
                    break;
                default:
                    TextField textField = new TextField();
                    textField.setName(key);
                    List<String> stringList = new ArrayList<>();
                    textField.setTextValues(stringList);
                    for (Object objectValue : value.getMultipleValues()) {
                        stringList.add((String) objectValue);
                    }
                    result.put(key, textField);
                    break;
            }
        }

        return result;
    }

    private List<Keyword> buildParents(com.tridion.taxonomies.Keyword keyword) throws StorageException {
        List<Keyword> result = new ArrayList<>();

        for (com.tridion.taxonomies.Keyword parent : keyword.getParentKeywords()) {
            result.add(build(parent));
        }

        return result;
    }

    private List<TCMURI> buildRelatedKeywords(com.tridion.taxonomies.Keyword keyword) throws StorageException {
        List<TCMURI> result = new ArrayList<>();

        for (String tcmUri : keyword.getRelatedKeywordURIS()) {
            try {
                TCMURI relatedURI = new TCMURI(tcmUri);
                result.add(relatedURI);
            } catch (ParseException pe) {
                throw new StorageException(pe);
            }
        }

        return result;
    }

    private List<Keyword> buildChildren(com.tridion.taxonomies.Keyword keyword) throws StorageException {
        List<Keyword> result = new ArrayList<>();

        for (com.tridion.taxonomies.Keyword child : keyword.getKeywordChildren()) {
            result.add(build(child));
        }

        return result;
    }
}
