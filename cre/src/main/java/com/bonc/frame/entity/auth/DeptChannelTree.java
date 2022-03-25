package com.bonc.frame.entity.auth;

import java.util.LinkedList;
import java.util.List;

public class DeptChannelTree {
    String id;
    String name;
    String pid;
    // 是否为一级部门
    boolean isRoot = true;
    List<DeptChannelTree> children;

    public DeptChannelTree() {

    }

    public DeptChannelTree(String id, String pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        children = new LinkedList<>();
    }

    public void addChild(DeptChannelTree child) {
        this.children.add(child);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    // 如果传入的对象是该对象的孩子返回true
    public boolean isChild(DeptChannelTree child) {
        if (this.id == null || child.pid == null) {
            return false;
        }
        if (this.id .equals(child.pid)) {
            return true;
        }
        return false;
    }

    public boolean isParent(DeptChannelTree parent) {
        if (this.pid == null || parent.id == null) {
            return false;
        }
        if (this.pid .equals(parent.id)) {
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPid() {
        return pid;
    }

    public List<DeptChannelTree> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "DeptChannelTree{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pid='" + pid + '\'' +
                ", isRoot=" + isRoot +
                ", children=" + children +
                '}';
    }

    // 自顶向下构建树
    public static List<DeptChannelTree> listToTree(List<DeptChannelTree> list) {
        List<DeptChannelTree> queue = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            final DeptChannelTree tree = list.get(i);
            for (DeptChannelTree pre : queue) {
                if (pre.isChild(tree)) {
                    pre.addChild(tree);
                    tree.setRoot(false);
                }
                if (pre.isParent(tree)) {
                    tree.addChild(pre);
                    pre.setRoot(false);
                }
            }
            queue.add(tree);
        }
        final List<DeptChannelTree> treeList = new LinkedList<>();
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isRoot()) {
                treeList.add(queue.get(i));
            }
        }

        return treeList;
    }
}
