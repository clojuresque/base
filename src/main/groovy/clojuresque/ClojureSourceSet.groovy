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

import org.gradle.api.Project
import org.gradle.api.file.FileTree

import groovy.lang.Closure

import java.io.File

class ClojureSourceSet {
    private Project project
    private File sourceDir
    @Delegate private FileTree sources

    public ClojureSourceSet(Project project, Object dir) {
        this.project = project
        this.sources = project.fileTree {
            include "**/*.clj"
        }
        setSourceDir(dir)
    }

    public void setSourceDir(Object dir) {
        this.sourceDir = project.file(dir)
        this.sources.from this.sourceDir
    }

    public File getSourceDir() {
        return this.sourceDir
    }

    public void includeNamespace(String pattern) {
        sources.include(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj"
        )
    }

    public void excludeNamespace(String pattern) {
        sources.exclude(
            pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj"
        )
    }
}
