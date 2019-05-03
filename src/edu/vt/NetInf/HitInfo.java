package edu.vt.NetInf;

import java.math.BigInteger;

public class HitInfo {
    public String NId,Parent;
    public BigInteger unixTime;


    public HitInfo(String NId, BigInteger unixTime) {
        this.NId = NId;
        Parent = null;
        this.unixTime = unixTime;

    }

    //Streaming in from the file TSIn ??

    // Streaming out Save?




}
