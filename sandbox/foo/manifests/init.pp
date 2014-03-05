class foo {
    file {
        '/tmp/foo.war':
            source => "puppet:///modules/foo/foo.war"
    }

    track { '/tmp/foo2.war':
    }

#  track2 { "testing": command => "/bin/touch /tmp/xyz"}
}
