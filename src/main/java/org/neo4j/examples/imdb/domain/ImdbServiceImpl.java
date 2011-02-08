package org.neo4j.examples.imdb.domain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.neo4j.examples.imdb.util.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
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

    public Actor createActor( final String name )
    {
        final Node actorNode = graphDbService.createNode();
        final Actor actor = new ActorImpl( actorNode );
        actor.setName( name );
        searchEngine.indexActor( actor );
        nodeIndex.add(actorNode, NAME_INDEX, name);
        return actor;
    }

    public Movie createMovie( final String title, final int year )
    {
        final Node movieNode = graphDbService.createNode();
        final Movie movie = new MovieImpl( movieNode );
        movie.setTitle( title );
        movie.setYear( year );
        searchEngine.indexMovie( movie );
        nodeIndex.add(movieNode, TITLE_INDEX, title);
        return movie;
    }

    public Role createRole( final Actor actor, final Movie movie,
        final String roleName )
    {
        if ( actor == null )
        {
            throw new IllegalArgumentException( "Null actor" );
        }
        if ( movie == null )
        {
            throw new IllegalArgumentException( "Null movie" );
        }
        final Node actorNode = ((ActorImpl) actor).getUnderlyingNode();
        final Node movieNode = ((MovieImpl) movie).getUnderlyingNode();
        final Relationship rel = actorNode.createRelationshipTo( movieNode,
            RelTypes.ACTS_IN );
        final Role role = new RoleImpl( rel );
        if ( roleName != null )
        {
            role.setName( roleName );
        }
        return role;
    }

    public Actor getActor( final String name )
    {
        Node actorNode = getSingleNode(NAME_INDEX, name);
        if ( actorNode == null )
        {
            actorNode = searchEngine.searchActor( name );
        }
        Actor actor = null;
        if ( actorNode != null )
        {
            actor = new ActorImpl( actorNode );
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

    public List<?> getBaconPath( final Actor actor )
    {
        return Collections.emptyList();
        /*
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
        final Node actorNode = ((ActorImpl) actor).getUnderlyingNode();
        final List<Node> list = pathFinder.shortestPath( actorNode, baconNode,
            RelTypes.ACTS_IN );
        return convertNodesToActorsAndMovies( list );
        */
    }

    private List<?> convertNodesToActorsAndMovies( final List<Node> list )
    {
        final List<Object> actorAndMovieList = new LinkedList<Object>();
        int mod = 0;
        for ( Node node : list )
        {
            if ( mod++ % 2 == 0 )
            {
                actorAndMovieList.add( new ActorImpl( node ) );
            }
            else
            {
                actorAndMovieList.add( new MovieImpl( node ) );
            }
        }
        return actorAndMovieList;
    }
}