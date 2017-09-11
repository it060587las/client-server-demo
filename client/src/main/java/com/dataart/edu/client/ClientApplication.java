package com.dataart.edu.client;

import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * ClientApplication.
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 * @see Client
 */
@Slf4j
public class ClientApplication {

    private final int minValidTcpPortRange = 1;
    private final int maxValidTcpPortRange = 65535;
    private final int defaultClientPort = 3000;
    private final String defaultHostToConnect = "localhost";
    private final List<String> avaliableArguments = Arrays.asList(
            "-serverPort", 
            "-serverHost", 
            "-addbird", 
            "-addsighting", 
            "-listsightings", 
            "-listbirds", 
            "-remove", 
            "-quit");

    /**
     * Run client application.
     *
     * @param commandLineParameters command line parameters.
     */
    private void runApplication(String[] commandLineParameters) {
        CommandCreator commandCreator;
        try (Client client = createClient(commandLineParameters);) {
            commandCreator = new CommandCreator();
            client.connectToServer();
            BaseClientRequestDto command = commandCreator.prepareCommandToSendOnServer(Arrays.asList(commandLineParameters));
            if (command == null) {
                log.info("Unknown command.\n Please, provide one command from list\n{}\n", Arrays.toString(avaliableArguments.toArray()));
                return;
            }
            client.execute(command);
        } catch (NumberFormatException nfe) {
            log.info("Number Format Exception {}\n", nfe.getMessage());
            log.info("Please, rerun application with correct parameters.");
        } catch (IOException e) {
            log.info("Problem during running of client application: {}.", e.getMessage());
        }
    }

    /**
     * Create client base on command line parameters.
     *
     * @param commandLineParameters command line parameters.
     * @return Client
     * @throws NumberFormatException if port is in incorrect format or range.
     * @see Client
     */
    private Client createClient(String commandLineParameters[]) throws NumberFormatException {
        String currentOption = null, serverHost = null;
        int port = -1;
        Map<String, String> commanArgumentsMap = new HashMap(commandLineParameters.length);
        for (String commandLineArgument : commandLineParameters) {
            if (avaliableArguments.contains(commandLineArgument)) {
                currentOption = commandLineArgument;
                commanArgumentsMap.put(commandLineArgument, commandLineArgument);
                continue;
            }
            if (currentOption != null) {
                switch (currentOption) {
                    case "-serverPort":
                        port = Integer.parseInt(commandLineArgument);
                        if (port < minValidTcpPortRange || port > maxValidTcpPortRange) {
                            throw new NumberFormatException("Incorrect port range.");
                        }
                        break;
                    case "-serverHost":
                        serverHost = commandLineArgument;
                        break;
                    default:
                }
                currentOption = null;
            }
        }
        port = port == -1 ? defaultClientPort : port;
        serverHost = serverHost == null ? defaultHostToConnect : serverHost;
        return new Client(port, serverHost);
    }

    /**
     * Entry point of application.
     *
     * @param args command line arguments.
     */
    public static void main(String args[]) {
        ClientApplication a = new ClientApplication();
        a.runApplication(args);
    }
}
