package com.ctrip.platform.dal.cluster.strategy.rule;

import com.ctrip.platform.dal.cluster.SQLData;
import com.ctrip.platform.dal.cluster.exception.DalClusterException;

import java.util.Map;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public class ModShardRule implements ShardRule {

    public static final String MOD_PROPERTY_NAME = "shards";
    public static final String SHARD_KEY_PROPERTY_NAME = "shardKey";

    private int mod = 1;
    private String shardKey;

    @Override
    public String shardByValue(Object value) {
        if (value == null)
            throw new DalClusterException("Shard value cannot be found");
        try {
            Number shardValueNumber = (Number) value;
            return String.valueOf(shardValueNumber.longValue() % mod);
        } catch (Throwable t) {
            throw new DalClusterException("Shard value is not a number", t);
        }
    }

    @Override
    public String shardByFields(SQLData fields) {
        return shardByValue(fields.getValue(shardKey));
    }

    @Override
    public ShardRule fork(Properties properties) {
        ModShardRule rule = new ModShardRule();
        String modProperty = properties.getProperty(MOD_PROPERTY_NAME);
        rule.setMod(modProperty != null ? Integer.parseInt(modProperty) : mod);
        String shardKeyProperty = properties.getProperty(SHARD_KEY_PROPERTY_NAME);
        rule.setShardKey(shardKeyProperty != null ? shardKeyProperty : shardKey);
        return rule;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

    public void setShardKey(String shardKey) {
        this.shardKey = shardKey;
    }

}
