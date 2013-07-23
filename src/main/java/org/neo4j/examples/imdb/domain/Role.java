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

/**
 * A role holds role information of an actor in a specific movie.
 */
public interface Role
{
    /**
     * Returns the role name. Ex: "Reeves, Keanu" acted in "Matrix, The (1999)"
     * as "Neo". This method would then return "Neo" as name.
     * @return name of this role or null if no role found
     */
    String getName();

    /**
     * Sets the role name.
     * @param name
     *            role name
     */
    void setName( String name );

    /**
     * Returns the movie this role is connected to.
     * @return movie for this role
     */
    Movie getMovie();

    /**
     * Returns the actor for this role.
     * @return actor for this role
     */
    Person getActor();
}
