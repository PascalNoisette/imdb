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
package org.graphipedia.dataimport;

public class ProgressCounter {

    private static final int THOUSAND = 1000;
    private static final int SMALL_STEP = 1 * THOUSAND;
    private static final int BIG_STEP = 50 * THOUSAND;
    private long startTime = System.currentTimeMillis(); 
    private String name;
    private int count = 0;
    
    public ProgressCounter(String name) {
        this.name = name;
    }
    
    public int getCount() {
        return count;
    }

    public synchronized void increment() {
        count++;
        if (count % BIG_STEP == 0) {
            long timeSpent = System.currentTimeMillis() - startTime;
            System.out.print(". "+ count / THOUSAND +"k");
            System.out.println(" (" + (count/(timeSpent/1000)) +  " " + name + " per second)");
        } else if (count % SMALL_STEP == 0) {
            System.out.print(".");
        }
    }

}
