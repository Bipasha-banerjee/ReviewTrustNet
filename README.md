# ReviewTrustNet

This is the repository for the project for CS 5614. The team members are Bipasha Banerjee, Smridh Malhotra and Ramya Nandigam. 

The goal of the project is to give trust value to users and thus generate a trust review network. The dataset used is from the SNAP dataset of Amazon Musical Instrument reviews. We have based our project on two different ideas.
- NetInf
- Eigen Trust


## Process File
To run the module, the user has to first prepare the data. For this run the PrepareData.java file and make sure to enter the correct input file path in function fetchIntoList() and the desired output file path in writeToFile().
A sample input file to the model looks like

User ID 1                   User ID 2          Usefulness      UnixTime1      UnixTime2      Product ID 

ABC68JUCPTVOE	      A3W2E6S24BTXXK	     0.01	         1333152000	    1401235200	   B000068NW5 

ABC68JUCPTVOE	     A3872Y2XH0YDX1	       0.01	         1333152000	    1363132800	   B000068NW5 

ABC68JUCPTVOE	     A398X9POBHK69N	       0.01	         1333152000	     1388966400	   B000068NW5 

## Cascade.java
This file lays out the generation of cascades along with the calculation of probabilities of each of the cascades as well as carries out the changes in the parent of a particular node.

## GenerateNet.java

This is the main file from which the execution point starts. Both the graph creation and the graph eigen value calculations can be done from this module. The code has not been polished yet, so to run either one of them, the other one needs to be commented out.

## Graph.java

All the graph logic (like adding node to a graph, adding edges, and calculating the degree of a node) is present in this module.

## OutputGraph.java

This contains the output logic of the graph along with the EigenTrust logic.

## prepareGraph.java
This file outputs the graph created by the NetInf part of the code. It uses the Jung JAR of JAVA to do so.


## Other supporting files
Files like EdgeInfo.java, HitInfo.java are supporting files needed to create the model of the graph.
