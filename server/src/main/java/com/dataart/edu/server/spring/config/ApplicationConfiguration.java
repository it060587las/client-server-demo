package com.dataart.edu.server.spring.config;

import com.dataart.edu.server.ConfigurationCreator.ServerConfiguration;
import com.dataart.edu.server.NioBasedServer;
import com.dataart.edu.server.ServerApplication;
import com.dataart.edu.server.ServerMessageProcessor;
import com.dataart.edu.server.SocketChannelsProcessor;
import com.dataart.edu.server.dao.BirdAndSightDaoImpl;
import com.dataart.edu.server.dao.BirdsDaoWrapper;
import com.dataart.edu.server.dao.IBirdsDao;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration of application components.
 *
 * @author alitvinov
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class ApplicationConfiguration {

    /**
     * Create ServerMessageProcessor.
     *
     * @return ServerMessageProcessor
     */
    @Bean
    public ServerMessageProcessor getServerMessageProcessor() {
        return new ServerMessageProcessor();
    }

    /**
     * Create SocketChannelsProcessor.
     *
     * @return SocketChannelsProcessor
     */
    @Bean
    public SocketChannelsProcessor getSocketChannelsProcessor() {
        return new SocketChannelsProcessor();
    }

    /**
     * Create DAO component.
     *
     * @return BirdAndSightDaoImpl
     */
    @Bean
    public IBirdsDao getDao() {
        return new BirdAndSightDaoImpl();
    }

    /**
     * Create BirdsDaoWrapper, which does saving of data from DAO to disk.
     *
     * @return BirdsDaoWrapper
     */
    @Bean
    public BirdsDaoWrapper getDaoWrapper() {
        return new BirdsDaoWrapper();
    }

    /**
     * Get ReadWriteLock, which will be use to synchronize DAO and DAO wrapper.
     *
     * @return ReadWriteLock
     */
    @Bean
    public ReadWriteLock getReadWriteLock() {
        return new ReentrantReadWriteLock();
    }

    /**
     * Create NioBasedServer
     *
     * @return NioBasedServer
     */
    @Bean
    public NioBasedServer getNioBasedServer() {
        return new NioBasedServer();
    }

    /**
     * Consumer, which will be called, when IOException arise.
     *
     * @return Consumer of SelectionKey
     */
    @Bean
    public Consumer<SelectionKey> getIoErrorConsumer() {
        return (key) -> {
            SelectionKey selectionKey = (SelectionKey) key;
            selectionKey.attach(null);
            try {
                log.info("Try to close client connection during IOException.");
                ((SocketChannel) selectionKey.channel()).close();
                log.info("Closing of connection successfull.");
            } catch (IOException e) {
                log.error("Problem occured during closing client connection. Connection, probably, already unavaliable.", e);
            }
        };
    }

    /**
     * Get ServerConfiguration.
     *
     * @return ServerConfiguration
     */
    @Bean
    public ServerConfiguration getConfiguration() {
        return ServerApplication.getApplicationConfig();
    }
}
