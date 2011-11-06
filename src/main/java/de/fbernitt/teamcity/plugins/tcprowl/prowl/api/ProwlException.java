package de.fbernitt.teamcity.plugins.tcprowl.prowl.api;

/**
 * Exceptions while calling prowl.
 */
public class ProwlException extends RuntimeException {

    public ProwlException(Throwable t) {
        super(t);
    }

    public ProwlException (String message) {
        super(message);
    }

    public ProwlException(String s, Throwable t) {
        super(s, t);
    }
}
