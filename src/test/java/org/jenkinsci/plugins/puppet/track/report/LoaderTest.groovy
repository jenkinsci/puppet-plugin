package org.jenkinsci.plugins.puppet.track.report

import org.junit.Assert
import org.junit.Test

/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
class LoaderTest extends Assert {
    /**
     * Basic data binding test.
     */
    @Test
    void loadReport() {
        def r = PuppetReport.load(this.class.getResourceAsStream("report1.yaml"))
        assert r.environment=="production"
        assert r.host=="dragon"

        def s = r.resource_statuses["File[/tmp/foo.war]"];
        assert s.changed
        assert s.title=="/tmp/foo.war"
        assert !s.skipped
        assert !s.failed
        assert s.resource_type=="File"
        assert s.resource=="File[/tmp/foo.war]"
        assert s.events.size()==1

        def e = s.events[0]
        assert e.property=="ensure"
        assert e.name=="file_created"
        assert e.previous_value=="absent"
        assert e.desired_value=="file"
        assert e.message=="defined content as '{md5}e4a57ad2a0bc444804d53916ee23770f'"
    }
    /**
     * Basic data binding test.
     */
    @Test
    void loadReport3() {
        def r = PuppetReport.load(this.class.getResourceAsStream("report3.yaml"))
        assert r.environment=="production"
        assert r.host=="dragon"

        def s = r.resource_statuses["Track[/opt/apache-tomcat/webapps/petclinic.war]"];
        assert s.changed
        assert s.title=="/opt/apache-tomcat/webapps/petclinic.war"
        assert !s.skipped
        assert !s.failed
        assert s.resource_type=="Track"
        assert s.resource=="Track[/opt/apache-tomcat/webapps/petclinic.war]"
        assert s.events.size()==1

        def e = s.events[0]
        assert e.property=="md5"
        assert e.name=="md5_changed"
        assert e.previous_value=="notcomputed"
        assert e.desired_value=="computed"
        assert e.message=="{md5}808480f0ffd870fe9af90c94d060e744"
    }
}
