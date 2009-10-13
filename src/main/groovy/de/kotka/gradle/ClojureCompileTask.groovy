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

import java.io.File

public class ClojureCompileTask extends SourceTask {
    private File sourceDir
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
            FileTree source = task.source
            File destDir = task.destinationDir
            Project project = task.project.ant.project
            String cp = task.classpath.getAsPath()
            Java java = new Java()

            Path path = new Path(project)
            path.append(new Path(project, task.sourceDir.path))
            path.append(new Path(project, destDir.path))
            path.append(new Path(project, cp))

            java.project = project
            java.classpath = path
            java.classname = "de.kotka.gradle.ClojureCompile"
            task.addProperty(java, "clojure.compile.path", destDir)
            task.addProperty(java, "clojure.compile.warn-on-reflection",
                "false")
            java.fork = true

            source.each {
                task.addCommandLineArgument(java, it)
            }

            destDir.mkdirs()
            java.execute()
        } ] as Action

        this.doLast(action)
    }

    public File getSourceDir() {
        return sourceDir
    }

    public void setSourceDir(File dir) {
        sourceDir = dir
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
