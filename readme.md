# Big Data Exercise

This repository contains my solution to the "MovieRecommender" exercise where I created the "MovieRecommender.java" class that contains the necessary methods to pass the proposed tests.

* **MovieRecommender** Uses Amazon movie reviews sample data   [stanford.edu/data/web-Movies.html](http://snap.stanford.edu/data/web-Movies.html) for a simple movie recommender
    
 
## Setup

1. Install the  JDK 7.0
2. [Download & Install Maven](http://maven.apache.org/download.cgi)
   
 
## How to run tests

    1. Add the path to the "movies.txt.gz" file in "MovieRecommenderTest.java" on line 18
    2. From the repository root execute "mvn test"
    3. Wait for the test to finish :)
    # In the "data" folder the files are generated with the processed information, in "movies.csv" there is the information of the reviews [userId,itemId,score] and in the files "users.csv" and "products.csv " have the mapping of names to ids
 
