/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.nczonline.web.cssembed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
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
    private CSSURLEmbedder embedder;
    
    public CSSURLEmbedderTest() {
    }
    
    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
        embedder = null;
    }
    
    @Test
    public void testAbsoluteLocalFile() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithMhtml() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        String mhtmlUrl = "http://www.example.com/dir/";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.MHTML_OPTION, true);
        embedder.setMHTMLRoot(mhtmlUrl);
        embedder.setFilename("styles_ie.css");
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("/*\nContent-Type: multipart/related; boundary=\"" + CSSURLEmbedder.MHTML_SEPARATOR + 
                "\"\n\n--" + CSSURLEmbedder.MHTML_SEPARATOR + "\nContent-Location:folder.png\n" +
                "Content-Transfer-Encoding:base64\n\n" + folderDataURI.substring(folderDataURI.indexOf(",")+1) +
                "\n\n--" + CSSURLEmbedder.MHTML_SEPARATOR + "--\n" +
                "*/\nbackground: url(mhtml:" + mhtmlUrl + "styles_ie.css!folder.png);", result);
    }
    
    @Test
    public void testAbsoluteLocalFileMultipleOneLine() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png); background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + "); background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithDoubleQuotes() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(\"folder.png\");";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithSingleQuotes() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url('folder.png');";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }     
    
    @Test (expected=IOException.class)
    public void testAbsoluteLocalFileWithMissingFile() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(fooga.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code),true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals(code, result);
    }
    
    @Test
    public void testAbsoluteLocalFileWithMissingFilesEnabled() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(fooga.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.SKIP_MISSING_OPTION, true);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals(code, result);
    }



    @Test
    public void testAbsoluteLocalFileUnderMaxLength() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.DATAURI_OPTION, true, 1000);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileOverMaxLength() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.DATAURI_OPTION, true, 200);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals(code, result);
    }
    
    @Test
    public void testAbsoluteLocalFileUnderMaxImageSize() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.DATAURI_OPTION, true, 0, 300);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals("background: url(" + folderDataURI + ");", result);
    }
    
    @Test
    public void testAbsoluteLocalFileOverMaxImageSize() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.png);";
        
        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.DATAURI_OPTION, true, 0, 200);
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        
        String result = writer.toString();
        assertEquals(code, result);
    }
    
    @Test
    public void testReadFromAndWriteToSameFile() throws IOException {
        String filename = this.getClass().getClassLoader().getResource("samefiletest.css").getPath().replace("%20", " ");
        File file = new File(filename);
        Reader in = new InputStreamReader(new FileInputStream(file));
        
        embedder = new CSSURLEmbedder(in, true);
        in.close();
        
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));        
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));
        writer.close();
        
        in = new InputStreamReader(new FileInputStream(file));
        char[] chars = new char[(int)file.length()];
        in.read(chars, 0, (int)file.length());
        in.close();
        
        String result = new String(chars);
        assertEquals("background: url(" + folderDataURI + ");", result);
    }

    @Test
    public void testRegularUrlWithMhtml() throws IOException {
        String filename = CSSURLEmbedderTest.class.getResource("folder.png").getPath().replace("%20", " ");
        String code = "background: url(folder.txt);";
        String mhtmlUrl = "http://www.example.com/dir/";

        StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.MHTML_OPTION, true);
        embedder.setMHTMLRoot(mhtmlUrl);
        embedder.setFilename("styles_ie.css");
        embedder.embedImages(writer, filename.substring(0, filename.lastIndexOf("/")+1));


        String result = writer.toString();
        assertEquals("background: url(folder.txt);", result);
    }
    
    @Test
    public void testRemoteUrlWithQueryString() throws IOException {
    	final String expectedUrl = "http://some-http-server.com/image/with/query/parameters/image.png?a=b&c=d";
    	String code = "background : url(/image/with/query/parameters/image.png?a=b&c=d)";
    	
    	StringWriter writer = new StringWriter();
        embedder = new CSSURLEmbedder(new StringReader(code), CSSURLEmbedder.DATAURI_OPTION, true, 0, 200) {
        	/*
        	 * Override method to prevent a network call during unit tests
        	 */
			@Override
			String getImageURIString(String url, String originalUrl) throws IOException {
				if(url.equals("")) {
					throw new IllegalArgumentException("Expected URL " + expectedUrl + ", but found " + url);
				}
				return "data:image/gif;base64,AAAABBBBCCCCDDDD";
			}
        };
        embedder.embedImages(writer, "http://some-http-server.com/");
        
        String result = writer.toString();
        assertEquals("background : url(data:image/gif;base64,AAAABBBBCCCCDDDD)", result);
    }
    
    @Test
    public void testImageDetection() {
    	String tests[] = {
    		"file://path/to/image.png",
    		"http://some.server.com/image.png",
    		"http://some.server.com/image.png?param=legalvalue&anotherparam=anothervalue",
    		"http://some.server.com/image.png?param=illegal.value.with.period"
    	};
    	boolean expectedImage[] = {
    		true, true, true, false
    	};
    	
    	for(int i=0; i<tests.length; i++) {
    		if(expectedImage[i]) {
    			assertTrue("Expected " + tests[i] + " to resolve to an image", CSSURLEmbedder.isImage(tests[i]));
    		}
    		else {
    			assertFalse("Did NOT expect " + tests[i] + " to resolve as an image", CSSURLEmbedder.isImage(tests[i]));
    		}
    	}
    	
    }
}