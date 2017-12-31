package com.vijayrc.tools.dash.config;

import com.vijayrc.tools.dash.repo.AllConfigs;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

/**
 * Created by vxr63 on 7/12/2015.
 */
public class ConfigTest {

    @Test
    @Ignore
    public void shouldReadYaml() throws Exception {
        AllConfigs allConfigs = new AllConfigs("C:/DEP/dash/src/main/resources/config-sio.yml");
        System.out.println(allConfigs);
    }

}