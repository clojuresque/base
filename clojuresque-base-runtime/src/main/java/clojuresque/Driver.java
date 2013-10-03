/*-
 * Copyright 2009-2013 © Meikel Brandmeyer.
 * All rights reserved.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package clojuresque;

import clojure.lang.RT;
import clojure.lang.Var;

public class Driver {
    static final Var require = RT.var("clojure.core", "require");
    static final Var apply   = RT.var("clojure.core", "apply");
    static final Var symbol  = RT.var("clojure.core", "symbol");
    static final Var seq     = RT.var("clojure.core", "seq");
    static final Var next    = RT.var("clojure.core", "next");
    static final Var sa      = RT.var("clojure.core", "shutdown-agents");

    public static void main(String[] args) throws Exception {
        int exitCode = 1;
        final String command = args[0];

        try {
            require.invoke(symbol.invoke("clojuresque.util"));
            final Var resolve  = RT.var("clojuresque.util", "resolve-required");
            final Var driverFn = (Var)resolve.invoke(command);

            if (driverFn == null)
                throw new Exception(String.format("Unknown command: %s", command));

            Boolean result = (Boolean)apply.invoke(
                    driverFn.deref(),
                    next.invoke(seq.invoke(args))
            );
            if (result)
                exitCode = 0;
        } finally {
            sa.invoke();
        }

        System.exit(exitCode);
    }
}
