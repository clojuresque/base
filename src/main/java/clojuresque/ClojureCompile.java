/*-
 * Copyright 2009,2010 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package clojuresque;

import clojure.lang.ISeq;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Symbol;
import clojure.lang.LispReader;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PushbackReader;
import java.io.IOException;

public class ClojureCompile {
    private static final String COMPILE_PATH_PROP = "clojure.compile.path";
    private static final Var COMPILE_PATH =
        RT.var("clojure.core", "*compile-path*");

    private static final String WARN_ON_REFLECTION_PROP =
        "clojure.compile.warn-on-reflection";
    private static final Var WARN_ON_REFLECTION =
        RT.var("clojure.core", "*warn-on-reflection*");

    private static final IPersistentSet NS_FORMS =
        RT.set(Symbol.intern("ns"), Symbol.intern("clojure.core", "ns"));

    private static final String DO_COMPILE_PROP = "clojuresque.compile";
    private static final Var compile = RT.var("clojure.core", "compile");
    private static final Var require = RT.var("clojure.core", "require");

    private static Object findNamespace(Object form) {
        if (!(form instanceof ISeq))
            return null;

        ISeq s = (ISeq)form;

        if (!(NS_FORMS.contains(s.first())))
            return null;

        return s.next().first();
    }

    private static ISeq findNamespaces(ISeq files) throws Exception {
        IPersistentSet seen = RT.set();

        while (files != null) {
            String file = (String)files.first();
            PushbackReader rdr =
                new PushbackReader(
                        new BufferedReader(
                            new FileReader(
                                new File(file))));

            try {
                Object o = LispReader.read(rdr, false, null, false);
                Object ns = findNamespace(o);
                if (ns != null) {
                    seen = (IPersistentSet)seen.cons(ns);
                }
            } finally {
                rdr.close();
            }

            files = files.next();
        }
        return RT.seq(seen);
    }

    public static void compileFiles(ISeq namespaces) throws Exception {
        String compilePath = System.getProperty(COMPILE_PATH_PROP);
        boolean warnOnReflection =
            System.getProperty(WARN_ON_REFLECTION_PROP).equals("true");
        boolean doCompile = System.getProperty(DO_COMPILE_PROP).equals("true");

        IPersistentMap threadBindings = RT.map(
                COMPILE_PATH, compilePath,
                WARN_ON_REFLECTION, warnOnReflection
                );

        Var.pushThreadBindings(threadBindings);
        try {
            while (namespaces != null) {
                if (doCompile)
                    compile.invoke(namespaces.first());
                else
                    require.invoke(namespaces.first());
                namespaces = namespaces.next();
            }
        } finally {
            Var.popThreadBindings();
        }
    }

    public static void main(String[] args) throws Exception {
        ISeq argsSeq = RT.seq(args);
        String command = (String)argsSeq.first();

        if (command.equals("compile")) {
            compileFiles(findNamespaces(argsSeq.next()));
        }
    }
}
