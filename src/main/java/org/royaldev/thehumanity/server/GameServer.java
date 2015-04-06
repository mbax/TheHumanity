package org.royaldev.thehumanity.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.net.InetSocketAddress;

public class GameServer {

    public GameServer(final String hostname, final int port) {
        final Server server = new Server(new InetSocketAddress(hostname, port));
        try {
            server.setHandler(this.getServletContextHandler(this.getContext()));
            server.start();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private WebApplicationContext getContext() {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(
            "org.royaldev.thehumanity.server"
        ); // Package where @Configuration things are
        context.getEnvironment().setDefaultProfiles("dev");
        return context;
    }

    private ServletContextHandler getServletContextHandler(final WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath("/");
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), "/*");
        contextHandler.addEventListener(new ContextLoaderListener(context));
        //        contextHandler.setResourceBase(new ClassPathResource("web").getURI().toString());
        //        contextHandler.setResourceBase("src/main/resources/web");
        contextHandler.setResourceBase("src/main/webapp");
        contextHandler.setErrorHandler(new ErrorHandler());
        contextHandler.getErrorHandler().setShowStacks(true);
        return contextHandler;
    }

}
