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
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Upload

class ClojurePluginConvention {
    Project project
    boolean warnOnReflection
    boolean aotCompile

    private boolean didConfigureDeployerJars = false

    public ClojurePluginConvention(Project project) {
        this.project = project
        this.warnOnReflection = false
        this.aotCompile = false
    }

    public void clojureSnapshotsRepo(RepositoryHandler repos) {
        repos.mavenRepo name: 'clojureHudson', urls: 'http://build.clojure.org/snapshots'
    }

    public void clojarsRepo(RepositoryHandler repos) {
        repos.mavenRepo name: 'clojars', urls: 'http://clojars.org/repo'
    }

    public void gradleHomeRepo(RepositoryHandler repos) {
        String home = System.getenv('GRADLE_HOME')
        if (home != null) {
            repos.flatDir name: 'gradleHome', dirs: new File(home + '/lib')
        }
    }

    private void configureDeployerJars() {
        if (!didConfigureDeployerJars) {
            project.configurations {
                clojarsDeployerJars {
                    visible = false
                    description = 'Private configuration to hold ssh wagon dependencies'
                }
            }
            project.dependencies {
                clojarsDeployerJars 'org.apache.ant:ant-jsch:1.7.0'
            }

            project.ant.taskdef name: 'clojarsScpDeploy', classname: 'org.apache.tools.ant.taskdefs.optional.ssh.Scp', classpath: project.configurations.clojarsDeployerJars.asPath

            didConfigureDeployerJars = true
        }
    }

    public void configureClojarsDeploy(Upload task) {
        File dummyRepo = new File(project.buildDir, 'deploy')

        configureDeployerJars()

        project.configure(task) {
            repositories.mavenDeployer {
                name = 'dummyClojarsLocalDeployer'
                description  = 'Dummy deployer to trick gradle into pom generation'
                repository(url: 'file://' + dummyRepo.path)
            }
        }

        task.doLast {
            project.ant.clojarsScpDeploy(todir: 'clojars@clojars.org:', keyfile: new File(project.clojarsKeyfile).absolutePath, passphrase: project.clojarsPassphrase) {
                fileset(dir: dummyRepo, excludes: '**/maven-metadata.xml*')
            }
        }
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
