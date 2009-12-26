# clojuresque – a Clojure plugin for Gradle

## What is Gradle?

[Gradle][] is a build system written in Java and [Groovy][]. One advantage
of [Gradle][] is, that the build script itself is also a [Groovy][] script.
That means whatever can be done in [Groovy][] can be done in the build
script. This is useful to abstract away common patterns in the build like
repetition or conditional decisions.

On top of that [Gradle][] provides a convenient build system which comes
in form of different plugins. Each plugin defines certain conventions which
(if followed) automasie 95% of the build completely. Still the conventions
are freely configurable to be adapted to different project structures.

## What is clojuresque?

[clojuresque][cg] is now a plugin for [Gradle][], which adds [Clojure][clj]
support. It allows compilation with automatic namespace recognition. The
plugin is based on the Java plugin and hooks into the standard configurations
and archives.

Contrary to the [Groovy][] plugin, there is no special `groovy` configuration.
Java code in the same project might want to hook into [Clojure][] as well. So
the [Clojure][] jar must be available also to the Java portion of the project.

## Caveats

* Sitting between the chairs of [Clojure][clj]'s namespace based and the
  other languages file based compilation, you have to include or exclude
  namespaces based on filenames for now.

* Automatic recognition of namespaces only works if the `ns` form is the
  first in the file. Comments may precede the form. The symbol is allowed
  to be fully qualified: `clojure.core/ns`.

* In order for the scp deployment of [Clojars][cr] to work, you have to
  replace the jsch jar in `${{GRADLE_HOME}/lib` with jsch-0.1.29.jar.
  Otherwise the transfer might hang.  See this [ant bug][antbug] for more
  information.

## Usage

Create a `build.gradle` script in the root directory of your project. *Note
that gradle derives the project name from the name of this directory!*

    buildscript {
        repositories {
            mavenRepo name: 'clojars', urls: 'http://clojars.org/repo'
        }
        dependencies {
            classpath 'clojuresque:clojuresque:1.2.0'
        }
    }
    
    group = 'example.group'
    version = '1.0.0'
    
    usePlugin(cojuresque.ClojurePlugin)
    
    warnOnReflection = true
    aotCompile = true
    
    clojureSnapshotsRepo(repositories)
    clojarsRepo(repositories)
    gradleHomeRepo(repositories)
    
    dependencies {
        compile 'org.clojure:clojure:1.1.0-master-SNAPSHOT'
    }
    
    configureClojarsDeploy(uploadArchives)

A small walkthrough:

* The `buildscript` part defines a dependency and automatically fetches
  clojuresque from Clojars.
* The `group` and `version` properties define the respective attributes of
  your project. They are required for the POM generation.
* `usePlugin` basically loads the clojure plugin.
* `warnOnReflection` turns on the reflection warnings of the clojure compiler
* `aotCompile` specifies whether to produce a source jar or an AOT compiled
  jar. The default is produce a source jar, because they also tend to be
  smaller. [This issue was discussed on the Google group.][aot]
* `clojureSnapshotsRepo` tells gradle where to find the clojure and contrib
  snapshots, ie. where it can find the [Hudson server][hudson].
* `clojarsRepo` adds the [Clojars Repository][cr] in a similar way.
* `gradleHomeRepo` is used to add `${GRADLE_HOME}/lib` as a local repository.
  This is useful to minimise the download of the scp ant task dependencies.
  *Note that `GRADLE_HOME` must be set for this to work!* *Note, that this
  must come before a possible `mavenCentral()`!*
* In the `dependencies` section we add a dependency on the current master
  SNAPSHOT.
* Last but not least: `configureClojarsDeploy` configures the given `Upload`
  task to deploy to the [Clojars Repository][cr]

This can be even more simplified by registering clojuresque with your local
Gradle installation. Put the clojuresque jar in `${GRADLE_HOME}/lib` and add
the following line to `${GRADLE_HOME}/plugin.properties`:

    clojure=clojuresque.ClojurePlugin

From now on you can skip the whole `buildscript` stuff and just use
`usePlugin('clojure')` to load the plugin.

## Clojars Deployment

Additional to configuration of the `Upload` tasks with `configureClojarsDeploy`
you also have to specify the location of the keyfile and the passphrase you
use to access [Clojars][cr]. This can be done independent of the projects
in a file called `${HOME}/.gradle/gradle.properties`.

    clojarsKeyfile = /Users/mb/.ssh/id_dsa
    clojarsPassphrase = My super secret passphrase

**Be sure to correctly secure this file! Or your key might be compromised!**

## Issues

This is **alpha** software! Expect problems! Please report issues in the
bugtracker at [bitbucket in the 'Issues' tab][cg].

-- 
Meikel Brandmeyer <mb@kotka.de>
Frankfurt am Main, December 2009

[Gradle]: http://www.gradle.org
[Groovy]: http://groovy.codehaus.org
[clj]:    http://clojure.org
[cg]:     http://bitbucket.org/kotarak/clojuresque
[cr]:     http://clojars.org
[hudson]: http://build.clojure.org
[antbug]: https://issues.apache.org/bugzilla/show_bug.cgi?id=41090
[aot]:    http://groups.google.com/group/clojure/browse_thread/thread/6cef4fcf523f936/3cfe17ba2d2a8a23
