package org.jenkinsci.plugins.puppet.track.report;

import java.util.logging.Logger;

/**
 * SnakeYAML databinding class for parsing puppet report.
 *
 * @author Kohsuke Kawaguchi
 */
public class PuppetEvent {
    public String property;
    public String message;
    public String previous_value, desired_value;
    public String name;
    public String status;

    /**
     * Figure out MD5 checksum from {@link #message}
     */
    public String getNewChecksum() {
        /*
            three possible messages (can contain additional suffix):

            return "defined content as '#{newvalue}'"
            return "undefined content from '#{currentvalue}'"
            return "content changed '#{currentvalue}' to '#{newvalue}'"
         */
        String ret = null;

        if (message.startsWith("defined content as")) {
            ret = extractChecksum(1);
        } else if (message.startsWith("undefined content from ")) {
            ret = null;
        } else if (message.startsWith("content changed")) {
            ret = extractChecksum(3);
        } else if(message.startsWith("audit change: previously recorded value")) {
        	ret = extractAuditChecksum(10);
        } else if(message.startsWith("audit change: newly-recorded value")) {
        	ret = extractAuditChecksum(4);
        }

        LOGGER.fine("Message: " + message);
        LOGGER.fine("Extracted: " + ret);

        return ret;
    }

    public String getOldChecksum() {
    	String ret = null;

        if (message.startsWith("defined content as")) {
            ret = null;
        } else if (message.startsWith("undefined content from ")) {
            ret = extractChecksum(1);
        } else if (message.startsWith("content changed")) {
            ret = extractChecksum(1);
        } else if(message.startsWith("audit change: previously recorded value")) {
        	ret = extractAuditChecksum(5);
        } else if(message.startsWith("audit change: newly-recorded value")) {
        	ret = null;
        }

        LOGGER.fine("Message: " + message);
        LOGGER.fine("Extracted: " + ret);

        return ret;
    }

    private String extractChecksum(int index) {
        String[] t = message.split("\'");
        String v = t[index];
        if (v.startsWith("{md5}")) {
            return v.substring(5);
        } else {
            LOGGER.fine("Expected to find {md5} but got "+v);
            return null;    // failed to parse
        }
    }

    private String extractAuditChecksum(int index) {
        String[] t = message.split(" ");
        String v = t[index];
        if (v.startsWith("{md5}")) {
            return v.substring(5);
        } else {
            LOGGER.fine("Expected to find {md5} but got "+v);
            return null;    // failed to parse
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PuppetEvent.class.getName());
}
