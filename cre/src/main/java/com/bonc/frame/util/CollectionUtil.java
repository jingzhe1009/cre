package com.bonc.frame.util;

import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * @author yedunyao
 * @date 2019/8/29 16:15
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 比较新旧两个集合，
     * 返回新集合中需要插入的数据集合，
     * 旧集合将只保留需要删除的数据
     *
     * @param news
     * @param olds 旧集合将只保留需要删除的数据
     * @param <E>
     * @return 新集合中需要插入的数据集合
     */
    public static <E> List<E> diffCollection(Collection<E> news, Collection<E> olds) {
        if (news == null || news.isEmpty()) {
            return Collections.emptyList();
        }

        if (olds == null || olds.isEmpty()) {
            return ImmutableList.copyOf(news);
        }

        List<E> needInsert = new LinkedList<>();
        final Iterator<E> iterator = news.iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (olds.contains(e)) {
                olds.remove(e);
            } else {
                needInsert.add(e);
            }
        }
        return needInsert;
    }

    /**
     * 差集 A-B
     *
     * @param a
     * @param b
     * @param <E>
     * @return
     */
    public static <E> List<E> subtraction(Collection<E> a, Collection<E> b) {
        if (isEmpty(a)) {
            return Collections.emptyList();
        }

        if (isEmpty(b)) {
            return ImmutableList.copyOf(a);
        }

        final Iterator<E> iterator = b.iterator();
        while (iterator.hasNext()) {
            E e = iterator.next();
            if (a.contains(e)) {
                a.remove(e);
            }
        }
        return ImmutableList.copyOf(a);
    }

}
