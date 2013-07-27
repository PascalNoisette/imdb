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

class PersonBatchImpl implements Person
{
    private static final String NAME_PROPERTY = "name";
    
    private long underlyingNodeId = -1;
    
    private String name;

    PersonBatchImpl( final long id )
    {
        this.underlyingNodeId = id;
    }
    
    @Override
    public long getId()
    {
        return this.underlyingNodeId;
    }

    @Override
    public final String getName()
    {
        return name;
    }

    @Override
    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public Iterable<Movie> getRoles()
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean equals( final Object otherActor )
    {
        if ( otherActor instanceof PersonBatchImpl )
        {
            return this.underlyingNodeId == (((PersonBatchImpl) otherActor)
                .getId() );
        }
        return false;
    }

    @Override
    public int hashCode()
    {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString()
    {
        return "Actor '" + this.getName() + "'";
    }

    @Override
    public Role getRole(Movie inMovie) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
