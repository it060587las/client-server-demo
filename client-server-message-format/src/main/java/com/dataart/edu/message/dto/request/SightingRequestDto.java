package com.dataart.edu.message.dto.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author alitvinov
 */
@Data
@NoArgsConstructor
public class SightingRequestDto extends BaseClientRequestDto{
   
    private long start;

    private long end;

    private String location;

    /**
     * Create SightingRequestDto.
     * @param command command
     * @param name name
     * @param location location 
     * @param start date of sigthing.
     */
    public SightingRequestDto(ClientAction command, String name,  String location, long start) {
        super(command, name);
        this.start = start;
        this.location = location;
    }
    
    /**
     * Create SightingRequestDto.
     * @param command command
     * @param name name
     * @param location location
     * @param start start of search period 
     * @param end end of search period
     */
    public SightingRequestDto(ClientAction command, String name, String location, long start, long end ) {
        super(command, name);
        this.start = start;
        this.end = end;
        this.location = location;
    }
    
    /**
     * Serializer for SightingRequestDto.
     */
    public final static class SightingRequestDtoSerializer extends Serializer<SightingRequestDto>{
        
        /**
         * Write object to KRYO.
         * @param kryo KRYO
         * @param output output
         * @param object SightingRequestDto object
         */
        @Override
        public void write(Kryo kryo, Output output, SightingRequestDto object) {
            output.writeString(object.getCommand().name());
            output.writeString(object.getName());
            output.writeString(object.getLocation());
            output.writeLong(object.getStart());
            output.writeLong(object.getEnd());            
        }

        /**
         * Write object from KRYO.
         * @param kryo KRYO
         * @param input input 
         * @param type object type 
         * @return SightingRequestDto object
         */
        @Override
        public SightingRequestDto read(Kryo kryo, Input input, Class<SightingRequestDto> type) {
            String action=input.readString();
            String name=input.readString();            
            String location=input.readString();
            long start=input.readLong();
            long end=input.readLong();
            return new SightingRequestDto(ClientAction.valueOf(action), name, location, start, end);
        }        
    }
    
}
