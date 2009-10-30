/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nczonline.web.cssembed;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicholas C. Zakas
 */
public class CSSURLEmbedderTest {

    public CSSURLEmbedderTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testRelativeLocalFile() throws IOException {
            System.out.println("HERE"); 
        String code = "background: url(folder.png)";
        StringWriter writer = new StringWriter();
        
        CSSURLEmbedder.embedImages(new StringReader(code), writer);
        
        String result = writer.toString();
        
        assertEquals("", result);
           
    }

}