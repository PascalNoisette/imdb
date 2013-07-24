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

public interface Movie
{
    /**
     * Returns the title of this movie.
     * @return title of this movie.
     */
    String getTitle();

    /**
     * Set the title of this movie.
     * @param title
     *            title of movie
     */
    void setTitle( String title );

    /**
     * Returns the year this movie was released.
     * @return the year this movie was released
     */
    int getYear();

    /**
     * Set the year of this movie.
     * @param year
     *            year of movie
     */
    void setYear( int year );
    
    /**
     * Set additional attribute to node
     * @param Map
     *            properties
     */
    void addProperties( Map<String, ? extends Object> properties );


    /**
     * Return the property value value
     * @param key
     * @return String
     */
    public String getProperty(String key);

  /**
     * Returns all person related to this movie.
     * @return list
     */
    public Iterable<Person> getPersons();
}
