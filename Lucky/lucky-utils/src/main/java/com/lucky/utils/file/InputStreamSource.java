package com.lucky.utils.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/28 0028 15:44
 */
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}
