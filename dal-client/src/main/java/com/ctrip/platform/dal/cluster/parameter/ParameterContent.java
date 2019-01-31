package com.ctrip.platform.dal.cluster.parameter;

/**
 * @author c7ch23en
 */
public class ParameterContent {

    private Object value;

    private int sqlType;

    protected ParameterContent(Object value) {
        this(value, -1);
    }

    protected ParameterContent(Object value, int sqlType) {
        this.value = value;
        this.sqlType = sqlType;
    }

    public Object getValue() {
        return value;
    }

    public int getSqlType() {
        return sqlType;
    }

}
