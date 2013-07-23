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
package org.neo4j.examples.imdb.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.neo4j.examples.imdb.domain.ImdbService;
import org.neo4j.examples.imdb.parser.ImdbParser;
import org.neo4j.examples.imdb.parser.ImdbReader;
import org.springframework.beans.factory.annotation.Autowired;

public class ImdbSetupControllerDelegate implements SetupControllerDelegate
{
    private static final String IMDB_DATADIR = "target/classes/data/";
    @Autowired
    private ImdbReader imdbReader;
    @Autowired
    private ImdbService imdbService;

    @Override
    public void getModel( final Object command, final Map<String,Object> model )
        throws ServletException
    {
        final ImdbParser parser = new ImdbParser( imdbReader );
        StringBuffer message = new StringBuffer( 200 );
        try
        {
            message.append(
                parser.parseMovies( IMDB_DATADIR + "movies.list.gz" ) ).append(
                '\n' );
            message.append(
                parser.parseActors( IMDB_DATADIR + "actors.list.gz",
                    IMDB_DATADIR + "actresses.list.gz" ) ).append( '\n' );
           message.append(
                parser.parseDirectors( IMDB_DATADIR + "directors.list.gz"  )).append( '\n' );
           message.append(
                parser.parseRatings(IMDB_DATADIR + "ratings.list.gz" ) ).append(
                '\n' );
            imdbService.setupReferenceRelationship();
        }
        catch ( IOException e )
        {
            message.append( "Something went wrong during the setup process:\n" )
                .append( e.getMessage() );
        }
        model.put( "setupMessage", message.toString() );
    }
}
