package com.sap.cp.cf.demoapps;

import com.sap.cloud.security.xsuaa.mock.XsuaaMockWebServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;

public class XsuaaMockPostProcessor implements EnvironmentPostProcessor, DisposableBean {

    private final XsuaaMockWebServer mockAuthorizationServer = new XsuaaMockWebServer();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.acceptsProfiles(Profiles.of("uaamock"))) {
            environment.getPropertySources().addFirst(this.mockAuthorizationServer);
        }
    }

    @Override
    public void destroy() throws Exception {
        this.mockAuthorizationServer.destroy();
    }
}