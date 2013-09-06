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

import java.util.HashMap;
import java.util.Map;
import org.graphipedia.dataimport.ProgressCounter;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.tooling.GlobalGraphOperations;

public class Stat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ProgressCounter actorCount = new ProgressCounter("person");
        GraphDatabaseService graphDb = getGraphDb();
        ResourceIterator<Node> it = GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(DynamicLabel.label("PERSON")).iterator();
        Transaction tx = null;
        tx = graphDb.beginTx();
        while (it.hasNext()) {
            Node person = it.next();
            
            if (actorCount.getCount() % 2000 == 0) {
                tx.success();
                tx.finish();
                tx = graphDb.beginTx();
            }
            Iterable<Relationship> relationships = person.getRelationships();
            
            long movieCount = 0;
            float palmares = 0;
            long viewers = 0;
            for (Relationship r : relationships){
                Node movie = r.getOtherNode(person);
                movieCount += 1;
                if (movie.hasProperty("rank") && movie.hasProperty("votes")) {
                    palmares += (Float)movie.getProperty("rank");
                    viewers += (Long)movie.getProperty("votes");
                }
            }
            person.setProperty("movie_count", movieCount);
            person.setProperty("palmares", palmares);
            person.setProperty("viewers", viewers);
            
            
            actorCount.increment();
        }
        
        tx.success();
        tx.finish();
    }

    public static GraphDatabaseService getGraphDb() {
        GraphDatabaseService graphDb = null;
        

        Map<String, String> opts = new HashMap();
        opts.put("execution_guard_enabled", "false");
        opts.put("keep_logical_logs", "false");
        opts.put("neostore.nodestore.db.mapped_memory", "100M");
        opts.put("neostore.relationshipstore.db.mapped_memory", "100M");
        opts.put("neostore.propertystore.db.mapped_memory", "200M");
        opts.put("neostore.propertystore.db.strings.mapped_memory", "350M");
        opts.put("neostore.propertystore.db.arrays.mapped_memory", "350M");
        opts.put("wrapper.java.initmemory", "728");
        opts.put("wrapper.java.maxmemory", "2800");

        graphDb = new EmbeddedGraphDatabase("target/neo4j-db", opts);
        registerShutdownHook(graphDb);
        return graphDb;
    }
    
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
