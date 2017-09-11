/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.server;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * Component for reading command line arguments, and create Configuration from
 * them.
 *
 * @author alitvinov
 * @see ServerConfiguration
 */
public class ConfigurationCreator {

    /**
     * Min port range
     */
    private final int minValidTcpPortRange = 1;
    /**
     * Max port range
     */
    private final int maxValidTcpPortRange = 65535;
    /**
     * Avaliable command line arguments.
     */
    private final List<String> avaliableArguments = Arrays.asList("-port", "-data", "-proc_count");
    /**
     * Default server port.
     */
    private final static int DEFAULT_SERVER_PORT = 3000;
    /**
     * Default server host.
     */
    private final static String DEFAULT_SERVER_HOST = "localhost";
    /**
     * Default proc count.
     */
    private final static int DEFAULT_PROC_COUNT = 2;
    /**
     * Default server directory
     */
    private final static String DEFAULT_DIRECTORY = System.getProperty("user.home") + File.separator + "serverdata";

    /**
     * Container with server configuration.
     */
    @ToString
    @Getter
    public final static class ServerConfiguration {

        private int port = DEFAULT_SERVER_PORT;
        private String dataDirectory = DEFAULT_DIRECTORY;
        private int procCount = DEFAULT_PROC_COUNT;
        private final String host = DEFAULT_SERVER_HOST;
    }

    /**
     * Parse argument list.
     *
     * @param args argument list.
     * @return ServerConfiguration.
     */
    public ServerConfiguration parseArgumentList(String args[]) {
        String currentOption = null;
        ServerConfiguration configuration = new ServerConfiguration();

        for (String commandLineArgument : args) {
            if (avaliableArguments.contains(commandLineArgument)) {
                currentOption = commandLineArgument;
                continue;
            }
            if (currentOption != null) {
                setOptionToConfiguration(currentOption, commandLineArgument, configuration);
                currentOption = null;
            }
        }
        return configuration;
    }

    /**
     * Setting value of command line argument to configuration.
     *
     * @param currentOption current command line argument.
     * @param optionValue argument value.
     * @param configuration configuration.
     */
    private void setOptionToConfiguration(String currentOption, String optionValue, ServerConfiguration configuration) {
        switch (currentOption) {
            case "-port":
                configuration.port = Integer.parseInt(optionValue);
                if (configuration.port < minValidTcpPortRange || configuration.port > maxValidTcpPortRange) {
                    throw new NumberFormatException("Port range invalid.");
                }
                break;
            case "-data":
                configuration.dataDirectory = optionValue;
                break;
            case "-proc_count":
                configuration.procCount = Integer.parseInt(optionValue);
                if (configuration.procCount < 0) {
                    throw new NumberFormatException("Invalid value of proc_count parameter.");
                }
                break;
        }
    }
}
