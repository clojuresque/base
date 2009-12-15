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

package clojuresque

import org.gradle.api.Project

class ClojurePluginConvention {
    Project project
    boolean warnOnReflection
    boolean aotCompile

    public ClojurePluginConvention(Project project) {
        this.project = project
        this.warnOnReflection = false
        this.aotCompile = false
    }

    public void clojureSnapshotsRepo() {
        project.repositories.mavenRepo name: 'clojureHudson', urls: 'http://build.clojure.org/snapshots'
    }

    public void clojarsRepo() {
        project.repositories.mavenRepo name: 'clojars', urls: 'http://clojars.org/repo'
    }

    public boolean getWarnOnReflection() {
        return this.warnOnReflection
    }

    public void setWarnOnReflection(boolean flag) {
        this.warnOnReflection = flag
    }

    public boolean getAotCompile() {
        return this.aotCompile
    }

    public void setAotCompile(boolean flag) {
        this.aotCompile = flag
    }
}
