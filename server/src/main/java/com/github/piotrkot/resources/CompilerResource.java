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
package com.github.piotrkot.resources;

import com.codahale.metrics.annotation.Timed;
import com.github.piotrkot.api.PersistentLogs;
import com.github.piotrkot.core.Archive;
import com.github.piotrkot.core.DBLogs;
import com.github.piotrkot.core.InputStreamCache;
import com.github.piotrkot.core.Maven;
import io.dropwizard.auth.Auth;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.util.StringMapper;

/**
 * Compiler resource.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@Path("/compiler")
@Slf4j
public final class CompilerResource {
    /**
     * Executor of the background jobs.
     */
    private final transient ExecutorService executor;
    /**
     * Random request generator.
     */
    private final transient SecureRandom random;
    /**
     * DBI access point.
     */
    private final transient DBI dbi;
    /**
     * DB messages.
     */
    private final transient PersistentLogs dbmesg;

    /**
     * Class constructor.
     * @param dbaccess DBI access point.
     */
    public CompilerResource(final DBI dbaccess) {
        this.dbi = dbaccess;
        this.random = new SecureRandom();
        this.executor = Executors.newCachedThreadPool();
        this.dbmesg = new DBLogs(this.dbi);
    }

    /**
     * Ping server.
     * @return Pong message.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public static String ping() {
        return "pong";
    }

    /**
     * Compile the sources.
     * @param user User name.
     * @param stream File input stream.
     * @param details File content details.
     * @return Request id.
     */
    @Path("/source")
    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Integer compileSources(@Auth final String user,
        @FormDataParam("file") final InputStream stream,
        @FormDataParam("file") final FormDataContentDisposition details) {
        final int request = this.random.nextInt();
        this.dbi.withHandle(
            han -> han.insert(
                "INSERT INTO requests (req, mesg) VALUES (?, ?)",
                request,
                "Compiling Job started"
            )
        );
        final InputStreamCache cache = new InputStreamCache(stream);
        this.executor.execute(
            () -> {
                final Optional<java.nio.file.Path> dir =
                    new Archive(this.dbmesg, request, cache.file())
                        .uncompress();
                if (dir.isPresent()) {
                    new Maven(this.dbmesg, request, dir.get()).build();
                }
            }
        );
        return request;
    }

    /**
     * Append server logs.
     * @param user User name.
     * @param line Message log.
     * @param request User request.
     */
    @Path("/logs/{request}")
    @POST
    @Timed
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_PLAIN)
    public void appendLogs(@Auth final String user,
        @FormParam("log") final String line,
        @PathParam("request") final int request) {
        this.dbmesg.append(request, line);
    }

    /**
     * Show server messages.
     * @param user User name.
     * @param request User request.
     * @return Messages as string joined on system new line character.
     */
    @Path("/logs/{request}")
    @GET
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    public String showLogs(@Auth final String user,
        @PathParam("request") final int request) {
        return this.dbi.withHandle(
            han -> han.createQuery(
                "SELECT mesg FROM requests WHERE req = :r ORDER BY timestmp ASC"
            ).bind("r", request).map(StringMapper.FIRST).list()
        ).stream().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Delete server messages.
     * @param user User name.
     * @param request User request.
     * @return Number of deleted messages.
     */
    @Path("/logs/{request}")
    @DELETE
    @Timed
    @Produces(MediaType.TEXT_PLAIN)
    public Integer deleteLogs(@Auth final String user,
        @PathParam("request") final long request) {
        return this.dbi.withHandle(
            han -> han.update(
                "DELETE FROM requests WHERE req = ?", request
            )
        );
    }

    /**
     * User authentication.
     * @param user User name.
     * @return Session for the user. When fails returns null.
     */
    @Path("/auth")
    @POST
    @Timed
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String authenticate(@Auth final String user) {
        return user;
    }
}
