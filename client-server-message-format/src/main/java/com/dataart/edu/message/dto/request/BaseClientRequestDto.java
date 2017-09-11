package com.dataart.edu.message.dto.request;

import com.dataart.edu.message.dto.BaseMessageDto;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for request, which will be send from client program to server.
 *
 * @see BaseMessageDto
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-07
 */
@Data
@NoArgsConstructor
public class BaseClientRequestDto extends BaseMessageDto {

    private ClientAction command;

    private String name;

    /**
     * Create instance of client request.
     * 
     * @param command command which must be executed
     */
    public BaseClientRequestDto(ClientAction command) {
        this.command = command;
    }

    /**
     * Create instance of client request.
     * @param command
     * @param name
     */
    public BaseClientRequestDto(ClientAction command, String name) {
        this.command = command;
        this.name = name;
    }               
    
    /**
     * Serializer for BaseClientRequestDto.
     */
    public final static class BaseClientRequestDtoSerializer extends Serializer<BaseClientRequestDto>{

        /**
         * Write BaseClientRequestDto to KRYO.
         * @param kryo KRYO
         * @param output output.
         * @param object BaseClientRequestDto object.
         */
        @Override
        public void write(Kryo kryo, Output output, BaseClientRequestDto object) {
            output.writeString(object.getCommand().name());
            output.writeString(object.getName());
        }

        /**
         * Read BaseClientRequestDto from KRYO.
         * @param kryo KRYO.
         * @param input input
         * @param type BaseClientRequestDto class
         * @return BaseClientRequestDto object
         */
        @Override
        public BaseClientRequestDto read(Kryo kryo, Input input, Class<BaseClientRequestDto> type) {
            String action=input.readString();
            String name=input.readString();            
            return new BaseClientRequestDto(ClientAction.valueOf(action), name);
        }        
    }
}
