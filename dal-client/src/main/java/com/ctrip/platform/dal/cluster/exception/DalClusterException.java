package com.ctrip.platform.dal.cluster.exception;

/**
 * @author c7ch23en
 */
public class DalClusterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DalClusterException() {}

    public DalClusterException(String message) {
        super(message);
    }

    public DalClusterException(Throwable cause) {
        super(cause);
    }

    public DalClusterException(String message, Throwable cause) {
        super(message, cause);
    }

}
