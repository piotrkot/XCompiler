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
package com.github.piotrkot.views;

import com.github.piotrkot.auth.User;
import io.dropwizard.views.View;
import lombok.Getter;

/**
 * Compiler view.
 *
 * @author Piotr Kotlicki (piotr.kotlicki@gmail.com)
 * @version $Id$
 * @since 1.0
 */
@SuppressWarnings({"PMD.BeanMembersShouldSerialize", "PMD.UnusedPrivateField"
    , "PMD.SingularField"})
public final class CompilerView extends View {
    /**
     * Template location.
     */
    private static final String LOCATION = "mustache/compiler.mustache";
    /**
     * Logged in user.
     */
    @Getter
    private final User usr;

    /**
     * Compiler view.
     * @param user Logged in user.
     */
    public CompilerView(final User user) {
        super(CompilerView.LOCATION);
        this.usr = user;
    }
}
