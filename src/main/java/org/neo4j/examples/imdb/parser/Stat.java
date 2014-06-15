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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.graphipedia.dataimport.ProgressCounter;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class Stat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GraphDatabaseService graphDb = getGraphDb();
        actorPalmares(graphDb);
        moviePalmaresThreshold(graphDb);
    }
    
    /**
     * @param graphDb
     */
    public static void actorPalmares(GraphDatabaseService graphDb) {

        ProgressCounter actorCount = new ProgressCounter("person");
        Transaction tx = graphDb.beginTx();
        ResourceIterator<Node> it = GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(DynamicLabel.label("PERSON")).iterator();
        
        while (it.hasNext()) {
            Node person = it.next();
            
            if (actorCount.getCount() % 2000 == 0) {
                tx.success();
                tx.close();
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
        tx.close();
    }

    public static void moviePalmaresThreshold(GraphDatabaseService graphDb) {

        ProgressCounter movieCount = new ProgressCounter("movies");
        Transaction tx = graphDb.beginTx();
        ResourceIterator<Node> it = GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(DynamicLabel.label("MOVIE")).iterator();

        while (it.hasNext()) {
            Node movie = it.next();
            if (movieCount.getCount() % 2000 == 0) {
                tx.success();
                tx.close();
                tx = graphDb.beginTx();
            }
            
            Float topX = retriveTopXPalmares(movie, 3);
            movie.setProperty("actor_palmares_top_3", topX);
            
            
            movieCount.increment();
        }
        
        tx.success();
        tx.close();
    }
    
    public static Float retriveTopXPalmares(Node movie, int X) 
    {        
        TreeSet<Float> actorsPalmares = retriveActorPalmares(movie);
        Float lastTop;
        Float topX = new Float(0);
        
        while ((lastTop = actorsPalmares.pollLast()) != null && X > 0) {
             topX = lastTop;
             X--;
             
        }
        
        return topX;
    }
    
    public static TreeSet<Float> retriveActorPalmares(Node movie) {
        Iterable<Relationship> relationships = movie.getRelationships();

        Set<Long> actors = new HashSet<Long>();
        TreeSet<Float> actorsPalmares = new TreeSet<Float>();
        for (Relationship r : relationships) {
            Node actor = r.getOtherNode(movie);
            if (actor.hasProperty("palmares") && !actors.contains(actor.getId())) {
                actors.add(actor.getId());
                actorsPalmares.add((Float)actor.getProperty("palmares"));
            }
        } 
        
        return actorsPalmares;
    }
    
    public static GraphDatabaseService getGraphDb() {
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
            .newEmbeddedDatabaseBuilder( "target/neo4j-db" )
            .loadPropertiesFromFile( "target/classes/conf/neo4j.properties" )
            .newGraphDatabase();
        
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
