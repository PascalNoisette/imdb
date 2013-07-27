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

import java.util.Map;


class MovieBatchImpl implements Movie
{
    
    private long underlyingNodeId = -1;
    private String title;
    private int year;


    MovieBatchImpl(long id) {
        this.underlyingNodeId = id;
    }
    
    @Override
    public long getId() {
        return this.underlyingNodeId;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public void setTitle( final String title )
    {
        this.title = title;
    }

    @Override
    public int getYear()
    {
        return year;
    }

    @Override
    public void setYear( final int year )
    {
        this.year = year;
    }

    @Override
    public Iterable<Person> getPersons()
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean equals( final Object otherMovie )
    {
        if ( otherMovie instanceof MovieBatchImpl )
        {
            return this.underlyingNodeId == ( ((MovieBatchImpl) otherMovie)
                .getId());
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
       return "Movie '" + this.getTitle()+ "'";
    }

    @Override
    public void addProperties(Map<String, ? extends Object> properties) {
         throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getProperty(String key) 
    {
         throw new UnsupportedOperationException("Not supported yet."); 
    }
}
