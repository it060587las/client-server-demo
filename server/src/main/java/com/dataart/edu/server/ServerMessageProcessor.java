package com.dataart.edu.server;

import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.BirdDto;
import com.dataart.edu.message.dto.BirdSightDto;
import com.dataart.edu.message.dto.request.AddBirdRequestDto;
import com.dataart.edu.server.dao.IBirdsDao;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import static com.dataart.edu.message.dto.request.ClientAction.ADD;
import static com.dataart.edu.message.dto.request.ClientAction.ADD_SIGHT;
import static com.dataart.edu.message.dto.request.ClientAction.LIST;
import static com.dataart.edu.message.dto.request.ClientAction.LIST_SIGHTS;
import static com.dataart.edu.message.dto.request.ClientAction.QUIT;
import static com.dataart.edu.message.dto.request.ClientAction.REMOVE;
import com.dataart.edu.message.dto.request.SightingRequestDto;
import com.dataart.edu.message.dto.response.ServerResponseDto;
import com.dataart.edu.server.ConfigurationCreator.ServerConfiguration;
import com.dataart.edu.server.dao.BirdsDaoWrapper;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Processing of incoming messages on server.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@Slf4j
public class ServerMessageProcessor {

    /**
     * Default period to wait executor termination.
     */
    public final static int WAIT_TERMINATION_PERIOD_SECONDS = 10;
    
    private ExecutorService procCountExecutor;
    @Autowired
    private Consumer<SelectionKey> writeErrorConsumer;
    @Autowired
    private IBirdsDao daoComponent;
    @Autowired
    private SocketChannelsProcessor socketChannelProcessor;
    @Autowired
    private ServerConfiguration config;
    @Autowired
    private BirdsDaoWrapper daoWrapper;

    /**
     * Create executor with proc_count size after initialization of all
     * properties.
     */
    @PostConstruct
    private void initExecutor() {
        procCountExecutor = Executors.newFixedThreadPool(config.getProcCount());
        log.info("Starting FixedThreadPool with size {} successfull", config.getProcCount());
    }

    /**
     * Processing incoming message.
     *
     * @param clientCommand command from client.
     * @param selectionKey key of SocketChannel
     * @param sourceMessageBody message body in bytes
     * @see BaseClientRequestDto
     * @see SelectionKey
     */
    public void processMessage(final BaseClientRequestDto clientCommand, final SelectionKey selectionKey, byte[] sourceMessageBody) {
        procCountExecutor.submit(() -> {
            try {
                this.processMessageNoConcurrent(clientCommand, selectionKey, sourceMessageBody);
            } catch (IOException e) {
                log.error("IOException during sending message to client. Connection will be closed.", e);
                writeErrorConsumer.accept(selectionKey);
            }
        });
    }

    /**
     * Processing of command from client.
     *
     * @param clientRequest command from client.
     * @param selectionKey key of SocketChannel
     * @throws IOException if, for example, client terminated connection.
     * @see BaseClientRequestDto
     * @see SelectionKey
     */
    private void processMessageNoConcurrent(final BaseClientRequestDto clientRequest, SelectionKey selectionKey, byte messageAsBytes[]) throws IOException {
        ServerResponseDto serverAnswer = new ServerResponseDto();
        try {
            switch (clientRequest.getCommand()) {
                case ADD:
                    AddBirdRequestDto addCommand = BaseMessageDto.deserialize(messageAsBytes, AddBirdRequestDto.class);
                    daoComponent.addBird(new BirdDto(clientRequest.getName(), addCommand.getColor(), addCommand.getHeight(), addCommand.getWeight()));
                    break;
                case ADD_SIGHT:
                    SightingRequestDto addSightingCommand = BaseMessageDto.deserialize(messageAsBytes, SightingRequestDto.class);
                    daoComponent.addSight(new BirdSightDto(clientRequest.getName(), addSightingCommand.getLocation(), addSightingCommand.getStart()));
                    break;
                case LIST:
                    List<BirdDto> resultList = daoComponent.findAllBirds();
                    serverAnswer.setResultData(resultList);
                    break;
                case LIST_SIGHTS:
                    SightingRequestDto listSightingCommand = BaseMessageDto.deserialize(messageAsBytes, SightingRequestDto.class);
                    List<BirdSightDto> resultSet = daoComponent.findSight(new BirdSightDto(clientRequest.getName(), listSightingCommand.getLocation(), listSightingCommand.getStart(), listSightingCommand.getEnd()));
                    serverAnswer.setResultData(resultSet);
                    break;
                case REMOVE:
                    daoComponent.removeBird(clientRequest.getName());
                    break;
                case QUIT:
                    log.info("Recived command QUITE. Server will be stoped.");
                    answerWithMessageToClient(serverAnswer, selectionKey, true);
                    return;
                default:
                    serverAnswer.setSuccess(false);
                    serverAnswer.setError("Unknown command.");
            }
        } catch (IllegalArgumentException e) {
            log.info("Logic exception during request execution:{}.", e.getMessage());
            serverAnswer.setSuccess(false);
            serverAnswer.setError(e.getMessage());
        }
        answerWithMessageToClient(serverAnswer, selectionKey, false);
    }

    /**
     * Answer to client.
     *
     * @param serverAnswer answer which will be send to client.
     * @param selectionKey key of client channel.
     * @param withServerStop if true - this means that QUITE must be processed.
     * @throws IOException if, for example, client terminated connection.
     * @see ServerResponseDto
     * @see SelectionKey
     */
    private void answerWithMessageToClient(ServerResponseDto serverAnswer, SelectionKey selectionKey, boolean withServerStop) throws IOException {
        ((SocketChannel) selectionKey.channel()).write(ByteBuffer.wrap(BaseMessageDto.serialize(serverAnswer)));
        if (withServerStop) {
            stop();
        }
    }

    /**
     * Stop all components.
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        socketChannelProcessor.stop();
        log.info("Try stoping DAO component");
        boolean daoStopped = daoWrapper.stopPeriodicSaveToDisk();
        log.info("DAO stopped {}", daoStopped);
        log.info("Try stoping executor");
        procCountExecutor.shutdownNow();
        boolean procCountExecutorStoped;
        try {
            procCountExecutorStoped
                    = procCountExecutor.awaitTermination(WAIT_TERMINATION_PERIOD_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            procCountExecutorStoped = false;
            log.info("Failed stop executor. {}", ie.getMessage());
        }
        log.info("Executor stopped {}", procCountExecutorStoped);
    }
}
