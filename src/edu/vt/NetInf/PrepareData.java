package edu.vt.NetInf;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PrepareData {
     List<dataObject> dataList = new ArrayList<>();
     List<tuples> tupleList = new ArrayList<>();
    class dataObject{

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

    }
    class tuples{
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



    public  void main(String[] args) throws IOException {


        fetchIntoList();
        processObjects();
        writeToFile();


    }



    void fetchIntoList(){



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
