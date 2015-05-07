package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;

/**
 * Exposed at /puppet to receive report submissions from puppet over HTTP.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class UnprotectedRootActionImpl implements UnprotectedRootAction {
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
        // TODO: stapler YAML support

        PuppetReport.load(req.getReader()).process();

        return HttpResponses.ok();
    }

    public static UnprotectedRootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(UnprotectedRootAction.class).get(UnprotectedRootActionImpl.class);
    }
}
