# CSSEmbed
Copyright (c) 2009 Nicholas C. Zakas. All rights reserved.

## About

CSSEmbed is a small program/library to automate embedding of data URIs in
CSS files.

---

## Usage

    java -jar cssembed-x.y.z.jar -o <output filename> <input filename>

For example:

     java -jar cssembed-x.y.z.jar -o styles_new.css styles.css

When the -o flag is omitted, the output ends up on stdout, thus you can direct the output to a file directly:

     java -jar cssembed-x.y.z.jar styles.css > styles_new.css

Complete usage instructions are available using the -h flag:

    Usage: java -jar cssembed-x.y.z.jar [options] [input file]

    Global Options
       -h, --help            Displays this information.
       --charset             Character set of the input file.
       -v, --verbose         Display informational messages and warnings.
       -root                 Prepends  to all relative URLs.
       -o                    Place the output into . Defaults to stdout.

---

## License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.