package edu.vt.NetInf;

public class HitInfo {
    public String NId,Parent;
    public Long unixTime;

    public HitInfo(String NId, String parent, Long unixTime) {
        this.NId = NId;
        Parent = parent;
        this.unixTime = unixTime;
    }

    //Streaming in from the file TSIn ??

    // Streaming out Save?




}
