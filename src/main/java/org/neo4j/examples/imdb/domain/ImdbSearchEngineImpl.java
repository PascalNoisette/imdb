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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.ValueContext;
import org.springframework.beans.factory.annotation.Autowired;

public class ImdbSearchEngineImpl implements ImdbSearchEngine
{
    private static final String NAME_PART_INDEX = "name.part";
    private static final String WORD_PROPERTY = "word";
    private static final String COUNT_PROPERTY = "count_uses";
    private static final String TITLE_PART_INDEX = "title.part";
    private static final String SEARCH_INDEX_NAME = "search";

    private GraphDatabaseService graphDbService;

    private Index<Node> nodeIndex;

    @Autowired
    public void setGraphDbService(GraphDatabaseService graphDbService) {
        this.graphDbService = graphDbService;
        this.nodeIndex = graphDbService.index().forNodes(SEARCH_INDEX_NAME);
    }

    @Override
    public void indexActor( Person actor )
    {
        index( actor.getName(), ((PersonImpl) actor).getUnderlyingNode(),
            NAME_PART_INDEX );
    }

    @Override
    public void indexMovie( Movie movie )
    {
        index( movie.getTitle(), ((MovieImpl) movie).getUnderlyingNode(),
            TITLE_PART_INDEX );
    }

    @Override
    public IndexHits<Node> searchActor( String name )
    {
        return nodeIndex.get( name, NAME_PART_INDEX );
    }

    @Override
    public IndexHits<Node> searchMovie( String title )
    {
        return nodeIndex.get( title, TITLE_PART_INDEX );
    }

    private String[] splitSearchString( final String value )
    {
        return value.toLowerCase( Locale.ENGLISH ).split( "[^\\w]+" );
    }

    private void index( final String value, final Node node,
        final String partIndexName )
    {
        for ( String part : splitSearchString( value ) )
        {
            nodeIndex.add(node, partIndexName, part);
        }
    }

    @Override
    public void indexProperty(long nodeId, String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
