package com.ctrip.framework.dal.cluster.config;

import com.ctrip.framework.dal.cluster.Cluster;
import com.ctrip.framework.dal.cluster.exception.DalClusterConfigException;
import com.ctrip.framework.dal.cluster.exception.DalClusterException;
import com.ctrip.framework.dal.cluster.meta.*;
import com.ctrip.framework.dal.cluster.strategy.shard.CustomNamePattern;
import com.ctrip.framework.dal.cluster.strategy.shard.ModShardRule;
import com.ctrip.framework.dal.cluster.strategy.shard.ModShardStrategy;
import com.ctrip.framework.dal.cluster.strategy.shard.NoShardRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author c7ch23en
 */
public abstract class XMLConfigLoader implements ConfigLoader, XMLConfigConstants {

    protected List<Cluster> loadClusters(InputStream stream) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        return loadClusters(doc);
    }

    private List<Cluster> loadClusters(Document doc) throws Exception {
        Element root = doc.getDocumentElement();
        assertNotNull(root, "Root element not found");
        if (!DAL.equalsIgnoreCase(root.getTagName()))
            throw new DalClusterConfigException("Root element should be 'DAL'");
        List<Node> clusterNodes = getChildNodes(root, CLUSTER);
        assertNotEmpty(clusterNodes, String.format("Element <%s> not found", CLUSTER));
        List<Cluster> clusters = new ArrayList<>();
        for (Node clusterNode : clusterNodes)
            clusters.add(loadCluster(clusterNode));
        return clusters;
    }

    private Cluster loadCluster(Node clusterNode) throws Exception {
        String name = getAttribute(clusterNode, NAME);
        assertNotNull(name, String.format("Attribute '%s' of Element <%s> not found", NAME, CLUSTER));
        DalCluster cluster = new DalCluster(name);

        List<Node> databaseShardsNodes = getChildNodes(clusterNode, DATABASE_SHARDS);
        assertNotEmpty(databaseShardsNodes, String.format("Element <%s> not found", DATABASE_SHARDS));
        assertSingleton(databaseShardsNodes, String.format("More than one <%s> element found", DATABASE_SHARDS));
        List<DatabaseShard> databaseShards = loadDatabaseShards(databaseShardsNodes.get(0));
        for (DatabaseShard databaseShard : databaseShards)
            cluster.addDatabaseShard(databaseShard);
        // logictables
        List<Node> logicTablesNodes = getChildNodes(clusterNode, LOGIC_TABLES);
        assertSingleton(logicTablesNodes, String.format("More than one <%s> element found", LOGIC_TABLES));

        List<Node> shardStrategyNodes = getChildNodes(clusterNode, MOD_SHARD_STRATEGY);
        List<Node> modShardStrategyNodes = getChildNodes(clusterNode, MOD_SHARD_STRATEGY);
        if (modShardStrategyNodes.size() > 0 || shardStrategyNodes.size() == 0) {
            ModShardRule dbShardRule = new ModShardRule();
            dbShardRule.setMod(databaseShards.size());
            dbShardRule.setShardKey("ID");
            NoShardRule tableShardRule = new NoShardRule();
            CustomNamePattern customNamePattern = new CustomNamePattern("#logicTableName#");
            ModShardStrategy strategy = new ModShardStrategy(dbShardRule, tableShardRule, customNamePattern);
            cluster.setShardStrategy(strategy);
        }
/*

        // shardrules
        List<Node> shardRulesNodes = getChildNodes(clusterNode, SHARD_RULES);
        assertSingleton(shardRulesNodes, String.format("More than one <%s> element found", SHARD_RULES));
        if (shardRulesNodes.size() > 0) {
            ShardRules result = loadShardRules(shardRulesNodes.get(0));
            List<ShardRule> shardRules = result.getShardRules();
            for (ShardRule shardRule : shardRules)
                cluster.addShardRule(shardRule);
            cluster.setDefaultDbShardRule(result.getDefaultDbShardRule());
            cluster.setDefaultTableShardRule(result.getDefaultTableShardRule());
        }
        // namepatterns
        List<Node> tableNamePatternsNodes = getChildNodes(clusterNode, TABLE_NAME_PATTERNS);
        assertSingleton(tableNamePatternsNodes, String.format("More than one <%s> element found", TABLE_NAME_PATTERNS));
        if (tableNamePatternsNodes.size() > 0) {
            TableNamePatterns result = loadTableNamePatterns(tableNamePatternsNodes.get(0));
            List<TableNamePattern> tableNamePatterns = result.getTableNamePatterns();
            for (TableNamePattern tableNamePattern : tableNamePatterns)
                cluster.addTableNamePattern(tableNamePattern);
            cluster.setDefaultTableNamePattern(result.getDefaultTableNamePattern());
        }
*/

        return cluster;
    }

    private List<DatabaseShard> loadDatabaseShards(Node databaseShardsNode) throws Exception {
        List<Node> databaseShardNodes = getChildNodes(databaseShardsNode, DATABASE_SHARD);
        assertNotEmpty(databaseShardNodes, String.format("Element <%s> not found", DATABASE_SHARD));
        List<DatabaseShard> databaseShards = new ArrayList<>();
        for (Node databaseShardNode : databaseShardNodes)
            databaseShards.add(loadDatabaseShard(databaseShardNode));
        return databaseShards;
    }

    private DatabaseShard loadDatabaseShard(Node databaseShardNode) throws Exception {
        String id = getAttribute(databaseShardNode, ID);
        assertNotNull(id, String.format("Attribute '%s' of Element <%s> not found", ID, DATABASE_SHARD));
        DefaultDatabaseShard databaseShard = new DefaultDatabaseShard(Integer.parseInt(id));
        List<Node> databaseNodes = getChildNodes(databaseShardNode, DATABASE);
        assertNotEmpty(databaseNodes, String.format("Element <%s> not found", DATABASE));
        for (Node databaseNode : databaseNodes)
            databaseShard.addDatabase(loadDatabase(databaseNode));
        return databaseShard;
    }

    private Database loadDatabase(Node databaseNode) throws Exception {
        CommonDatabase database = new CommonDatabase();
        String role = getAttribute(databaseNode, ROLE, DatabaseRole.MASTER.getName());
        if (DatabaseRole.MASTER.getName().equalsIgnoreCase(role))
            database.setRole(DatabaseRole.MASTER);
        else if (DatabaseRole.SLAVE.getName().equalsIgnoreCase(role))
            database.setRole(DatabaseRole.SLAVE);
        else
            throw new DalClusterException("Database role invalid");
        database.setReadWeights(Integer.parseInt(getAttribute(databaseNode, READ_WEIGHTS, "1")));
        database.setServer(getAttribute(databaseNode, SERVER));
        database.setPort(Integer.parseInt(getAttribute(databaseNode, PORT)));
        database.setDbName(getAttribute(databaseNode, DB));
        database.setUsername(getAttribute(databaseNode, USERNAME));
        database.setPassword(getAttribute(databaseNode, PASSWORD));
        String tagsStr = getAttribute(databaseNode, TAGS, "");
        String[] tags = tagsStr.split(",");
        for (String tag : tags)
            database.addTag(tag);
        return database;
    }
/*

    private ShardRules loadShardRules(Node shardRulesNode) throws Exception {
        List<Node> shardRuleNodes = getChildNodes(shardRulesNode, SHARD_RULE);
        ShardRules shardRules = new ShardRules();
        for (Node shardRuleNode : shardRuleNodes) {
            ShardRule shardRule = loadShardRule(shardRuleNode);
            shardRules.addShardRule(shardRule);
            if (Boolean.parseBoolean(getAttribute(shardRuleNode, DB_DEFAULT)))
                shardRules.setDefaultDbShardRule(shardRule);
            if (Boolean.parseBoolean(getAttribute(shardRuleNode, TABLE_DEFAULT)))
                shardRules.setDefaultTableShardRule(shardRule);
        }
        if (shardRules.getShardRules().size() == 1) {
            shardRules.setDefaultDbShardRule(shardRules.getShardRules().get(0));
            shardRules.setDefaultTableShardRule(shardRules.getShardRules().get(0));
        }
        return shardRules;
    }

    private ShardRule loadShardRule(Node shardRuleNode) throws Exception {
        String name = getAttribute(shardRuleNode, NAME);
        assertNotNull(name, String.format("Attribute '%s' of Element <%s> not found", NAME, SHARD_RULE));
        ShardRule shardRule;
        String preset = getAttribute(shardRuleNode, USE_PRESET);
        Class<? extends ShardRule> clazz = shardRulePresets.get(preset);
        if (clazz != null) {
            shardRule = clazz.newInstance();
            shardRule.setRuleName(name);
            return shardRule;
        }
        shardRule = new ModShardRule(name, 3);  // !!!!!!!!!!!!!!!!!!!!!!!!!
        return shardRule;
    }

    private TableNamePatterns loadTableNamePatterns(Node tableNamePatternsNode) throws Exception {
        List<Node> tableNamePatternNodes = getChildNodes(tableNamePatternsNode, TABLE_NAME_PATTERN);
        TableNamePatterns tableNamePatterns = new TableNamePatterns();
        for (Node tableNamePatternNode : tableNamePatternNodes) {
            TableNamePattern tableNamePattern = loadTableNamePattern(tableNamePatternNode);
            tableNamePatterns.addTableNamePattern(tableNamePattern);
            if (Boolean.parseBoolean(getAttribute(tableNamePatternNode, DEFAULT)))
                tableNamePatterns.setDefaultTableNamePattern(tableNamePattern);
        }
        if (tableNamePatterns.getTableNamePatterns().size() == 1)
            tableNamePatterns.setDefaultTableNamePattern(tableNamePatterns.getTableNamePatterns().get(0));
        return tableNamePatterns;
    }

    private TableNamePattern loadTableNamePattern(Node tableNamePatternNode) throws Exception {
        String name = getAttribute(tableNamePatternNode, NAME);
        assertNotNull(name, String.format("Attribute '%s' of Element <%s> not found", NAME, TABLE_NAME_PATTERN));
        TableNamePattern tableNamePattern;
        String preset = getAttribute(tableNamePatternNode, USE_PRESET);
        Class<? extends TableNamePattern> clazz = tableNamePatternPresets.get(preset);
        if (clazz != null) {
            tableNamePattern = clazz.newInstance();
            tableNamePattern.setPatternName(name);
            return tableNamePattern;
        }
        tableNamePattern = new NoShardTableNamePattern(name);
        return tableNamePattern;
    }
*/

    private List<Node> getChildNodes(Node parent, String name) {
        List<Node> nodes = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (name.equalsIgnoreCase(child.getNodeName())) {
                nodes.add(child);
            }
        }
        return nodes;
    }

    private String getAttribute(Node node, String name) {
        return getAttribute(node, name, null);
    }

    private String getAttribute(Node node, String name, String defaultValue) {
        try {
            String attribute = node.getAttributes().getNamedItem(name).getNodeValue();
            if (attribute != null) {
                attribute = attribute.trim();
                if (!attribute.isEmpty())
                    return attribute;
            }
        } catch (NullPointerException e) {}
        return defaultValue;
    }

    private void assertNotEmpty(List<Node> nodes, String message) throws Exception {
        if (nodes.isEmpty())
            throw new DalClusterConfigException(message);
    }

    private void assertSingleton(List<Node> nodes, String message) throws Exception {
        if (nodes.size() > 1)
            throw new DalClusterConfigException(message);
    }

    private void assertNotNull(Object object, String message) throws Exception {
        if (object == null)
            throw new DalClusterConfigException(message);
    }

/*

    private class ShardRules {

        private List<ShardRule> shardRules = new ArrayList<>();
        private ShardRule defaultDbShardRule;
        private ShardRule defaultTableShardRule;

        public List<ShardRule> getShardRules() {
            return shardRules;
        }

        public ShardRule getDefaultDbShardRule() {
            return defaultDbShardRule;
        }

        public ShardRule getDefaultTableShardRule() {
            return defaultTableShardRule;
        }

        public void addShardRule(ShardRule shardRule) {
            shardRules.add(shardRule);
        }

        public void setDefaultDbShardRule(ShardRule defaultDbShardRule) {
            this.defaultDbShardRule = defaultDbShardRule;
        }

        public void setDefaultTableShardRule(ShardRule defaultTableShardRule) {
            this.defaultTableShardRule = defaultTableShardRule;
        }

    }

    private class TableNamePatterns {

        private List<TableNamePattern> tableNamePatterns = new ArrayList<>();
        private TableNamePattern defaultTableNamePattern;

        public List<TableNamePattern> getTableNamePatterns() {
            return tableNamePatterns;
        }

        public TableNamePattern getDefaultTableNamePattern() {
            return defaultTableNamePattern;
        }

        public void addTableNamePattern(TableNamePattern tableNamePattern) {
            tableNamePatterns.add(tableNamePattern);
        }

        public void setDefaultTableNamePattern(TableNamePattern defaultTableNamePattern) {
            this.defaultTableNamePattern = defaultTableNamePattern;
        }

    }
*/

}
