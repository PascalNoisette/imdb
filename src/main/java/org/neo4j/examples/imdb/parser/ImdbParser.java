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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
import org.graphipedia.dataimport.ProgressCounter;
import org.neo4j.examples.imdb.domain.RelTypes;

/**
 * A <code>ImdbParser</code> can parse the movie and actor/actress lists from
 * the imdb text data (http://www.imdb.com/interfaces). It uses an
 * {@link ImdbReader} forwarding the parsed information.
 */
public class ImdbParser
{
    private static final String MOVIES_MARKER = "MOVIES LIST";
    private static final int MOVIES_SKIPS = 2;
    private static final String ACTRESSES_MARKER = "THE ACTRESSES LIST";
    private static final int ACTRESS_SKIPS = 4;
    private static final String ACTOR_MARKER = "THE ACTORS LIST";
    private static final int ACTOR_SKIPS = 4;
    private static final int BUFFER_SIZE = 200;
    private final ImdbReader reader;

    /**
     * Create a new Imdb parser.
     * @param reader
     *            reader this parser will use to forward events to
     */
    public ImdbParser( final ImdbReader reader )
    {
        if ( reader == null )
        {
            throw new IllegalArgumentException( "Null ImdbReader" );
        }
        this.reader = reader;
    }

    /**
     * Parsers a tab-separated movie list file, each line containing a movie
     * title and the year the movie was released. The file can be .gz or .zip
     * compressed, and must then have the corresponding file extension.
     * @param file
     *            name of movie list file
     * @throws IOException
     *             if unable to open the movie list file
     */
    public String parseMovies( final String file ) throws IOException
    {
        final List<MovieData> buffer = new LinkedList<MovieData>();
        if ( file == null )
        {
            throw new IllegalArgumentException( "Null movie file" );
        }
        BufferedReader fileReader = getFileReader( file, MOVIES_MARKER,
            MOVIES_SKIPS );
        String line = fileReader.readLine();
        ProgressCounter movieCount = new ProgressCounter("movies");
        while ( line != null )
        {
            // get rid of blank lines and TV shows
            if ( "".equals( line ) || line.indexOf( "(TV)" ) != -1 )
            {
                line = fileReader.readLine();
                continue;
            }
            final int yearSep = line.indexOf( '\t' );
            if ( yearSep > 0 )
            {
                final String title = line.substring( 0, yearSep ).trim();
                String yearString = line.substring( yearSep ).trim();
                if ( yearString.length() > 4 )
                {
                    yearString = yearString.substring( 0, 4 );
                }
                if ( yearString.length() == 0 || yearString.charAt( 0 ) == '?'
                    || title.contains( "{" ) || title.startsWith( "\"" ) )
                {
                    line = fileReader.readLine();
                    continue;
                }
                final int year = Integer.parseInt( yearString );
                buffer.add( new MovieData( title, year ) );
                movieCount.increment();
                if ( movieCount.getCount() % BUFFER_SIZE == 0 )
                {
                    reader.newMovies( buffer );
                    buffer.clear();
                }
            }
            line = fileReader.readLine();
        }
        reader.newMovies( buffer );
        return (movieCount.getCount() + " movies parsed and injected.");
    }
    
    
    
    /**
     * Parsers a tab-separated movie rating list file, each line containing a movie
     * title and the year the movie was released. The file can be .gz or .zip
     * compressed, and must then have the corresponding file extension.
     * @param file
     *            name of movie list file
     * @throws IOException
     *             if unable to open the movie list file
     */
    public String parseRatings( final String file ) throws IOException
    {
        if ( file == null )
        {
            throw new IllegalArgumentException( "Null rating file" );
        }
        BufferedReader fileReader = getFileReader( file, "MOVIE RATINGS REPORT", 2);
        String line = fileReader.readLine();
        ProgressCounter ratingCount = new ProgressCounter("ratings");
        
        while ( line != null && !"".equals( line ))
        {            
            String[] tokens = line.split("  ");
            String title = tokens[tokens.length-1].trim();
            String rank = tokens[tokens.length-2].trim();  
            String votes = tokens[tokens.length-3].trim();  
            if (title.contains( "{" ) || title.startsWith( "\"" ) )
            {
                line = fileReader.readLine();
                continue;
            }
            
            reader.newRating(new RatingData( title, rank, votes));
            ratingCount.increment();
           
            line = fileReader.readLine();
        }
        return (ratingCount.getCount() + " ratings parsed and injected.");
    }
 
    /**
     * Parsers a tab-separated actors list file. A line begins with actor name
     * then followed by a tab and a movie title the actor acted in. Additional
     * movies the current actor acted in are found on the following line that
     * starts with a tab followed by the movie title.
     * @param actorFile
     *            name of actor list file
     * @param actressFile
     *            TODO
     * @throws IOException
     *             if unable to open actor list file
     */
    public String parseActors( final String actorFile, final String actressFile )
        throws IOException
    {
        if ( actorFile == null )
        {
            throw new IllegalArgumentException( "Null actor file" );
        }
        if ( actressFile == null )
        {
            throw new IllegalArgumentException( "Null actress file" );
        }
        String result = "";
        BufferedReader fileReader = getFileReader( actorFile, ACTOR_MARKER,
            ACTOR_SKIPS );
        result += "Actors: " + parsePersonFile( fileReader, RelTypes.ACTOR ) + "\n";
        fileReader.close();
        fileReader = getFileReader( actressFile, ACTRESSES_MARKER,
            ACTRESS_SKIPS );
        result += "Actresses: " + parsePersonFile( fileReader, RelTypes.ACTOR );
        return result;
    }


    public Object parsePersonFile(BufferedReader fileReader, RelTypes batchName) throws IOException {
    
        if ( fileReader == null )
        {
            throw new IllegalArgumentException( "Null " + batchName + " file" );
        }
        String line = fileReader.readLine();
        String currentActor = null;
        final List<PersonData> buffer = new LinkedList<PersonData>();
        final List<RoleData> movies = new ArrayList<RoleData>();
        int movieCount = 0;
        ProgressCounter actorCount = new ProgressCounter(batchName.toString());
        while ( line != null )
        {
            // get rid of blank lines
            if ( "".equals( line ) )
            {
                line = fileReader.readLine();
                continue;
            }
            int actorSep = line.indexOf( '\t' );
            if ( actorSep >= 0 )
            {
                String actor = line.substring( 0, actorSep ).trim();
                if ( !"".equals( actor ) )
                {
                    if ( movies.size() > 0 )
                    {
                        buffer.add( new PersonData( currentActor, movies
                            .toArray( new RoleData[movies.size()] ) ) );
                        actorCount.increment();
                        movies.clear();
                    }
                    currentActor = actor;
                }
                String title = line.substring( actorSep ).trim();
                if ( title.length() == 0 || title.contains( "{" )
                    || title.startsWith( "\"" ) || title.contains( "????" ) )
                {
                    line = fileReader.readLine();
                    continue;
                }
                int characterStart = title.indexOf( '[' );
                int characterEnd = title.indexOf( ']' );
                String character = null;
                if ( characterStart > 0 && characterEnd > characterStart )
                {
                    character = title.substring( characterStart + 1,
                        characterEnd );
                }
                int creditStart = title.indexOf( '<' );
                // int creditEnd = title.indexOf( '>' );
                // String credit = null;
                // if ( creditStart > 0 && creditEnd > creditStart )
                // {
                // credit = title.substring( creditStart + 1, creditEnd );
                // }
                if ( characterStart > 0 )
                {
                    title = title.substring( 0, characterStart ).trim();
                }
                else if ( creditStart > 0 )
                {
                    title = title.substring( 0, creditStart ).trim();
                }
                int spaces = title.indexOf( "  " );
                if ( spaces > 0 )
                {
                    if ( title.charAt( spaces - 1 ) == ')'
                        && title.charAt( spaces + 2 ) == '(' )
                    {
                        title = title.substring( 0, spaces ).trim();
                    }
                }
                movies.add( new RoleData( title, batchName, character ) );
                movieCount++;
                if ( movieCount % BUFFER_SIZE == 0 )
                {
                    reader.newPersons( buffer );
                    buffer.clear();
                }
            }
            line = fileReader.readLine();
        }
        reader.newPersons( buffer );
        return (actorCount.getCount() + " " + batchName + " added including " + movieCount + " characters parsed and injected.");
    }

    /**
     * Get file reader that corresponds to file extension.
     * @param file
     *            the file name
     * @param pattern
     *            TODO
     * @param skipLines
     *            TODO
     * @return a file reader that uncompresses data if needed
     * @throws IOException
     * @throws FileNotFoundException
     */
    private BufferedReader getFileReader( final String file, String pattern,
        int skipLines ) throws IOException, FileNotFoundException
    {
        BufferedReader fileReader;
        // support compressed files
        if ( file.endsWith( ".gz" ) )
        {
            fileReader = new BufferedReader( new InputStreamReader(
                new GZIPInputStream( new FileInputStream( file ) ) ) );
        }
        else if ( file.endsWith( ".zip" ) )
        {
            fileReader = new BufferedReader( new InputStreamReader(
                new ZipInputStream( new FileInputStream( file ) ) ) );
        }
        else
        {
            fileReader = new BufferedReader( new FileReader( file ) );
        }

        String line = "";
        while ( !pattern.equals( line ) )
        {
            line = fileReader.readLine();
        }
        for ( int i = 0; i < skipLines; i++ )
        {
            line = fileReader.readLine();
        }

        return fileReader;
    }

    
    public Object parseDirectors(String filename) throws IOException {
        return parsePersonFile(getFileReader(filename, "THE DIRECTORS LIST", 4 ), RelTypes.DIRECTOR);
    }

    public Object parseComposers(String filename) throws IOException {
        return parsePersonFile(getFileReader(filename, "THE COMPOSERS LIST", 4 ), RelTypes.COMPOSER);
    }

    public Object parseProducers(String filename) throws IOException {
        return parsePersonFile(getFileReader(filename, "THE PRODUCERS LIST", 4 ), RelTypes.PRODUCER);
    }

    public Object parseWriters(String filename) throws IOException {
        return parsePersonFile(getFileReader(filename, "THE WRITERS LIST", 4 ), RelTypes.WRITER);
    }


    public Object parseGenres(String filename) throws IOException  {
        BufferedReader fileReader = getFileReader(filename, "8: THE GENRES LIST", 2 );
        String line = fileReader.readLine();
        ProgressCounter genreCount = new ProgressCounter("genres");
        
        while ( line != null && !"".equals( line ))
        {            
            String[] tokens = line.split("\t\t\t\t\t");
            if (tokens.length != 2)
            {
                line = fileReader.readLine();
                continue;
            }
            String title = tokens[0].trim();
            String genre = tokens[1].trim();
            if (title.contains( "{" ) || title.startsWith( "\"" ) )
            {
                line = fileReader.readLine();
                continue;
            }
            
            reader.newGenre(new GenreData( title, genre));
            genreCount.increment();
           
            line = fileReader.readLine();
        }
        return (genreCount.getCount() + " genres parsed and injected.");
    }

    Object parseKeywords(String filename) throws IOException {
        BufferedReader fileReader = getFileReader(filename, "8: THE KEYWORDS LIST", 2 );
        String line = fileReader.readLine();
        ProgressCounter keywordsCount = new ProgressCounter("keywords");
        final List<String> keywords = new LinkedList<String>();
        String previousTitle = null;
        
        while ( line != null && !"".equals( line ))
        {
            String[] tokens = line.split("\t\t\t\t\t");
            if (tokens.length != 2)
            {
                line = fileReader.readLine();
                continue;
            }
            String title = tokens[0].trim();
            String keyword = tokens[1].trim();
            
            if (title.contains( "{" ) || title.startsWith( "\"" ) )
            {
                line = fileReader.readLine();
                continue;
            }
            
            if (previousTitle == null) {
                previousTitle = title;
            }
            
            if (!title.equals(previousTitle)) {
                reader.newKeywords(previousTitle, keywords);
                keywords.clear();
                previousTitle = title; 
            }
            
            keywords.add(keyword);
            keywordsCount.increment();
            line = fileReader.readLine();
        }
        if (!keywords.isEmpty()) {
            reader.newKeywords(previousTitle, keywords);
        }
        return (keywordsCount.getCount() + " genres parsed and injected.");
    }
}
