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
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.ServletException;

import org.neo4j.examples.imdb.domain.Actor;
import org.neo4j.examples.imdb.domain.ImdbService;
import org.neo4j.examples.imdb.domain.Movie;
import org.neo4j.examples.imdb.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MovieFindControllerDelegate implements FindControllerDelegate
{
    @Autowired
    private ImdbService imdbService;

    @Override
    public String getFieldName()
    {
        return "title";
    }

    @Override
    @Transactional
    public void getModel( final Object command, final Map<String,Object> model )
        throws ServletException
    {
        final String title = ((MovieForm) command).getTitle();
        final Movie movie = imdbService.getMovie( title );
        populateModel( model, movie );
    }

    private void populateModel( final Map<String,Object> model,
        final Movie movie )
    {
        if ( movie == null )
        {
            model.put( "movieTitle", "No movie found" );
            model.put( "actorNames", Collections.emptyList() );
        }
        else
        {
            model.put( "movieTitle", movie.getTitle() );
            model.put( "movieRatings",  movie.getProperty("rank") );
            final Collection<ActorInfo> actorInfo = new TreeSet<ActorInfo>();
            for ( Actor actor : movie.getActors() )
            {
                actorInfo.add( new ActorInfo( actor, actor.getRole( movie ) ) );
            }
            model.put( "actorInfo", actorInfo );
        }
    }

    public static final class ActorInfo implements Comparable<ActorInfo>
    {
        private String name;
        private String role;

        public ActorInfo( final Actor actor, final Role role )
        {
            setName( actor.getName() );
            if ( role == null || role.getName() == null )
            {
                setRole( "(unknown)" );
            }
            else
            {
                setRole( role.getName() );
            }
        }

        public void setName( final String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public void setRole( final String role )
        {
            this.role = role;
        }

        public String getRole()
        {
            return role;
        }

        @Override
        public int compareTo( ActorInfo otherActorInfo )
        {
            return getName().compareTo( otherActorInfo.getName() );
        }
    }
}
