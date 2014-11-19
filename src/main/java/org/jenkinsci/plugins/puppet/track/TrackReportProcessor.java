package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.jenkinsci.plugins.puppet.track.report.PuppetEvent;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.jenkinsci.plugins.puppet.track.report.PuppetStatus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Looks for reports from "track" resources.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class TrackReportProcessor extends PuppetReportProcessor {
    private final static Logger LOGGER = Logger.getLogger(TrackReportProcessor.class.getName());

    public void process(PuppetReport r) throws IOException {
        LOGGER.log(Level.FINE, "Process report {0}", r);

        for (PuppetStatus st : r.resources("Track")) {
            for (PuppetEvent ev : st.events) {
                String msg = ev.message;
                if (msg.startsWith("{md5}")) {
                    String checksum = msg.substring(5);
                    PuppetDeploymentFacet df = getDeploymentFacet(checksum);
                    if (df == null){
                        LOGGER.log(Level.FINEST, "Ignore event because no deployment facet was found: {0} - {1} - {2}", new Object[]{ev, st, r});
                    } else {
                        LOGGER.log(Level.FINE, "Record event with matching facet: {0} - {1} - {2}", new Object[]{ev, st, r});
                        df.add(new HostRecord(r.host, r.environment, st.title, null));
                    }
                } else {
                    LOGGER.log(Level.FINE, "Skip event with unexpected message format (don't start with \"{md5}\"):{0} - {1} - {2}", new Object[]{ev, st, r});
                }
            }
        }
    }
}
