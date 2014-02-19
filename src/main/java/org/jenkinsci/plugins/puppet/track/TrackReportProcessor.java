package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.jenkinsci.plugins.puppet.track.report.PuppetEvent;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.jenkinsci.plugins.puppet.track.report.PuppetStatus;

import java.io.IOException;

/**
 * Looks for reports from "track" resources.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class TrackReportProcessor extends PuppetReportProcessor {
    @Override
    public void process(PuppetReport r) throws IOException {
        for (PuppetStatus st : r.resources("Track")) {
            for (PuppetEvent ev : st.events) {
                String msg = ev.message;
                if (msg.startsWith("{md5}")) {
                    String checksum = msg.substring(5);
                    PuppetDeploymentFacet df = getDeploymentFacet(checksum);
                    if (df!=null) {
                        df.add(new HostRecord(r.host, r.environment, st.title, null));
                    }
                }
            }
        }
    }
}
