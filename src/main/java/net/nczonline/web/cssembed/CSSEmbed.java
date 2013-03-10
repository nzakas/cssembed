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

import jargs.gnu.CmdLineParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;


public class CSSEmbed {    

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //default settings
        boolean verbose = false;
        String charset = null;
        String outputFilename = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Writer out = null;
        Reader in = null;
        String root;
        int options = CSSURLEmbedder.DATAURI_OPTION;
        
        //initialize command line parser
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option verboseOpt = parser.addBooleanOption('v', "verbose");
        CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");
        CmdLineParser.Option charsetOpt = parser.addStringOption("charset");
        CmdLineParser.Option rootOpt = parser.addStringOption("root");
        CmdLineParser.Option outputFilenameOpt = parser.addStringOption('o', "output");
        CmdLineParser.Option mhtmlOpt = parser.addBooleanOption("mhtml");
        CmdLineParser.Option mhtmlRootOpt = parser.addStringOption("mhtmlroot");
        CmdLineParser.Option skipMissingOpt = parser.addBooleanOption("skip-missing");
        CmdLineParser.Option uriLengthOpt = parser.addIntegerOption("max-uri-length");
        CmdLineParser.Option imageSizeOpt = parser.addIntegerOption("max-image-size");
        
        try {
            
            //parse the arguments
            parser.parse(args);

            //figure out if the help option has been executed
            Boolean help = (Boolean) parser.getOptionValue(helpOpt);
            if (help != null && help.booleanValue()) {
                usage();
                System.exit(0);
            } 
            
            //determine boolean options
            verbose = parser.getOptionValue(verboseOpt) != null;
            
            //check for charset
            charset = (String) parser.getOptionValue(charsetOpt);
            if (charset == null || !Charset.isSupported(charset)) {
                charset = System.getProperty("file.encoding");
                if (charset == null) {
                    charset = "UTF-8";
                }
                if (verbose) {
                    System.err.println("\n[INFO] Using charset " + charset);
                }
            }
          
            //get the file arguments
            String[] fileArgs = parser.getRemainingArgs();
            String inputFilename = null;
            
            //if no file is given, use stdin
            if (fileArgs.length == 0){
                in = new InputStreamReader(System.in, charset);
            } else {
                //only the first filename is used
                inputFilename = fileArgs[0];                     
                in = new InputStreamReader(new FileInputStream(inputFilename), charset);            
            }

            //determine if there's a maximum URI length
            int uriLength = CSSURLEmbedder.DEFAULT_MAX_URI_LENGTH;
            Integer maxUriLength = (Integer) parser.getOptionValue(uriLengthOpt);
            if (maxUriLength != null){
                uriLength = maxUriLength.intValue();
                if (uriLength < 0){
                    uriLength = 0;
                }
            }
            
            //maximum size allowed for image files
            int imageSize = 0;
            Integer imageSizeOption = (Integer) parser.getOptionValue(imageSizeOpt);
            if (imageSizeOption != null){
                imageSize = imageSizeOption.intValue();
                if (imageSize < 0){
                    imageSize = 0;
                }
            }

            //determine if MHTML mode is on
            boolean mhtml = parser.getOptionValue(mhtmlOpt) != null;
            if(mhtml){
                options = CSSURLEmbedder.MHTML_OPTION;
            }
            String mhtmlRoot = (String) parser.getOptionValue(mhtmlRootOpt);
            if (mhtml && mhtmlRoot == null){
                throw new Exception("Must use --mhtmlroot when using --mhtml.");
            }
            
            //are missing files ok?
            boolean skipMissingFiles = parser.getOptionValue(skipMissingOpt) != null;
            if(skipMissingFiles) {
                options = options | CSSURLEmbedder.SKIP_MISSING_OPTION;
            }
            
            CSSURLEmbedder embedder = new CSSURLEmbedder(in, options, verbose, uriLength, imageSize);            
            embedder.setMHTMLRoot(mhtmlRoot);
            
            //close in case writing to the same file
            in.close(); in = null;
            
            //get root for relative URLs
            root = (String) parser.getOptionValue(rootOpt);
            if(root == null){
            
                if (inputFilename != null) {
                    //no root specified, so get from input file
                    root = (new File(inputFilename)).getCanonicalPath();
                    root = root.substring(0, root.lastIndexOf(File.separator));                
                } else {
                    throw new Exception("Must use --root when not specifying a filename.");
                }
            }
            
            if (!root.endsWith(File.separator)){
                root += File.separator;
            }
            
            if (verbose){
                System.err.println("[INFO] Using '" + root + "' as root for relative file paths.");
            }
                                  
            //get output filename
            outputFilename = (String) parser.getOptionValue(outputFilenameOpt);            
            if (outputFilename == null) {
                if (verbose){
                    System.err.println("[INFO] No output file specified, defaulting to stdout.");
                }                
                
                out = new OutputStreamWriter(System.out);
            } else {
                File outputFile = new File(outputFilename);
                if (verbose){
                    System.err.println("[INFO] Output file is '" + outputFile.getAbsolutePath() + "'");
                }
                embedder.setFilename(outputFile.getName());
                out = new OutputStreamWriter(bytes, charset);
            }            
            
            //set verbose option
            embedder.embedImages(out, root);
            
        } catch (CmdLineParser.OptionException e) {
            usage();
            System.exit(1);            
        } catch (Exception e) { 
            System.err.println("[ERROR] " + e.getMessage());
            if (verbose){
                e.printStackTrace();
            }
            System.exit(1);
        } finally {
            if (out != null) {
                try {
                    out.close();
                    
                    if(bytes.size() > 0) {
                        bytes.writeTo(new FileOutputStream(outputFilename));
                    }
                } catch (IOException e) {
                    System.err.println("[ERROR] " + e.getMessage());
                    if (verbose){
                        e.printStackTrace();
                    }
                }
            }            
        }
        
    }
    
    /**
     * Outputs help information to the console.
     */
    private static void usage() {
        System.out.println(
            "\nUsage: java -jar cssembed-x.y.z.jar [options] [input file]\n\n"

                        + "Global Options\n"
                        + "  -h, --help            Displays this information.\n"
                        + "  --charset <charset>   Character set of the input file.\n"
                        + "  --mhtml               Enable MHTML mode.\n"
                        + "  --mhtmlroot <root>    Use <root> as the MHTML root for the file.\n"                        
                        + "  -v, --verbose         Display informational messages and warnings.\n"
                        + "  --root <root>         Prepends <root> to all relative URLs.\n"
                        + "  --skip-missing        Don't throw an error for missing image files.\n"
                        + "  --max-uri-length len  Maximum length for a data URI. Defaults to 32768.\n"
                        + "  --max-image-size size Maximum image size (in bytes) to convert.\n"
                        + "  -o <file>             Place the output into <file>. Defaults to stdout.");
    }
}