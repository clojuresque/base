/*-
 * Copyright 2009-2015 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Licensed under the EUPL V.1.1 (cf. file EUPL-1.1 distributed with the
 * source code.) Translations in other european languages available at
 * https://joinup.ec.europa.eu/software/page/eupl.
 *
 * Alternatively, you may choose to use the software under the MIT license
 * (cf. file MIT distributed with the source code).
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

        def runtime = [
            "clojuresque/util.clj",
            "clojuresque/tasks/test_junit.clj",
            "clojuresque/tasks/test.clj"
        ].collect { owner.class.classLoader.getResourceAsStream(it) }

        project.clojureexec {
            ConfigureUtil.configure delegate, this.jvmOptions
            classpath = project.files(
                this.srcDirs,
                this.classesDir,
                this.classpath
            )
            standardInput = Util.toInputStream([
                runtime,
                "(clojuresque.tasks.test/main)",
                Util.optionsToStream(options)
            ])
        }
    }
}
