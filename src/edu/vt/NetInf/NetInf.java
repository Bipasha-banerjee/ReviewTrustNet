package edu.vt.NetInf;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NetInf {

    HashMap<String, NodeInfo> NodeHMap = new HashMap<>();
    HashMap<EdgePair,EdgeInfo> EdgeInfoMap = new HashMap<>();
    ArrayList<GainPair> GainList = new ArrayList<>();
    HashMap<EdgePair,CascIdList>  CascPerEdge = new HashMap<>();
    CascadeList cascadeList = new CascadeList();
    Graph graph, groundTruth;
    boolean BoundOn, CompareGroundTruth;
    HashMap<EdgePair,Double> Alphas = new HashMap<>();
    HashMap<EdgePair,Double> Betas = new HashMap<>();
    HashMap<EdgePair,Long> Deltas = new HashMap<>();
    HashMap<String,Long> TimeH = new HashMap<>();


    public NetInf() {
        BoundOn = false;
        CompareGroundTruth = false;
    }

    public NetInf(boolean boundOn, boolean compareGroundTruth) {
        BoundOn = boundOn;
        CompareGroundTruth = compareGroundTruth;
    }

    public static class EdgePair{
        public String Source;
        public String Destination;

        public EdgePair(String source, String destination) {
            Source = source;
            Destination = destination;
        }
    }

    public class CascadeList{
        ArrayList<Cascade> cascadeList = new ArrayList<>();
        public CascadeList() {
        }
        public void Add(Cascade c){
            cascadeList.add(c);
        }
        public int Size()
        {
            return cascadeList.size();
        }
        public Cascade get(int i)
        {
            return cascadeList.get(i);
        }
    }
    public class GainPair{
        public Double GainValue;
        public EdgePair edgePair;

        public GainPair(Double gainValue, EdgePair edgePair) {
            GainValue = gainValue;
            this.edgePair = edgePair;
        }
    }

    public void AddCasc(Cascade c ){

        cascadeList.Add(c);
        for(int i=0; i<c.Len(); i++)
        {
            if(!NodeHMap.containsKey(c.getNode(i))){
                NodeHMap.put(c.getNode(i), new NodeInfo(c.getNode(i),1));

            }
            else{
                NodeInfo nodeinfo = NodeHMap.get(c.getNode(i));
                nodeinfo.setCascadeNumber(nodeinfo.getCascadeNumber()+1);
                NodeHMap.put(c.getNode(i),nodeinfo);
            }
        }
    }

    public void loadGroundTruth(String path, double betaMn , double betaMx) throws FileNotFoundException {

        File file =
                new File(path);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String[] lineSplit = line.split(",");
            groundTruth.AddNode(lineSplit[0]);
            if(!TimeH.containsKey(lineSplit[0])){
                TimeH.put(lineSplit[0], Long.valueOf(lineSplit[3]));
            }
        }
        Scanner sc1 = new Scanner(file);

        while(sc1.hasNextLine()){
            String line = sc.nextLine();
            String[] lineSplit = line.split(",");
            groundTruth.AddEdge(lineSplit[0], lineSplit[1],-1);
            Alphas.put(new EdgePair(lineSplit[0],lineSplit[1]), Double.valueOf(lineSplit[2]));
            double boundedRandomValue = ThreadLocalRandom.current().nextDouble(0, 1);
            Betas.put(new EdgePair(lineSplit[0],lineSplit[1]), boundedRandomValue);
            Deltas.put(new EdgePair(lineSplit[0],lineSplit[1]),Long.valueOf(lineSplit[4]) - Long.valueOf(lineSplit[3]));
            System.out.println(groundTruth.getNodes() + "Nodes and" + groundTruth.getEdges() + "Edges added.");


        }
    }

    public Cascade genCascade(Cascade C,HashMap<EdgePair,Integer> EdgesUsed){
        HashMap<String,Long> InfectedNIdH = new HashMap<>();
        HashMap<String,String> InfectedByH = new HashMap<>();
        Long GlobalTime;
        String StartNId;
        double alpha,beta;

        if(groundTruth.getNodes() ==0){
            return null;
        }
        while(C.Len() < 2){
            C.Clr();
            InfectedNIdH.clear();
            InfectedByH.clear();

            StartNId = groundTruth.GetRndNId();
            GlobalTime = TimeH.get(StartNId);
            InfectedNIdH.put(StartNId,TimeH.get(StartNId));
            Long window = GlobalTime + 500000000;

            while(true){

                InfectedNIdH = sort(InfectedNIdH);
                Iterator it = InfectedNIdH.entrySet().iterator();
                Map.Entry pair = (Map.Entry) it.next();
                String NId = (String) pair.getKey();
                GlobalTime = (Long) pair.getValue();
                if(GlobalTime>=window){
                    break;
                }
                C.Add(NId,GlobalTime);

                Graph.Node N = groundTruth.getNode(NId);
                for(int e=0; e<N.getOutDeg();e++) {
                    int EId = N.getOutEId(e);
                    Graph.Edge edge = groundTruth.getEdge(EId);
                    String DstNid = edge.getDstNId();
                    beta = Betas.get(new EdgePair(NId, DstNid));
                    if (ThreadLocalRandom.current().nextDouble(0, 1) > beta) {
                        continue;
                    }
                    if (InfectedByH.containsKey(NId) && InfectedNIdH.get(NId).equals(DstNid)) {
                        continue;
                    }
                    Long t1 = TimeH.get(NId);
                    if (InfectedNIdH.containsKey(DstNid)) {
                        String parent = InfectedByH.get(DstNid);
                        if ((InfectedNIdH.get(DstNid) - TimeH.get(parent)) > (InfectedNIdH.get(DstNid) - t1)) {
                            InfectedByH.put(DstNid, NId);
                        }
                    } else {
                        InfectedNIdH.put(DstNid, TimeH.get(DstNid));
                        InfectedByH.put(DstNid, NId);

                    }
                }
                InfectedNIdH.put(NId,window);

            }

        }
        C.Sort();
        Iterator EIt = InfectedByH.entrySet().iterator();
        while(EIt.hasNext()){
            Map.Entry pair = (Map.Entry) EIt.next();
            String dst = (String) pair.getKey();
            String src = (String) pair.getValue();
            EdgePair edge = new EdgePair(src,dst);
            if(!EdgesUsed.containsKey(edge)){
                EdgesUsed.put(edge,0);
            }
            EdgesUsed.put(edge,EdgesUsed.get(edge)+1);
        }
        return C;


    }

    public HashMap<String,Long> sort(
            HashMap<String,Long> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Long> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        HashMap<String, Long> sortedMap =
                new HashMap<>();

        Iterator<Long> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Long val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Long comp1 = passedMap.get(key);
                Long comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public class CascIdList
    {
        ArrayList<Integer> cascIdList = new ArrayList<>();
        public CascIdList() {
        }
        public void Add(int i){
        cascIdList.add(i);
        }

        public int Size()
        {
            return cascIdList.size();
        }
        public int get(int i)
        {
            return cascIdList.get(i);
        }

    }
    public void init()
    {
        HashMap<String, CascIdList> CascPN = new HashMap<>();
        graph = new Graph();
        GainList.clear();
        CascPerEdge.clear();

        for(int c=0;c<cascadeList.Size();c++)
        {
            for(int i=0; i<=cascadeList.get(c).Len();i++)
            {
                if(!graph.isNode(cascadeList.get(c).getNode(i)))
                {
                    graph.AddNode(cascadeList.get(c).getNode(i));
                }
                if(!CascPN.containsKey(cascadeList.get(c).getNode(i)))
                {
                    CascPN.put(cascadeList.get(c).getNode(i),new CascIdList());
                }
                CascIdList cascIdList =  CascPN.get(cascadeList.get(c).getNode(i));
                cascIdList.Add(c);
                CascPN.put(cascadeList.get(c).getNode(i),cascIdList);//check how it performs
            }
            cascadeList.get(c).initProb();
        }

        Iterator it = graph.NodeH.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            String NId = (String) pair.getKey();
            CascIdList cIdList = CascPN.get(NId);
            for(int c=0; c<cascadeList.Size(); c++)
            {
                for(int i=0;i<cascadeList.get(c).Len(); i++) {
                    if(cascadeList.get(cIdList.get(c)).getNode(i)==NId){
                        continue;
                    }
                    if(cascadeList.get(cIdList.get(c)).getUnixTime(cascadeList.get(cIdList.get(c)).getNode(i)) < cascadeList.get(cIdList.get(c)).getUnixTime(NId)){
                        if(!CascPerEdge.containsKey(new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId)))

                        {
                        // graph.AddEdge(cascadeList.get(cIdList.get(c)).getNode(i),NId,-1);
                         GainList.add(new GainPair(Double.MAX_VALUE,new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId)));
                         CascPerEdge.put((new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId)), new CascIdList());
                        }

                        CascIdList cascadeList1 = CascPerEdge.get(new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId));
                        cascadeList1.Add(c);
                        CascPerEdge.put((new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId)), cascadeList1);
                    }
                }
            }
        }

    }

    public double GetAllCascProb( String n1, String n2) {
        double p = 0.0;
        double alpha  = 0.0;

        if (n1.equals(null) && n2.equals(null)) {
            for (int c = 0; c < cascadeList.Size(); c++) {
                p += cascadeList.get(c).updateProb(n1, n2, false, Alphas ); }
            return p;
            }
        CascIdList cList = new CascIdList();
        if(CascPerEdge.containsKey(new EdgePair(n1,n2)) ){
            cList = CascPerEdge.get(new EdgePair(n1, n2));
        }


        for(int c=0;c<cList.Size();c++)
        {
            p+=(cascadeList.get(cList.get(c)).updateProb(n1,n2,false,Alphas))-(cascadeList.get(cList.get(c)).CurProb);
        }
        return p;
        }

        public EdgePair GetBestEdge(double CurProb, double LastGain, boolean msort, int attempts)
        {
            EdgePair BestEdge;
            double BestGain = Double.MIN_VALUE;
            ArrayList<GainPair> gainListToSort = new ArrayList<>();
            ArrayList<Integer> ZeroEdges = new ArrayList<>();
            int BestGainIndex = -1;

            if(msort){
                for(int i=0; i < Math.min(attempts - 1,GainList.size());i++){
                    gainListToSort.add(GainList.get(i));

                }
                gainListToSort.sort(); ///////////Start from here

            }


          //  TVec<TPair<TFlt, TIntPr> > EdgeGainCopyToSortV;
        }

    public void GreedyOpt(int MaxEdges)
    {
        double CurProb = GetAllCascProb(null, null);
        double LastGain =Double.MAX_VALUE;
        int attempts = 0;
        Boolean msort = false;

        for(int k=0; k< MaxEdges && GainList.size()>0; k++ )
        {
            double prev = CurProb;
             EdgePair BestE = GetBestEdge(CurProb, LastGain, msort, attempts);
        }

    }
}





