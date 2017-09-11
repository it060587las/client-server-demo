package com.dataart.edu.message.dto.request;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO fro add bird request.
 *
 * @author alitvinov
 */
@Data
@NoArgsConstructor
public class AddBirdRequestDto extends BaseClientRequestDto {

    private String color;

    private double weight;

    private double height;

    /**
     * Create AddBirdRequestDto.
     *
     * @param command command
     * @param name name of bird
     * @param color color of bird
     * @param weight weight
     * @param height height
     */
    public AddBirdRequestDto(ClientAction command, String name, String color, double weight, double height) {
        super(command, name);
        this.color = color;
        this.weight = weight;
        this.height = height;
    }

    /**
     * Serializer for AddBirdRequestDto.
     */
    public final static class AddBirdRequestDtoSerializer extends Serializer<AddBirdRequestDto> {

        /**
         * Write AddBirdRequestDto to KRYO.
         *
         * @param kryo KRYO object
         * @param output output
         * @param object AddBirdRequestDto object
         */
        @Override
        public void write(Kryo kryo, Output output, AddBirdRequestDto object) {
            output.writeString(object.getCommand().name());
            output.writeString(object.getName());
            output.writeString(object.getColor());
            output.writeDouble(object.getWeight());
            output.writeDouble(object.getHeight());
        }

        /**
         * Read AddBirdRequestDto from KRYO.
         *
         * @param kryo KRYO object
         * @param input KRYO input
         * @param type AddBirdRequestDto class
         * @return AddBirdRequestDto object.
         */
        @Override
        public AddBirdRequestDto read(Kryo kryo, Input input, Class<AddBirdRequestDto> type) {
            String action = input.readString();
            String name = input.readString();
            String color = input.readString();
            double weight = input.readDouble();
            double height = input.readDouble();
            return new AddBirdRequestDto(ClientAction.valueOf(action), name, color, weight, height);
        }
    }
}
