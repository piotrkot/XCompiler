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
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.DBI;

/**
 * DB log messages appender.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Slf4j
public final class DBLogs implements PersistentLogs {
    /**
     * DBI access.
     */
    private final transient DBI dbi;

    /**
     * Class constructor.
     * @param dbaccess DBI access.
     */
    public DBLogs(final DBI dbaccess) {
        this.dbi = dbaccess;
    }

    /**
     * Append log message for the given request.
     * @param request Client request.
     * @param line Message line.
     */
    public void append(final int request, final String line) {
        this.dbi.withHandle(
            han -> han.insert(
                "INSERT INTO requests (req, mesg) VALUES (?, ?)",
                request,
                line
            )
        );
        log.info("Appended info request: {}", request);
    }

    /**
     * Append error log for the given request.
     * @param request Client request.
     * @param error Error message.
     */
    public void append(final int request, final Throwable error) {
        this.append(request, String.format("ERROR: %s", error.getMessage()));
        log.info("Appended error request: {}", request);
    }
}
