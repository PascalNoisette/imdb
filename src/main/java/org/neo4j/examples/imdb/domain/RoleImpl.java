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

import org.neo4j.graphdb.Relationship;

class RoleImpl implements Role
{
    private static final String CHARACTER_NAME_PROPERTY = "character_name";

    private Relationship underlyingRel = null;

    private long underlyingRelId = -1;

    RoleImpl( final Relationship rel )
    {
        this.underlyingRel = rel;
    }

    RoleImpl(long id) {
        this.underlyingRelId = id;
    }

    Relationship getUnderlyingRelationship()
    {
        return this.underlyingRel;
    }

    @Override
    public Person getActor()
    {
        return new PersonImpl( underlyingRel.getStartNode() );
    }

    @Override
    public Movie getMovie()
    {
        return new MovieImpl( underlyingRel.getEndNode() );
    }

    @Override
    public String getCharacter()
    {
        return ( String ) underlyingRel.getProperty( CHARACTER_NAME_PROPERTY, null );
    }

    @Override
    public void setCharacter( String name )
    {
        underlyingRel.setProperty( CHARACTER_NAME_PROPERTY, name );
    }

    @Override
    public String getName()
    {
        return ( String ) underlyingRel.getType().name();
    }


    @Override
    public boolean equals( Object otherRole )
    {
        if ( otherRole instanceof RoleImpl )
        {
            return this.underlyingRel.equals( ((RoleImpl) otherRole)
                .getUnderlyingRelationship() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.underlyingRel.hashCode();
    }

    @Override
    public String toString()
    {
        String role = this.getName();
        if ( role == null )
        {
            role = "";
        }
        return this.getActor() + "-[" + role + "]->" + this.getMovie();
    }
}
