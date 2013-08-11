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

import org.neo4j.examples.imdb.domain.MovieFormat;

public class MovieData
{
    private final String title;
    private final int year;
    private final MovieFormat format;

    /**
     * Create container for movie data.
     * @param title title of movie
     * @param year release year of movie
     */
    MovieData( final String title, final int year, final MovieFormat format )
    {
        this.title = title;
        this.year = year;
        this.format = format;
    }

    public String getTitle()
    {
        return title;
    }

    public int getYear()
    {
        return year;
    }

    public MovieFormat getFormat() {
        return format;
    }
}
