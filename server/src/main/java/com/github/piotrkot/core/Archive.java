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

import com.github.piotrkot.api.PersistentLogs;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Compressed archive.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Slf4j
public final class Archive {
    /**
     * Compressed file.
     */
    private final transient File zip;
    /**
     * DB log messages.
     */
    private final transient PersistentLogs logs;
    /**
     * Request id.
     */
    private final transient int rqst;

    /**
     * Class constructor.
     * @param dblogs DB log messages.
     * @param request Request id.
     * @param file Compressed file stream.
     */
    public Archive(final PersistentLogs dblogs, final int request,
        final File file) {
        this.logs = dblogs;
        this.rqst = request;
        this.zip = file;
    }

    /**
     * Uncompress archive.
     * @return Path to the decompressed temporary directory.
     */
    public Optional<Path> uncompress() {
        Optional<Path> unzipped = Optional.empty();
        try {
            final Path unzip = Files.createTempDirectory("unzip");
            new ZipFile(this.zip).extractAll(unzip.toAbsolutePath().toString());
            final String msg = String.format(
                "Zip archive content uncompressed in %s",
                unzip.toFile().getName()
            );
            log.info(msg);
            this.logs.append(this.rqst, msg);
            unzipped = Optional.of(unzip);
        } catch (final ZipException | IOException ex) {
            log.error("Could not read zip", ex);
            this.logs.append(this.rqst, ex);
        }
        return unzipped;
    }
}
