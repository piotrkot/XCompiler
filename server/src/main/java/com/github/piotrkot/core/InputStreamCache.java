/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 piotrkot
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.piotrkot.core;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import lombok.SneakyThrows;

/**
 * Temporary cache of input stream so that it can be re-read.
 *
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class InputStreamCache {
    /**
     * Temporal file name.
     */
    private static final String TEMP_NAME = "temp";
    /**
     * Temporal file suffix.
     */
    private static final String TEMP_SUFF = ".strm";
    /**
     * Temporal file.
     */
    private final transient File temp;
    /**
     * Class constructor.
     *
     * @param stream Character stream to be temporarily stored.
     */
    @SneakyThrows
    public InputStreamCache(final InputStream stream) {
        this.temp = File.createTempFile(TEMP_NAME, TEMP_SUFF);
        ByteStreams.copy(stream, new FileOutputStream(this.temp));
    }
    /**
     * Copy of original character stream.
     *
     * @return New instance of character stream reader.
     */
    @SneakyThrows
    public InputStream reader() {
        return new FileInputStream(this.temp);
    }
    /**
     * Copy of original character stream as a file.
     *
     * @return New instance of character stream reader.
     */
    @SneakyThrows
    public File file() {
        return this.temp;
    }
}
