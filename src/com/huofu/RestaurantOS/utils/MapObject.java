package com.huofu.RestaurantOS.utils;

import java.util.HashMap;

/**
 * Created by akwei on 10/31/14.
 */
public class MapObject extends HashMap{

    private boolean isStringEmpty(String value){
        return (value==null) || (value.length()==0);
    }

    public Integer getInteger(String key) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            if (this.isStringEmpty((String) value)) {
                return null;
            }
            try {
                return Integer.parseInt((String) value);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Integer getInteger(String key, Integer defValue) {
        Integer n = getInteger(key);
        if (n == null) {
            return defValue;
        }
        return n;
    }

    public int getInt(String key, int defValue) {
        return getInteger(key, defValue);
    }

    public String getString(String key) {
        Object obj = this.get(key);
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Number) {
            return String.valueOf(obj);
        }
        return null;
    }

    public long getLong(String key, long defValue) {
        return getLLong(key, defValue);
    }

    public Long getLLong(String key) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            if (this.isStringEmpty((String) value)) {
                return null;
            }
            try {
                return Long.parseLong((String) value);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Long getLLong(String key, Long defValue) {
        Long n = getLLong(key);
        if (n == null) {
            return defValue;
        }
        return n;
    }

    public double getDouble(String key, double defValue) {
        return getDDouble(key, defValue);
    }

    public Double getDDouble(String key) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            if (this.isStringEmpty((String) value)) {
                return null;
            }
            try {
                return Double.parseDouble((String) value);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Double getDDouble(String key, Double defValue) {
        Double n = getDDouble(key);
        if (n == null) {
            return defValue;
        }
        return n;
    }

    public boolean getBoolean(String key, boolean defValue) {
        return getBBoolean(key, defValue);
    }

    public Boolean getBBoolean(String key) {
        Object value = this.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            if (this.isStringEmpty((String) value)) {
                return null;
            }
            try {
                return Boolean.parseBoolean((String) value);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBBoolean(String key, Boolean defValue) {
        Boolean n = getBBoolean(key);
        if (n == null) {
            return defValue;
        }
        return n;
    }
}