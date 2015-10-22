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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 * Maven build.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Slf4j
public final class Maven {
    /**
     * Maven project directory.
     */
    private final transient Path dir;
    /**
     * Persistent log messages.
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
     * @param directory Maven project directory.
     */
    public Maven(final PersistentLogs dblogs, final int request,
        final Path directory) {
        this.logs = dblogs;
        this.rqst = request;
        this.dir = directory;
    }

    /**
     * Build of maven project.
     * @return Result of the build.
     */
    public Optional<InvocationResult> build() {
        Optional<InvocationResult> result = Optional.empty();
        final InvocationRequest request = new DefaultInvocationRequest();
        if (this.pomFile().isPresent()) {
            request.setPomFile(this.pomFile().get().toFile());
            request.setGoals(Collections.singletonList("test"));
            try {
                final Invoker invoker = new DefaultInvoker();
                invoker.setOutputHandler(
                    line -> this.logs.append(this.rqst, line)
                );
                invoker.setErrorHandler(
                    line -> this.logs.append(this.rqst, line)
                );
                result = Optional.of(invoker.execute(request));
            } catch (final MavenInvocationException ex) {
                log.error("Maven invocation failed", ex);
                this.logs.append(this.rqst, ex);
            }
        } else {
            final String mesg = "POM file not found";
            log.info(mesg);
            this.logs.append(this.rqst, mesg);
        }
        return result;
    }

    /**
     * Searches for pom.xml file in the, presumably, project.
     * @return Path of pom.xml file.
     */
    private Optional<Path> pomFile() {
        Optional<Path> pom = Optional.empty();
        try {
            pom = Files.find(
                this.dir,
                Integer.MAX_VALUE,
                (path, attributes) -> "pom.xml".equals(path.toFile().getName())
            ).findFirst();
        } catch (final IOException ex) {
            log.error("Corrupted maven project", ex);
            this.logs.append(this.rqst, ex);
        }
        return pom;
    }
}
