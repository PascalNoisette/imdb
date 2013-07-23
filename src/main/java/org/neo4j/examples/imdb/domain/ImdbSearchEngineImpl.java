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
            NAME_PART_INDEX, ImdbSearchRelTypes.PART_OF_NAME );
    }

    @Override
    public void indexMovie( Movie movie )
    {
        index( movie.getTitle(), ((MovieImpl) movie).getUnderlyingNode(),
            TITLE_PART_INDEX, ImdbSearchRelTypes.PART_OF_TITLE );
    }

    @Override
    public Node searchActor( String name )
    {
        return searchSingle( name, NAME_PART_INDEX, ImdbSearchRelTypes.PART_OF_NAME );
    }

    @Override
    public Node searchMovie( String title )
    {
        return searchSingle( title, TITLE_PART_INDEX, ImdbSearchRelTypes.PART_OF_TITLE );
    }

    private String[] splitSearchString( final String value )
    {
        return value.toLowerCase( Locale.ENGLISH ).split( "[^\\w]+" );
    }

    private Node getSingleNode(String key, String value)
    {
        IndexHits<Node> hits = nodeIndex.get( key, value );
        for ( Node node : hits )
        {
            return node;
        }
        return null;
    }

    private void index( final String value, final Node node,
        final String partIndexName, final ImdbSearchRelTypes relType )
    {
        for ( String part : splitSearchString( value ) )
        {
            Node wordNode = getSingleNode(partIndexName, part);
            if ( wordNode == null )
            {
                wordNode = graphDbService.createNode();
                // not needed for the functionality
                nodeIndex.add(wordNode, partIndexName, part);

                wordNode.setProperty( WORD_PROPERTY, part );
            }
            wordNode.createRelationshipTo( node, relType );
            wordNode.setProperty( COUNT_PROPERTY, ((Integer) wordNode
                .getProperty( COUNT_PROPERTY, 0 )) + 1 );
        }
    }

    private Node searchSingle( final String value, final String indexName,
        final ImdbSearchRelTypes wordRelType )
    {
        // get the words in the search
        final List<Node> wordList = findSearchWords( value, indexName );
        if ( wordList.isEmpty() )
        {
            return null;
        }
        final Node startNode = wordList.remove( 0 );
        // set up a match to use if everything else fails
        Node match = startNode.getRelationships( wordRelType ).iterator()
            .next().getEndNode();
        // check if there is only one node in the list
        if ( wordList.isEmpty() )
        {
            return match;
        }
        int bestCount = 0;
        final int listSize = wordList.size();
        for ( Relationship targetRel : startNode.getRelationships( wordRelType ) )
        {
            Node targetNode = targetRel.getEndNode();
            int hitCount = 0;
            for ( Relationship wordRel : targetNode
                .getRelationships( wordRelType ) )
            {
                if ( wordList.contains( wordRel.getStartNode() ) )
                {
                    if ( ++hitCount == listSize )
                    {
                        return targetNode;
                    }
                }
            }
            if ( hitCount > bestCount )
            {
                match = targetNode;
                bestCount = hitCount;
            }
        }
        return match;
    }

    private List<Node> findSearchWords( final String userInput,
        final String partIndexName )
    {
        final List<Node> wordList = new ArrayList<Node>();
        // prepare search terms
        for ( String part : splitSearchString( userInput ) )
        {
            Node wordNode = getSingleNode(partIndexName, part);
            if ( wordNode == null || !wordNode.hasRelationship()
                || wordList.contains( wordNode ) )
            {
                continue;
            }
            wordList.add( wordNode );
        }
        if ( wordList.isEmpty() )
        {
            return Collections.emptyList();
        }
        // sort words according to the number of relationships (ascending)
        Collections.sort( wordList, new Comparator<Node>()
        {
            @Override
            public int compare( final Node left, final Node right )
            {
                int leftCount = (Integer) left.getProperty( COUNT_PROPERTY, 0 );
                int rightCount = (Integer) right
                    .getProperty( COUNT_PROPERTY, 0 );
                return leftCount - rightCount;
            }
        } );
        return wordList;
    }
}
