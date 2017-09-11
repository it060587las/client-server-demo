package com.dataart.edu.message.format;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to simplify reading of BaseMessage from ByteBuffer.
 *
 * <p>
 * Object of this class is used, to consistently read portions of bytes from
 * ByteBuffer, and when message is fully read, return this message. After, next
 * message can be read.
 *
 * @see BaseStruct
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-07
 */
@RequiredArgsConstructor
@Slf4j
public class BinaryMessageReader {
    /**
     * Indicates, that length of current message is unknown.
     */
    private final static int LENGTH_OF_MESSAGE_IS_UNKNOWN = -1;
    /**
     * Buffer, which will store length of message.
     */
    private final ByteBuffer lengthByteBuffer = ByteBuffer.allocate(Integer.BYTES);
    /**
     * Buffer which will store bytes of message.
     */
    private ByteBuffer bodyByteBuffer = null;
    /**
     * Key of channel, with which this object is associated.
     */
    @Getter
    private final SelectionKey selectionKey;

    /**
     * Consistently read data from buffer.
     *     
     * @param channelByteBuffer buffer, from which data will be read.
     * <b>Important:</b>
     * buffer must be avaliable for reading - method flip() must be called.
     * @param realNumberBytesRead real number of bytes, witch was read from
     * Channel to ByteBuffer.     
     *
     * @return null - if message is not fully read, message as byte array if
     * message is fully read.
     *
     * @see BaseStruct
     * @see ByteBuffer
     * @see SocketChannel
     * @see SelectionKey
     */
    public byte[] readMessageFromByteBuffer(
            ByteBuffer channelByteBuffer,
            int realNumberBytesRead) {
        int startOfDataPosition = 0;
        if (!this.isLengthDefined()) {            
            startOfDataPosition = this.readBytesToLengthByteBuffer(channelByteBuffer.array(), realNumberBytesRead);            
        }
        if (this.isLengthDefined() && startOfDataPosition < realNumberBytesRead) {            
            if (this.readBytesToBody(channelByteBuffer, startOfDataPosition, realNumberBytesRead)) {                
                return this.getMessageBody();
            } else {                
                return null;
            }
        }
        return null;
    }

    /**
     * Is length of message known.
     *
     * @return true if all 4 bytes of length was read.
     */
    private boolean isLengthDefined() {
        return lengthByteBuffer.limit() == lengthByteBuffer.position();
    }

    /**
     * Allocate message body ByteBuffer with length from message length buffer.
     *
     */
    private void defineLengthAndInitBodyBuffer() {
        lengthByteBuffer.flip();
        bodyByteBuffer = ByteBuffer.allocate(lengthByteBuffer.getInt());
    }

    /**
     * Try to read portion of bytes to message length ByteBuffer.
     *
     * @param bytesFromChannelByteBuffer array of bytes, which was read from
     * channel.
     * @param realNumberBytesRead real number of bytes, which was read from
     * channel.
     * <p>
     * To get byte bytesFromByteBuffer from ByteBuffer method array() is used.
     * It returns array with length of ByteBuffer limit(). In order to read only
     * "fresh" bytes numberBytesRead must be provided. It must be less or equal
     * then ByteBuffer limit.
     *
     * @return -1 if length is not fully read, otherwise start of message bytes
     * in bytesFromByteBuffer array.
     *
     * @see ByteBuffer
     */
    private int readBytesToLengthByteBuffer(byte[] bytesFromChannelByteBuffer,
            int realNumberBytesRead) {
        int currentPosition = 0;
        for (byte bt : bytesFromChannelByteBuffer) {
            currentPosition++;
            if (currentPosition > realNumberBytesRead) {
                break;
            }
            lengthByteBuffer.put(bt);
            if (isLengthDefined()) {
                this.defineLengthAndInitBodyBuffer();
                return currentPosition;
            }
        }
        return LENGTH_OF_MESSAGE_IS_UNKNOWN;
    }

    /**
     * Read portion of bytes to message body ByteBuffer.
     *
     * @param channelByteBuffer ByeBuffer which was read from channel.
     * @param from start position of message bytes.
     * @param to length of portion, that must be read.
     *
     * @return true - if message fully read, false - read must be continued.
     *
     * @see ByteBuffer
     */
    private boolean readBytesToBody(ByteBuffer channelByteBuffer, int from, int to) {
        this.bodyByteBuffer.put(channelByteBuffer.array(), from, to - from);
        return this.bodyByteBuffer.limit() == this.bodyByteBuffer.position();
    }

    /**
     * Get bytes of message body.
     *
     * @return message body as byte[].
     */
    private byte[] getMessageBody() {
        try {
            return this.bodyByteBuffer != null && bodyByteBuffer.limit() == this.bodyByteBuffer.position() ? this.bodyByteBuffer.array() : null;
        } finally {
            cleanup();
        }
    }

    /**
     * Cleanup resources before next message will be processed.
     */
    private void cleanup() {
        this.bodyByteBuffer.clear();
        this.bodyByteBuffer = null;
        this.lengthByteBuffer.clear();
    }
}
