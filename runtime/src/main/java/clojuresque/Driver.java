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
import clojure.lang.Symbol;

public class Driver {
    public static void main(String[] args) throws Exception {
        int exitCode = 1;
        final String command = args[0];

        int slash = command.indexOf("/");

        final String namespace = command.substring(0, slash);
        final String function  = command.substring(slash + 1);

        try {
            RT.var("clojure.core", "require").invoke(Symbol.create(namespace));
            RT.var("clojure.core", "apply").invoke(
                    RT.var(namespace, function).deref(),
                    RT.next(RT.seq(args))
            );
            exitCode = 0;
        } finally {
            RT.var("clojure.core", "shutdown-agents").invoke();
        }

        System.exit(exitCode);
    }
}
