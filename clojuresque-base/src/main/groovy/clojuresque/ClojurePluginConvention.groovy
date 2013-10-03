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

package clojuresque

import clojuresque.tasks.ClojureExecAction
import kotka.gradle.utils.ConfigureUtil

import org.gradle.api.Project
import org.gradle.process.ExecResult

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.lang.Closure

class ClojurePluginConvention {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClojurePluginConvention)
    private final Project project

    public ClojurePluginConvention(Project project) {
        this.project = project
    }

    public ExecResult clojureexec(Closure spec) {
        ClojureExecAction action = ConfigureUtil.configure(
            new ClojureExecAction(project.fileResolver,
                project.configurations.clojuresque),
            spec
        )
        return action.execute()
    }

    public void setAotCompile(boolean f) {
        Util.deprecationWarning(LOGGER, "aotCompile", "clojure.aotCompile")
        project.clojure.aotCompile = f
    }

    public boolean getAotCompile() {
        Util.deprecationWarning(LOGGER, "aotCompile", "clojure.aotCompile")
        return project.clojure.aotCompile
    }

    public void setWarnOnReflection(boolean f) {
        Util.deprecationWarning(LOGGER, "warnOnReflection", "clojure.warnOnReflection")
        project.clojure.warnOnReflection = f
    }

    public boolean getWarnOnReflection() {
        Util.deprecationWarning(LOGGER, "warnOnReflection", "clojure.warnOnReflection")
        return project.clojure.warnOnReflection
    }
}
