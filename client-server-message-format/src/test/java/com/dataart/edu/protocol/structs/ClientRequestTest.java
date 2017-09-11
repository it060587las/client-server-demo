/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.protocol.structs;

import com.dataart.edu.message.dto.request.ClientAction;
import com.dataart.edu.message.dto.BaseMessageDto;
import com.dataart.edu.message.dto.request.AddBirdRequestDto;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Check serialization and deserialization of ClientRequest/
 *
 * @see BaseClientRequestDto
 * @author alitvinov
 */
public class ClientRequestTest {

    public ClientRequestTest() {
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
     *
     */
    @Test
    public void testSerializeAndDeserialize() {
        AddBirdRequestDto request = new AddBirdRequestDto(ClientAction.ADD, "some name", "some color", 1.0, 2.0);        
        byte[] requestAsBytes = BaseMessageDto.serialize(request);
        BaseClientRequestDto baseRequest=BaseMessageDto.deserialize(Arrays.copyOfRange(requestAsBytes, Integer.BYTES, requestAsBytes.length), BaseClientRequestDto.class);    
        assertTrue(baseRequest.getCommand()==ClientAction.ADD);
        AddBirdRequestDto expected = BaseMessageDto.deserialize(Arrays.copyOfRange(requestAsBytes, Integer.BYTES, requestAsBytes.length), AddBirdRequestDto.class);                
        assertTrue(request.getName().equals(expected.getName()));
        assertTrue(request.getColor().equals(expected.getColor()));        
    }        
}
