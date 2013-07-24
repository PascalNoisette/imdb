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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;

import org.neo4j.examples.imdb.domain.Person;
import org.neo4j.examples.imdb.domain.ImdbService;
import org.neo4j.examples.imdb.domain.Movie;
import org.neo4j.examples.imdb.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ActorFindControllerDelegate implements FindControllerDelegate
{
    @Autowired
    private ImdbService imdbService;

    @Override
    public String getFieldName()
    {
        return "name";
    }

    @Override
    @Transactional
    public void getModel( final Object command, final Map<String,Object> model )
        throws ServletException
    {
        final String name = ((ActorForm) command).getName();
        final Person actor = imdbService.getPerson( name );
        populateModel( model, actor );
    }

    private void populateModel( final Map<String,Object> model,
        final Person actor )
    {
        if ( actor == null )
        {
            model.put( "actorName", "No actor found" );
            model.put( "kevinBaconNumber", "" );
            model.put( "movieTitles", Collections.emptyList() );
        }
        else
        {
            model.put( "actorName", actor.getName() );
            final List<?> baconPathList = imdbService.getBaconPath( actor );
            model.put( "kevinBaconNumber", baconPathList.size() / 2 );
            final Collection<MovieInfo> movieInfo = new TreeSet<MovieInfo>();
            for ( Movie movie : actor.getRoles() )
            {
                movieInfo.add( new MovieInfo( movie, actor.getRole( movie ) ) );
            }
            model.put( "movieInfo", movieInfo );
            final List<String> baconPath = new LinkedList<String>();
            for ( Object actorOrMovie : baconPathList )
            {
                if ( actorOrMovie instanceof Person )
                {
                    baconPath.add( ((Person) actorOrMovie).getName() );
                }
                else if ( actorOrMovie instanceof Movie )
                {
                    baconPath.add( ((Movie) actorOrMovie).getTitle() );
                }
            }
            model.put( "baconPath", baconPath );
        }
    }

    public static final class MovieInfo implements Comparable<MovieInfo>
    {
        private String title;
        private String role;

        MovieInfo( final Movie movie, final Role role )
        {
            setTitle( movie.getTitle() );
            if ( role == null || role.getName() == null )
            {
                setRole( "(unknown)" );
            }
            else
            {
                setRole( role.getName() );
            }
        }

        public final void setTitle( final String title )
        {
            this.title = title;
        }

        public String getTitle()
        {
            return title;
        }

        public final void setRole( final String role )
        {
            this.role = role;
        }

        public String getRole()
        {
            return role;
        }

        @Override
        public int compareTo( final MovieInfo otherMovieInfo )
        {
            return getTitle().compareTo( otherMovieInfo.getTitle() );
        }
    }
}
