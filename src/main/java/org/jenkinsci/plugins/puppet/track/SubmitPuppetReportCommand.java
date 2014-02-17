package org.jenkinsci.plugins.puppet.track;

import hudson.Extension;
import hudson.cli.CLICommand;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;

/**
 * Accepts puppet reports via CLI and SSH.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class SubmitPuppetReportCommand extends CLICommand {
    @Override
    public String getShortDescription() {
        return "Processes puppet report";
    }

    @Override
    protected int run() throws Exception {
        PuppetReport.load(stdin).process();
        return 0;
    }
}
