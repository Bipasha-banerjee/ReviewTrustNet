package edu.vt.NetInf;

import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.*;

public class countFiles {
    public static void main(String args[]) throws IOException {

        System.out.println("Inside fetchIntoList");
        File inputFile = new File("/Users/bipashabanerjee/IdeaProjects/wordCount/src/Cell_Phones_and_Accessories_5.json");
        // BufferedReader in
        //       = new BufferedReader(new FileReader("/Users/bipashabanerjee/IdeaProjects/wordCount/src/data_3.json"));
        Scanner s = new Scanner(inputFile);
        ArrayList<JSONObject> contentsAsJsonObjects = new ArrayList<JSONObject>();
        Map<String,Integer> myMap = new HashMap<>();
        int count =0;
        System.out.println("after content as json");
        while (s.hasNextLine())
        {
            count ++;

            String str = s.nextLine();


            contentsAsJsonObjects.add(new JSONObject(str));


        }
        for (JSONObject jobj : contentsAsJsonObjects)
        {
            // System.out.println(jobj);
            String reviewerID = jobj.getString("reviewerID");
            //  System.out.println(reviewerID);
            // String productID = jobj.getString("asin");
            if(!myMap.containsKey(reviewerID))
            {
                myMap.put(reviewerID,0);
            }
            myMap.put(reviewerID, myMap.get(reviewerID)+1);

        }
        System.out.println("count"+count);

      /*  String outputPath = "/Users/bipashabanerjee/IdeaProjects/wordCount/src/";

        FileWriter fileWriter = new FileWriter(outputPath + "outPro.txt");
        PrintWriter pw = new PrintWriter(fileWriter);
        Iterator iter1 = myMap.entrySet().iterator();
        while(iter1.hasNext())
        {
            Map.Entry myPair = (Map.Entry) iter1.next();
            String myString = (String) myPair.getKey();
            Integer c = (Integer) myPair.getValue();
            pw.println(myString+" "+ c);
            System.out.println(myString+" "+c);
        }*/

        for (Map.Entry<String, Integer> entry : myMap.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        System.out.println(myMap.size());
    }
}
