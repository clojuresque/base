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

import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction

import java.io.InputStream

public class ClojureCompileTask extends ClojureSourceTask {
    public InputStream compileOrRequire() {
        String cmd = String.format(
            this.class.getResourceAsStream("ClojureCompileDriver.clj").text,
            source.collect {
                String.format("(java.io.File. \"%s\")", it.path)
            }.join(" "),
            project.aotCompile ? "compile" : "require"
        )

        return clojureInput(cmd)
    }

    @TaskAction
    public void compile() {
        if (destinationDir == null) {
            throw new StopExecutionException("destinationDir not set!")
        }
        destinationDir.mkdirs()

        project.javaexec {
            this.jvmClosure()
            systemProperties "clojure.compile.path": this.destinationDir.path
            classpath = project.files(
                this.inputRoots,
                this.destinationDir,
                project.configurations.development,
                this.compileClasspath
            )
            main = "clojure.main"
            args = [ "-" ]
            standardInput = this.compileOrRequire()
        }

        if (!project.aotCompile) {
            project.copy {
                from this.source
                into this.destinationDir
            }
        }
    }
}
