package cn.moecity.myapplication;

import java.util.List;

public class Node {
    private NodeDao nodeDao=new NodeDao();
    private int nodeNo;
    private int preCount;
    private List<Integer> preNode;
    private Boolean hasNext;
    private List<Integer> nextNode;
    private Boolean isVisible;
    private Boolean isLocked;
    private MyLocation myLocation;
    private int locID;
    private int distanceToCurrent;
    private String locName;

    public Node() {
    }

    public Node(int nodeNo, int preCount, List<Integer> preNode, Boolean hasNext, List<Integer> nextNode, Boolean isVisible, Boolean isLocked, MyLocation myLocation) {
        this.nodeNo = nodeNo;
        this.preCount = preCount;
        this.preNode = preNode;
        this.hasNext = hasNext;
        this.nextNode = nextNode;
        this.isVisible = isVisible;
        this.isLocked = isLocked;
        this.myLocation = myLocation;
        this.locID = myLocation.getLocNo();
        this.locName = myLocation.getLocName();
    }

    public int getDistanceToCurrent() {
        return distanceToCurrent;
    }

    public void setDistanceToCurrent(int distanceToCurrent) {
        this.distanceToCurrent = distanceToCurrent;
    }

    public int getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(int nodeNo) {
        this.nodeNo = nodeNo;
    }

    public int getPreCount() {
        return preCount;
    }

    public void setPreCount(int preCount) {
        this.preCount = preCount;
    }

    public List<Integer> getPreNode() {
        return preNode;
    }

    public void setPreNode(List<Integer> preNode) {
        this.preNode = preNode;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<Integer> getNextNode() {
        return nextNode;
    }

    public void setNextNode(List<Integer> nextNode) {
        this.nextNode = nextNode;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public MyLocation getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(MyLocation myLocation) {
        this.myLocation = myLocation;
    }

    public int getLocID() {
        return locID;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocID(MyLocation myLocation) {
        this.locID = myLocation.getLocNo();
    }

    public void setLocName(MyLocation myLocation) {
        this.locName = myLocation.getLocName();
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeDao=" + nodeDao +
                ", nodeNo=" + nodeNo +
                ", preCount=" + preCount +
                ", preNode=" + nodeDao.IntToString(preNode) +
                ", hasNext=" + hasNext +
                ", nextNode=" + nodeDao.IntToString(nextNode) +
                ", isVisible=" + isVisible +
                ", isLocked=" + isLocked +
                ", myLocation=" + myLocation +
                '}';
    }
}