package com.bonc.frame.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author yedunyao
 * @date 2019/6/3 20:26
 */
public class MyBeanUtilsBean extends BeanUtilsBean {

    private Log log = LogFactory.getLog(MyBeanUtilsBean.class);

    private static final ContextClassLoaderLocal<MyBeanUtilsBean>
            BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal<MyBeanUtilsBean>() {
        // Creates the default instance used when the context classloader is unavailable
        @Override
        protected MyBeanUtilsBean initialValue() {
            return new MyBeanUtilsBean();
        }
    };

    public static MyBeanUtilsBean getInstance() {
        return BEANS_BY_CLASSLOADER.get();
    }

    public void populate(final Object bean, final Map<String, ? extends Object> properties)
            throws IllegalAccessException, InvocationTargetException {

        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("BeanUtils.populate(" + bean + ", " +
                    properties + ")");
        }

        // Loop through the property name/value pairs to be set
        for (final Map.Entry<String, ? extends Object> entry : properties.entrySet()) {
            // Identify the property name and value(s) to be assigned
            final String name = entry.getKey();
            if (name == null) {
                continue;
            }

            // Perform the assignment for this property
            if (entry.getValue() != null) {
                setProperty(bean, name, entry.getValue());
            }

        }

    }

}
