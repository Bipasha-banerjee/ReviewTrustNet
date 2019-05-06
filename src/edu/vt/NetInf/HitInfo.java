package edu.vt.NetInf;

import java.math.BigInteger;

public class HitInfo {
    public String NId,Parent;
    public Long unixTime;


    public HitInfo(String NId, Long unixTime) {

        this.NId = NId;
        this.Parent = "null";
        this.unixTime = unixTime;
        //System.out.println(Parent.equals("null"));

    }

    //Streaming in from the file TSIn ??

    // Streaming out Save?




}
