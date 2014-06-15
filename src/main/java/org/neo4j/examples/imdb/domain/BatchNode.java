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

import java.util.Map;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;

/**
 *
 * @author Pascal
 */
class BatchNode implements Node {
    
    Long id = null;
    Map<String, Object> nodeProperties = null;

    BatchNode(Map<String, Object> nodeProperties) {
        
    }

    BatchNode(Long nodeId, Map<String, Object> nodeProperties) {
        this.id = nodeId;
        this.nodeProperties = nodeProperties;
    }

    @Override
    public long getId() 
    {
        if (id == null) {
            throw new RuntimeException("Id is null");
        }
        return id;
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<Relationship> getRelationships() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasRelationship() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<Relationship> getRelationships(RelationshipType... rts) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<Relationship> getRelationships(Direction drctn, RelationshipType... rts) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasRelationship(RelationshipType... rts) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasRelationship(Direction drctn, RelationshipType... rts) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<Relationship> getRelationships(Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasRelationship(Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<Relationship> getRelationships(RelationshipType rt, Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasRelationship(RelationshipType rt, Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Relationship getSingleRelationship(RelationshipType rt, Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Relationship createRelationshipTo(Node node, RelationshipType rt) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Traverser traverse(Traverser.Order order, StopEvaluator se, ReturnableEvaluator re, RelationshipType rt, Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Traverser traverse(Traverser.Order order, StopEvaluator se, ReturnableEvaluator re, RelationshipType rt, Direction drctn, RelationshipType rt1, Direction drctn1) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Traverser traverse(Traverser.Order order, StopEvaluator se, ReturnableEvaluator re, Object... os) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public GraphDatabaseService getGraphDatabase() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean hasProperty(String string) {
        return this.nodeProperties.containsKey(string);
    }

    @Override
    public Object getProperty(String string) {
        if ("id".equals(string)) {
            return this.getId();
        }
        return this.nodeProperties.get(string);
    }

    @Override
    public Object getProperty(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setProperty(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Object removeProperty(String string) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Iterable<String> getPropertyKeys() {
        return this.nodeProperties.keySet();
    }


    @Override
    public void addLabel(Label label) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLabel(Label label) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasLabel(Label label) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ResourceIterable<Label> getLabels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Iterable<RelationshipType> getRelationshipTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDegree() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDegree(RelationshipType rt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDegree(Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDegree(RelationshipType rt, Direction drctn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
