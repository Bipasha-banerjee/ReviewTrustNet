package edu.vt.NetInf;


import Jama.Matrix;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class NetInf {

    HashMap<String, NodeInfo> NodeHMap = new HashMap<>();
    HashMap<EdgePair,EdgeInfo> EdgeInfoMap = new HashMap<>();
    ArrayList<GainPair> GainList = new ArrayList<>();
    HashMap<EdgePair,CascIdList>  CascPerEdge = new HashMap<>();
    CascadeList cascadeList = new CascadeList();
    Graph graph= new Graph();
    Graph groundTruth = new Graph();
    outputGraph outputGraph = new outputGraph();
    boolean BoundOn, CompareGroundTruth;
    double alphaParam = 1.0;
    HashMap<EdgePair,Double> Alphas = new HashMap<>();
    HashMap<EdgePair,Double> Betas = new HashMap<>();
    HashMap<EdgePair,BigInteger> Deltas = new HashMap<>();
    HashMap<String,BigInteger> TimeH = new HashMap<>();
    double[][] CMatrix;
    double[][] PMatrix;
    double thresholdEigen = 0.5;
    int trusted = 100;
    double a=0.2;
    int iterations = 20;
    Matrix tkFinal;


    public NetInf() {
        BoundOn = false;
        CompareGroundTruth = false;
    }

    public NetInf(boolean boundOn, boolean compareGroundTruth) {
        BoundOn = boundOn;
        CompareGroundTruth = compareGroundTruth;
    }

    public void clear(){
        NodeHMap.clear();
        GainList.clear();
        CascPerEdge.clear();
        cascadeList.clear();
        graph.Clr();
        groundTruth.Clr();
        Alphas.clear();
        Betas.clear();
        Deltas.clear();
        TimeH.clear();
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
        public void clear(){cascadeList.clear();}
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

    public void loadGroundTruth(String path, double betaMn , double betaMx) throws FileNotFoundException, NumberFormatException {

        File file =
                new File(path);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String[] lineSplit = line.split(",");
            groundTruth.AddNode(lineSplit[0]);
           //BigInteger l = new BigInteger("1234647748828723642992749");
            // System.out.println(l);
            if(!TimeH.containsKey(lineSplit[0])){
              //  System.out.println(lineSplit[0]);
                TimeH.put(lineSplit[0], new BigInteger(lineSplit[3]));
            }
        }

        Scanner sc1 = new Scanner(file);

        while(sc1.hasNextLine()){
            String line = sc1.nextLine();
            String[] lineSplit = line.split(",");
            groundTruth.AddEdge(lineSplit[0], lineSplit[1],-1);
            Alphas.put(new EdgePair(lineSplit[0],lineSplit[1]), Double.valueOf(lineSplit[2]));
            double boundedRandomValue = ThreadLocalRandom.current().nextDouble(0, 1);
            Betas.put(new EdgePair(lineSplit[0],lineSplit[1]), boundedRandomValue);
            Deltas.put(new EdgePair(lineSplit[0],lineSplit[1]),new BigInteger(lineSplit[4]).subtract(new BigInteger(lineSplit[3])));
            System.out.println(groundTruth.getNodes() + "Nodes and " + groundTruth.getEdges() + "Edges added.");


        }
    }

    public Cascade genCascade(Cascade C,HashMap<EdgePair,Integer> EdgesUsed){
        HashMap<String,BigInteger> InfectedNIdH = new HashMap<>();
        HashMap<String,String> InfectedByH = new HashMap<>();
        BigInteger GlobalTime;
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
            BigInteger window = GlobalTime.add(new BigInteger("500000000")) ;

            while(true){

                InfectedNIdH = sort(InfectedNIdH);
                Iterator it = InfectedNIdH.entrySet().iterator();
                Map.Entry pair = (Map.Entry) it.next();
                String NId = (String) pair.getKey();
                GlobalTime = (BigInteger) pair.getValue();
                if(GlobalTime.compareTo(window) == 1 || GlobalTime.compareTo(window) ==0 ){
                    break;
                }
                C.Add(NId,GlobalTime);

                Graph.Node N = groundTruth.getNode(NId);

                for(int e=0; e<N.getOutDeg();e++) {
                    int EId = N.getOutEId(e);
                    Graph.Edge edge = groundTruth.getEdge(EId);
                    String DstNid = edge.getDstNId();
                    //System.out.println(NId+" "+DstNid);
                   // System.out.println(Betas.get(new EdgePair(NId, DstNid)));
                        beta = Betas.get(new EdgePair(NId, DstNid));

                        System.out.println("Beta"+beta);
                        if (ThreadLocalRandom.current().nextDouble(0, 1) > beta) {
                            continue;
                        }
                        if (InfectedByH.containsKey(NId) && InfectedNIdH.get(NId).equals(DstNid)) {
                            continue;
                        }
                        BigInteger t1 = TimeH.get(NId);
                        if (InfectedNIdH.containsKey(DstNid)) {
                            String parent = InfectedByH.get(DstNid);
                            if ((InfectedNIdH.get(DstNid).subtract(TimeH.get(parent))).compareTo(InfectedNIdH.get(DstNid).subtract(t1)) == 1) {
                                InfectedByH.put(DstNid, NId);
                            }
                        } else {
                            InfectedNIdH.put(DstNid, TimeH.get(DstNid));
                            InfectedByH.put(DstNid, NId);

                        }
                    }
                    InfectedNIdH.put(NId, window);


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

    public HashMap<String,BigInteger> sort(
            HashMap<String,BigInteger> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<BigInteger> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        HashMap<String, BigInteger> sortedMap =
                new HashMap<>();

        Iterator<BigInteger> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            BigInteger val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                BigInteger comp1 = passedMap.get(key);
                BigInteger comp2 = val;

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
                    if((cascadeList.get(cIdList.get(c)).getUnixTime(cascadeList.get(cIdList.get(c)).getNode(i))).compareTo(cascadeList.get(cIdList.get(c)).getUnixTime(NId))==-1){
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
                p += cascadeList.get(c).updateProb(n1, n2, false, Alphas, alphaParam); }
            return p;
            }
        CascIdList cList = new CascIdList();
        if(CascPerEdge.containsKey(new EdgePair(n1,n2)) ){
            cList = CascPerEdge.get(new EdgePair(n1, n2));
        }


        for(int c=0;c<cList.Size();c++)
        {
            p+=(cascadeList.get(cList.get(c)).updateProb(n1,n2,false,Alphas,alphaParam))-(cascadeList.get(cList.get(c)).CurProb);
        }
        return p;
        }

        public EdgePair GetBestEdge(double CurProb, double LastGain, boolean msort, int attempts) {
            EdgePair BestEdge= new EdgePair(null,null);
            double BestGain = Double.MIN_VALUE;
            ArrayList<GainPair> gainListToSort = new ArrayList<>();
            ArrayList<Integer> ZeroEdges = new ArrayList<>();
            int BestGainIndex = -1;

            if (msort) {
                for (int i = 0; i < Math.min(attempts - 1, GainList.size()); i++) {
                    gainListToSort.add(GainList.get(i));

                }
                gainListToSort.sort(new sortGainList());

                for (int i = 0, ii = 0, j = 0; ii < gainListToSort.size(); j++) {
                    if ((i + gainListToSort.size() < GainList.size()) &&
                            (gainListToSort.get(ii).GainValue < GainList.get(i + gainListToSort.size()).GainValue)) {
                        GainList.add(j, GainList.get(gainListToSort.size() + i));
                        i++;
                    } else {
                        GainList.add(j, gainListToSort.get(ii));
                        ii++;
                    }
                }
                attempts = 0;

                for (int e = 0; e < GainList.size(); e++) {
                    EdgePair Edge = GainList.get(e).edgePair;
                    if (graph.isEdge(Edge.Source, Edge.Destination, true)) {
                        continue;
                    }

                    double EProb = GetAllCascProb(Edge.Source, Edge.Destination);
                    GainList.get(e).GainValue = EProb;
                    if (BestGain < EProb) {
                        BestGain = EProb;
                        BestGainIndex = e;
                        BestEdge = Edge;
                    }
                    attempts++;

                    if (!graph.isEdge(Edge.Source, Edge.Destination, true) && graph.getEdges() > 1) {
                        if (EProb == 0)
                            ZeroEdges.add(e);

                    }

                    if (e + 1 == GainList.size() || BestGain >= GainList.get(e + 1).GainValue) {
                        CurProb += BestGain;

                        if (BestGain == 0)
                            return new EdgePair(null, null);
                        GainList.remove(BestGainIndex);

                        for (int i=ZeroEdges.size()-1; i>=0; i--) {
                            if (ZeroEdges.get(i) > BestGainIndex)
                                GainList.remove(ZeroEdges.get(i-1));
                            else
                                GainList.remove(ZeroEdges.get(i));
                        }
                        if (GainList.size() > 2) { attempts -= (ZeroEdges.size()-1); }

                        msort = (attempts > 1);

                        LastGain = BestGain;

                        return BestEdge;
                    }
                }



            }
            return new EdgePair(null,null);
        }


                //  TVec<TPair<TFlt, TIntPr> > EdgeGainCopyToSortV;


    public void GreedyOpt()
    {
        double CurProb = GetAllCascProb(null, null);
        double LastGain =Double.MAX_VALUE;
        int attempts = 0;
        Boolean msort = false;
        Cascade x = new Cascade();

        while(true)
        {
            double prev = CurProb;
             EdgePair BestE = GetBestEdge(CurProb, LastGain, msort, attempts);
            if (BestE == new EdgePair(null, null)) // if we cannot add more edges, we stop
                break;

            double value = x.TransProb(BestE.Source,BestE.Destination,alphaParam,Alphas.get(new EdgePair(BestE.Source,BestE.Destination)));

            graph.AddEdge(BestE.Source,BestE.Destination,-1,value);
           /* double Bound = 0;
            if (BoundOn)
                Bound = GetBound(BestE, prev);
*/
            CascIdList cascEdge = CascPerEdge.get(BestE);
            for (int c = 0; c < cascEdge.Size(); c++) {
                cascadeList.get(cascEdge.get(c)).updateProb(BestE.Source, BestE.Destination, true,Alphas,alphaParam); // update probabilities
            }


        }

    }

    void saveGraphText() throws IOException {
        System.out.println("Inside save");
        int size = graph.getEdges();
        String path = "/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/outputFiles/";

        FileWriter fileWriter = new FileWriter(path+"outputGraph.csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(int i =0; i< size;i++){
            Graph.Edge edge  = graph.getEdge(i);
            printWriter.println(edge.getId() + "," + edge.getSrcNId() + "," + edge.getDstNId() + "," + edge.getValue());
        }
    }

    void AddtoOutputGraph(){
        Iterator it = graph.NodeH.keySet().iterator();
        while(it.hasNext()){
            String NId = (String) it.next();
            if(!outputGraph.isNode(NId)) {
                outputGraph.AddNode(NId);
            }
        }
        for(int i=0; i< graph.getEdges();i++){
            Graph.Edge edge = graph.getEdge(i);
            String srcId = edge.getSrcNId();
            String dstId = edge.getDstNId();

            double value = edge.getValue();
            outputGraph.AddEdge(srcId,dstId,-1,value);
            if(!outputGraph.containsEdge(srcId,dstId)){
                outputGraph.create(srcId,dstId);
            }
            outputGraph.AddEdgeValue(srcId,dstId,value);
        }

    }

    void setupCmatrix(){
        HashMap<String,Integer> NodeIdH = outputGraph.initNodeIdH();
        CMatrix = new double[NodeIdH.size()][NodeIdH.size()];
        Iterator it = outputGraph.valueH.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            edu.vt.NetInf.outputGraph.EdgePair ep = (outputGraph.EdgePair) pair.getKey();
            ArrayList<Double> values = outputGraph.getEdgeValue(ep.Source,ep.Destination);
            double sij = 0.0;
            for(int i=0; i < values.size();i++){
                if(values.get(i) > thresholdEigen){
                    sij += 1;
                }
                else{
                    sij -= 1;
                }
            }

            CMatrix[NodeIdH.get(ep.Source)][NodeIdH.get(ep.Destination)] = sij;

        }

        for(int i =0; i < CMatrix.length;i++){
            double total = 0;
            for (int j =0; j< CMatrix[i].length;j++){
                total += CMatrix[i][j];
            }

            for( int j =0; j < CMatrix[i].length;j++){
               if(total>0){
                   CMatrix[i][j] = CMatrix[i][j]/total;
                }
               else{
                   CMatrix[i][j] = 1/trusted;
               }
            }
        }

        }

void processEigen(){
        PMatrix = new double[CMatrix.length][CMatrix[0].length];
        Matrix pMatrix = new Matrix(PMatrix);
        Matrix cMatrix = new Matrix(CMatrix);
        Matrix tk = pMatrix.copy();
        Matrix tkplus1 = null;
        Matrix cTrans = cMatrix.transpose();
        for(int i=0;i<iterations; i++)
        {
            tkplus1 = cTrans.times(tk);
            tkplus1 = tkplus1.times(1-a);
            tkplus1 =tkplus1.plus(pMatrix.times(a));
            tk = tkplus1.copy();
        }
        tkFinal=tk.copy();
}



    class  sortGainList implements Comparator<GainPair>{
        public int compare(GainPair a, GainPair b){
            return (int) (b.GainValue - a.GainValue);

        }

    }




}





