package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.jenkinsci.plugins.puppet.track.report.PuppetEvent;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.jenkinsci.plugins.puppet.track.report.PuppetStatus;

import java.io.IOException;

/**
 * Looks for reports from file resources.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class FileReportProcessor extends PuppetReportProcessor {
    @Override
    public void process(PuppetReport r) throws IOException {
        for (PuppetStatus st : r.resource_statuses.values()) {
            // TODO: pluggability for matching resources
            if (st.resource_type.equals("File")) {
                for (PuppetEvent ev : st.events) {
                    PuppetDeploymentFacet df = getDeploymentFacet(ev.getNewChecksum());
                    if (df!=null) {
                        String old = ev.getOldChecksum();
                        if (old!=null && Jenkins.getInstance().getFingerprintMap().get(old)==null)
                            old = null; // unknown fingerprint
                        df.add(new HostRecord(r.host, r.environment, st.title, old));
                    }

                    // TODO: record undeploy
                }
            }
        }
    }
}
