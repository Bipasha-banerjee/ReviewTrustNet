package edu.vt.NetInf;

public class HitInfo {
    public String NId,Parent;
    public Long unixTime;


    public HitInfo(String NId, Long unixTime) {
        this.NId = NId;
        Parent = null;
        this.unixTime = unixTime;

    }

    //Streaming in from the file TSIn ??

    // Streaming out Save?




}
