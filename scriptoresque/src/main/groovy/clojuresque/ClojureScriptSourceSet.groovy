/*-
 * Copyright 2012 © Meikel Brandmeyer.
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

class ClojureScriptSourceSet {
    private final SourceDirectorySet clojureScript
    private final UnionFileTree allClojureScript
    private final PatternFilterable clojureScriptPatterns = new PatternSet()

    public ClojureScriptSourceSet(String displayName, FileResolver fileResolver) {
        clojureScript = new DefaultSourceDirectorySet(String.format("%s ClojureScript source", displayName), fileResolver)
        clojureScript.filter.include("**/*.cljs")
        clojureScriptPatterns.include("**/*.cljs")
        allClojureScript = new UnionFileTree(String.format("%s ClojureScript source", displayName), clojureScript.matching(clojureScriptPatterns))
    }

    public SourceDirectorySet getClojureScript() {
        return clojureScript
    }

    public ClojureScriptSourceSet clojureScript(Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, this.clojureScript)
        return this
    }

    public PatternFilterable getClojureScriptSourcePatterns() {
        return clojureScriptPatterns
    }

    public FileTree getAllClojureScript() {
        return allClojureScript
    }

    public void clojureScriptIncludeNamespace(String pattern) {
        clojureScript.include(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".cljs"
        )
    }

    public void clojureScriptExcludeNamespace(String pattern) {
        clojureScript.exclude(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".cljs"
        )
    }
}
