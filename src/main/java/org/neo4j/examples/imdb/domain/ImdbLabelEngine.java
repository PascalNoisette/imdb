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

import java.util.HashMap;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.IndexCreator;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Pascal
 */
class ImdbLabelEngine {
    
    @Autowired
    private BatchInserter batchInserter;
    
    private HashMap<String, BatchLabel> labels;
    
    
    protected void _init() {
        labels = new HashMap<String, BatchLabel>();
        _preparePersonLabel();
        _prepareMovieLabel();
    }

    Label getLabel(String name) {
        if (labels == null) {
            _init();
        }
        
        if (labels.get(name) == null) {
            _prepareStandardLabel(name);
        }
        return labels.get(name).getLabel();
    }
    
    final protected void _preparePersonLabel() {
        _prepareStandardLabel("PERSON");
        addPropertyToLabel("PERSON", "name");
    }
    
    
    final protected void _prepareMovieLabel() {
        _prepareStandardLabel("MOVIE");
        addPropertyToLabel("MOVIE", "title");
        addPropertyToLabel("MOVIE", "year");
    }
    
    protected void _prepareStandardLabel(String key) 
    {
        Label lbl = DynamicLabel.label( key );
        IndexCreator creator = batchInserter.createDeferredSchemaIndex( lbl );
        
        labels.put(key, new BatchLabel(lbl, creator));
    }
    
    public void addPropertyToLabel(String labelName, String key) {
        if (!labels.get(labelName).contains(key)) {
            labels.get(labelName).getIndexCreator().on( key );
            labels.get(labelName).add(key);
        }
    }
}
