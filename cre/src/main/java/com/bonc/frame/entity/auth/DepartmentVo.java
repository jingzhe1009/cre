package com.bonc.frame.entity.auth;

import java.util.LinkedList;
import java.util.List;


public class DepartmentVo {

    String id;
    String departName;
    String pid;
    // 是否为一级部门
    boolean isRoot = true;
    List<DepartmentVo> children;

    public DepartmentVo(String id, String pid, String departName) {
        this.id = id;
        this.pid = pid;
        this.departName = departName;
        children = new LinkedList<>();
    }

    public void addChild(DepartmentVo child) {
        this.children.add(child);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    // 如果传入的对象是该对象的孩子返回true
    public boolean isChild(DepartmentVo child) {
        if (this.id == null || child.pid == null) {
            return false;
        }
        if (this.id .equals(child.pid)) {
            return true;
        }
        return false;
    }

    public boolean isParent(DepartmentVo parent) {
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

    public String getDepartName() {
        return departName;
    }

    public String getPid() {
        return pid;
    }

    public List<DepartmentVo> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "DepartmentVo{" +
                "id='" + id + '\'' +
                ", departName='" + departName + '\'' +
                ", pid='" + pid + '\'' +
                ", isRoot=" + isRoot +
                ", children=" + children +
                '}';
    }

    // 自顶向下构建树
	public static List<DepartmentVo> departmentTreeInfo(List<DepartmentVo> list) {
        List<DepartmentVo> queue = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            final DepartmentVo departmentVo = list.get(i);
            for (DepartmentVo pre : queue) {
                if (pre.isChild(departmentVo)) {
                    pre.addChild(departmentVo);
                    departmentVo.setRoot(false);
                }
                if (pre.isParent(departmentVo)) {
                    departmentVo.addChild(pre);
                    pre.setRoot(false);
                }
            }
            queue.add(departmentVo);
        }
        final List<DepartmentVo> departments = new LinkedList<>();
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isRoot()) {
                departments.add(queue.get(i));
            }
        }

        return departments;
    }

}
