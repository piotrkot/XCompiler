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

package com.github.piotrkot.auth;

import com.github.piotrkot.CompilerConf;
import com.google.common.base.Optional;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;

/**
 * Very simple authenticator.
 * @author Piotr Kotlicki (piotr.kotlicki@lhsystems.com)
 * @version $Id$
 * @since 1.0
 */
@Slf4j
public final class SimpleAuthenticator implements
    Authenticator<BasicCredentials, User> {
    /**
     * Authentication configuration.
     */
    private final transient CompilerConf conf;

    /**
     * Class constructor.
     * @param config Configuration.
     */
    public SimpleAuthenticator(final CompilerConf config) {
        this.conf = config;
    }

    @Override
    public Optional<User> authenticate(final BasicCredentials cred) {
        Optional<User> option = Optional.absent();
        try {
            final User user = new User(cred.getUsername(), cred.getPassword());
            Request.Post(String.format("%s/auth", this.conf.getCompiler()))
                .setHeader("Authorization", user.toBase64())
                .execute().returnContent().asString();
            option = Optional.of(user);
        } catch (final IOException ex) {
            log.error("Authentication failure", ex);
        }
        return option;
    }
}
