package com.nex.ticket.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            // Allow backslash and space in the request target
            // Note: Space is still technically a delimiter, so this might not fully resolve 
            // a literal space at the end of the request line, but it relaxes query/path parsing.
            connector.setProperty("relaxedQueryChars", "\\ ");
            connector.setProperty("relaxedPathChars", "\\ ");
        });
    }
}
