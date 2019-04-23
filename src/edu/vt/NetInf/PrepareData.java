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
        long timeDifference;
        String productID;

        public tuples(String reviewerID1, String reviewerID2, double usefulness, long timeDifference,String productID) {
            this.reviewerID1 = reviewerID1;
            this.reviewerID2 = reviewerID2;
            this.usefulness = usefulness;
            this.timeDifference = timeDifference;
            this.productID = productID;
        }

        @Override
        public String toString() {
            return reviewerID1 + "," +  reviewerID2 + "," + usefulness + "," + timeDifference + "," + productID;

        }
    }



    public static void main(String[] args) throws IOException {


        fetchIntoList();
      //  processObjects();
       // writeToFile();


    }



    static void fetchIntoList() throws IOException {
        System.out.println("Inside fetchIntoList");
        BufferedReader in
                = new BufferedReader(new FileReader("/Users/bipashabanerjee/IdeaProjects/ReviewTrustNet/data_1.json"));

        ArrayList<JSONObject> contentsAsJsonObjects = new ArrayList<JSONObject>();
        System.out.println("after content as json");
        while(true)
        {

            String str = in.readLine();
            if(str==null)break;
            contentsAsJsonObjects.add(new JSONObject(str));
        }


       for (JSONObject jobj : contentsAsJsonObjects)
       {

           if (jobj.getString("reviewerID").equals("anonymous")|| jobj.getString("reviewerID").equals("Amazon Customer")){
               continue;
           }
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
                div = (Double.valueOf(i))/(Double.valueOf(j));
           }
           else
               div = 0.01;

           dataList.add(new dataObject(reviewerID,productID,div,rating,unixTime));





       }
       System.out.println(dataList.get(0));
       System.out.println(dataList.get(dataList.size()-1));


    }

     void processObjects(){
        for(int i = 0; i < dataList.size();i++){
               dataObject obj1 = dataList.get(i);
               String productId = obj1.productID;
            for(int j = 0; j<dataList.size();j++){

                if(i!=j){
                    dataObject obj2 = dataList.get(j);
                    if(!obj1.reviewerID.equals(obj2.reviewerID)){
                        if(obj1.productID.equals(obj2.productID)){
                            if(obj1.UnixreviewTime < obj2.UnixreviewTime){
                                tupleList.add(new tuples(obj1.reviewerID,obj2.reviewerID,obj1.usefullness,obj2.UnixreviewTime - obj1.UnixreviewTime,obj1.productID));
                            }
                        }

                    }
                }

            }

        }
    }

    void writeToFile() throws IOException {
        FileWriter fileWriter = new FileWriter("<filename>+1");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        tupleList.sort(new comparetuple());
        int j =0;
        String prevID = tupleList.get(0).productID;
        for(int i =0; i < tupleList.size(); i++){

            if(tupleList.get(i).productID.equals(prevID)){
                printWriter.println(tupleList.get(i).toString());
                j++;
            }



        }
        System.out.println(j);
    }
    class comparetuple implements Comparator<tuples>{

        public int compare(tuples a, tuples b){
            return  a.productID.compareTo(b.productID);
        }



    }
}
