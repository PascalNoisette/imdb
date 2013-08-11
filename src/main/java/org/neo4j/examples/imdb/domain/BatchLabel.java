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

import java.util.ArrayList;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.IndexCreator;

/**
 *
 * @author Pascal
 */
class BatchLabel {
    private Label label;
    private IndexCreator creator;
    private ArrayList<String> keyInCreatorIndex = new ArrayList<String>();
    
    public BatchLabel(Label l, IndexCreator c) {
        label = l;
        creator = c;
    }
    
    public BatchLabel add(String key) {
        keyInCreatorIndex.add(key);
        return this;
    }
    
    public boolean contains(String key) {
        return keyInCreatorIndex.contains(key);
    }

    public Label getLabel() {
        return label;
    }

    public IndexCreator getIndexCreator() {
        return creator;
    }
}
