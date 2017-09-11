package com.dataart.edu.server;

import com.dataart.edu.server.ConfigurationCreator.ServerConfiguration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * NIO based server.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@Slf4j
public class NioBasedServer {
    @Autowired
    private ServerConfiguration config;
    @Autowired
    private SocketChannelsProcessor channelsProcessor;
    @Autowired
    private ApplicationContext appContext;

    /**
     * Starting of server.
     *
     * @throws IOException if some error during start arise.
     */
    public void run() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress(this.config.getHost(), this.config.getPort());
        serverSocket.bind(hostAddress);
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT, null);
        channelsProcessor.processRegisteredChannels(selector, serverSocket);
    }
    /**
     * Stop server.
     * @throws IOException 
     */
    public void stop()throws IOException{
        ServerMessageProcessor processor=appContext.getBean(ServerMessageProcessor.class);
        processor.stop();                
    }
}
