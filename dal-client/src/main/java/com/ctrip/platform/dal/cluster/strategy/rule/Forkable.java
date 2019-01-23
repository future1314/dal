package com.ctrip.platform.dal.cluster.strategy.rule;

import java.util.Map;
import java.util.Properties;

/**
 * @author c7ch23en
 */
public interface Forkable<T> {

    T fork(Properties properties);

}
