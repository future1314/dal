package com.ctrip.platform.dal.cluster.exception;

/**
 * @author c7ch23en
 */
public class DalClusterConfigException extends DalClusterException {

    private static final long serialVersionUID = 1L;

    public DalClusterConfigException() {}

    public DalClusterConfigException(String message) {
        super(message);
    }

    public DalClusterConfigException(Throwable cause) {
        super(cause);
    }

    public DalClusterConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
