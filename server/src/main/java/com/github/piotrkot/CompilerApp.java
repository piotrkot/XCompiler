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
package com.github.piotrkot;

import com.github.piotrkot.auth.SimpleAuthenticator;
import com.github.piotrkot.resources.CompilerResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.SneakyThrows;
import org.skife.jdbi.v2.DBI;

/**
 * Compiler application.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public final class CompilerApp extends Application<CompilerConf> {
    /**
     * Main runnable method.
     * @param args Arguments.
     */
    @SneakyThrows
    public static void main(final String... args) {
        new CompilerApp().run(args);
    }

    @Override
    public String getName() {
        return "Compiler";
    }

    @Override
    public void initialize(final Bootstrap<CompilerConf> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public void run(final CompilerConf conf,
        final Environment env) {
        env.jersey().register(
            AuthFactory.binder(
                new BasicAuthFactory<>(
                    new SimpleAuthenticator(conf),
                    "Login credentials",
                    String.class
                )
            )
        );
        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(env, conf.getDatabase(), "postgresql");
        env.jersey().register(new CompilerResource(dbi));
    }
}
