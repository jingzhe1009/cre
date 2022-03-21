package com.bonc.frame.entity.modelCompare.entity.entity;

import java.util.List;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/12 15:19
 */
public class ModelObject {
    private Object BASE;
    private Object VERSON;
    private ModelObjectCONTENT CONTENT; // ADD UPDATE DELETE


    class ModelObjectCONTENT {
        ModelObjectCONTENTaaa ADD;
        ModelObjectCONTENTaaa UPDATE;
        ModelObjectCONTENTaaa DELETE;

        class ModelObjectCONTENTaaa {
            List<RECTObject> RECT;
            List<PATHObject> PATH;
        }
    }
}
