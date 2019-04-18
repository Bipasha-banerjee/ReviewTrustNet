package edu.vt.NetInf;

public class HitInfo {
    public String NId,Parent;
    public Long unixTime;
    public Integer usefullness;

    public HitInfo(String NId, Long unixTime,int usefullness) {
        this.NId = NId;
        Parent = null;
        this.unixTime = unixTime;
        this.usefullness = usefullness;
    }

    //Streaming in from the file TSIn ??

    // Streaming out Save?




}
