package org.gracilianomp.utils;

import java.io.*;

public class StreamUtils {

    static public String read(File file) throws IOException {
        try ( FileInputStream fin = new FileInputStream(file) ) {
            return read(fin);
        }
        catch (IOException e) {
            throw e ;
        }
        catch (Exception e) {
            throw new IOException("Can't read file: "+ file, e) ;
        }
    }

    static public String read(InputStream fin) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024*4] ;

        try {
            int r ;
            while ( (r = fin.read(buf)) >= 0 ) {
                bout.write(buf, 0, r);
            }
        }
        catch (IOException e) {
            throw e ;
        }

        String string = new String(bout.toByteArray());
        return string ;
    }


}
