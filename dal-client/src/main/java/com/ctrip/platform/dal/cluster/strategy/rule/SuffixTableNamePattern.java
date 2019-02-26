package com.ctrip.platform.dal.cluster.strategy.rule;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public class SuffixTableNamePattern implements TableNamePattern {

    public static final String SEPARATOR_PROPERTY_NAME = "separator";
    public static final String DIGIT_PROPERTY_NAME = "digit";

    private String separator = "";
    private int digit = 0;

    @Override
    public String getTargetTableName(String logicTableName, int shardId) {
        if (digit == 0)
            return logicTableName;
        String format = String.format("%%0%dd", digit);
        String suffix = String.format(format, shardId);
        return logicTableName + separator + suffix;
    }

    @Override
    public TableNamePattern fork(Properties properties) {
        SuffixTableNamePattern pattern = new SuffixTableNamePattern();
        String separatorProperty = properties.getProperty(SEPARATOR_PROPERTY_NAME);
        pattern.setSeparator(separatorProperty != null ? separatorProperty : separator);
        String digitProperty = properties.getProperty(DIGIT_PROPERTY_NAME);
        pattern.setDigit(digitProperty != null ? Integer.parseInt(digitProperty) : digit);
        return pattern;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setDigit(int digit) {
        this.digit = digit;
    }

}
