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

/**
 * Holds information about what role an actor has in a movie
 */
public class RoleData
{
    private final String title;
    private final String role;

    RoleData( final String title, final String role )
    {
        this.title = title;
        this.role = role;
    }

    /**
     * Returns the title of the movie, never <code>null</code>.
     * @return title of the movie
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * Returns the role the actor had in the movie, may be <code>null</code>
     * if no information is available.
     * @return actor role or null if information not avilable
     */
    public String getRole()
    {
        return this.role;
    }
}
