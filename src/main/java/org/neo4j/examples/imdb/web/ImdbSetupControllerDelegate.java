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

import java.util.Map;

import javax.servlet.ServletException;
import org.neo4j.examples.imdb.parser.Setup;

import org.springframework.beans.factory.annotation.Autowired;

public class ImdbSetupControllerDelegate implements SetupControllerDelegate
{
    @Autowired
    private Setup setup;

    @Override
    public void getModel( final Object command, final Map<String,Object> model )
        throws ServletException
    {
        model.put( "setupMessage", setup.run() );
    }
}
