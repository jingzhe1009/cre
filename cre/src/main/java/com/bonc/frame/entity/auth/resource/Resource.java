package com.bonc.frame.entity.auth.resource;

import com.google.common.base.Objects;

import java.util.LinkedList;
import java.util.List;

/**
 * 资源表
 * 菜单、按钮资源
 *
 * @author yedunyao
 * @date 2019/8/13 16:36
 */
public class Resource extends DataResource implements Cloneable {

//    private String resourceId;

    private String resourceName;

    private String resourceTypeId;

    private String resourceUrl;

    private String resourcePid;

    // 用于前端权限页面页面回显 如果为true就显示 方框
    private boolean display = false;

    // 用于前端权限页面页面回显 如果为true就勾选 对勾
    private boolean choosed = false;

    private boolean root = true;

//    private List<Resource> resources;

    public boolean isDisplay() {
        return display;
    }
    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public void addChild(Resource child) {
        if (resources == null) {
            resources = new LinkedList<>();
        }
        this.resources.add(child);
    }

    // 如果传入的对象是该对象的孩子返回true
    public boolean isChild(Resource child) {
        if (this.resourceId == null || child.resourcePid == null) {
            return false;
        }
        if (this.resourceId.equals(child.resourcePid)) {
            return true;
        }
        return false;
    }

    public boolean isParent(Resource parent) {
        if (this.resourcePid == null || parent.resourceId == null) {
            return false;
        }
        if (this.resourcePid.equals(parent.resourceId)) {
            return true;
        }
        return false;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getResourcePid() {
        return resourcePid;
    }

    public void setResourcePid(String resourcePid) {
        this.resourcePid = resourcePid;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    // 自顶向下构建树
    public static List<Resource> parse2Tree(List<Resource> list) {
        List<Resource> queue = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            final Resource obj = list.get(i);
            for (Resource pre : queue) {
                if (pre.isChild(obj)) {
                    pre.addChild(obj);
                    obj.setRoot(false);
                }
                if (pre.isParent(obj)) {
                    obj.addChild(pre);
                    pre.setRoot(false);
                }
            }
            queue.add(obj);
        }
        final List<Resource> heads = new LinkedList<>();
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isRoot()) {
                heads.add(queue.get(i));
            }
        }

        return heads;
    }

    @Override
    public String toString() {
       return Objects.toStringHelper(this)
                .add("resourceId", resourceId)
                .add("resourceName", resourceName)
                .add("resourceTypeId", resourceTypeId)
                .add("resourceUrl", resourceUrl)
                .add("resourcePid", resourcePid)
                .add("choosed", choosed)
                .add("display",display)
                .add("root", root)
                .add("resources", resources)
                .toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
