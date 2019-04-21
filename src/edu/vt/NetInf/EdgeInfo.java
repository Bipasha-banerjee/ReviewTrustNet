package edu.vt.NetInf;

public class EdgeInfo {

    private Integer CascadeNumber;
    private Double MarginalGain, MarginalBound, MedianTimeDiff, AverageTimeDiff;

    public EdgeInfo()
    {}
    public EdgeInfo(Integer cascadeNumber, Double marginalGain, Double marginalBound, Double medianTimeDiff, Double averageTimeDiff) {
        CascadeNumber = cascadeNumber;
        MarginalGain = marginalGain;
        MarginalBound = marginalBound;
        MedianTimeDiff = medianTimeDiff;
        AverageTimeDiff = averageTimeDiff;
    }

    public EdgeInfo(Integer cascadeNumber, Double marginalGain, Double medianTimeDiff, Double averageTimeDiff) {
        CascadeNumber = cascadeNumber;
        MarginalGain = marginalGain;
        MedianTimeDiff = medianTimeDiff;
        AverageTimeDiff = averageTimeDiff;
    }
    //Stream In and Stream Out have been left out.
}
