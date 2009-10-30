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
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashSet;
import net.nczonline.web.datauri.DataURIGenerator;

/**
 * Generator for Data URIs.
 * @author Nicholas C. Zakas
 */
public class CSSURLEmbedder { 
    
    private static boolean verbose = false;
    private static HashSet<String> imageTypes;
    
    static {
        imageTypes = new HashSet<String>();
        imageTypes.add("jpg");
        imageTypes.add("jpeg");
        imageTypes.add("gif");
        imageTypes.add("png");
    }
    
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
    
    /**
     * Embeds data URI images into a CSS file.
     * @param in The CSS source code.
     * @param out The place to write out the source code.
     * @throws java.io.IOException
     */
    public static void embedImages(Reader in, Writer out) throws IOException {
        embedImages(in, out, null);
    }
        
    /**
     * Embeds data URI images into a CSS file.
     * @param in The CSS source code.
     * @param out The place to write out the source code.
     * @param root The root to prepend to any relative paths.
     * @throws java.io.IOException
     */
    public static void embedImages(Reader in, Writer out, String root) throws IOException {
        BufferedReader reader = new BufferedReader(in);        
        StringBuilder builder = new StringBuilder();
        String line;
        int lineNum = 1;
        
        
        while((line = reader.readLine()) != null){
            
            int start = 0;
            int pos = line.indexOf("url(", start);
            int npos;
            
            if (lineNum > 1){
                builder.append("\n");
            }
            
            if (pos > -1){
                while (pos > -1){
                    pos += 4;
                    builder.append(line.substring(start, pos));
                    npos = line.indexOf(")", pos);
                    String url = line.substring(pos, npos).trim();
                    String newUrl = url;
                    
                    if (verbose){
                        System.err.println("[INFO] Found URL '" + url + "' at line " + lineNum + ", col " + pos + ".");
                    }
                    if (url.indexOf("http:") != 0 && root != null){
                        newUrl = root + url;
                        if (verbose){
                            System.err.println("[INFO] Applying root to URL, URL is now '" + newUrl + "'.");
                        }                        
                    }
                    
                    builder.append(getImageURIString(newUrl, url));

                    start = npos;                    
                    pos = line.indexOf("url(", start);
                } 
                
                //finish out the line
                if (start < line.length()){
                    builder.append(line.substring(start));
                }
            } else {
                builder.append(line);
            }
            
            lineNum++;
        }
        
        out.write(builder.toString());
        
    }
    
    /**
     * Returns a URI string for the given URL. If the URL is for an image, 
     * the data URI will be returned. If the URL is not for an image, then the
     * original URI is returned.
     * @param url The URL to attempt to read.
     * @param originalUrl The original URL as stated in the source code.
     * @return The appropriate data URI to use.
     * @throws java.io.IOException
     */
    private static String getImageURIString(String url, String originalUrl) throws IOException {
        
        //check the extension - only encode for images
        String fileType = url.substring(url.lastIndexOf(".") + 1);
        
        //it's an image, so encode it
        if (imageTypes.contains(fileType)){
            
            DataURIGenerator.setVerbose(verbose);
            
            StringWriter writer = new StringWriter();
            
            if (url.startsWith("http://")){
                if (verbose){
                    System.err.println("[INFO] Downloading '" + url + "' to generate data URI.");
                }                
                
                DataURIGenerator.generate(new URL(url), writer); 
              
            } else {
                if (verbose){
                    System.err.println("[INFO] Opening file '" + url + "' to generate data URI.");
                }                
                
                File file = new File(url);
                
                if (verbose && !file.isFile()){
                    System.err.println("[INFO] Could not find file '" + file.getCanonicalPath() + "'.");
                }                 
                
                DataURIGenerator.generate(new File(url), writer); 
            }

            if (verbose){
                System.err.println("[INFO] Generated data URI for '" + url + "'.");
            }              
            
            return writer.toString();
            
        } else {
            
            if (verbose){
                System.err.println("[INFO] URL '" + originalUrl + "' is not an image, skipping.");
            }
            
            //not an image, ignore
            return originalUrl;
        }
        
    }
    
            
}
