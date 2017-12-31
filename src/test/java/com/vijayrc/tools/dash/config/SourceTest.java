package com.vijayrc.tools.dash.config;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by vxr63 on 7/19/2015.
 */
public class SourceTest {

    @Test
    public void shouldParseHostAndPort() {
        boolean flatten = false;
        Source source1 = new Source("test", "https://10.10.2.2:8080/some", "2", "4", "username", "password", flatten);
        Source source2 = new Source("test", "https://gogo:8080/some", "2", "4", "username", "password", flatten);
        Source source3 = new Source("test", "https://172.23.158.2:443/rio/manage/metrics", "2", "4", "username", "password", flatten);
        Source source4 = new Source("test", "http://172.23.158.2:443/rio/manage/metrics", "2", "4", "username", "password", flatten);

        assertThat(source1.getHost(), is("10.10.2.2"));
        assertThat(source1.getPort(), is("8080"));

        assertThat(source2.getHost(), is("gogo"));
        assertThat(source2.getPort(), is("8080"));

        assertThat(source3.getHost(), is("172.23.158.2"));
        assertThat(source3.getPort(), is("443"));

        assertThat(source4.getHost(), is("172.23.158.2"));
        assertThat(source4.getPort(), is("443"));
    }


}