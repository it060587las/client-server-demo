/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.protocol.structs;

import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import com.dataart.edu.message.dto.request.ClientAction;
import com.dataart.edu.message.format.BinaryMessageReader;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Check reading of message using MessageContainter.
 *
 * @see MessageContainter.
 * @author alitvinov
 */
public class MessageContainterTest {

    public MessageContainterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Try emulate process of partial reading bytes from SocketChanel, and
     * construction of message from bytes.
     */
    @Test
    public void testReadBytes() {
        BaseClientRequestDto request = new BaseClientRequestDto(ClientAction.ADD, "test");
        byte[] objectAsBytes = BaseMessageDto.serialize(request);
        int bufferSize = 7, counter = 0;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        BinaryMessageReader conveyor = new BinaryMessageReader(null);        
        //try to emulate partial read from channel into buffer
        //with different real number of bytes read
        byte [] expectedAsBytes=null;
        while (counter < objectAsBytes.length) {
            int randowpartToRead = ThreadLocalRandom.current().nextInt(0, bufferSize + 1);
            System.out.println("Random portion of data " + randowpartToRead);
            int numberofelementleft = objectAsBytes.length - counter < randowpartToRead ? objectAsBytes.length - counter : randowpartToRead;
            buffer.put(objectAsBytes, counter, numberofelementleft);
            int bposition = buffer.position();;
            buffer.flip();
            expectedAsBytes=conveyor.readMessageFromByteBuffer(buffer, numberofelementleft);            
            counter += bposition;
            buffer.clear();
        }
        Assert.assertTrue(expectedAsBytes != null);
        BaseClientRequestDto expected=BaseMessageDto.deserialize(expectedAsBytes, BaseClientRequestDto.class);
        Assert.assertEquals(expected.getName(), request.getName());        
    }
}
