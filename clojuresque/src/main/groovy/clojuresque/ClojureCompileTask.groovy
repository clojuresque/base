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
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

import java.io.File
import java.io.InputStream

import groovy.lang.Closure

public class ClojureCompileTask extends ClojureSourceTask {
    def File destinationDir
    def FileCollection classpath
    def SourceDirectorySet clojureRoots
    def Closure jvmOptions = {}

    @OutputDirectory
    public File getDestinationDir() {
        return this.destinationDir
    }

    @InputFiles
    public FileCollection getClasspath() {
        return this.classpath
    }

    public void classpath(Object... coll) {
        classpath = classpath.plus(project.files(coll))
    }

    @TaskAction
    public void compile() {
        if (destinationDir == null) {
            throw new StopExecutionException("destinationDir not set!")
        }
        destinationDir.mkdirs()

        List<String> options = []
        if (project.aotCompile) {
            options.add("--compile")
        } else {
            options.add("--require")
        }
        if (project.warnOnReflection) {
            options.add("--warn-on-reflection")
        }


        project.clojureexec {
            this.jvmOptions()
            systemProperties "clojure.compile.path": this.destinationDir.path
            classpath = project.files(
                this.clojureRoots.srcDirs,
                this.destinationDir,
                this.classpath
            )
            main = "clojuresque.tasks.compile/main"
            args = options + this.source.files
        }

        if (!project.aotCompile) {
            project.copy {
                from this.source
                into this.destinationDir
            }
        }
    }
}
