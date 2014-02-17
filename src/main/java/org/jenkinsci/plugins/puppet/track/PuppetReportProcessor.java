package org.jenkinsci.plugins.puppet.track;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Util;
import hudson.model.Fingerprint;
import jenkins.model.FingerprintFacet;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
import org.jenkinsci.plugins.puppet.track.report.PuppetReport;

import java.io.IOException;
import java.util.Collection;

/**
 * Extension point for other plugins to look at submitted puppet resources and find intereting MD5 checksums.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class PuppetReportProcessor implements ExtensionPoint {
    public abstract void process(PuppetReport r) throws IOException;

    /**
      * Resolve {@link DeploymentFacet} to attach the record to, or null if there's none.
     *
     * This is a convenience method for subtypes.
      */
     protected PuppetDeploymentFacet getDeploymentFacet(String md5) throws IOException {
         if (md5==null)  return null;

         Fingerprint f = Jenkins.getInstance().getFingerprintMap().get(md5);
         if (f==null)    return null;

         Collection<FingerprintFacet> facets = f.getFacets();
         PuppetDeploymentFacet df = findDeploymentFacet(facets);
         if (df==null) {
             df = new PuppetDeploymentFacet(f,System.currentTimeMillis());
             facets.add(df);
         }
         return df;
     }

     private PuppetDeploymentFacet findDeploymentFacet(Collection<FingerprintFacet> facets) {
         for (PuppetDeploymentFacet df : Util.filter(facets, PuppetDeploymentFacet.class)) {
             return df;
         }
         return null;
     }


    public static ExtensionList<PuppetReportProcessor> all() {
        return Jenkins.getInstance().getExtensionList(PuppetReportProcessor.class);
    }
}
