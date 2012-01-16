/*-
 * Copyright 2009-2011 © Meikel Brandmeyer.
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
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import java.io.File
import java.io.InputStream

import groovy.lang.Closure

public class ClojureTestTask extends ClojureSourceTask {
    def File classesDir
    def FileCollection testClasspath
    def SourceDirectorySet testRoots
    def Closure jvmOptions = {}
    def List<String> tests = []

    @InputFiles
    public FileCollection getTestClasspath() {
        return this.testClasspath
    }

    public void testClasspath(Object... coll) {
        testClasspath = testClasspath.plus(project.files(coll))
    }

    @TaskAction
    public void runTests() {
        project.clojureexec {
            this.jvmOptions()
            classpath = project.files(
                this.testRoots.srcDirs,
                this.classesDir,
                this.testClasspath
            )
            if (tests.size() == 0) {
                main = "clojuresque.tasks.test/test-namespaces"
                args = this.source.files
            } else {
                main = "clojuresque.tasks.test/test-vars"
                args = tests
            }
        }
    }
}
