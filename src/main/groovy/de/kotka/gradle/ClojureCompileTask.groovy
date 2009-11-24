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

package de.kotka.gradle

import org.gradle.api.Action
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceTask

import org.apache.tools.ant.Project
import org.apache.tools.ant.taskdefs.Java
import org.apache.tools.ant.types.Environment
import org.apache.tools.ant.types.Path

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io.File
import java.util.Set

public class ClojureCompileTask extends SourceTask {
    private static Logger logger = LoggerFactory.getLogger(ClojureCompileTask)

    private ClojureSourceSet sourceSet
    private File destinationDir
    private FileCollection classpath

    public ClojureCompileTask() {
        super()
        initialise()
    }

    public ClojureCompileTask(org.gradle.api.Project project, String string) {
        super()
        initialise()
    }

    private void initialise() {
        def action = [ execute: { ClojureCompileTask task ->
            Set<File> sourceDirs = task.sourceSet.srcDirs
            File destDir = task.destinationDir
            FileTree source = task.source
            FileCollection cp = task.classpath

            Map args = [
                classname:    'de.kotka.gradle.ClojureCompile',
                failOnError:  true,
                fork:         true,
                classpathref: 'compile.classpath'
            ]

            ant.path(id: 'compile.classpath') {
                sourceDirs.each {
                    logger.debug("Add {} (source dir) to Ant classpath!", it)
                    pathelement(location: it)
                }

                logger.debug("Add {} (dest dir) to Ant classpath!",
                    destDir.path)
                pathelement(location: destDir.path)

                cp.each {
                    logger.debug("Add {} to Ant classpath!", it)
                    pathelement(location: it)
                }
            }

            destDir.mkdirs()

            ant.java(args) {
                sysproperty(key: "clojure.compile.path", value: destDir)
                sysproperty(key: "clojure.compile.warn-on-reflection",
                    value: "false")
                source.each {
                    arg(value: it)
                }
            }
        } ] as Action

        this.doLast(action)
    }

    public ClojureSourceSet getSourceSet() {
        return sourceSet
    }

    public void setSourceSet(ClojureSourceSet set) {
        sourceSet = set
    }

    public File getDestinationDir() {
        return destinationDir
    }

    public void setDestinationDir(File dir) {
        destinationDir = dir
    }

    public FileCollection getClasspath() {
        return classpath
    }

    public void setClasspath(FileCollection path) {
        classpath = path
    }

    public void addCommandLineArgument(Java java, File f) {
        java.createArg().setFile(f)
    }

    public void addCommandLineArgument(Java java, String s) {
        java.createArg().setValue(s)
    }

    public void addProperty(Java java, String k, File f) {
        Environment.Variable v = new Environment.Variable()
        v.setKey(k)
        v.setFile(f)
        java.addSysproperty(v)
    }

    public void addProperty(Java java, String k, String s) {
        Environment.Variable v = new Environment.Variable()
        v.setKey(k)
        v.setValue(s)
        java.addSysproperty(v)
    }
}
