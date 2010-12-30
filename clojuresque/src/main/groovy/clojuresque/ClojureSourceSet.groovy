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

package clojuresque

import groovy.lang.Closure

import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.UnionFileTree
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.util.ConfigureUtil

class ClojureSourceSet {
    private final SourceDirectorySet clojure
    private final UnionFileTree allClojure
    private final PatternFilterable clojurePatterns = new PatternSet()

    public ClojureSourceSet(String displayName, FileResolver fileResolver) {
        clojure = new DefaultSourceDirectorySet(String.format("%s Clojure source", displayName), fileResolver)
        clojure.filter.include("**/*.clj")
        clojurePatterns.include("**/*.clj")
        allClojure = new UnionFileTree(String.format("%s Clojure source", displayName), clojure.matching(clojurePatterns))
    }

    public SourceDirectorySet getClojure() {
        return clojure
    }

    public ClojureSourceSet clojure(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, this.clojure)
        return this
    }

    public PatternFilterable getClojureSourcePatterns() {
        return clojurePatterns
    }

    public FileTree getAllClojure() {
        return allClojure
    }

    public void clojureIncludeNamespace(String pattern) {
        clojure.include(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj"
        )
    }

    public void clojureExcludeNamespace(String pattern) {
        clojure.exclude(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj"
        )
    }
}
