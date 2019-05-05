package edu.vt.NetInf;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class outputGraph {
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
        private Double value = 0.0;

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

        public Edge(Integer id, String srcNId, String dstNId, double value) {
            this.id = id;
            this.srcNId = srcNId;
            this.dstNId = dstNId;
            this.value = value;
        }

        //TSIn and TSout


        public Double getValue() {
            return value;
        }

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

    public class EdgePair{
        public String Source;
        public String Destination;

        public EdgePair(String source, String destination) {
            Source = source;
            Destination = destination;
        }
    }

    public class valueList {
        ArrayList<Double> valueList;
        public valueList(){
            valueList = new ArrayList<>();
        }
        void Add(double d){valueList.add(d);}
        double Get(int i) { return  valueList.get(i);}
        int Size() {return  valueList.size();}
        ArrayList<Double> get(){ return  valueList;}
    }
    //Iterators to add?

    private Integer  MxEId=0;

    public Map<String, Node> NodeH = new LinkedHashMap<>();
    public ArrayList<Edge> EdgeH = new ArrayList<>();
    public Map<EdgePair,valueList> valueH = new LinkedHashMap<>();



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
        return EdgeH.contains(Eid);
    }

    boolean isEdge(String srcNId, String dstNId, boolean Dir){
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

    Edge getEdge(String srcNId, String dstNId, boolean Dir){
        Node src = getNode(srcNId);
        for(int edge=0 ; edge < src.getOutDeg();edge++){
            Edge edge1 = getEdge(src.getOutEId(edge));
            if(dstNId == edge1.dstNId){
                return edge1;
            }
        }
        if(!Dir){
            for(int edge=0 ; edge < src.getInDeg();edge++){
                Edge edge1 = getEdge(src.getInEId(edge));
                if(dstNId == edge1.srcNId){
                    return edge1;
                }
            }

        }
        return null;
    }

    int AddEdge(String srcNid, String dstNid, int Eid){
        if(Eid == -1){
            Eid = MxEId;
            MxEId++;
        }
        if(!isEdge(Eid)){
            if(isNode(srcNid) && isNode((dstNid))){
                EdgeH.add(Eid, new Edge(Eid,srcNid,dstNid));
                getNode(srcNid).outEid.add(Eid);
                getNode(dstNid).inEid.add(Eid);
            }
        }
        return Eid;
    }

    int AddEdge(String srcNid, String dstNid, int Eid,double value){
        if(Eid == -1){
            Eid = MxEId;
            MxEId++;
        }
        if(!isEdge(Eid)){
            if(isNode(srcNid) && isNode((dstNid))){
                EdgeH.add(Eid, new Edge(Eid,srcNid,dstNid,value));
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

    HashMap<String,Integer> initNodeIdH(){
        HashMap<String,Integer> NodeIdH = new HashMap<>();
        Iterator it = NodeH.keySet().iterator();
        int i = 0;
        while(it.hasNext()){
            String node = (String) it.next();
            NodeIdH.put(node,i);
            i++;
        }
        return NodeIdH;

    }

    boolean containsEdge(String src,String dst){
        return  valueH.containsKey(new EdgePair(src,dst));
    }

    void create(String src, String dst){
        valueH.put(new EdgePair(src,dst),new valueList());
    }
    public EdgePair findValueH(String Source, String Destination)
    {

        for (Map.Entry<EdgePair, valueList> entry : valueH.entrySet()) {
            if(Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination)))
            {
                //EdgePair ep1 = new EdgePair(Source,Destination);

                return entry.getKey();
            }
            //   System.out.println(entry.getKey().Source + ":"+ entry.getKey().Destination+" : "+entry.getValue());
        }
        return null;

    }

    void AddEdgeValue(String src, String dst,double value){

        EdgePair ep = findValueH(src,dst);
        valueList vl = valueH.get(ep);
        vl.Add(value);
        valueH.put(ep,vl);
    }

    ArrayList<Double> getEdgeValue(String src, String dst){
        return valueH.get(new EdgePair(src,dst)).get();
    }














}
