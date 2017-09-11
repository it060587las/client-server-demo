/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dataart.edu.server;

import com.dataart.edu.client.Client;
import com.dataart.edu.message.dto.request.AddBirdRequestDto;
import com.dataart.edu.message.dto.request.ClientAction;
import com.dataart.edu.message.dto.request.BaseClientRequestDto;
import com.dataart.edu.message.dto.response.ServerResponseDto;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test of collaboration between server and client.
 *
 * @author alitvinov
 *
 */
public class ServerApplicationTest {

    private static final String TEST_PORT = "9999";

    private final static int NUMBER_OF_TEST_CLIENT = 10;

    private static final String TEST_HOST = "localhost";

    public ServerApplicationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException, URISyntaxException {
        String dir = getTestDataDirectory();
        Path birds=Paths.get(dir + File.separator + "birds");
        Path sight=Paths.get(dir + File.separator + "sights");        
        if (Files.exists(Paths.get(dir))) {
            if (Files.exists(birds))
                Files.delete(birds);
            if (Files.exists(sight))
                Files.delete(sight);            
            Files.delete(Paths.get(dir));
        }
    }

    @After
    public void tearDown() {
    }

    private String getTestDataDirectory() throws URISyntaxException {
        return Paths.get(ServerApplicationTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile().getAbsolutePath() + File.separator + "serverdata";
    }

    private void startServerInSeparateThread() {
        Thread serverThread = new Thread(() -> {
            try {
                String[] args = new String[]{"-port", TEST_PORT, "-data", getTestDataDirectory()};
                ServerApplication.main(args);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    private BaseClientRequestDto getClientRequest(long counter) {
        AddBirdRequestDto req = new AddBirdRequestDto(ClientAction.ADD, "test name" + counter, "test color", 10.00, 20.00);
        return req;
    }

    private void stopServer() throws IOException {
        Client client = new Client(Integer.parseInt(TEST_PORT), TEST_HOST);
        client.connectToServer();
        client.execute(new BaseClientRequestDto(ClientAction.QUIT));
    }

    @Test
    public void testMain() throws InterruptedException, IOException, ExecutionException {
        startServerInSeparateThread();
        //wait untill server starts
        Thread.sleep(3000);
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_TEST_CLIENT);
        AtomicLong counter = new AtomicLong(0);
        List<Future<ServerResponseDto>> result = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Future<ServerResponseDto> expected = executor.submit(() -> {
                try {
                    Client client = new Client(Integer.parseInt(TEST_PORT), TEST_HOST);
                    client.connectToServer();
                    return client.execute(getClientRequest(counter.incrementAndGet()));
                } catch (IOException ex) {
                    return null;
                }
            });
            result.add(expected);
        }
        for (Future<ServerResponseDto> expected : result) {
            Assert.assertTrue(expected != null);
            if (!expected.get().isSuccess()) {
                System.out.println(expected.get());
            }
            Assert.assertTrue(expected.get().isSuccess());
        }
        stopServer();
    }

}
