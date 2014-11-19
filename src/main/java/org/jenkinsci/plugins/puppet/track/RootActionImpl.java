package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

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
    private final static Logger LOGGER = Logger.getLogger(RootActionImpl.class.getName());

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

        PuppetReport.load(req.getReader()).process();

        return HttpResponses.ok();
    }

    public static RootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(RootAction.class).get(RootActionImpl.class);
    }
}
