package com.bonc.frame.entity.auth.resource;

import com.google.common.base.Objects;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yedunyao
 * @date 2019/8/15 17:07
 */
public class DataResource implements Cloneable {

    protected String resourceId;

    protected List<Resource> resources;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("resourceId", resourceId)
                .add("resources", resources)
                .toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DataResource dr = (DataResource) super.clone();
        if (dr != null) {
            if (this.resources != null) {
                dr.resources = new LinkedList<>();
                for (Resource resource : this.resources) {
                    dr.resources.add((Resource) resource.clone());
                }
            }
        }
        return super.clone();
    }
}
