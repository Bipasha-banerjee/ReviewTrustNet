package edu.vt.NetInf;

import java.util.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public String GetParent(String NId){
        return NIdHitH.get(NId).Parent;

    }

    public double getAlpha(){
        return Alpha;
    }

    public long getUnixTime(String NId){
        return NIdHitH.get(NId).unixTime;
    }

    public void Add(String Nid, Long unixTime, int usefullness){
        NIdHitH.put(Nid,new HitInfo(Nid,unixTime, usefullness));
    }

    public  void Del(String NId){
        NIdHitH.remove(NId);
    }

    public boolean IsNode(String Nid){
        return NIdHitH.containsKey(Nid);
    }



    void Sort(boolean order){

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









    //getNode NodeId search via same NodeId?



    //Streaming in Constructor TSIn?
    //Stream out TSOut Save


}

class compareNId implements Comparator<HitInfo>{

    public int compare(HitInfo a, HitInfo b){
        return (int) (a.unixTime - b.unixTime);
    }



}