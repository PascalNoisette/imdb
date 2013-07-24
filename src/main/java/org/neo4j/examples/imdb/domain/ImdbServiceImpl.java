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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.neo4j.examples.imdb.util.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class ImdbServiceImpl implements ImdbService
{
    private GraphDatabaseService graphDbService;
    private Index<Node> nodeIndex;

    @Autowired
    private PathFinder pathFinder;
    @Autowired
    private ImdbSearchEngine searchEngine;

    private static final String EXACT_INDEX_NAME = "exact";
    private static final String TITLE_INDEX = "title";
    private static final String NAME_INDEX = "name";


    @Autowired
    public void setGraphDbService(GraphDatabaseService graphDbService) {
        this.graphDbService = graphDbService;
        this.nodeIndex = graphDbService.index().forNodes(EXACT_INDEX_NAME);
    }

    @Override
    public Person createPerson( final String name ) {
         Person person = getExactPerson(name);
         if (person == null) {
             person = _createPerson(name);
         }
         return person;
    }

    private Person _createPerson( final String name )
    {
        final Node actorNode = graphDbService.createNode();
        final Person actor = new PersonImpl( actorNode );
        actor.setName( name );
        searchEngine.indexActor( actor );
        nodeIndex.add(actorNode, NAME_INDEX, name);
        return actor;
    }

    @Override
    public Movie createMovie( final String title, final int year ) 
    {
        Movie movie = getExactMovie(title);
        if (movie == null) {
            movie = _createMovie(title, year);
        }
        return movie;
    }
    
    private Movie _createMovie( final String title, final int year )
    {
        final Node movieNode = graphDbService.createNode();
        final Movie movie = new MovieImpl( movieNode );
        movie.setTitle( title );
        movie.setYear( year );
        searchEngine.indexMovie( movie );
        nodeIndex.add(movieNode, TITLE_INDEX, title);
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
        final Node actorNode = ((PersonImpl) actor).getUnderlyingNode();
        final Node movieNode = ((MovieImpl) movie).getUnderlyingNode();
        final Relationship rel = actorNode.createRelationshipTo( movieNode,
            roleName);
        final Role role = new RoleImpl( rel );
        if ( characterName != null )
        {
            role.setCharacter(characterName );
        }
        return role;
    }

    @Override
    public Person getActor( final String name )
    {
        Node actorNode = getSingleNode(NAME_INDEX, name);
        if ( actorNode == null )
        {
            actorNode = searchEngine.searchActor( name );
        }
        Person actor = null;
        if ( actorNode != null )
        {
            actor = new PersonImpl( actorNode );
        }
        return actor;
    }
    
    
    public Person getExactPerson( final String name )
    {
        Node actorNode = getSingleNode(NAME_INDEX, name);
        Person actor = null;
        if ( actorNode != null )
        {
            actor = new PersonImpl( actorNode );
        }
        return actor;
    }

    private Node getSingleNode(String key, String value) {
        IndexHits<Node> hits = nodeIndex.get(key, value);
        for (Node node : hits) {
            return node;
        }
        return null;
    }

    @Override
    public Movie getMovie( final String title )
    {
        Node movieNode = getExactMovieNode( title );
        if ( movieNode == null )
        {
            movieNode = searchEngine.searchMovie( title );
        }
        Movie movie = null;
        if ( movieNode != null )
        {
            movie = new MovieImpl( movieNode );
        }
        return movie;
    }

    @Override
    public Movie getExactMovie( final String title )
    {
        Node movieNode = getExactMovieNode( title );
        Movie movie = null;
        if ( movieNode != null )
        {
            movie = new MovieImpl( movieNode );
        }
        return movie;
    }

    private Node getExactMovieNode( final String title )
    {
        return getSingleNode( TITLE_INDEX, title );
    }

    @Override
    @Transactional
    public void setupReferenceRelationship()
    {
        Node baconNode = getSingleNode( "name", "Bacon, Kevin" );
        if ( baconNode == null )
        {
            throw new NoSuchElementException(
                "Unable to find Kevin Bacon actor" );
        }
        Node referenceNode = graphDbService.getReferenceNode();
        referenceNode.createRelationshipTo( baconNode, RelTypes.IMDB );
    }

    @Override
    public List<?> getBaconPath( final Person actor )
    {
        final Node baconNode;
        if ( actor == null )
        {
            throw new IllegalArgumentException( "Null actor" );
        }
        try
        {
            baconNode = graphDbService.getReferenceNode().getSingleRelationship(
                RelTypes.IMDB, Direction.OUTGOING ).getEndNode();
        }
        catch ( NoSuchElementException e )
        {
            throw new NoSuchElementException(
                "Unable to find Kevin Bacon actor" );
        }
        final Node actorNode = ((PersonImpl) actor).getUnderlyingNode();
        final List<Node> list = pathFinder.shortestPath( actorNode, baconNode,
            RelTypes.ACTOR );
        return convertNodesToActorsAndMovies( list );
    }

    private List<?> convertNodesToActorsAndMovies( final List<Node> list )
    {
        final List<Object> actorAndMovieList = new LinkedList<Object>();
        int mod = 0;
        for ( Node node : list )
        {
            if ( mod++ % 2 == 0 )
            {
                actorAndMovieList.add( new PersonImpl( node ) );
            }
            else
            {
                actorAndMovieList.add( new MovieImpl( node ) );
            }
        }
        return actorAndMovieList;
    }

    @Override
    public void addPropertiesToMovie(String title, Map<String, ? extends Object> properties) {
        Movie movie = getMovie(title);
        if (movie != null) {
            movie.addProperties( properties );
        }
    }
}