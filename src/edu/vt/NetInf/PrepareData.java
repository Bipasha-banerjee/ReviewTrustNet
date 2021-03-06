package edu.vt.NetInf;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
//import org.json.parser.JSONParser;
//import org.json.parser.ParseException;

public class PrepareData {
     static List<dataObject>  dataList = new ArrayList<>();
     static List<tuples> tupleList = new ArrayList<>();
    static class dataObject{

        String reviewerID;
        String productID;
        double usefullness;
        double rating;
        long UnixreviewTime;

        public dataObject(String reviewerID, String productID, double usefullness, double rating, long unixreviewTime) {
            this.reviewerID = reviewerID;
            this.productID = productID;
            this.usefullness = usefullness;
            this.rating = rating;
            UnixreviewTime = unixreviewTime;

        }

        @Override
        public String toString() {
            return "dataObject{" +
                    "reviewerID='" + reviewerID + '\'' +
                    ", productID='" + productID + '\'' +
                    ", usefullness=" + usefullness +
                    ", rating=" + rating +
                    ", UnixreviewTime=" + UnixreviewTime +
                    '}';
        }
    }
   static  class tuples{
        String reviewerID1;
        String reviewerID2;
        double usefulness;
        long UnixTime1;
       long UnixTime2;
        String productID;

        public tuples(String reviewerID1, String reviewerID2, double usefulness, long UnixTime1,long UnixTime2, String productID) {
            this.reviewerID1 = reviewerID1;
            this.reviewerID2 = reviewerID2;
            this.usefulness = usefulness;
            this.UnixTime1 = UnixTime1;
            this.UnixTime2 = UnixTime2;
            this.productID = productID;
        }

        @Override
        public String toString() {
            return reviewerID1 + "," +  reviewerID2 + "," + usefulness + "," + UnixTime1 +"," + UnixTime2 + "," + productID;

        }
    }



    public static void main(String[] args) throws IOException {


        fetchIntoList();
        processObjects();
        List<List<tuples>> list = splitTuple();
        //System.out.println(list.get(0));
        //System.out.println(list.get(1).get(0));
        //System.out.println(list.get(2).get(0));

        for(int i=0; i< list.size();i++){


            writeToFile(i,list.get(i));


        }


       // writeToFile();


    }



    static void fetchIntoList() throws IOException {
    //    System.out.println("Inside fetchIntoList");
        BufferedReader in
                = new BufferedReader(new FileReader("/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/Musical_Instruments_5.json"));

        ArrayList<JSONObject> contentsAsJsonObjects = new ArrayList<JSONObject>();
     //   System.out.println("after content as json");
        while(true)
        {

            String str = in.readLine();
          //  System.out.println(str);
            if(str==null)break;
            contentsAsJsonObjects.add(new JSONObject(str));
        }


       for (JSONObject jobj : contentsAsJsonObjects)
       {


           String reviewerID = jobj.getString("reviewerID");
           String productID = jobj.getString("asin");
           JSONArray jarray = jobj.getJSONArray("helpful");
           double rating = jobj.getDouble("overall");
           long unixTime= jobj.getLong("unixReviewTime");

           Integer i = jarray.getInt(0);
           Integer j = jarray.getInt(1);
           double div =0;
           if(i!=0 && j!=0)
           {
               if(i==1 && j==1){
                   div = 0.7;
               }
               else{
                   div = (Double.valueOf(i))/(Double.valueOf(j));
               }

           }

           else
               continue;
               //div = 0.01;

           dataList.add(new dataObject(reviewerID,productID,div,rating,unixTime));





       }



    }

    static void processObjects(){
        for(int i = 0; i < dataList.size();i++){
               dataObject obj1 = dataList.get(i);
               String productId = obj1.productID;
              // System.out.println("product id "+productId);
            for(int j = 0; j<dataList.size();j++){

                if(i!=j){
                    dataObject obj2 = dataList.get(j);
                    if(!obj1.reviewerID.equals(obj2.reviewerID)){
                        if(obj1.productID.equals(obj2.productID)){
                            if(obj1.UnixreviewTime < obj2.UnixreviewTime){
                                tupleList.add(new tuples(obj1.reviewerID,obj2.reviewerID,obj1.usefullness,obj1.UnixreviewTime,  obj2.UnixreviewTime,obj1.productID));
                            }
                        }

                    }
                }

            }

        }
    }

    static List<List<tuples>> splitTuple(){
        List<List<tuples>> finalTuple = new ArrayList<>();

        tupleList.sort(new comparetuple());

        String productId = tupleList.get(0).productID;
        int i = 0;
        finalTuple.add(new ArrayList<tuples>());
        for(tuples t : tupleList){
          //  System.out.println(t);
            if(t.productID.equals(productId))
            {

                finalTuple.get(i).add(t);
            }
            else{
                productId = t.productID;
                i++;
                finalTuple.add(new ArrayList<tuples>());
                finalTuple.get(i).add(t);
            }
        }
       // System.out.println(finalTuple.get(1));
        return finalTuple;

    }

    static void writeToFile(int k, List<tuples> lst ) throws IOException {
       // System.out.println(lst);
        String path = "/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/musicOutputW0/";

        FileWriter fileWriter = new FileWriter(path+"GroundTruth"+k+".csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);


        //String prevID = tupleList.get(0).productID;
        for(int i =0; i < lst.size(); i++){


                printWriter.println(lst.get(i).toString());

               // System.out.println(lst.get(0));






        }
        printWriter.close();

    }

    static public void writeToFile() throws IOException {
        String path = "/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/outputFiles/";

        FileWriter fileWriter = new FileWriter(path+"GroundTruth.csv");
        PrintWriter printWriter = new PrintWriter(fileWriter);


        //String prevID = tupleList.get(0).productID;
        for(int i =0; i < tupleList.size(); i++){


            printWriter.println(tupleList.get(i).toString());

            // System.out.println(lst.get(0));
        }
        printWriter.close();

    }
    static class comparetuple implements Comparator<tuples>{

        public int compare(tuples a, tuples b){
            return  a.productID.compareTo(b.productID);
        }



    }
}
