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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

class MovieImpl implements Movie
{
    private static final String TITLE_PROPERTY = "title";
    private static final String YEAR_PROPERTY = "year";

    private final Node underlyingNode;

    MovieImpl( final Node node )
    {
        this.underlyingNode = node;
    }

    Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }

    @Override
    public String getTitle()
    {
        return (String) underlyingNode.getProperty( TITLE_PROPERTY );
    }

    @Override
    public void setTitle( final String title )
    {
        underlyingNode.setProperty( TITLE_PROPERTY, title );
    }

    @Override
    public int getYear()
    {
        return (Integer) underlyingNode.getProperty( YEAR_PROPERTY );
    }

    @Override
    public void setYear( final int year )
    {
        underlyingNode.setProperty( YEAR_PROPERTY, year );
    }

    @Override
    public Iterable<Person> getPersonsByType(RelTypes type)
    {
        final List<Person> actors = new LinkedList<Person>();
        for ( Relationship rel : underlyingNode.getRelationships(
            type, Direction.INCOMING ) )
        {
            actors.add( new PersonImpl( rel.getStartNode() ) );
        }
        return actors;
    }

    @Override
    public boolean equals( final Object otherMovie )
    {
        if ( otherMovie instanceof MovieImpl )
        {
            return this.underlyingNode.equals( ((MovieImpl) otherMovie)
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
        return (String) underlyingNode.getProperty( TITLE_PROPERTY );
    }

    @Override
    public void addProperties(Map<String, ? extends Object> properties) {
        Transaction tx = underlyingNode.getGraphDatabase().beginTx();
        try {
            for (Map.Entry<String, ? extends Object> en : properties.entrySet()) {
                String key = en.getKey();
                Object value = en.getValue();
                underlyingNode.setProperty(key , value);
            }
            tx.success();
        } catch (Exception e) {
            tx.failure();
        } finally {
            tx.finish();
        }
    }

    @Override
    public String getProperty(String key) 
    {
        if (underlyingNode.hasProperty(key)) {
            return underlyingNode.getProperty(key).toString();
        }
        return "";
    }
}
