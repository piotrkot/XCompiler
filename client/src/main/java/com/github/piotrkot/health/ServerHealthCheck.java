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
package com.github.piotrkot.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.Range;
import lombok.EqualsAndHashCode;
import org.apache.http.client.fluent.Request;

/**
 * Connection to the server Health Check.
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = false)
public final class ServerHealthCheck extends HealthCheck {
    /**
     * HTTP OK response code (minimal as a value).
     */
    private static final int HTTP_OK_MIN = 200;
    /**
     * HTTP OK response code (maximal as a value).
     */
    private static final int HTTP_OK_MAX = 299;
    /**
     * Server location.
     */
    private final transient String addr;

    /**
     * Class constructor.
     * @param address Server address.
     */
    public ServerHealthCheck(final String address) {
        super();
        this.addr = address;
    }

    @Override
    public Result check() throws Exception {
        Result result = Result.healthy();
        if (this.addr.isEmpty() || !Range.closed(HTTP_OK_MIN, HTTP_OK_MAX)
            .contains(
                Request.Get(this.addr).execute().returnResponse()
                    .getStatusLine().getStatusCode()
            )
            ) {
            result = Result.unhealthy("No connection to the server");
        }
        return result;
    }
}
