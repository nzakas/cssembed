/*
 * Copyright (c) 2009 Nicholas C. Zakas. All rights reserved.
 * http://www.nczonline.net/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
 
package net.nczonline.web.cssembed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.io.Reader;

/**
 * Generator for Data URIs.
 * @author Nicholas C. Zakas
 */
public class CSSURLEmbedder { 
    
    private static boolean verbose = false;
    
    
    //--------------------------------------------------------------------------
    // Get/Set verbose flag
    //--------------------------------------------------------------------------    
    
    public static boolean getVerbose(){
        return verbose;
    }
    
    public static void setVerbose(boolean newVerbose){
        verbose = newVerbose;
    }
    
    //--------------------------------------------------------------------------
    // Embed images
    //--------------------------------------------------------------------------
    
    
    public static void embedImages(Reader in, Writer out) throws IOException {
        BufferedReader reader = new BufferedReader(in);        
        StringBuilder builder = new StringBuilder();
        String line;
        int lineNum = 1;
        
        
        while((line = reader.readLine()) != null){
            
            int start = 0;
            int pos = line.indexOf("url(", start);
            
            if (pos > -1){
                while (pos > -1){
                    pos += 4;
                    builder.append(line.substring(start, pos));
                    String url = line.substring(pos, line.indexOf(")", pos));
                    if (verbose){
                        System.err.println("[INFO] Found URL '" + url + "' at line " + lineNum + ", col " + pos + ".");
                    }

                    start = pos + url.length();
                    pos = line.indexOf("url(", start);
                } 
            } else {
                builder.append(line);
            }
            
            builder.append("\n");
            lineNum++;
        }
        
        
    }
    
            
}
