package com.lucky.utils.fileload;

import com.lucky.utils.annotation.Nullable;
import com.lucky.utils.file.InputStreamSource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 15:34
 */
public interface Resource extends InputStreamSource {

    /**
     * 返回Resource所指向的底层资源是否存在
     * @return 存在返回true，否则返回false
     */
    boolean exists();

    /**
     * 返回当前Resource代表的底层资源是否可读
     * @return 可读返回true，否则返回false
     */
    default boolean isReadable() {
        return exists();
    }

    /**
     * 返回Resource资源文件是否已经打开<br/>
     * 如果返回true，则只能被读取一次然后关闭以避免内存泄漏；<br/>
     * 常见的Resource实现一般返回false
     * @return
     */
    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    /**
     * 如果当前Resource代表的底层资源能由java.util.URL代表，则返回该URL，否则抛出IO异常
     * @return
     * @throws IOException
     */
    URL getURL() throws IOException;

    /**
     * 如果当前Resource代表的底层资源能由java.util.URI代表，则返回该URI，否则抛出IO异常
     * @return
     * @throws IOException
     */
    URI getURI() throws IOException;

    /**
     * 如果当前Resource代表的底层资源能由java.io.File代表，则返回该File，否则抛出IO异常
     * @return
     * @throws IOException
     */
    File getFile() throws IOException;

    /**
     *
     * @return
     * @throws IOException
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * 返回当前Resource代表的底层文件资源的长度，一般是值代表的文件资源的长度
     * @return
     * @throws IOException
     */
    long contentLength() throws IOException;

    /**
     * 返回当前Resource代表的底层资源的最后修改时间。
     * @return
     * @throws IOException
     */
    long lastModified() throws IOException;

    /**
     * 相对转绝对
     * 用于创建相对于当前Resource代表的底层资源的资源
     * 比如当前Resource代表文件资源“d:/test/”则createRelative（“test.txt”）将返回表文件资源“d:/test/test.txt”Resource资源。
     * @param relativePath
     * @return
     * @throws IOException
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * 返回当前Resource代表的底层文件资源的文件路径<br/>
     * 比如File资源“file://d:/test.txt”将返回“d:/test.txt”<br/>
     * 而URL资源http://www.javass.cn将返回“”，因为只返回文件路径
     *
     * @return
     */
    @Nullable
    String getFilename();

    /**
     * 返回当前Resource代表的底层资源的描述符，通常就是资源的全路径（实际文件名或实际URL地址）
     * @return
     */
    String getDescription();
}
