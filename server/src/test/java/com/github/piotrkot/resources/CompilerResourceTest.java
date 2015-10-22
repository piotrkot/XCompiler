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

import com.github.piotrkot.CompilerApp;
import com.github.piotrkot.CompilerConf;
import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import org.apache.http.Header;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * Compressed archive.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class CompilerResourceTest {
    /**
     * Application Rule.
     */
    @ClassRule
    public static final DropwizardAppRule<CompilerConf> APP_RULE =
        new DropwizardAppRule<>(CompilerApp.class, "compiler-test.yml");
    /**
     * Correct authorization header.
     * @checkstyle MultipleStringLiterals (5 lines)
     */
    private static final Header OK_AUTH = new BasicHeader(
        "Authorization",
        String.format(
            "Basic %s",
            Base64.getEncoder().encodeToString("test:test".getBytes())
        )
    );
    /**
     * Incorrect authorization header.
     */
    private static final Header NOK_AUTH = new BasicHeader(
        "Authorization",
        String.format(
            "Basic %s",
            Base64.getEncoder().encodeToString("test:t".getBytes())
        )
    );
    /**
     * HTTP OK response's range.
     */
    private static final Range<Integer> HTTP_RESP_OK = Range.closed(200, 299);

    /**
     * POST authorization method should return name if appropriate.
     * @throws Exception If something fails.
     */
    @Test
    public void testAuthenticate() throws Exception {
        final String uri = String.format(
            "http://localhost:%d/compiler/auth",
            CompilerResourceTest.APP_RULE.getLocalPort()
        );
        Assert.assertEquals(
            "test",
            Request.Post(uri).setHeader(OK_AUTH)
                .execute().returnContent().asString()
        );
        // @checkstyle MagicNumber (2 lines)
        Assert.assertEquals(
            401L,
            (long) Request.Post(uri).setHeader(NOK_AUTH)
                .execute().returnResponse().getStatusLine().getStatusCode()
        );
    }

    /**
     * POST upload file method should be successful.
     * @throws Exception If something fails.
     */
    @Test
    public void testCompileSources() throws Exception {
        final String uri = String.format(
            "http://localhost:%d/compiler/source",
            CompilerResourceTest.APP_RULE.getLocalPort()
        );
        Assert.assertTrue(
            HTTP_RESP_OK.contains(
                Request.Post(uri).setHeader(OK_AUTH)
                    .body(MultipartEntityBuilder.create().addBinaryBody(
                            "file",
                            new ByteArrayInputStream("hello".getBytes()),
                            ContentType.DEFAULT_BINARY,
                            "noname"
                        ).build()
                    ).execute().returnResponse().getStatusLine().getStatusCode()
            )
        );
    }

    /**
     * Appending logs should add them on the server.
     * @throws Exception If something fails.
     */
    @Test
    public void testAppendLogs() throws Exception {
        final String req = "log";
        final String mesg = "message";
        final String uri = String.format(
            "http://localhost:%d/compiler/logs/0",
            CompilerResourceTest.APP_RULE.getLocalPort()
        );
        Assert.assertTrue(
            HTTP_RESP_OK.contains(
                Request.Delete(uri).setHeader(OK_AUTH)
                    .execute().returnResponse().getStatusLine().getStatusCode()
            )
        );
        Assert.assertTrue(
            HTTP_RESP_OK.contains(
                Request.Post(uri).setHeader(OK_AUTH)
                    .bodyForm(
                        Form.form().add(req, mesg).build()
                    ).execute().returnResponse().getStatusLine().getStatusCode()
            )
        );
        Assert.assertTrue(
            HTTP_RESP_OK.contains(
                Request.Post(uri).setHeader(OK_AUTH)
                    .bodyForm(
                        Form.form().add(req, mesg).build()
                    ).execute().returnResponse().getStatusLine().getStatusCode()
            )
        );
        Assert.assertEquals(
            Joiner.on(System.lineSeparator()).join(mesg, mesg),
            Request.Get(uri).setHeader(OK_AUTH)
                .execute().returnContent().asString()
        );
    }
}
