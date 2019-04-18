package edu.vt.NetInf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cascade {

    public Map<String, HitInfo> NIdHitH;
    public Double CurProb;
    public Double Alpha;
    public Integer Model;
    public Double Eps;

    public Cascade(){
        NIdHitH = new HashMap<>();
        CurProb = 0.0;
        Alpha = 1.0;
        Eps = 1e-64;
        Model = 0;
    }

    public Cascade(Double alpha) {
        Alpha = alpha;
        NIdHitH = new HashMap<>();
        CurProb = 0.0;
        Eps = 1e-64;
        Model = 0;
    }

    public Cascade(Double alpha, Integer model) {
        Alpha = alpha;
        Model = model;
        NIdHitH = new HashMap<>();
        CurProb = 0.0;
        Eps = 1e-64;
    }


    public Cascade(Double alpha,Double eps) {
        Alpha = alpha;
        NIdHitH = new HashMap<>();
        CurProb = 0.0;
        Eps = eps;
        Model = 0;

    }
    public Cascade(Double alpha,Double eps,Integer model) {
        Alpha = alpha;
        NIdHitH = new HashMap<>();
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

    public String GetParent(int NId){
        return NIdHitH.get(NId).Parent;

    }



    //getNode NodeId search via same NodeId?



    //Streaming in Constructor TSIn?
    //Stream out TSOut Save


}