IMDB Example Application for Neo4j
==================================

Setup the database
mvn clean
mvn compile
mvn exec:java

Run the application:
mvn jetty:run

Stop the application:
mvn jetty:stop

Please consider downloading the full dump available at http://www.imdb.com/interfaces to replace the samples supplied.

Statistics on the lattest IMDB dump files :
-   850 000 movies (6000 movies/s)
- 2 700 000 actors (1000 actors/s)
- 300k directors, 100k composers, 500k producers, 400k writers, 150k cinematographers, 300k ratings, 700k genres (6000 genres/s), 3100k keywords (10000 keywords/s), 800k countries (5000 countries/s), 700k languages (5000 languages/s)
Total time: 1h