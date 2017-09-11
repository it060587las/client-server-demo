package com.dataart.edu.message.dto;

import com.dataart.edu.message.format.util.KryoUtil;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Base class for messages, which are used to exchange between client and server
 * program.
 * <p>
 * In communication between server and client, binary format is used. It is used
 * for saving resources, it achieved by:
 * <ul>
 * <li> Using Kryo Java library - it is fast and resource effective
 * <li> No additional deserialization need (to JSON or XML)
 * </ul>
 *
 * @see Information about
 * <a href="https://github.com/EsotericSoftware/kryo">Kryo project</a>.
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-07
 */
public class BaseMessageDto {

    /**
     * Transforming of message to byte array using Kryo, with leading 4 bytes
     * with message length.
     *
     * @param message message which must be serialized.
     * @return byte[] - 4 bytes with length + <b>this</b> converted to byte
     * array.
     */
    public static byte[] serialize(BaseMessageDto message) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (Output output = new Output(bos);) {
            KryoUtil.getKryoForThread().writeObject(output, message);
        }
        ByteBuffer messageByteBuffer = ByteBuffer.allocate(Integer.BYTES + bos.toByteArray().length);
        messageByteBuffer.putInt(bos.toByteArray().length);
        messageByteBuffer.put(bos.toByteArray());
        return messageByteBuffer.array();
    }

    /**
     * Read message of specific type from byte array.
     *
     * @param <T> generic type of, message, that must be read. Must be
     * descendant of BaseMessage.
     * @param messageAsBytes message in byte array representation.
     * @param objectType Class of message, that must be read.
     *
     * @return deserialized message with type T.
     */
    public static <T extends BaseMessageDto> T deserialize(byte[] messageAsBytes, Class<T> objectType) {
        ByteArrayInputStream bis = new ByteArrayInputStream(messageAsBytes);
        try (Input in = new Input(bis);) {
            return KryoUtil.getKryoForThread().readObject(in, objectType);
        }
    }

}
