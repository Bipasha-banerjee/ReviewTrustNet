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





    }
}
