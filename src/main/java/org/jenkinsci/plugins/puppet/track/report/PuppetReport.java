package org.jenkinsci.plugins.puppet.track.report;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
import org.jenkinsci.plugins.puppet.track.PuppetReportProcessor;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SnakeYAML databinding class for parsing puppet report.
 *
 * @author Kohsuke Kawaguchi
 */
public class PuppetReport {
    public String host;
    public String environment;
    public String time;
    public String configuration_version;

    public Map<String,PuppetStatus> resource_statuses = new HashMap<String, PuppetStatus>();

    /**
     * Lists up {@link PuppetStatus}es whose resource type matches the given type.
     */
    public Iterable<PuppetStatus> resources(final String type) {
        return new Iterable<PuppetStatus>() {
            public Iterator<PuppetStatus> iterator() {
                return Iterators.filter(resource_statuses.values().iterator(),new Predicate<PuppetStatus>() {
                    public boolean apply(PuppetStatus st) {
                        return st.resource_type.equals(type);
                    }
                });
            }
        };
    }

    /**
     * Process this report with {@link PuppetReportProcessor} and record all the fingerprints.
     */
    public void process() throws IOException {
        Jenkins.getInstance().checkPermission(DeploymentFacet.RECORD);

        // fill in missing default values, if any.
        if (host==null)           host = "unknown";
        if (environment==null)    environment = "unknown";

        for (PuppetReportProcessor prp : PuppetReportProcessor.all())
            prp.process(this);
    }

    public static PuppetReport load(InputStream in) {
        return (PuppetReport)PARSER.load(in);
    }

    public static PuppetReport load(Reader r) {
        return (PuppetReport)PARSER.load(r);
    }

    public static final Yaml PARSER = buildParser();

    private static Yaml buildParser() {
        Constructor c = new Constructor(PuppetReport.class) {
            {
                // ignore missing properties in YAML that we don't care
                PropertyUtils p = new PropertyUtils();
                p.setSkipMissingProperties(true);
                setPropertyUtils(p);

                // map symbol to String
                addTypeDescription(new TypeDescription(String.class,"!ruby/sym"));
            }

            /**
             * If we encounter unknown tags, ignore them.
             */
            @Override
            protected Class<?> getClassForNode(Node node) {
                Class c;
                try {
                    c = super.getClassForNode(node);
                } catch (Exception e) {
                    c = node.getType();
                }

                // for security reasons, restrict classes that YAML will try to instantiate
                if (c==Object.class || c==String.class || c==Boolean.class || c==Integer.class)        return c;
                if (!c.getName().startsWith("org.jenkinsci.plugins.puppet.track.report."))
                    throw new YAMLException("Invalid class name: "+c.getName());
                return c;
            }
        };

        return new Yaml(c);
    }
}
