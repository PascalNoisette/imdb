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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

class PersonImpl implements Person
{
    private static final String NAME_PROPERTY = "name";

    private final Node underlyingNode;

    PersonImpl( final Node node )
    {
        this.underlyingNode = node;
    }

    Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }

    @Override
    public final String getName()
    {
        return (String) underlyingNode.getProperty( NAME_PROPERTY );
    }

    @Override
    public void setName( final String name )
    {
        underlyingNode.setProperty( NAME_PROPERTY, name );
    }

    @Override
    public Iterable<Movie> getRoles()
    {
        final List<Movie> movies = new LinkedList<Movie>();
        for ( Relationship rel : underlyingNode.getRelationships(
            RelTypes.ACTS_IN, Direction.OUTGOING ) )
        {
            movies.add( new MovieImpl( rel.getEndNode() ) );
        }
        return movies;
    }

    @Override
    public Role getRole( final Movie inMovie )
    {
        final Node movieNode = ((MovieImpl) inMovie).getUnderlyingNode();
        for ( Relationship rel : underlyingNode.getRelationships(
            RelTypes.ACTS_IN, Direction.OUTGOING ) )
        {
            if ( rel.getEndNode().equals( movieNode ) )
            {
                return new RoleImpl( rel );
            }
        }
        return null;
    }

    @Override
    public boolean equals( final Object otherActor )
    {
        if ( otherActor instanceof PersonImpl )
        {
            return this.underlyingNode.equals( ((PersonImpl) otherActor)
                .getUnderlyingNode() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.underlyingNode.hashCode();
    }

    @Override
    public String toString()
    {
        return "Actor '" + this.getName() + "'";
    }

    @Override
    public Iterable<Movie> getMovies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
