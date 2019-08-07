package cn.moecity.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeDao {
    private MyLocationDao myLocationDao=new MyLocationDao();
    public List<Node> CreateNodes(){
        List<Node> nodeList=new ArrayList<>();
        List<Integer> preNodes=new ArrayList<>();
        List<Integer> nextNodes=new ArrayList<>();
        preNodes.add(0);
        nextNodes.add(2);
        nodeList.add(new Node(1,0,preNodes,true,nextNodes,true,true,myLocationDao.getLocationByLocId(1)));
        Log.e("location",myLocationDao.getLocationByLocId(1).getLocName());
        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(1);
        nextNodes.add(3);
        nodeList.add(new Node(2,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(2)));
        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(2);
        nextNodes.add(4);nextNodes.add(5);nextNodes.add(6);
        nodeList.add(new Node(3,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(3)));

        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(3);
        nextNodes.add(7);
        nodeList.add(new Node(4,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(4)));
        nodeList.add(new Node(5,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(5)));
        nodeList.add(new Node(6,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(6)));

        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(4);preNodes.add(5);preNodes.add(6);
        nextNodes.add(8);nextNodes.add(9);nextNodes.add(10);
        nodeList.add(new Node(7,3,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(7)));

        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(7);nextNodes.add(11);
        nodeList.add(new Node(8,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(8)));
        nodeList.add(new Node(9,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(9)));
        nodeList.add(new Node(10,1,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(5)));

        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(8);preNodes.add(9);preNodes.add(10);
        nextNodes.add(12);
        nodeList.add(new Node(11,2,preNodes,true,nextNodes,false,true,myLocationDao.getLocationByLocId(10)));

        preNodes=new ArrayList<>();
        nextNodes=new ArrayList<>();
        preNodes.add(11);
        nextNodes.add(0);
        nodeList.add(new Node(12,1,preNodes,false,nextNodes,false,true,myLocationDao.getLocationByLocId(1)));
        return nodeList;
    }

    public List<Node> UnlockNode(List<Node> nodeList,int unLockId){
        List<Node> newList=new ArrayList<>();

        for (Node node : nodeList) {

            if (node.getNodeNo() == unLockId) {
                node.setLocked(false);
                node.setVisible(false);
            }
            newList.add(node);
        }
        return checkVisible(newList);
    }
    public List<Node> checkVisible(List<Node> nodeList){

        Set<Integer> unLockedListNo=new HashSet<>();
        Set<Integer> lockedList=new HashSet<>();
        Set<Integer> nearNext=new HashSet<>();
        List<Node> newList=new ArrayList<>();
        Set<Integer> visibleList=new HashSet<>();
        Set<Integer> newVisibleList=new HashSet<>();
        for (Node node : nodeList) {
            if (node.getVisible() == false && node.getLocked() == false) {
                unLockedListNo.add(node.getNodeNo());
                nearNext.addAll(node.getNextNode());
            } else if (node.getVisible() == false && node.getLocked() == true) {
                lockedList.add(node.getNodeNo());
            } else if (node.getVisible() == true && node.getLocked() == false) {
                visibleList.add(node.getNodeNo());
            }
        }


        nearNext.removeAll(unLockedListNo);
        nearNext.removeAll(visibleList);
        List<Integer> nearNextList=new ArrayList<Integer>(nearNext);

        for (int nearNextNo : nearNextList) {
            for (Node temp : nodeList) {
                if (temp.getNodeNo() == nearNextNo) {
                    int tempCount=0;
                    for (int nodeNo : temp.getPreNode()) {
                        if (unLockedListNo.contains(nodeNo)) {
                            tempCount++;
                            //Log.e("count",tempCount+"");
                        }
                    }
                    if (tempCount==temp.getPreCount()){
                        lockedList.remove(nearNextNo);
                        unLockedListNo.addAll(temp.getPreNode());
                        newVisibleList.add(temp.getNodeNo());
                        newVisibleList.removeAll(temp.getPreNode());
                    }
                }
            }
        }
//        Log.e("next",IntToString(new ArrayList<Integer>(nearNext)));
//        Log.e("lockedList",IntToString(new ArrayList<Integer>(lockedList)));
//        Log.e("unLockedListNo",IntToString(new ArrayList<Integer>(unLockedListNo)));
//        Log.e("newVisibleList",IntToString(new ArrayList<Integer>(newVisibleList)));
        for (Node node : nodeList) {

            if (lockedList.contains(node.getNodeNo())) {
                node.setLocked(true);
                node.setVisible(false);
            } else if (unLockedListNo.contains(node.getNodeNo())) {
                node.setLocked(false);
                node.setVisible(false);
            } else if (newVisibleList.contains(node.getNodeNo())) {
                node.setLocked(true);
                node.setVisible(true);

            }
            newList.add(node);
        }


        return newList;
    }

    public String IntToString(List<Integer> integerList){
        String out="[";
        for (int i : integerList) {
            out += i + ",";
        }
        if (out.length()>1)
        out=out.substring(0,out.length()-1);
        out+="]";
        return out;
    }

    public JSONArray SaveToJSON(List<Node> nodeList){
        JSONArray jsonArray=new JSONArray();
        for (Node node : nodeList) {
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("nodeNo", node.getNodeNo());
                jsonObject.put("preCount", node.getPreCount());
                jsonObject.put("preNode", IntToString(node.getPreNode()));
                jsonObject.put("hasNext", node.getHasNext());
                jsonObject.put("nextNode", IntToString(node.getNextNode()));
                jsonObject.put("isVisible", node.getVisible());
                jsonObject.put("isLocked", node.getLocked());
                jsonObject.put("locID", node.getLocID());
                jsonObject.put("locName", node.getLocName());
                jsonObject.put("distanceToCurrent", node.getDistanceToCurrent() + "m");
                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
}
