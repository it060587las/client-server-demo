package com.dataart.edu.server;

import com.dataart.edu.message.format.util.FileUtil;
import com.dataart.edu.server.ConfigurationCreator.ServerConfiguration;
import com.dataart.edu.server.spring.config.ApplicationConfiguration;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Server Application, which reads command line parameters, and starts server.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 *
 * @see ConfigurationCreator
 * @see ApplicationConfiguration 
 */
@Slf4j
public class ServerApplication {
    /**
     * Storage for configuration.
     */
    private static final ThreadLocal<ServerConfiguration> THREAD_LOCAL_CONFIG = new ThreadLocal<>();

    /**
     * Getting current application configuration.
     *
     * @return ServerConfiguration
     */
    public static ServerConfiguration getApplicationConfig() {
        return THREAD_LOCAL_CONFIG.get();
    }

    /**
     * Run application.
     *
     * @param args command line argument list.
     */
    private void runApplication(String args[]) {
        try {
            ConfigurationCreator configurationCreator = new ConfigurationCreator();
            ServerConfiguration configuration = configurationCreator.parseArgumentList(args);
            log.info("Starting server with configuration: [{}]", configuration);
            FileUtil.checkFilePermission(configuration.getDataDirectory());
            THREAD_LOCAL_CONFIG.set(configuration);
            runComponents();
        } catch (NumberFormatException nfe) {
            log.info("Incorrect number format occured: {}", nfe.getMessage());
        } catch (IOException e) {
            log.info("Problem occured during checking of server working directory: {}.", e.getMessage());
        }
    }

    /**
     * Starting of all required components.
     *
     * @param serverConfiguration configuration of server.
     */
    private void runComponents() {
        NioBasedServer server=null;
        try {
            ApplicationContext rootContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
            server = rootContext.getBean(NioBasedServer.class);
            server.run();
        } catch (IOException e) {            
            log.info("Problem while starting server components: {}.", e.getMessage());
            if (server!=null){
                try{
                    server.stop();
                }catch(IOException stopException){
                    log.info("Problem stopping server {}", stopException.getMessage());
                }
            }
        }
    }

    /**
     * Application entry point.
     *
     * @param args command line arguments.
     */
    public static void main(String args[]) {
        ServerApplication a = new ServerApplication();
        a.runApplication(args);
    }
}
