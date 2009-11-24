/*-
 * Copyright 2009 © Meikel Brandmeyer.
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

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.util.PatternSet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.UnionFileTree
import org.gradle.util.ConfigureUtil

import groovy.lang.Closure

import java.io.File
import java.util.Set

public class DefaultClojureSourceSet implements ClojureSourceSet {
    private DefaultSourceDirectorySet clojure
    private PatternSet clojurePatterns
    private UnionFileTree allClojure
    private boolean warnOnReflection

    public DefaultClojureSourceSet(String displayName,
            FileResolver fileResolver) {
        String desc = String.format("%s Clojure source", displayName)

        clojure = new DefaultSourceDirectorySet(desc, fileResolver)
        clojure.filter.include("**/*.clj")

        clojurePatterns = new PatternSet()
        clojurePatterns.include("**/*.clj")

        allClojure = new UnionFileTree(desc, clojure.matching(clojurePatterns))

        warnOnReflection = false
    }

    public DefaultSourceDirectorySet getClojure() {
        return clojure
    }

    public ClojureSourceSet clojure(Closure closure) {
        ConfigureUtil.configure(closure, getClojure())
        return this
    }

    public FileTree getAllClojure() {
        return allClojure
    }

    public Set<File> getSrcDirs() {
        return clojure.srcDirs
    }

    public boolean getWarnOnReflection() {
        return warnOnReflection
    }

    public void setWarnOnReflection(boolean flag) {
        warnOnReflection = flag
    }
}
