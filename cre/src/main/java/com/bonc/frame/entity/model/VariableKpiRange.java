package com.bonc.frame.entity.model;

import com.bonc.frame.util.CollectionUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: wangzhengbao
 * @DATE: 2019/12/30 17:17
 */
public class VariableKpiRange {
    private String type;
    private String result;
    private Double max = null;
    private Double min = null;
    private String haveMin = null;
    private String haveMax = null;
    private Set<String> strRanges;

    public String getResult() {
        if ("2".equals(type) || "7".equals(type)) {
            Object minValue = null;
            if (min != null) {
                if (min == Double.NEGATIVE_INFINITY) {
                    minValue = min;
                } else {
                    minValue = min.longValue();
                }
            }
            Object maxValue = null;
            if (max != null) {
                if (max == Double.POSITIVE_INFINITY) {
                    maxValue = max;
                } else {
                    maxValue = max.longValue();
                }
            }

            return haveMin + minValue + "," + maxValue + haveMax;
        } else if ("4".equals(type) || "6".equals(type) ) {
            return haveMin + min + "," + max + haveMax;
        } else {
            if (strRanges == null || strRanges.isEmpty()) {
                strRanges = new HashSet<>();
            }
            String string = strRanges.toString();
            return string.substring(1, string.length() - 1);
        }
    }


    public void addStrRange(String range) {
        if (strRanges == null) {
            strRanges = new HashSet<>();
        }
        strRanges.add(range);
    }

    public void addAllStrRange(Collection<String> range) {
        if (strRanges == null) {
            strRanges = new HashSet<>();
        }
        if (CollectionUtil.isEmpty(range)) {
            return;
        }
        strRanges.addAll(range);
    }

    public Set<String> getStrRanges() {
        return strRanges;
    }

    public void setStrRange(Set<String> strRanges) {
        this.strRanges = strRanges;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public String getHaveMin() {
        return haveMin;
    }

    public void setHaveMin(String haveMin) {
        this.haveMin = haveMin;
    }

    public String getHaveMax() {
        return haveMax;
    }

    public void setHaveMax(String haveMax) {
        this.haveMax = haveMax;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStrRanges(Set<String> strRanges) {
        this.strRanges = strRanges;
    }
}
