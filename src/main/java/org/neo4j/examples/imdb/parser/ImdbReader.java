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
package org.neo4j.examples.imdb.parser;

import java.util.List;

/**
 * Reads events from the {@link ImdbParser}. 
 */
public interface ImdbReader
{
    /**
     * Creates new movies with specified <code>title</code> and
     * <code>year</code> from a {@link MovieData} list.
     * Every movie will be indexed.
     * @param movieList movies to create and index
     */
    void newMovies( List<MovieData> movieList );

    /**
     * Creates new actors specifying what movies the actors acted in
     * from a {@link ActorData} list.
     * Every actor will be indexed.
     * @param personList actors to create and index
     */
    void newPersons( List<PersonData> personList );

    /**
     * Import rating for movie
     * 
     * @param ratingData 
     */
    void newRating(RatingData ratingData);

    /**
     * Import movie genre
     * 
     * @param genreData 
     */
    void newGenre(GenreData genreData);

    /**
     * Add list of keyword to movie
     * @param title
     * @param keywords
     */
    public void newAtributeMultiple(String previousTitle, String attributeName, List<String> keywords);

    /**
     * Flush before proceecing next file
     */
    public void flush();

}
