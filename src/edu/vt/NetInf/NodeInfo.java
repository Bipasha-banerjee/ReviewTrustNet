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

    // Stream In and Stream Out have been left out.
}
