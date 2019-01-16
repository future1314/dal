package com.ctrip.framework.dal.cluster.config;

/**
 * @author c7ch23en
 */
public interface XMLConfigConstants {

    /**
     * XML elements
     */
    String DAL = "DAL";
    String CLUSTER = "Cluster";
    String DATABASE_SHARDS = "DatabaseShards";
    String DATABASE_SHARD = "DatabaseShard";
    String DATABASE = "Database";
    String LOGIC_TABLES = "LogicTables";
    String LOGIC_TABLE = "LogicTable";
    String DB_SHARD_RULE = "DbShardRule";
    String TABLE_SHARD_RULE = "TableShardRule";
    String TABLE_NAME_PATTERN = "TableNamePattern";
    String MOD_SHARD_STRATEGY = "ModShardStrategy";
    String SHARD_RULES = "ShardRules";
    String SHARD_RULE = "ShardRule";
    String TABLE_NAME_PATTERNS = "TableNamePatterns";

    /**
     * XML attributes
     */
    String ID = "index";
    String NAME = "name";
    String CATEGORY = "category";
    String ROLE = "role";
    String READ_WEIGHTS = "readWeights";
    String SERVER = "server";
    String PORT = "port";
    String DB = "dbName";
    String USERNAME = "username";
    String PASSWORD = "password";
    String TAGS = "tags";
    String USE_PRESET = "usePreset";
    String CLASS = "class";
    String DB_DEFAULT = "dbDefault";
    String TABLE_DEFAULT = "tableDefault";
    String DEFAULT = "default";

}
