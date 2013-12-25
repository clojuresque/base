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
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

class ClojureCompile extends ClojureSourceTask {
    @OutputDirectory
    @Delayed
    def destinationDir

    @InputFiles
    @Delayed
    def classpath

    @Input
    @Delayed
    def aotCompile = false

    @Input
    @Delayed
    def warnOnReflection = false

    def dirMode  = null
    def fileMode = null

    @Delayed
    def jvmOptions = {}

    @TaskAction
    void compile() {
        def destDir = getDestinationDir()
        if (destDir == null) {
            throw new StopExecutionException("destinationDir not set!")
        }
        destDir.mkdirs()

        def options = [
            compileMode:      (getAotCompile()) ? "compile" : "require",
            warnOnReflection: (getWarnOnReflection()),
            sourceFiles:      source.files.collect { it.path }
        ]

        project.clojureexec {
            ConfigureUtil.configure delegate, this.jvmOptions
            systemProperties "clojure.compile.path": destDir.path
            classpath = project.files(
                this.srcDirs,
                destDir,
                this.classpath
            )
            main = "clojuresque.tasks.compile/main"
            standardInput = Util.optionsToStream(options)
        }

        if (!getAotCompile()) {
            project.copy {
                dirMode  = this.dirMode
                fileMode = this.fileMode

                from srcDirs
                into destDir
            }
        }
    }
}
