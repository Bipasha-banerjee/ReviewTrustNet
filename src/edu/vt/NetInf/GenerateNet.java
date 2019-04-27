package edu.vt.NetInf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class GenerateNet {
    public static void main(String[] args) throws FileNotFoundException {
        NetInf netInf = new NetInf();
        netInf.loadGroundTruth("<path>",0,1);
        HashMap<NetInf.EdgePair,Integer> EdgesUsed = new HashMap<>();
        int last = 0;
        int NCascades = 100;

        for(int i = 0; (i<NCascades) || ((double)EdgesUsed.size() < - (double)NCascades/100.0 * (double)netInf.groundTruth.getEdges()); i++){
            Cascade C = new Cascade();
             C = netInf.genCascade(C,EdgesUsed);
             if(C!=null){

                 netInf.AddCasc(C);
             }

             }


        }





    }

