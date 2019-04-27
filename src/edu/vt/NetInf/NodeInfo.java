package edu.vt.NetInf;

public class NodeInfo {
    private String NId;
    private Integer CascadeNumber;

    public NodeInfo()
    { }
    public NodeInfo(String NodeName, Integer Volume){
        NId = NodeName;
        CascadeNumber = Volume;
    }


    public String getNId() {
        return NId;
    }

    public void setNId(String NId) {
        this.NId = NId;
    }

    public Integer getCascadeNumber() {
        return CascadeNumber;
    }

    public void setCascadeNumber(Integer cascadeNumber) {
        CascadeNumber = cascadeNumber;
    }

    // Stream In and Stream Out have been left out.
}
