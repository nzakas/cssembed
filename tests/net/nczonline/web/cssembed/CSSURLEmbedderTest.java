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
    
    private static String folderDataURI = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACIAAAAbCAMAAAAu7K2VAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAAwUExURWxsbNbW1v/rhf/ge//3kf/Ub9/f3/b29oeHh/7LZv/0juazTktLS8WSLf//mf/////BPrAAAAB4SURBVHja3NLdCoAgDIbhqbXZz2f3f7eZWUpMO67nQEReBqK0vaLPJohYegnSYqSdYAtRGvUYVpJhPpx7z2piLSqsJQ73oY1ztGREuEwBpCUTwpAt7cRmncRlnWTMoCdcXxmrdiMxngpvtDcSNkX9AvTnv9uyCzAAgzAw+dNAwOQAAAAASUVORK5CYII=";

    public CSSURLEmbedderTest() {
    }

    @Before
    public void setUp() {
        CSSURLEmbedder.setVerbose(true);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testAbsoluteLocalFile() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        CSSURLEmbedder.embedImages(new StringReader(code), writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileMultipleOneLine() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png); background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        CSSURLEmbedder.embedImages(new StringReader(code), writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + "); background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithDoubleQuotes() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(\"folder.png\");";
        
        StringWriter writer = new StringWriter();
        CSSURLEmbedder.embedImages(new StringReader(code), writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithSingleQuotes() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url('folder.png');";
        
        StringWriter writer = new StringWriter();
        CSSURLEmbedder.embedImages(new StringReader(code), writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }     
    
    
}