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
import com.github.piotrkot.CompilerConf;
import com.github.piotrkot.auth.User;
import com.github.piotrkot.views.CompilerView;
import com.github.piotrkot.views.ResultView;
import io.dropwizard.auth.Auth;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
     * Authentication configuration.
     */
    private final transient CompilerConf conf;

    /**
     * Class constructor.
     * @param config Configuration.
     */
    public CompilerResource(final CompilerConf config) {
        this.conf = config;
    }

    /**
     * Main view.
     * @param user Logged in user.
     * @return Compiler view.
     */
    @GET
    @Timed
    @Synchronized
    @Produces(MediaType.TEXT_HTML)
    public CompilerView compilerView(@Auth final User user) {
        return new CompilerView(user);
    }

    /**
     * Upload source for compiler.
     * @param user Logged in user.
     * @param stream File input stream.
     * @param details Form data details.
     * @return Compiler result view.
     */
    @Path("/source")
    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response uploadSource(@Auth final User user,
        @FormDataParam("inputFile") final InputStream stream,
        @FormDataParam("inputFile") final FormDataContentDisposition details) {
        Response response = Response.serverError().build();
        try {
            final String request = Request.Post(
                String.format("%s/source", this.conf.getCompiler())
            ).setHeader(this.authorization(user))
                .body(MultipartEntityBuilder.create().addBinaryBody(
                        "file",
                        stream,
                        ContentType.DEFAULT_BINARY,
                        details.getName()
                    ).build()
                ).execute().returnContent().asString();
            response = Response.seeOther(
                URI.create(String.format("/compiler/result/%s", request))
            ).build();
        } catch (final IOException ex) {
            log.error("Cannot connect to the server", ex);
        }
        return response;
    }

    /**
     * Showing server results.
     * @param user Logged in user.
     * @param request User request/session.
     * @return Server compiler results.
     */
    @Path("/result/{request}")
    @GET
    @Timed
    @Produces(MediaType.TEXT_HTML)
    public ResultView showResults(@Auth final User user,
        @PathParam("request") final String request) {
        String results = "Cannot fetch server logs";
        try {
            results = Request.Get(
                String.format("%s/logs/%s", this.conf.getCompiler(), request)
            ).setHeader(this.authorization(user)).execute()
                .returnContent().asString();
        } catch (final IOException ex) {
            log.error("Cannot call GET on server", ex);
        }
        return new ResultView(user, results);
    }

    /**
     * Basic authorization header.
     * @param user User logged in.
     * @return Header.
     */
    private Header authorization(final User user) {
        return new BasicHeader("Authorization", user.toBase64());
    }
}
