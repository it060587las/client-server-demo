/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.client;

import com.dataart.edu.client.util.FunctionalUtil;
import com.dataart.edu.message.dto.request.AddBirdRequestDto;
import com.dataart.edu.message.dto.request.ClientAction;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import com.dataart.edu.message.dto.request.SightingRequestDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

/**
 * Create command to appropriate command line argument.
 * 
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-06
 */
@Slf4j
public class CommandCreator {
    
    private final Properties messages = new Properties();
    
    private void initMessages() throws IOException {
        this.messages.load(ClientApplication.class.getClassLoader().getResourceAsStream("messages.properties"));
    }
    
    /**
     * Create CommandCreator.
     * @throws IOException if problem during reading of messages arise.
     */
    public CommandCreator() throws IOException{
        this.initMessages();
    }
    
    /**
     * Prepare command to send to server base on command line arguments.
     *
     * @param commandLineParameters list of command line arguments.
     * @return ClientRequest or null if command is unknown.
     */
    public BaseClientRequestDto prepareCommandToSendOnServer(List<String> commandLineParameters) {
        if (commandLineParameters.contains("-addbird")) {
            return prepareAddBirdCommand();
        } else if (commandLineParameters.contains("-addsighting")) {
            return prepareAddSightingCommand(false);
        } else if (commandLineParameters.contains("-listsightings")) {
            return prepareAddSightingCommand(true);
        } else if (commandLineParameters.contains("-listbirds")) {
            return new BaseClientRequestDto(ClientAction.LIST);
        } else if (commandLineParameters.contains("-remove")) {
            return prepareRemoveBirdCommand();
        } else if (commandLineParameters.contains("-quit")) {
            return new BaseClientRequestDto(ClientAction.QUIT);
        }
        return null;
    }

    private BaseClientRequestDto prepareAddBirdCommand() {
        String name = readDatafromCommandLine(
                messages.getProperty("enter.bird.name"), 
                FunctionalUtil.CHECK_STRING_PREDICATE, 
                FunctionalUtil.STRING_ERR_CONSUMER, 
                FunctionalUtil.STRING_FUNCTION);
        String color = readDatafromCommandLine(
                messages.getProperty("enter.bird.color"), 
                FunctionalUtil.CHECK_STRING_PREDICATE, 
                FunctionalUtil.STRING_ERR_CONSUMER, 
                FunctionalUtil.STRING_FUNCTION);
        double weight = readDatafromCommandLine(
                messages.getProperty("enter.bird.weight"), 
                FunctionalUtil.CHECK_DOUBLE_PREDICATE, 
                FunctionalUtil.DOUBLE_ERR_CONSUMER, 
                FunctionalUtil.DOUBLE_FUNCTION);
        double height = readDatafromCommandLine(
                messages.getProperty("enter.bird.height"), 
                FunctionalUtil.CHECK_DOUBLE_PREDICATE, 
                FunctionalUtil.DOUBLE_ERR_CONSUMER, 
                FunctionalUtil.DOUBLE_FUNCTION);
        AddBirdRequestDto req = new AddBirdRequestDto(ClientAction.ADD, name, color, weight, height);
        return req;
    }

    private BaseClientRequestDto prepareAddSightingCommand(boolean isListSighting) {        
        String name = readDatafromCommandLine(
                messages.getProperty("enter.bird.name"), 
                FunctionalUtil.CHECK_STRING_PREDICATE, 
                FunctionalUtil.STRING_ERR_CONSUMER, 
                FunctionalUtil.STRING_FUNCTION);
        String location =null;
        DateTime startdate=null;
        DateTime enddate=null;
        if (!isListSighting) {
            location = readDatafromCommandLine(
                    messages.getProperty("enter.sighting.location"), 
                    FunctionalUtil.CHECK_STRING_PREDICATE, 
                    FunctionalUtil.STRING_ERR_CONSUMER, 
                    FunctionalUtil.STRING_FUNCTION);            
        }
        String message = isListSighting ?
                messages.getProperty("enter.sighting.startdate") : 
                messages.getProperty("enter.sighting.date");
        startdate = readDatafromCommandLine(message, 
                FunctionalUtil.CHECK_DATE_PREDICATE, 
                FunctionalUtil.DATE_ERR_CONSUMER, 
                FunctionalUtil.DATE_FUNCTION);
        
        if (isListSighting) {
           enddate = readDatafromCommandLine(
                    messages.getProperty("enter.sighting.enddate"), 
                    FunctionalUtil.CHECK_DATE_PREDICATE, 
                    FunctionalUtil.DATE_ERR_CONSUMER, 
                    FunctionalUtil.DATE_FUNCTION);
        }
        if (isListSighting){
            return new SightingRequestDto(ClientAction.LIST_SIGHTS, name, location, startdate.getMillis(), enddate.getMillis());
        } else {
            return new SightingRequestDto(ClientAction.ADD_SIGHT, name, location, startdate.getMillis());
        }        
    }

    private BaseClientRequestDto prepareRemoveBirdCommand() {
        BaseClientRequestDto req = new BaseClientRequestDto(ClientAction.REMOVE);
        String name = readDatafromCommandLine(
                messages.getProperty("enter.bird.name"), 
                FunctionalUtil.CHECK_STRING_PREDICATE, 
                FunctionalUtil.STRING_ERR_CONSUMER, 
                FunctionalUtil.STRING_FUNCTION);
        req.setName(name);
        return req;
    }

    private  <T> T readDatafromCommandLine(
            String message, 
            Predicate<String> checkStringPredicate, 
            Consumer<String> incorrectFormatAcceptor, 
            Function<? super String, T> stringToTypeConverter) {
        log.info(message);
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8.name());
        String readeFromCommandLine;
        while (!checkStringPredicate.test(readeFromCommandLine = sc.nextLine())) {
            incorrectFormatAcceptor.accept(readeFromCommandLine);
        }
        return stringToTypeConverter.apply(readeFromCommandLine);
    }
    
}
