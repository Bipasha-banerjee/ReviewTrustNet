package edu.vt.NetInf;


import Jama.Matrix;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.security.krb5.internal.crypto.Des;

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
    HashMap<EdgePair,Long> Deltas = new HashMap<>();
    HashMap<String,Long> TimeH = new HashMap<>();
    //double[][] CMatrix = new double[1325][1325];
    double[][] CMatrix = new double[609][609];
    double[][] PMatrix;
    double thresholdEigen = 0.5;
    int trusted = 100;
    double a=0.2;
    int iterations = 5;
    Matrix tkFinal;
    Boolean msort = false;
    double LastGain =Double.MAX_VALUE;
    int attempts = 0;
    double CurProb = GetAllCascProb("null", "null");

    public double[][] getCMatrix() {
        return CMatrix;
    }

    public void setCMatrix(double[][] CMatrix) {
        this.CMatrix = CMatrix;
    }

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
        msort = false;
        LastGain =Double.MAX_VALUE;
        attempts = 0;
         CurProb = GetAllCascProb("null", "null");

    }



     public static class EdgePair{
         public String Source;
         public String Destination;

         public void setSource(String source) {
             Source = source;
         }

         public void setDestination(String destination) {
             Destination = destination;
         }

         public EdgePair(String source, String destination) {
            Source = source;
            Destination = destination;
        }

        public String getSource() {
            return Source;
        }

        public String getDestination() {
            return Destination;
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

    public void AddCasc(Cascade c){

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
                TimeH.put(lineSplit[0],  Long.valueOf(lineSplit[3]));
            }
        }

        Scanner sc1 = new Scanner(file);

        while(sc1.hasNextLine()){
            String line = sc1.nextLine();
            String[] lineSplit = line.split(",");
            groundTruth.AddEdge(lineSplit[0], lineSplit[1],-1);
            Alphas.put(new EdgePair(lineSplit[0],lineSplit[1]), Double.valueOf(lineSplit[2]));
            double boundedRandomValue = ThreadLocalRandom.current().nextDouble(0, 1);


            EdgePair ep  = putInEdgePair(lineSplit[0],lineSplit[1]);
            if(ep != null){
                Betas.put(ep, boundedRandomValue);

            }else{
                EdgePair ep1 = new EdgePair(lineSplit[0],lineSplit[1]);
                Betas.put(ep1,boundedRandomValue);
            }

            EdgePair ep2  = putinAlphas(lineSplit[0],lineSplit[1]);
            if(ep2 != null){
                Alphas.put(ep2, Double.valueOf(lineSplit[2]));

            }else{
                EdgePair ep3 = new EdgePair(lineSplit[0],lineSplit[1]);
                Alphas.put(ep3, Double.valueOf(lineSplit[2]));
            }




         //   System.out.println(Betas.get(ep));
            Deltas.put(new EdgePair(lineSplit[0],lineSplit[1]),Long.valueOf(lineSplit[4]) - Long.valueOf(lineSplit[3]));
         //   System.out.println(groundTruth.getNodes() + "Nodes and " + groundTruth.getEdges() + "Edges added.");


        }
    }

    public double findEdgePair(String Source, String Destination)
    {

        for (Map.Entry<EdgePair, Double> entry : Betas.entrySet()) {
            if(Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination)))
            {
                //EdgePair ep1 = new EdgePair(Source,Destination);

                 return entry.getValue();
            }
            //   System.out.println(entry.getKey().Source + ":"+ entry.getKey().Destination+" : "+entry.getValue());
        }
        return 0.0;

    }

    public double findAlpha(String Source, String Destination)
    {

        for (Map.Entry<EdgePair, Double> entry : Alphas.entrySet()) {
            if(Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination)))
            {
                //EdgePair ep1 = new EdgePair(Source,Destination);

                return entry.getValue();
            }
            //   System.out.println(entry.getKey().Source + ":"+ entry.getKey().Destination+" : "+entry.getValue());
        }
        return 0.0;

    }

    public CascIdList findCascEdgePair(String Source, String Destination)
    {

        for (Map.Entry<EdgePair, CascIdList> entry : CascPerEdge.entrySet()) {
            if(Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination)))
            {
                //EdgePair ep1 = new EdgePair(Source,Destination);

                return entry.getValue();
            }
            //   System.out.println(entry.getKey().Source + ":"+ entry.getKey().Destination+" : "+entry.getValue());
        }
        return null;

    }

    public EdgePair putInEdgePair(String Source, String Destination) {
        for (Map.Entry<EdgePair, Double> entry : Betas.entrySet()) {
            if (Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination))) {
                return  entry.getKey();
            }

        }
        return null;
    }

    public EdgePair putinAlphas(String Source, String Destination) {
        for (Map.Entry<EdgePair, Double> entry : Alphas.entrySet()) {
            if (Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination))) {
                return  entry.getKey();
            }

        }
        return null;
    }

    public EdgePair putInEdgePaiCascr(String Source, String Destination) {
        for (Map.Entry<EdgePair, CascIdList> entry : CascPerEdge.entrySet()) {
            if (Source.equals(entry.getKey().Source) && (Destination.equals(entry.getKey().Destination))) {
                return  entry.getKey();
            }

        }
        return null;
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
            Long window = GlobalTime + 50000000;

            while(true){

                InfectedNIdH = sort(InfectedNIdH);
                Iterator it = InfectedNIdH.entrySet().iterator();
                Map.Entry pair = (Map.Entry) it.next();
                String NId = (String) pair.getKey();
                GlobalTime = (Long) pair.getValue();
                if(GlobalTime.compareTo(window) == 1 || GlobalTime.compareTo(window) ==0 ){
                    break;
                }
                C.Add(NId,GlobalTime);
                Graph.Node N = groundTruth.getNode(NId);

                for(int e=0; e<N.getOutDeg();e++) {
                    int EId = N.getOutEId(e);
                    Graph.Edge edge = groundTruth.getEdge(EId);
                    String DstNid = edge.getDstNId();
                  //  EdgePair ep1 = new EdgePair(NId, DstNid);



                        beta = findEdgePair(NId,DstNid);

                      //  System.out.println("Beta"+beta);
                        if (beta<0.5) {
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
                    InfectedNIdH.put(NId, window);


            }
            if(groundTruth.getNodes()<=2){
                break;
            }


        }

        C.Sort();
        Iterator EIt = InfectedByH.entrySet().iterator();
        while(EIt.hasNext()){
            Map.Entry pair = (Map.Entry) EIt.next();
            String dst = (String) pair.getKey();
            String src = (String) pair.getValue();
            EdgePair edge = null;
          //  int val = 0;

            for (Map.Entry<EdgePair, Integer> entry : EdgesUsed.entrySet()) {
                if (src.equals(entry.getKey().Source) && (dst.equals(entry.getKey().Destination))) {
                    edge =  entry.getKey();
                    EdgesUsed.put(edge,EdgesUsed.get(edge)+1);

                    break;
                }
            }
            if(edge == null){
                EdgesUsed.put(new EdgePair(src,dst),1);
            }




        }
        return C;


    }

   /* public HashMap<String,Long> sort(
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

                if (comp2.equals(comp1)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }*/
   public HashMap<String,Long> sort(HashMap<String,Long> passedMap) {
       List<Map.Entry<String, Long>> list =
               new LinkedList<Map.Entry<String, Long>>(passedMap.entrySet());

       // Sort the list
       Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
           public int compare(Map.Entry<String, Long> o1,
                              Map.Entry<String, Long> o2) {
               return (o1.getValue()).compareTo(o2.getValue());
           }
       });


       // put data from sorted list to hashmap
       HashMap<String, Long> temp = new LinkedHashMap<String, Long>();
       for (Map.Entry<String, Long> aa : list) {
           temp.put(aa.getKey(), aa.getValue());
       }
       return temp;
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
            for(int i=0; i<cascadeList.get(c).Len();i++)
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
            for(int c=0; c<cIdList.Size(); c++)
            {
                for(int i=0;i<cascadeList.get(cIdList.get(c)).Len(); i++) {
                    if(cascadeList.get(cIdList.get(c)).getNode(i)==NId){
                        continue;
                    }
                    if((cascadeList.get(cIdList.get(c)).getUnixTime(cascadeList.get(cIdList.get(c)).getNode(i))).compareTo(cascadeList.get(cIdList.get(c)).getUnixTime(NId))==1){
                        EdgePair ep = putInEdgePair(NId,cascadeList.get(cIdList.get(c)).getNode(i));
                        if(ep != null)
                        {
                        // graph.AddEdge(cascadeList.get(cIdList.get(c)).getNode(i),NId,-1);
                         GainList.add(new GainPair(Double.MAX_VALUE,new EdgePair(NId,cascadeList.get(cIdList.get(c)).getNode(i))));
                         CascPerEdge.put(ep, new CascIdList());
                         CascIdList cascadeList1 =  findCascEdgePair(NId,cascadeList.get(cIdList.get(c)).getNode(i));
                         cascadeList1.Add(c);
                            CascPerEdge.put(ep,cascadeList1);
                        }
                        //System.out.println(cascadeList.get(cIdList.get(c)).getNode(i));
                        else {
                            CascIdList cascadeList1 = findCascEdgePair(NId, cascadeList.get(cIdList.get(c)).getNode(i));
                            //CascIdList cascadeList1 = CascPerEdge.get(new EdgePair(cascadeList.get(cIdList.get(c)).getNode(i),NId));
                            cascadeList1.Add(c);
                            CascPerEdge.put((new EdgePair(NId,cascadeList.get(cIdList.get(c)).getNode(i))), cascadeList1);
                        }
                    }
                }
            }
        }

    }

    public double GetAllCascProb( String n1, String n2) {
        double p = 0.0;
        double alpha  = 0.0;
//System.out.println(n1.equals("null"));
        if (n1.equals("null") && n2.equals("null")) {
            for (int c = 0; c < cascadeList.Size(); c++) {
                p += cascadeList.get(c).updateProb(n1, n2, false, Alphas, alphaParam); }
            return p;
            }
        CascIdList cList = findCascEdgePair(n1,n2);

        if(cList == null){
            return p;
        }


        for(int c=0;c<cList.Size();c++)
        {
            p+=(cascadeList.get(cList.get(c)).updateProb(n1,n2,false,Alphas,alphaParam))-(cascadeList.get(cList.get(c)).CurProb);
        }
        return p;
        }

        public EdgePair GetBestEdge() {
            EdgePair BestEdge= new EdgePair("null","null");
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
                        GainList.set(j, GainList.get(gainListToSort.size() + i));
                        i++;
                    } else {
                        GainList.set(j, gainListToSort.get(ii));
                        ii++;
                    }
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

                        if (BestGain == Double.MIN_VALUE)
                            return new EdgePair("null", "null");
                        GainList.remove(BestGainIndex);

                        for (int i=ZeroEdges.size()-1; i>=0; i--) {
                            if (ZeroEdges.get(i) > BestGainIndex)
                                GainList.remove(ZeroEdges.get(i)-1);
                            else
                                GainList.remove(ZeroEdges.get(i));
                        }
                        if (GainList.size() > 2) { attempts -= (ZeroEdges.size()-1); }

                        msort = (attempts > 1);

                        LastGain = BestGain;

                        return BestEdge;
                    }
                }




            return new EdgePair("null","null");
        }


                //  TVec<TPair<TFlt, TIntPr> > EdgeGainCopyToSortV;


    public void GreedyOpt()
    {


        Cascade x = new Cascade();

        while(true)
        {
            double prev = CurProb;
             EdgePair BestE = GetBestEdge();
            if (BestE.Source.equals("null") && BestE.Destination.equals("null")) // if we cannot add more edges, we stop
                break;

            double alpha = findAlpha(BestE.Source,BestE.Destination);

            //double value = x.TransProb(BestE.Source,BestE.Destination,alpha,alphaParam);

            graph.AddEdge(BestE.Source,BestE.Destination,-1,alpha);
           /* double Bound = 0;
            if (BoundOn)
                Bound = GetBound(BestE, prev);
*/
      //     System.out.println("Edges are "+BestE.Source + " "+ BestE.Destination);
            CascIdList cascEdge = findCascEdgePair(BestE.Source, BestE.Destination);
        //    System.out.println("Cascade size"+ cascEdge.Size());
            for (int c = 0; c < cascEdge.Size(); c++) {
                cascadeList.get(cascEdge.get(c)).updateProb(BestE.Source, BestE.Destination, true,Alphas,alphaParam); // update probabilities
            }


        }

    }

    void saveGraphText() throws IOException {
     //   System.out.println("Inside save");
        int size = graph.getEdges();
        String path = "/Users/bipashabanerjee/Documents/CS/sem2/DBMS/project/power/powerOutput.txt";

        FileWriter fileWriter = new FileWriter(path+"outputGraph.csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(int i =0; i< size;i++){
            Graph.Edge edge  = graph.getEdge(i);
            printWriter.println(edge.getId() + "," + edge.getSrcNId() + "," + edge.getDstNId() + "," + edge.getValue());
            System.out.println(edge.getId() + "," + edge.getSrcNId() + "," + edge.getDstNId() + "," + edge.getValue());
        }
    }

    void AddtoOutputGraph() throws FileNotFoundException {
        File inputFile = new File("/Users/bipashabanerjee/Documents/CS/sem2/DBMS/project/power/powerOutput.txt");
        Scanner scanner = new Scanner(inputFile);
        while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] lineSplit = line.split(",");
            if(!outputGraph.isNode(lineSplit[0])) {
                outputGraph.AddNode(lineSplit[0]);
            }
            if(!outputGraph.isNode(lineSplit[1])) {
                outputGraph.AddNode(lineSplit[1]);
            }
            outputGraph.AddEdge(lineSplit[1],lineSplit[0],-1, Double.parseDouble(lineSplit[2]));
            if(!outputGraph.containsEdge(lineSplit[1],lineSplit[0])){
                outputGraph.create(lineSplit[1],lineSplit[0]);
            }
            outputGraph.AddEdgeValue(lineSplit[1],lineSplit[0], Double.parseDouble(lineSplit[2]));

        }
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

        }

    }
void predictUsefulness() throws IOException {

    HashMap<String,Integer> NodeIdH = outputGraph.initNodeIdH();
    double[][] valsTransposed = tkFinal.getArray();
    int total1 = 0;
    int usePositves = 0;
    int useNegatives = 0;
    int trustPositives = 0;
    int trustNegatives = 0;
    BufferedReader in
            = new BufferedReader(new FileReader("/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/dataSet/20.json"));

    ArrayList<JSONObject> contentsAsJsonObjects = new ArrayList<JSONObject>();
    //   System.out.println("after content as json");
    while (true) {

        String str = in.readLine();
        //  System.out.println(str);
        if (str == null) break;
        contentsAsJsonObjects.add(new JSONObject(str));
    }
    for (JSONObject jobj : contentsAsJsonObjects) {


        String reviewerID = jobj.getString("reviewerID");
        if(NodeIdH.containsKey(reviewerID)){
            int x = NodeIdH.get(reviewerID);

        }
        else{
            continue;
        }
        JSONArray jarray = jobj.getJSONArray("helpful");


        Integer i = jarray.getInt(0);
        Integer j = jarray.getInt(1);
        double div = 0;
        if (i != 0 && j != 0) {
            if (i == 1 && j == 1) {
                div = 0.7;
            } else {
                div = (Double.valueOf(i)) / (Double.valueOf(j));
            }

        } else
            continue;
        total1++;
        if(div> 0.6){
            usePositves++;
        }
        else{
            useNegatives++;
        }

        if(valsTransposed[i][0]>0.68){
            trustPositives++;
        }
        else{
            trustNegatives++;
        }



    }
    System.out.println("UsePositive "+usePositves);
    System.out.println("UseNegative" + useNegatives);
    System.out.println("trustPositives "+ trustPositives);
    System.out.println("trust-ve " + trustNegatives);
}

    void setupCmatrix(){


        HashMap<String,Integer> NodeIdH = outputGraph.initNodeIdH();
        Iterator it = outputGraph.valueH.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            edu.vt.NetInf.outputGraph.EdgePair ep = (outputGraph.EdgePair) pair.getKey();
            ArrayList<Double> values = outputGraph.getEdgeValue(ep.Source,ep.Destination);
            double sij = 0.0;
            for(int i=0; i < values.size();i++){
                //sij+=values.get(i);
                if(values.get(i) > thresholdEigen){
                    sij += 1;
                }
                else{
                    sij -= 1;
                }
            }
           // System.out.println(NodeIdH.get(ep.Source));
           // System.out.println(NodeIdH.get(ep.Destination));

            CMatrix[NodeIdH.get(ep.Source)][NodeIdH.get(ep.Destination)] = sij;

             //System.out.println(CMatrix[1][0] );

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
                   CMatrix[i][j] = 1/Double.valueOf(trusted);
               }
              // System.out.println(CMatrix[i][j]);
            }
        }

        }

void processEigen() throws IOException {
    PMatrix = new double[CMatrix.length][CMatrix[0].length];
    for (int i = 0; i < PMatrix.length; i++) {
        for (int j = 0; j < PMatrix[i].length; j++) {
            PMatrix[i][j] = 1 / Double.valueOf(trusted);
        }

    }
    Matrix pMatrix = new Matrix(PMatrix);
    Matrix cMatrix = new Matrix(CMatrix);
    Matrix tk = pMatrix.copy();
    Matrix tkplus1 = null;
    Matrix cTrans = cMatrix.transpose();
    for (int i = 0; i < iterations; i++) {
        tkplus1 = cTrans.times(tk);
        tkplus1 = tkplus1.times(1 - a);
        tkplus1 = tkplus1.plus(pMatrix.times(a));
        //System.out.println(tkplus1.minus(tk).norm2());
        tk = tkplus1.copy();
        //  System.out.println(tkplus1.minus(tk).norm2());
    }

    tkFinal = tk.copy();
    double[][] valsTransposed = tkFinal.getArray();

    // now loop through the rows of valsTransposed to print


    File output = new File("/Users/bipashabanerjee/Documents/CS/sem2/DBMS/project/power/powerEigenOutput.txt");
    FileWriter fileWriter = new FileWriter(output);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    for (int i = 0; i < valsTransposed.length; i++) {
        for (int j = 0; j < valsTransposed[i].length; j++) {

            printWriter.print(valsTransposed[i][j]);
            printWriter.print(" ");

            System.out.println(valsTransposed[i][0]);
        }
        printWriter.println();


        printWriter.close();
    }
}

       // System.out.println(tkFinal);




    class  sortGainList implements Comparator<GainPair>{
        public int compare(GainPair a, GainPair b){
            return (int) (b.GainValue - a.GainValue);

        }

    }




}





