IMDB Example Application for Neo4j
==================================

The documentation is found here:
http://wiki.neo4j.org/content/IMDB_Example


Run the application:
mvn jetty:run
browse to:
http://localhost:8080/imdb/setup.html

Stop the application:
mvn jetty:stop

If you inspect the graph using Neoclipse,
node-icons are found in
src/test/resources/icons

Run statistics on the lattest IMDB dump files :
- 1 456 929 actors (1428 actors/s) (19 078 actors/s using the alternative command line batch)
- 799 795 actreses (1204 actors/s) (17 045 actors/s)
- 273 833 directors (1396 directors/s) (15 625 directors/s)
- 133 533 composers (1162 composers/s) (20 000 composers/s)
- 463 274 producers (1535 producers/s) (16 666 producers/s)
- 347 432 writers (1570 writers/s) (14 285 writers/s)
- 284 824 ratings (451 ratings/s) (89 509 ratings/s)
- 698 090 genres (495 genres/s) (132 680 genres/s)