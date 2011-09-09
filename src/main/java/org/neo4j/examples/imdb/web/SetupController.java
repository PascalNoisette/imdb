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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class SetupController extends SimpleFormController
{
    private final SetupControllerDelegate delegate;

    public SetupController( final SetupControllerDelegate delegate )
    {
        super();
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(java
     * .lang.Object)
     */
    @Override
    protected ModelAndView onSubmit( final Object command ) throws ServletException
    {
        final Map<String,Object> model = new HashMap<String,Object>();
        delegate.getModel( command, model );
        return new ModelAndView( getSuccessView(), "model", model );
    }
}
