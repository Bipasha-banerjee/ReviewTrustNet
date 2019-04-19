package edu.vt.NetInf;

import java.util.ArrayList;

public class Graph {
    class Node{
        private String Id;

        private ArrayList<String> inNid, outNid;

        public Node(){
            Id = null;
            inNid = new ArrayList<>();
            outNid = new ArrayList<>();
        }

        public Node(String Id){
            this.Id = Id;
            inNid = new ArrayList<>();
            outNid = new ArrayList<>();
        }

        //Stream in ?

        //Stream out ?

        String getId(){
            return Id;
        }

        int getDeg(){
            return getInDeg() + getOutDeg();
        }

        int getInDeg(){
            return inNid.size();
        }

        int getOutDeg(){
            return outNid.size();
        }

        String getInNId(int edgeIndex){
            return inNid.get(edgeIndex);
        }

        String getOutNId(int edgeIndex){
            return outNid.get(edgeIndex);
        }

        boolean isInNid(String NId){
            return inNid.contains(NId);
        }

        boolean isOutNid(String NId){
            return outNid.contains(NId);
        }
    }

    class Edge{

    }
}
