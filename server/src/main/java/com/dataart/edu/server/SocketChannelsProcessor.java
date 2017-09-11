package com.dataart.edu.server;

import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import com.dataart.edu.message.format.BinaryMessageReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Processing of channels, which was registered with Selector.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 * @see Selector
 */
@Slf4j
public class SocketChannelsProcessor {

    /**
     * Default timeout for Selector select method.
     */
    private final static int SELECTOR_TIMEOUT = 5000;
    /**
     * Default buffer size for reading messages.
     */
    private final static int BUFFER_DEFAULT_SIZE = 256;
    /**
     * Processor of incoming messages.
     */
    @Autowired
    private ServerMessageProcessor messageProcessor;
    /**
     * Need selector process it's work or it should be stopped.
     */
    private final AtomicBoolean continueProcessing = new AtomicBoolean(true);
    /**
     * Consumer, which will accept IOException, if error during reading data
     * arise.
     */
    @Autowired
    private Consumer<SelectionKey> readErrorConsumer;

    /**
     * Processing of channels, registered with Selector.
     *
     * @param selector selector instance.
     * @param serverSocket server socket.
     * @throws IOException if error in read or write arise.
     */
    public void processRegisteredChannels(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        log.info("Starting dao background savers.");
        log.info("DAO background savers started.");
        SelectionKey keyOfChannelWithAvaliableData;
        while (continueProcessing.get()) {
            try {
                selector.select(SELECTOR_TIMEOUT);
            } catch (IllegalArgumentException timeoutException) {
                //it is not error, it used to periodically check,
                //need selector be terminated or not.              
                log.debug("Selector timeout occured.");
                continue;
            }
            Iterator<SelectionKey> chanelsWithAvaliableData = selector.selectedKeys().iterator();
            while (chanelsWithAvaliableData.hasNext()) {
                keyOfChannelWithAvaliableData = chanelsWithAvaliableData.next();
                if (keyOfChannelWithAvaliableData.isAcceptable()) {
                    processConnectNewClient(serverSocket, selector);
                    log.info("New client connected to server.");
                } else if (keyOfChannelWithAvaliableData.isReadable()) {
                    processReadClientChannel(keyOfChannelWithAvaliableData);
                }
                chanelsWithAvaliableData.remove();
            }

        }
        log.info("Channel processor successfully stoped.");
        log.info("Try close server socket.");
        if (serverSocket != null) {
            serverSocket.close();
        }
        log.info("Server socket closed.");
    }

    /**
     * New client is connected.
     *
     * @param serverSocket server socket
     * @param channelSelector selector
     * @throws IOException if exception during connect arise.
     */
    private void processConnectNewClient(ServerSocketChannel serverSocket, Selector channelSelector) throws IOException {
        SocketChannel clientChannel;
        SelectionKey clientSelectionKey;
        clientChannel = serverSocket.accept();
        clientChannel.configureBlocking(false);
        clientSelectionKey = clientChannel.register(channelSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        clientSelectionKey.attach(new BinaryMessageReader(clientSelectionKey));
    }

    /**
     * reading from channel
     *
     * @param clientKey key of client
     */
    private void processReadClientChannel(SelectionKey clientKey) {
        SocketChannel clientChannel;
        BinaryMessageReader binaryReader;
        ByteBuffer buffer;
        int numberOfBytesRead;
        clientChannel = (SocketChannel) clientKey.channel();
        binaryReader = (BinaryMessageReader) clientKey.attachment();
        try {
            buffer = ByteBuffer.allocate(BUFFER_DEFAULT_SIZE);
            while ((numberOfBytesRead = clientChannel.read(buffer)) > 0) {
                buffer.flip();
                byte[] message = binaryReader.readMessageFromByteBuffer(buffer, numberOfBytesRead);
                if (message != null) {
                    messageProcessor.processMessage(BaseMessageDto.deserialize(message, BaseClientRequestDto.class), binaryReader.getSelectionKey(), message);
                }
                buffer.clear();
            }
            if (numberOfBytesRead < 0) {
                clientKey.channel().close();
            }
        } catch (IOException e) {
            log.info("IOException occures: {}, connection will be closed.", e.getMessage());
            readErrorConsumer.accept(clientKey);
        }
    }

    /**
     * Stopping of processor.
     */
    public void stop() {
        log.info("Stoping server.....");
        continueProcessing.compareAndSet(true, false);
    }        
}
