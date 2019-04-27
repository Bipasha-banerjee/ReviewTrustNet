package edu.vt.NetInf;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Graph {
    class Node{
        private String Id;

        private ArrayList<Integer> inEid, outEid;

        public Node(){
            Id = null;
            inEid = new ArrayList<>();
            outEid = new ArrayList<>();
        }

        public Node(String Id){
            this.Id = Id;
            inEid = new ArrayList<>();
            outEid = new ArrayList<>();
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
            return inEid.size();
        }

        int getOutDeg(){
            return outEid.size();
        }

        int getInEId(int edgeIndex){
            return inEid.get(edgeIndex);
        }

        int getOutEId(int edgeIndex){
            return outEid.get(edgeIndex);
        }

        boolean isInNid(int EId){
            return inEid.contains(EId);
        }

        boolean isOutNid(int EId){
            return outEid.contains(EId);
        }
    }

    class Edge{
        private Integer id;
        private String srcNId, dstNId;

        public Edge(){
            id = -1;
            srcNId = null;
            dstNId = null;
        }

        public Edge(Integer id, String srcNId, String dstNId) {
            this.id = id;
            this.srcNId = srcNId;
            this.dstNId = dstNId;
        }

        //TSIn and TSout


        public Integer getId() {
            return id;
        }

        public String getSrcNId() {
            return srcNId;
        }

        public String getDstNId() {
            return dstNId;
        }
    }
    //Iterators to add?

    private Integer  MxEId;

    public Map<String, Node> NodeH = new LinkedHashMap<>();
    public Map<Integer,Edge> EdgeH = new LinkedHashMap<>();

    //TSIn and TSOut??

    //TCRef??

    //PNGraph will work as an instance?

    //Flag?

    //NODES

    int getNodes(){
        return NodeH.size();
    }
    Node getNode(String NId){
        return NodeH.get(NId);
    }

    boolean isNode(String NId){
        return NodeH.containsKey(NId);
    }

    String AddNode(String NId){
        if(!isNode(NId)){
            NodeH.put(NId,new Node(NId));
            return NId;
        }
        return "Exists";
    }

    String AddNode(Node N){
       return AddNode(N.Id);
    }

    void DelNode(String Nid){
        Node Node = getNode(Nid);
        for(int i = 0; i < Node.getOutDeg(); i++){
            int EId = Node.getOutEId(i);
            Edge edge = getEdge(EId);
            //Assert source??;
            Node dst = getNode(edge.dstNId);
            if(dst.inEid.contains(EId)){
                dst.inEid.remove(EId);
            }
            EdgeH.remove(EId);

        }
        for(int i = 0; i < Node.getInDeg(); i++){
            int EId = Node.getInEId(i);
            Edge edge = getEdge(EId);
            //Assert dest??;
            Node src = getNode(edge.srcNId);
            if(src.outEid.contains(EId)){
                src.outEid.remove(EId);
            }
            EdgeH.remove(EId);

        }
        NodeH.remove(Nid);

    }

    void DelNode(Node N){
        DelNode(N.Id);
    }

    String GetRndNId(){
        Collection coll = NodeH.values();
        Node[] nodes = (Node[]) coll.toArray();
        int randomInt = ThreadLocalRandom.current().nextInt(0,nodes.length);
        return nodes[randomInt].Id;

    }

    //Iterative beg end node functions?




    //EDGES

    int getEdges(){return EdgeH.size();}

    Edge getEdge(int EId){
        return EdgeH.get(EId);
    }

    boolean isEdge(int Eid){
        return EdgeH.containsKey(Eid);
    }

    boolean isEdge(String srcNId, String dstNId, int Eid, boolean Dir){
        Node src = getNode(srcNId);
        for(int edge=0 ; edge < src.getOutDeg();edge++){
            Edge edge1 = getEdge(src.getOutEId(edge));
            if(dstNId == edge1.dstNId){
                return true;
            }
        }
        if(!Dir){
            for(int edge=0 ; edge < src.getInDeg();edge++){
                Edge edge1 = getEdge(src.getInEId(edge));
                if(dstNId == edge1.srcNId){
                    return true;
                }
            }

        }
        return false;
    }

    int AddEdge(String srcNid, String dstNid, int Eid){
        if(Eid == -1){
            Eid = MxEId;
            MxEId++;
        }
        if(!isEdge(Eid)){
            if(isNode(srcNid) && isNode((dstNid))){
                EdgeH.put(Eid, new Edge(Eid,srcNid,dstNid));
                getNode(srcNid).outEid.add(Eid);
                getNode(dstNid).inEid.add(Eid);
            }
        }
        return Eid;
    }

    void DelEdge(int EId){
        if(isEdge(EId)){
            String srcNId = getEdge(EId).srcNId;
            String dstNId = getEdge(EId).dstNId;
            Node src = getNode(srcNId);
            if(src.outEid.contains(EId)){
                src.outEid.remove(EId);
            }

            Node dst = getNode(dstNId);
            if(src.inEid.contains(EId)){
                src.inEid.remove(EId);
            }
            EdgeH.remove(EId);

        }
    }

    //Edge Iterator functions?

    boolean Empty() { return getNodes()==0; }
    void Clr() { MxEId=0;  NodeH.clear();  EdgeH.clear(); }














}
