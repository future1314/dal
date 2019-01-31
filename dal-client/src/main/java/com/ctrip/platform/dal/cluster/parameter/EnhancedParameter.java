package com.ctrip.platform.dal.cluster.parameter;

/**
 * @author c7ch23en
 */
public class EnhancedParameter extends ParameterContent {

    private int index;
    private String name;

    public EnhancedParameter(int index, String name, Object value, int sqlType) {
        super(value, sqlType);
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

}
