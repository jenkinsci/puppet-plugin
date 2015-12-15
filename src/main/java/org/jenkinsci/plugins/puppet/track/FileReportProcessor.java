package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.jenkinsci.plugins.puppet.track.report.PuppetEvent;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;
import org.jenkinsci.plugins.puppet.track.report.PuppetStatus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Looks for reports from file resources.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class FileReportProcessor extends PuppetReportProcessor {
    private final static Logger LOGGER = Logger.getLogger(FileReportProcessor.class.getName());

    @Override
    public void process(PuppetReport r) throws IOException {
        LOGGER.log(Level.FINE, "Process report {0}", r);

        for (PuppetStatus st : r.resources("File")) {
            for (PuppetEvent ev : st.events) {
                PuppetDeploymentFacet df = getDeploymentFacet(ev.getNewChecksum());
                if (df==null) {
                    LOGGER.log(Level.FINEST, "Ignore event because no deployment facet was found: {0} - {1} - {2}", new Object[]{ev, st, r});
                } else {
                    String old = ev.getOldChecksum();
                    if (old!=null && Jenkins.getInstance().getFingerprintMap().get(old)==null)
                        old = null; // unknown fingerprint
                    df.add(new HostRecord(r.host, r.environment, st.title, old));
                    LOGGER.log(Level.FINE, "Record event with matching facet: {0} - {1} - {2}", new Object[]{ev, st, r});
                }

                // TODO: record undeploy
            }
        }
    }
}
