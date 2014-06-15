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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

class ImdbServiceBatchImpl implements ImdbService
{
    @Autowired
    private BatchInserter batchInserter;
    
    @Autowired @Qualifier("batchIndexerExact")
    private BatchInserterIndex batchIndexerExact;

    @Autowired
    private ImdbSearchEngine searchEngine;
    
    @Autowired
    private ImdbLabelEngine labelEngine;
    
    private static final String TITLE_INDEX = "title";
    private static final String NAME_INDEX = "name";

    @Override
    public Person createPerson( final String name ) {
         Person person = getPerson(name);
         if (person == null) {
             person = _createPerson(name);
         }
         return person;
    }
    
    private Person _createPerson( final String name )
    {
        final Person actor = new PersonBatchImpl( batchInserter.createNode(MapUtil.map(NAME_INDEX, name), labelEngine.getLabel("PERSON")) );
        actor.setName(name);
        searchEngine.indexActor( actor );
        batchIndexerExact.add(actor.getId(), MapUtil.map(NAME_INDEX, name));
        return actor;
    }

    @Override
    public Movie createMovie( final String title, final int year, MovieFormat format) 
    {
        Movie movie = getMovie(title);
        if (movie == null) {
            movie = _createMovie(title, year, format);
        }
        return movie;
    }
    
    private Movie _createMovie( final String title, final int year, MovieFormat format )
    {
        final Movie movie = new MovieBatchImpl( batchInserter.createNode(MapUtil.map(TITLE_INDEX, title, "year", year, "format", format.toString() ), labelEngine.getLabel("MOVIE")));
        movie.setTitle(title);
        movie.setYear(year);
        movie.setFormat(format);
        searchEngine.indexMovie( movie );
        batchIndexerExact.add(movie.getId(), MapUtil.map(TITLE_INDEX, title));
        return movie;
    }
    
    @Override
    public Role createRole( final Person actor, final Movie movie,
        final RelTypes roleName, final String characterName )
    {
        if ( actor == null )
        {
            throw new IllegalArgumentException( "Null actor" );
        }
        if ( movie == null )
        {
            throw new IllegalArgumentException( "Null movie" );
        }
        
        Map<String, Object> properties = null;
        if ( characterName != null )
        {
            properties = MapUtil.map("character_name", characterName);
        }
        final Role role = new RoleImpl(batchInserter.createRelationship(actor.getId(), movie.getId(), roleName,  properties));
        
        return role;
    }
    
    
    @Override
    public Person getPerson( final String name )
    {
        Node actorNode = getSingleNode(NAME_INDEX, name);
        Person actor = null;
        if ( actorNode != null )
        {
            actor = new PersonBatchImpl( actorNode.getId() );
        }
        return actor;
    }

    private Node getSingleNode(String key, String value) {
        BatchNode node = null;
        Long nodeId = batchIndexerExact.get(key, value).getSingle();

        if (nodeId != null) {
            node = new BatchNode(nodeId, batchInserter.getNodeProperties(nodeId));
        }
        return node;
    }

    @Override
    public Movie getMovie( final String title )
    {
        Node movieNode = getExactMovieNode( title );
        Movie movie = null;
        if ( movieNode != null )
        {
            movie = new MovieBatchImpl( movieNode.getId() );
        }
        return movie;
    }

    private Node getExactMovieNode( final String title )
    {
        return getSingleNode( TITLE_INDEX, title );
    }

    @Override
    public void setupReferenceRelationship()
    {
        Node baconNode = getSingleNode( "name", "Bacon, Kevin (I)" );
        if ( baconNode == null )
        {
            throw new NoSuchElementException(
                "Unable to find Kevin Bacon actor" );
        }
        batchInserter.createRelationship(batchInserter.getReferenceNode(), baconNode.getId(), RelTypes.IMDB, null);
    }

    @Override
    public List<?> getBaconPath( final Person actor )
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void addPropertiesToMovie(String title, Map<String, ? extends Object> properties) {
        Movie movie = getMovie(title);
        if (movie != null) {
            for (Map.Entry<String, ? extends Object> en : properties.entrySet()) {
                String key = en.getKey();
                Object value = en.getValue();
                batchInserter.setNodeProperty(movie.getId(), key, value);
                searchEngine.indexProperty(movie.getId(), key, value);
                labelEngine.addPropertyToLabel("MOVIE", key);
            }
        }
    }

    @Override
    public void addAtributeMultipleToMovie(String title, String attributeName, List<String> keywords) {
        Movie movie = getMovie(title);
        if (movie != null) {
            for (String keyword : keywords) {
                searchEngine.indexProperty(movie.getId(), attributeName, keyword);
            }
            batchInserter.setNodeProperty(movie.getId(), attributeName, keywords.toArray(new String[keywords.size()]));
        }
    }

    @Override
    public void indexRoles(Person actor, Set<RelTypes> distinctRoles) {
        Iterator<RelTypes> it = distinctRoles.iterator();
        while(it.hasNext()) {
            RelTypes relTypes = it.next();
            searchEngine.indexProperty(actor.getId(), "role", relTypes.toString());
        }
    }
    
        @Override
    public void flush() {
        batchIndexerExact.flush();
    }
}