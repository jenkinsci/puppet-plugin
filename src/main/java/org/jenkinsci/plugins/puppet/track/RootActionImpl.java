package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exposed at /puppet to receive report submissions from puppet over HTTP.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class RootActionImpl implements RootAction {

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "puppet";
    }

    /**
     * Receives the submission from HTTP reporter to track fingerprints.
     */
    @RequirePOST
    public HttpResponse doReport(StaplerRequest req) throws IOException {
        LOGGER.log(Level.FINE, "Process report");
        // TODO: stapler YAML support
        LOGGER.log(Level.FINE, "Puppet plugin has received a report from {0}", req.getRemoteAddr());
        try {
            BufferedReader bufferedreader = req.getReader();
            if (bufferedreader!=null) {
                PuppetReport puppetReport = PuppetReport.load(bufferedreader);
                puppetReport.process();
            } else {
                LOGGER.log(Level.WARNING, "Ignoring empty PuppetReport sent by {0}", req.getRemoteAddr());
            }
        } catch (IOException e)  {
            LOGGER.log(Level.WARNING, String.format("PuppetReport error loading report sent by %s", req.getRemoteAddr()), e);
            throw new IOException();
        } catch (NullPointerException e)  {
            LOGGER.log(Level.WARNING, String.format("PuppetReport error loading report sent by %s", req.getRemoteAddr()), e);
        }
        return HttpResponses.ok();
    }

    public static RootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(RootAction.class).get(RootActionImpl.class);
    }

    private static final Logger LOGGER = Logger.getLogger(RootActionImpl.class.getName());
}
