/*-
 * Copyright 2011-2013 © Meikel Brandmeyer.
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

class ClojureDoc extends ClojureSourceTask {
    @OutputDirectory
    @Delayed
    def destinationDir

    @InputFiles
    @Delayed
    def classpath

    @Delayed
    def jvmOptions

    @Input
    def codox = [:]

    @TaskAction
    void clojuredoc() {
        def destDir = getDestinationDir()
        if (destDir == null) {
            throw new StopExecutionException("destinationDir not set!")
        }
        destDir.mkdirs()

        def options = [
            destinationDir:  destDir.path,
            project: [
                name:        project.name ?: "",
                description: project.description ?: "",
                version:     project.version ?: ""
            ],
            codox:           codox,
            sourceDirs:      srcDirs.files.collect {
                relativize(it, project.projectDir)
            },
            sourceFiles:     source*.path
        ]

        def runtime = [
            "clojuresque/util.clj",
            "clojuresque/hiccup/util.clj",
            "clojuresque/hiccup/compiler.clj",
            "clojuresque/hiccup/core.clj",
            "clojuresque/hiccup/def.clj",
            "clojuresque/hiccup/element.clj",
            "clojuresque/hiccup/page.clj",
            "clojuresque/codox/utils.clj",
            "clojuresque/codox/reader.clj",
            "clojuresque/codox/main.clj",
            "clojuresque/codox/writer/html.clj",
            "clojuresque/tasks/doc.clj"
        ].collect { owner.class.classLoader.getResourceAsStream it }

        project.clojureexec {
            ConfigureUtil.configure delegate, this.jvmOptions
            classpath = project.files(
                this.srcDirs,
                this.classpath
            )
            standardInput = Util.toInputStream([
                runtime,
                "(clojuresque.tasks.doc/main)",
                Util.optionsToStream(options)
            ])
        }

        [
            "css/default.css",
            "js/page_effects.js",
            "js/jquery.min.js"
        ].each { f ->
            def dest = project.file("${destinationDir}/${f}")
            println "${f}"
            if (!dest.exists()) {
                dest.parentFile.mkdirs()
                dest.withOutputStream { output ->
                    def input = this.class.classLoader.
                        getResourceAsStream("clojuresque/codox/${f}")
                    output << input
                    input.close()
                }
            }
        }
    }

    def relativize(path, projectDir) {
        def pathS = path.path
        def pdirS = projectDir.path

        pathS.substring(pdirS.length() + 1)
    }
}
