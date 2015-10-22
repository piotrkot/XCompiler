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

import java.nio.charset.Charset;
import java.util.Base64;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Application user.
 * @author Piotr Kotlicki (piotr.kotlicki@lhsystems.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
@EqualsAndHashCode
@ToString
public final class User {
    /**
     * User name.
     */
    @Getter
    private final transient String name;
    /**
     * User password.
     */
    @Getter
    private final transient String pass;

    /**
     * Class constructor.
     * @param username User name.
     * @param password User password.
     */
    public User(final String username, final String password) {
        this.name = username;
        this.pass = password;
    }

    /**
     * Base64 encrypted representation of User.
     * @return Base64 encrypted string.
     * @checkstyle MethodName (2 lines)
     */
    public String toBase64() {
        return String.format(
            "Basic %s",
            Base64.getEncoder().encodeToString(
                String.format("%s:%s", this.name, this.pass)
                    .getBytes(Charset.forName("UTF-8"))
            )
        );
    }
}
