/*-
 * Copyright 2009-2013 © Meikel Brandmeyer.
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

package clojuresque.tasks

import clojuresque.Util

import kotka.gradle.utils.ConfigureUtil
import kotka.gradle.utils.Delayed

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

class ClojureTest extends ClojureSourceTask {
    @Delayed
    def classesDir

    @InputFiles
    @Delayed
    def classpath

    @Delayed
    def jvmOptions = {}

    @Input
    def junit = false

    @Delayed
    def junitOutputDir = null

    def tests = []

    @TaskAction
    void runTests() {
        def junitDir = getJunitOutputDir()
        if (junit) {
            if (junitDir == null) {
                throw new StopExecutionException("junitOutputDir is not set!")
            }
            junitDir.mkdirs()
        }

        def options = [
            sourceFiles:    source.files*.path,
            tests:          tests,
            junit:          junit,
            junitOutputDir: junitDir?.path,
        ]

        project.clojureexec {
            ConfigureUtil.configure delegate, this.jvmOptions
            classpath = project.files(
                this.srcDirs,
                this.classesDir,
                this.classpath
            )
            main = "clojuresque.tasks.test/main"
            standardInput = Util.optionsToStream(options)
        }
    }
}
