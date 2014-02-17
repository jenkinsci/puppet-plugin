package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import hudson.model.RootAction;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
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
        // TODO: stapler YAML support

        PuppetReport r = PuppetReport.load(req.getReader());
        processReport(r);

        return HttpResponses.ok();
    }

    public void processReport(PuppetReport r) throws IOException {
        Jenkins.getInstance().checkPermission(DeploymentFacet.RECORD);

        // fill in missing default values, if any.
        if (r.host==null)           r.host = "unknown";
        if (r.environment==null)    r.environment = "unknown";

        for (PuppetReportProcessor prp : PuppetReportProcessor.all())
            prp.process(r);
    }

    public static RootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(RootAction.class).get(RootActionImpl.class);
    }
}
