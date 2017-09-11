package com.dataart.edu.client;

import com.dataart.edu.message.dto.BirdDto;
import com.dataart.edu.message.dto.BirdSightDto;
import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import static com.dataart.edu.message.dto.request.ClientAction.ADD_SIGHT;
import static com.dataart.edu.message.dto.request.ClientAction.LIST_SIGHTS;
import static com.dataart.edu.message.dto.request.ClientAction.QUIT;
import com.dataart.edu.message.format.BinaryMessageReader;
import com.dataart.edu.message.dto.response.ServerResponseDto;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Client class.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@RequiredArgsConstructor
@Slf4j
public class Client implements Closeable {

    /**
     * Date formatter to format results.
     */
    private final DateTimeFormatter resultDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Client port, on which it will try to connect.
     */
    private final int port;
    /**
     * Host, on which client will try to connect
     */
    private final String host;
    /**
     * Default size of buffer, in which data will be read.
     */
    private final static int DEFAULT_CLIENT_BUFFER_SIZE = 256;
    /**
     * Client socket channel.
     */
    private SocketChannel socketClientChannel;

    /**
     * Try to connect to server.
     *
     * @throws IOException if connection fails.
     */
    public void connectToServer() throws IOException {
        socketClientChannel = SocketChannel.open();
        socketClientChannel.connect(new InetSocketAddress(host, port));
    }

    /**
     * Execute specified command.
     *
     * @param command command to send to server.
     * @return ServerResponse response of server
     * @throws IOException
     * @see ServerResponseDto
     */
    public ServerResponseDto execute(BaseClientRequestDto command) throws IOException {
        socketClientChannel.write(ByteBuffer.wrap(BaseMessageDto.serialize(command)));
        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_CLIENT_BUFFER_SIZE);
        int numberBytesRead;        
        BinaryMessageReader messageReadConveyor = new BinaryMessageReader(null);
        byte[] serverResponseAsBytes=null;
        while (serverResponseAsBytes == null && (numberBytesRead = socketClientChannel.read(buffer)) > 0) {
            buffer.flip();
            serverResponseAsBytes = messageReadConveyor.readMessageFromByteBuffer(buffer, numberBytesRead);
            buffer.clear();
        }
        ServerResponseDto serverResponse=BaseMessageDto.deserialize(serverResponseAsBytes, ServerResponseDto.class);
        if (serverResponse != null && serverResponse.isSuccess()) {
            displayResults(serverResponse, command);
        } else if (serverResponse != null) {
            log.info("Server answers with error: {}", serverResponse.getError());
        }
        return serverResponse;
    }

    /**
     * Display results of operation.
     *
     * @param serverResponse
     * @param command
     */
    private void displayResults(ServerResponseDto serverResponse, BaseClientRequestDto command) {
        switch (command.getCommand()) {
            case LIST:
                log.info("Number of found objects: {} object(s)", serverResponse.getResultData().size());
                serverResponse.getResultData().forEach((item) -> {
                    BirdDto birdDto = (BirdDto) item;
                    log.info("name={} color={} height={} width={}", birdDto.getName(), birdDto.getColor(), birdDto.getHeight(), birdDto.getWeight());
                });
                break;
            case ADD:
                log.info("Bird {} added successfully.", command.getName());
                break;
            case LIST_SIGHTS:
                log.info("Number of found objects: {} object(s)", serverResponse.getResultData().size());
                serverResponse.getResultData().forEach((item) -> {
                    BirdSightDto sightDto = (BirdSightDto) item;
                    log.info("name={} location={} date={}", sightDto.getName(), sightDto.getLocation(), resultDateFormatter.print((long) sightDto.getStart()));
                });
                break;
            case REMOVE:
                log.info("Bird {} successfully removed.", command.getName());
                break;
            case ADD_SIGHT:
                log.info("Sighting successfully added to bird {}", command.getName());
                break;
            case QUIT:
                log.info("Server successfully stoped");
        }
    }

    /**
     * Properly closing of SocketChannel
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (this.socketClientChannel != null) {
            this.socketClientChannel.close();
        }
    }

}
