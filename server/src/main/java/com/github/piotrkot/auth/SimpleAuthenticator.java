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

/**
 * Very simple authenticator.
 * @author Piotr Kotlicki (piotr.kotlicki@lhsystems.com)
 * @version $Id$
 * @since 1.0
 */
public final class SimpleAuthenticator implements
    Authenticator<BasicCredentials, String> {
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
    public Optional<String> authenticate(final BasicCredentials cred) {
        Optional<String> option = Optional.absent();
        if (this.conf.getUsername().equals(cred.getUsername())
            && this.conf.getPassword().equals(cred.getPassword())) {
            option = Optional.of(cred.getUsername());
        }
        return option;
    }
}
