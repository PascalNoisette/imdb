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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.Traversal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class ImdbServiceImpl implements ImdbService
{
    private GraphDatabaseService graphDbService;
    private Index<Node> nodeIndex;

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
         Person person = getPerson(name);
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
        Movie movie = getMovie(title);
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
    
    public Person getPerson( final String name )
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
        Node baconNode = getSingleNode( "name", "Bacon, Kevin (I)" );
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
        PathFinder<Path> finder = GraphAlgoFactory.shortestPath(
        Traversal.expanderForTypes( RelTypes.ACTOR, Direction.OUTGOING ), 15 );
        Iterator<Node> list = finder.findSinglePath( actorNode, baconNode ).nodes().iterator();
        return convertNodesToActorsAndMovies( list );
    }

    private List<?> convertNodesToActorsAndMovies( final Iterator<Node> list)
    {
        final List<Object> actorAndMovieList = new LinkedList<Object>();
        int mod = 0;
        while(list.hasNext()) {
            Node node = list.next();
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

    @Override
    public void addAtributeMultipleToMovie(String title, String attributeName, List<String> keywords) {
        //not available
    }

    @Override
    public void indexRoles(Person actor, Set<RelTypes> distinctRoles) {
         //not available
    }
}