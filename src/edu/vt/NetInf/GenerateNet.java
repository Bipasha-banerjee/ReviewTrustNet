package edu.vt.NetInf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class GenerateNet {
    public static void main(String[] args) throws IOException {
        NetInf netInf = new NetInf();

        // Either comment below or comment after the for loop for either creating the graph/computing eigen.

      /*  String path = "/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/musicOutputW0/";
        for (int j = 675; j < 676; j++) {
            netInf.clear();
            netInf.loadGroundTruth(path+"GroundTruth"+j+".csv", 0, 1);
            HashMap<NetInf.EdgePair, Integer> EdgesUsed = new HashMap<>();
            int last = 0;
            int NCascades = 10;

            for (int i = 0; (i < NCascades) || ((double) EdgesUsed.size() < -(double) NCascades / 100.0 * (double) netInf.groundTruth.getEdges()); i++) {
                Cascade C = new Cascade();
                C = netInf.genCascade(C, EdgesUsed);
                if (C != null) {

                    netInf.AddCasc(C);
                }
            }
            netInf.init();
            netInf.GreedyOpt();
            netInf.saveGraphText();
            System.out.println("File" + j);


        }*/
        netInf.AddtoOutputGraph();
        netInf.setupCmatrix();
        netInf.processEigen();
        netInf.predictUsefulness();
    }





    }

