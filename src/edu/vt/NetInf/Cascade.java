package edu.vt.NetInf;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import edu.vt.NetInf.NetInf.EdgePair;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;


import static java.util.Map.Entry.comparingByValue;

public class Cascade {

    public Map<String, HitInfo> NIdHitH;
    public Double CurProb;
    public Double Alpha;
    public Integer Model;
    public Double Eps;

    public Cascade(){
        NIdHitH = new LinkedHashMap<>();
        CurProb = 0.0;
        Alpha = 1.0;
        Eps = 1e-64;
        Model = 0;
    }

    public Cascade(Double alpha) {
        Alpha = alpha;
        NIdHitH = new LinkedHashMap<>();
        CurProb = 0.0;
        Eps = 1e-64;
        Model = 0;
    }

    public Cascade(Double alpha, Integer model) {
        Alpha = alpha;
        Model = model;
        NIdHitH = new LinkedHashMap<>();
        CurProb = 0.0;
        Eps = 1e-64;
    }


    public Cascade(Double alpha,Double eps) {
        Alpha = alpha;
        NIdHitH = new LinkedHashMap<>();
        CurProb = 0.0;
        Eps = eps;
        Model = 0;

    }
    public Cascade(Double alpha,Double eps,Integer model) {
        Alpha = alpha;
        NIdHitH = new LinkedHashMap<>();
        CurProb = 0.0;
        Eps = eps;
        Model = model;

    }

    public void Clr(){
        NIdHitH.clear();
        CurProb = 0.0;
        Alpha = 1.0;
    }

    public int Len(){
        return NIdHitH.size();
    }

    public String getNode(int index){
       // System.out.println(index);

        Object[] keysSet = NIdHitH.keySet().toArray();
        return keysSet[index].toString();

    }

    public String getParent(String NId){
        return NIdHitH.get(NId).Parent;

    }

    public double getAlpha(){
        return Alpha;
    }

    public Long getUnixTime(String NId){
        return NIdHitH.get(NId).unixTime;
    }

    public void Add(String Nid, Long unixTime){
        NIdHitH.put(Nid,new HitInfo(Nid,unixTime));
    }

    public  void Del(String NId){
        NIdHitH.remove(NId);
    }

    public boolean IsNode(String Nid){
        return NIdHitH.containsKey(Nid);
    }



    void Sort(){

        List<String> mapKeys = new ArrayList<>(NIdHitH.keySet());
        List<HitInfo> mapValues = new ArrayList<>(NIdHitH.values());
        Collections.sort(mapValues,new compareNId());
        Collections.sort(mapKeys);

        LinkedHashMap<String, HitInfo> sortedMap =
                new LinkedHashMap<>();

        Iterator<HitInfo> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            HitInfo val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                HitInfo comp1 = NIdHitH.get(key);
                HitInfo comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        NIdHitH = sortedMap;
    }

    Double TransProb(String N1, String N2, double alpha, double alphaParam) {
        if (!IsNode(N1) || !IsNode(N2)) {
            return Eps;
        }
        if (getUnixTime(N1) >= getUnixTime(N2)) {
            return Eps;
        }
        if (Model == 0) {
           // System.out.println("time 1 = " + getUnixTime(N1));
           // System.out.println("time 2 = " +getUnixTime(N2));
            double value1 = pow(2.71,(-alpha * ((getUnixTime(N2) - getUnixTime(N1))/604800)));
           // System.out.println("Time differnece is "+(getUnixTime(N2) - getUnixTime(N1))/604800);
          //  System.out.println("alpha"+alpha);
           // System.out.println(-alpha * (getUnixTime(N2) - getUnixTime(N1)));
            double value = (alpha * exp(-alpha * ((getUnixTime(N2) - getUnixTime(N1))/604800)))*alphaParam;
           // System.out.println("Value1 :"+value1);
            return value;//exponential
        }
        else if(Model == 1){
            return ((alpha-1)+pow((getUnixTime(N2) - getUnixTime(N1)),-alpha))*alphaParam; //Power-law
        }
        else {
            return alpha * ((getUnixTime(N2) - getUnixTime(N1)) * exp(-0.5 * alpha * pow(getUnixTime(N2) - getUnixTime(N1), 2)))*alphaParam; // rayleigh
        }
    }

//Node iters for graph or not? this is without, check for correction.
/*double getProb(Graph G){
        double P =0;
        for(int n = 0; n < Len(); n++){
            String dstNId = getNode(n);
            double dstTime = getUnixTime(dstNId);
            Graph.Node node = G.getNode(dstNId);
            double MxProb = log(Eps);  //Initial max probability set to min
            String bestParent = null;
            for(int e = 0; e < node.getInDeg();e++){
                int EId = node.getInEId(e);
                Graph.Edge edge = G.getEdge(EId);
                String srcId = edge.getSrcNId();
                if(IsNode(srcId) && getUnixTime(srcId)< dstTime){
                    double Prob = Math.log(TransProb(srcId,dstNId,0.0)); //see this alpha value of get Prob
                    if(MxProb <Prob){
                        MxProb = Prob;
                        bestParent = srcId;
                    }
                }
            }
            NIdHitH.get(dstNId).Parent = bestParent;
            P += MxProb;
        }
        return P;
}*/

void initProb(){
        CurProb = Math.log(Eps) * Len();
        for(String s : NIdHitH.keySet()){
            NIdHitH.get(s).Parent = "null";
        }
}

double updateProb(String N1, String N2, boolean update, HashMap<NetInf.EdgePair,Double> Alphas, double alphaParam){
        if(!IsNode(N1) || !IsNode(N2)){
            return CurProb;
        }
        if(getUnixTime(N1).compareTo(getUnixTime(N2)) ==1){
            return CurProb;
        }

    double alpha=0.0;
    double alphaParent=0.0;
    for (Map.Entry<EdgePair, Double> entry : Alphas.entrySet()) {
        if(N1.equals(entry.getKey().Source) && (N2.equals(entry.getKey().Destination)))
        {
            //EdgePair ep1 = new EdgePair(Source,Destination);

            alpha = entry.getValue();
            break;
        }
        //   System.out.println(entry.getKey().Source + ":"+ entry.getKey().Destination+" : "+entry.getValue());
    }

   /* if(Alphas.containsKey(new EdgePair(N1,N2))) {

       alpha = Alphas.get(new NetInf.EdgePair(N1, N2));

    }*/

    for (Map.Entry<EdgePair, Double> entry : Alphas.entrySet()) {
     //  System.out.println(N2+" "+getParent(N2));
        if(getParent(N2).equals(entry.getKey().Source) && (N2.equals(entry.getKey().Destination)))
        {
            //EdgePair ep1 = new EdgePair(Source,Destination);

            alphaParent = entry.getValue();
            break;
        }
    }


        double P1 = log(TransProb(getParent(N2),N2,alphaParent,alphaParam));
        double P2 = log(TransProb(N1,N2,alpha,alphaParam));
        if(P1<P2){
            if(update){
                CurProb = CurProb - P1 + P2;
                NIdHitH.get(N2).Parent = N1;
            }
            else{
                return CurProb - P1 + P2;
            }
        }
        return CurProb;
}


}


    //getNode NodeId search via same NodeId?



    //Streaming in Constructor TSIn?
    //Stream out TSOut Save




class compareNId implements Comparator<HitInfo>{

    public int compare(HitInfo a, HitInfo b){
        return (a.unixTime.compareTo(b.unixTime));
    }



}