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

import java.io.IOException;

import org.neo4j.examples.imdb.domain.ImdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Setup
{
    private static final String IMDB_DATADIR = "target/classes/data/";
    @Autowired
    private ImdbReader imdbReader;
    @Autowired
    private ImdbService imdbService;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ApplicationContext context = new FileSystemXmlApplicationContext(new String[] {"src/main/webapp/WEB-INF/imdb-app-servlet.xml"});
    	Setup selfInstance = (Setup)context.getBean("setup");
        selfInstance.run();
    }

    public String run()
    {
        final ImdbParser parser = new ImdbParser( imdbReader );
        StringBuffer message = new StringBuffer( 200 );
        try
        {
            System.out.println("\nParsing movies");
            message.append(
                parser.parseMovies( IMDB_DATADIR + "movies.list.gz" ) ).append(
                '\n' );
            
            System.out.println("\nParsing actors");
            message.append(
                parser.parseActors( IMDB_DATADIR + "actors.list.gz",
                    IMDB_DATADIR + "actresses.list.gz" ) ).append( '\n' );
            
            System.out.println("\nParsing director");
            message.append(
                parser.parseDirectors( IMDB_DATADIR + "directors.list.gz"  )).append( '\n' );
            
            System.out.println("\nParsing composers");
            message.append(
                parser.parseComposers( IMDB_DATADIR + "composers.list.gz"  )).append( '\n' );
            
            System.out.println("\nParsing producers");
            message.append(
                parser.parseProducers( IMDB_DATADIR + "producers.list.gz"  )).append( '\n' );
            
            System.out.println("\nParsing writers");
            message.append(
                parser.parseWriters( IMDB_DATADIR + "writers.list.gz"  )).append( '\n' );
            
            System.out.println("\nParsing ratings");
            message.append(
                parser.parseRatings(IMDB_DATADIR + "ratings.list.gz" ) ).append(
                '\n' );
            
            System.out.println("\nParsing genres");
            message.append(
                parser.parseGenres(IMDB_DATADIR + "genres.list.gz" ) ).append(
                '\n' );
            imdbService.setupReferenceRelationship();
        }
        catch ( IOException e )
        {
            message.append( "Something went wrong during the setup process:\n" )
                .append( e.getMessage() );
        }
        return message.toString();
    }
}
