package com.bonc.frame.entity.auth;

import java.util.LinkedList;
import java.util.List;

/**
 * 渠道树构建
 *
 * */
public class PlaceVo {
    //渠道id
    String id;
    //渠道名
    String placeName;
    //上级渠道id
    String pid;
    // 是否为根渠道
    boolean isRoot = true;
    List<PlaceVo> children;

    public PlaceVo(String id, String pid, String placeName) {
        this.id = id;
        this.pid = pid;
        this.placeName = placeName;
        children = new LinkedList<>();
    }

    public void addChild(PlaceVo child) {
        this.children.add(child);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    //如果传入的对象是该对象的子渠道返回true
    public boolean isChild(PlaceVo child) {
        //判断当前渠道id和传入渠道的上级渠道id是否都为空
        if (this.id == null || child.pid == null) {
            return false;
        }
        //判断当前对象的渠道id和传入渠道的上级渠道的id是否相同
        if (this.id.equals(child.pid)) {
            return true;
        }
        return false;
    }
    //如果传入的对象是该对象的上级渠道返回true
    public boolean isParent(PlaceVo parent) {
        //判断当前对象的上级渠道id和传入的上级渠道id是否为空
        if (this.pid == null || parent.id == null) {
            return false;
        }
        //判断当前对象的一级渠道id和传入的一级渠道是否相同
        if (this.pid .equals(parent.id)) {
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPid() {
        return pid;
    }

    public List<PlaceVo> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "PlaceVo{" +
                "id='" + id + '\'' +
                ", placeName='" + placeName + '\'' +
                ", pid='" + pid + '\'' +
                ", isRoot=" + isRoot +
                ", children=" + children +
                '}';
    }

    // 自顶向下构建渠道树
    public static List<PlaceVo> PlaceTreeInfo(List<PlaceVo> list) {
        List<PlaceVo> queue = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            final PlaceVo placeVo = list.get(i);
            for (PlaceVo pre : queue) {
                if (pre.isChild(placeVo)) {
                    pre.addChild(placeVo);
                    placeVo.setRoot(false);
                }
                if (pre.isParent(placeVo)) {
                    placeVo.addChild(pre);
                    pre.setRoot(false);
                }
            }
            queue.add(placeVo);
        }
        final List<PlaceVo> placeVos = new LinkedList<>();
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isRoot()) {
                placeVos.add(queue.get(i));
            }
        }

        return placeVos;
    }
}
