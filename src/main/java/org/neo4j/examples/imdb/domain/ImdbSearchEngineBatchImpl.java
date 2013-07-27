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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.ValueContext;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ImdbSearchEngineBatchImpl implements ImdbSearchEngine
{
    private static final String TITLE_INDEX = "title";
    private static final String NAME_INDEX = "name";
    
    @Autowired @Qualifier("batchIndexerSearch")
    private BatchInserterIndex batchIndexerSearch;

    @Override
    public void indexActor( Person actor )
    {
        indexProperty(actor.getId(), NAME_INDEX,  actor.getName());
    }

    @Override
    public void indexMovie( Movie movie )
    {
        indexProperty(movie.getId(), TITLE_INDEX,  movie.getTitle());
        indexProperty(movie.getId(), "year", movie.getYear());
    }
    
    @Override
    public void indexProperty(long nodeId, String key, Object value)
    {
        batchIndexerSearch.add(nodeId, MapUtil.map(key, value));
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
}
