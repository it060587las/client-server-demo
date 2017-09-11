package com.dataart.edu.server.dao;

import com.dataart.edu.message.dto.BirdDto;
import com.dataart.edu.message.dto.BirdSightDto;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of DAO component.
 * @author alitvinov
 */
public class BirdAndSightDaoImplTest {
    private static final DateTimeFormatter FORMATTER  = 
            new DateTimeFormatterBuilder()
                    .append(DateTimeFormat.forPattern("yyyy-MM-dd").getParser())
                    .toFormatter();            
            
    private BirdAndSightDaoImpl dao;
    
    public BirdAndSightDaoImplTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
        ;
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        dao=new BirdAndSightDaoImpl();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of findAllBirds method, of class BirdAndSightDaoImpl.
     */
    @Test
    public void testFindAllBirds() {
        System.out.println("findAllBirds");        
        List<BirdDto> expResult = dao.findAllBirds();
        assertEquals(expResult, Collections.EMPTY_LIST);
        BirdDto expectedBird=dao.addBird(new BirdDto("name", "color", 1, 1));
        expResult = dao.findAllBirds();
        assertTrue(expResult!=null && !expResult.isEmpty());
        assertEquals(expectedBird,expResult.get(0));        
    }

    /**
     * Test of addBird method, of class BirdAndSightDaoImpl.
     */
    @Test    
    public void testAddBird() {
        System.out.println("addBird");
        BirdDto elementToAdd = new BirdDto("name", "color", 1, 1);        
        BirdDto expResult = dao.addBird(elementToAdd);
        assertEquals(expResult.getName(), elementToAdd.getName());                
    }
    
    /**
     * Test of addBird method, of class BirdAndSightDaoImpl, which will attempts to add duplicate bird.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddBirdDuplicate() {
        System.out.println("addBirdDuplicate");        
        BirdDto elementToAdd = new BirdDto("name", "color", 1, 1);                
        BirdDto expResult = dao.addBird(elementToAdd);
        assertEquals(expResult.getName(), elementToAdd.getName());                        
        elementToAdd = new BirdDto("name", "color", 1, 1);    
        dao.addBird(elementToAdd);                
    }

    /**
     * Test of removeBird method, of class BirdAndSightDaoImpl.
     */
    @Test    
    public void testRemoveBird() {
        System.out.println("removeBird");
        BirdDto elementToAdd = new BirdDto("name", "color", 1, 1);                
        dao.addBird(elementToAdd);
        List<BirdDto> expectedResult=dao.findAllBirds();
        assertTrue(expectedResult.size()==1);
        dao.removeBird(elementToAdd.getName());    
        expectedResult=dao.findAllBirds();
        assertTrue(expectedResult.isEmpty());        
    }

    /**
     * Test of removeBird method, with not existing bird.
     */
    @Test(expected = IllegalArgumentException.class) 
    public void testRemoveBirdNotExisting() {
        System.out.println("removeBirdNotExisting");        
        dao.removeBird("notExistingName");        
    }
    
    /**
     * Test of addSight method, of class BirdAndSightDaoImpl, with tot existing bird name.
     */
    @Test(expected = IllegalArgumentException.class) 
    public void testAddSightNotExistingBird() {
        System.out.println("testAddSightNotExistingBird");
        dao.addSight(new BirdSightDto("notExistingName", "location", 0));
    }
    
    /**
     * Test of addSight method, of class BirdAndSightDaoImpl.
     */
    @Test
    public void testAddSight() {
        System.out.println("testAddSightNotExistingBird");        
        BirdDto birdToAdd = new BirdDto("someName", "color", 1, 1);        
        dao.addBird(birdToAdd);
        BirdSightDto elementToAdd=new BirdSightDto("someName",  "location", new Date().getTime());
        BirdSightDto expectedResult=dao.addSight(elementToAdd);
        assertEquals(expectedResult, elementToAdd);
    }

    /**
     * Test of findSight method, of class BirdAndSightDaoImpl.
     */
    @Test    
    public void testFindSight() {
        System.out.println("findSight");        
        testAddSight();        
        BirdSightDto elementToFind=new BirdSightDto("someName", "location", 
                FORMATTER.parseMillis("1999-01-01"), FORMATTER.parseMillis("2999-01-01"));
        List<BirdSightDto> expectedResult=dao.findSight(elementToFind);
        assertTrue(!expectedResult.isEmpty() && expectedResult.size()==1);
        elementToFind=new BirdSightDto("someName2", "location", 
                FORMATTER.parseMillis("1999-01-01"), FORMATTER.parseMillis("2999-01-01"));
        expectedResult=dao.findSight(elementToFind);
        assertTrue(expectedResult.isEmpty());
    }
    
}
