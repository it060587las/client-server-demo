/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.protocol.structs;

import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.response.ServerResponseDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author alitvinov
 */
public class ServerResponseTest {

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

    @Test
    public void testServerResponseSerializeAndDeserialize() {
        ServerResponseDto serverAnswer = new ServerResponseDto();
        serverAnswer.setError("test");
        List<Object[]> l = new ArrayList();
        l.add(new Object[]{"2222", "2222"});
        serverAnswer.setResultData(l);
        byte[] requestAsBytes = BaseMessageDto.serialize(serverAnswer);
        ServerResponseDto expected = BaseMessageDto.deserialize(Arrays.copyOfRange(requestAsBytes, Integer.BYTES, requestAsBytes.length), ServerResponseDto.class);
        assertEquals(expected.getError(), serverAnswer.getError());
        assertEquals(expected.getResultData().size(), serverAnswer.getResultData().size());
    }

}
