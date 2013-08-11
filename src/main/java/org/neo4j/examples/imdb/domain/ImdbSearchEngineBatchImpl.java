/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.examples.imdb.domain;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ImdbSearchEngineBatchImpl implements ImdbSearchEngine
{
    private static final String TITLE_INDEX = "title";
    private static final String NAME_INDEX = "name";
    
    @Autowired @Qualifier("batchIndexerSearch")
    private BatchInserterIndex batchIndexerSearch;

    @Autowired @Qualifier("batchIndexerFacets")
    private BatchInserterIndex batchIndexerFacets;
    
    @Autowired
    private BatchInserter batchInserter;
    
    @Autowired
    private ImdbLabelEngine labelEngine;
    
    private Long rootCategory = null;
       
    private final Map<String, FacetCategory> inMemoryCategoryIndex = new HashMap<String, FacetCategory>();

    @Override
    public void indexActor( Person actor )
    {
        batchIndexerSearch.add(actor.getId(), MapUtil.map(NAME_INDEX, actor.getName()));
    }

    @Override
    public void indexMovie( Movie movie )
    {
        batchIndexerSearch.add(movie.getId(), MapUtil.map(TITLE_INDEX, movie.getTitle()));
        indexProperty(movie.getId(), "year", movie.getYear());
        indexProperty(movie.getId(), "format", movie.getFormat().toString());
    }
    
    @Override
    public void indexProperty(long nodeId, String key, Object value)
    {
        batchIndexerSearch.add(nodeId, MapUtil.map(key, value));
        
        //also lead via relationship to a special facet node depicting the property
        if (value instanceof Float) {
            value = round((Float) value);
        }
        batchInserter.createRelationship(nodeId, getFacetNode(key, value), RelTypes.FACET, null);                //
    }
    
    private long getFacetNode(String key, Object value)
    {
        FacetCategory category = getCategoryNode(key);
        
        Long facetId = category.get(value.toString());
        if (facetId == null) {
            facetId = batchInserter.createNode(MapUtil.map("facet", key, "value", value), labelEngine.getLabel("FACET"));
            batchIndexerFacets.add(facetId, MapUtil.map("facet", key));
            batchIndexerFacets.add(facetId, MapUtil.map(key, value));
            labelEngine.addPropertyToLabel("FACET", "facet");
            labelEngine.addPropertyToLabel("FACET", key);
            batchInserter.createRelationship(facetId, category.getId(), RelTypes.VALUE, null); 
            category.put(value.toString(), facetId);
        }
        
        return facetId;
    }
    
    private FacetCategory getCategoryNode(String key)
    {
        FacetCategory category = inMemoryCategoryIndex.get(key);
        if (category == null) {
            category = new FacetCategory(batchInserter.createNode(MapUtil.map("category", key), labelEngine.getLabel("CATEGORY")));
            batchIndexerFacets.add(category.getId(), MapUtil.map("category", key));
            inMemoryCategoryIndex.put(key, category);
            labelEngine.addPropertyToLabel("CATEGORY", "category");
            batchInserter.createRelationship(category.getId(), getRootCategory() , RelTypes.CATEGORY, null); 
        }
        
        return category;
    }
    
    public static Float round(Float d) {
        BigDecimal bd = new BigDecimal(d.toString());
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public IndexHits<Node> searchActor( String name )
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public IndexHits<Node> searchMovie( String title )
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private long getRootCategory() {
        if (rootCategory == null) {
            rootCategory = batchInserter.createNode(MapUtil.map("root", "category"));
            batchIndexerFacets.add(rootCategory, MapUtil.map("root", "category"));
        }
        return rootCategory;
    }

    private static class FacetCategory extends Object{
        private Long nodeId;
        private Map<String, Long> inMemoryFacetIndex = new HashMap<String, Long>();

        public FacetCategory(Long nodeId) {
            this.nodeId = nodeId;
        }
        
        public Long getId()
        {
            return nodeId;
        }
        
        public Long get(String key) {
            return inMemoryFacetIndex.get(key);
        }
        
        public void put (String key, Long id) {
            inMemoryFacetIndex.put(key, id);
        } 
    }
}
