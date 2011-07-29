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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;

import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.IdentityMapper;

import java.io.*;

import java.util.Vector;
import java.util.Iterator;

//Define a custom Ant Task that calls into the CSS Embedder
public class CSSEmbedTask extends Task {
    
    //attribute options
    private String charset = "UTF-8";
    private String root;
    private boolean mhtml;
    private String mhtmlRoot;
    private boolean skipMissing;
    private boolean verbose = false;
    private int maxUriLength = 0;
    private int maxImageSize = 0;
    private File srcFile;
    private File destFile;
    
    //support nested resource collections & mappers
    private Mapper mapperElement = null;
    private Vector rcs = new Vector();
    
    //Simple Setters
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public void setRoot(String root) {
        this.root = root;
    }
    
    public void setMhtml(boolean mhtml) {
        this.mhtml = mhtml;
    }
    
    public void setMhtmlRoot(String mhtmlRoot) {
        this.mhtmlRoot = mhtmlRoot;
    }
    
    public void setSkipMissing(boolean skipMissing) {
        this.skipMissing = skipMissing;
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setMaxUriLength(int maxUriLength) {
        this.maxUriLength = maxUriLength;
    }
    
    public void setMaxImageSize(int maxImageSize) {
        this.maxImageSize = maxImageSize;
    }
    
    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }
    
    public void setDestFile(File destFile) {
        this.destFile = destFile;
    }
    
    //More complicated setters for nested elements...
    
    //add a collection of resources to copy
    public void add(ResourceCollection res) {
        rcs.add(res);
    }
    
    //mapper takes source files & converts them to dest files
    public Mapper createMapper() throws BuildException {
        if (mapperElement != null) {
            throw new BuildException("Cannot define more than one mapper", getLocation());
        }
        mapperElement = new Mapper(getProject());
        return mapperElement;
    }
    
    //support multiple types of filename mappers being added
    public void add(FileNameMapper fileNameMapper) {
        createMapper().add(fileNameMapper);
    }
    
    //returns the mapper to use based on nested elements, defaults to IdentityMapper
    private FileNameMapper getMapper() {
        FileNameMapper mapper = null;
        if (mapperElement != null) {
            mapper = mapperElement.getImplementation();
        } else {
            mapper = new IdentityMapper();
        }
        return mapper;
    }
    
    //ensure that attributes are legit
    protected void validateAttributes() throws BuildException {
        //if there's no nested resource containers make sure that a srcFile/destFile are set
        if(this.rcs == null || this.rcs.size() == 0) {
            if (this.srcFile == null || !this.srcFile.exists()) {
                throw new BuildException("Must specify an input file or at least one nested resource", getLocation());
            }
            
            if(this.destFile == null) {
                throw new BuildException("Must specify an output file or at least one nested resource", getLocation());
            }
        }
        
        if(this.mhtml && this.mhtmlRoot == null) {
            throw new BuildException("Must specify mhtmlRoot in mhtml mode", getLocation());
        }
        
        if(this.mhtmlRoot != null && !this.mhtml) {
            log("mhtmlRoot has no effect if mhtml mode is not activated", Project.MSG_WARN);
        }
    }
    
    //run the task
    public void execute () throws BuildException {
        validateAttributes();
        
        //set options flags
        int options = (this.mhtml) ? CSSURLEmbedder.MHTML_OPTION : CSSURLEmbedder.DATAURI_OPTION;
        if(skipMissing) {
            options = options | CSSURLEmbedder.SKIP_MISSING_OPTION;
        }
        
        if(srcFile != null && srcFile.exists()) {
            try {
                embed(srcFile, destFile, options);
            } catch(IOException ex) {
                throw new BuildException(ex.getMessage(), ex);
            }
        }
        
        FileNameMapper mapper = getMapper();
        
        for(Iterator it = this.rcs.iterator(); it.hasNext();) {
            ResourceCollection rc = (ResourceCollection) it.next();
            
            for(Iterator rcit = rc.iterator(); rcit.hasNext();) {
                FileResource fr = (FileResource) rcit.next();
                File in = fr.getFile();
                
                String[] mapped = mapper.mapFileName(in.getName());
                if (mapped != null && mapped.length > 0) {
                    for(int k = 0; k < mapped.length; k++) {
                        File out = getProject().resolveFile(in.getParent() + File.separator + mapped[k]);
                        
                        try {
                            embed(in, out, options);
                        } catch(IOException ex) {
                            throw new BuildException(ex.getMessage(), ex);
                        }
                    }
                }
            }
        }
    }
    
    private void embed(File input, File output, int options) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Reader in = new InputStreamReader(new FileInputStream(input), charset);
        Writer out = new OutputStreamWriter(bytes, charset);
        String pathRoot = root;
        
        if(pathRoot == null) {
            pathRoot = input.getCanonicalPath();
            pathRoot = pathRoot.substring(0, pathRoot.lastIndexOf(File.separator));                
        }
        
        if (!pathRoot.endsWith(File.separator)){
            pathRoot += File.separator;
        }
        
        if(verbose) {
            log("[INFO] embedding images from '" + input + "'");
        }
        
        CSSURLEmbedder embedder = new CSSURLEmbedder(in, options, verbose, maxUriLength, maxImageSize);
        
        if(mhtml) {
            embedder.setMHTMLRoot(mhtmlRoot);
            embedder.setFilename(output.getName());
        }
        
        embedder.embedImages(out, pathRoot);
        
        in.close();
        out.close();
        
        if(bytes.size() > 0) {
            if(verbose) {
                log("[INFO] Writing to file: " + output);
            }
            
            bytes.writeTo(new FileOutputStream(output));
        }
    }
}