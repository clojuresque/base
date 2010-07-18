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

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

import groovy.lang.Closure

import java.io.File
import java.io.ByteArrayInputStream
import java.util.ArrayList;
import java.util.List;

public class ClojureSourceTask extends SourceTask {
    def Closure jvmClosure = {}
    def FileCollection compileClasspath
    def File destinationDir
    def List<Object> inputRoots = new ArrayList<Object>()

    public ByteArrayInputStream clojureInput(String cmd) {
        return new ByteArrayInputStream(cmd.bytes)
    }

    @OutputDirectory
    public File getDestinationDir() {
        return this.destinationDir
    }

    public ClojureCompileTask inputRoot(Object... roots) {
        roots.each { this.inputRoots.add(it) }
        return this
    }

    public ClojureCompileTask jvm(Closure closure) {
        this.jvmClosure = closure
        return this
    }

    /* Duplicate the functionality of ClojureSourceSet. */
    public ClojureCompileTask includeNamespace(String pattern) {
        include(pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj")
        return this
    }

    public ClojureCompileTask excludeNamespace(String pattern) {
        exclude(pattern.replaceAll("-", "_").replaceAll("\\.", "/") + ".clj")
        return this
    }
}
