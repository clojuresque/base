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

* [Clojars][cr] deployment is currently broken, because the clojars server
  does not like a single Java implementation of ssh. With other servers like
  the „real“ ssh it works quite well, though. I suspect the issue to be on
  the clojars side.

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

**Note: Does work only on Unix/Mac OS via shell out to scp. Does not
work on Windows! Make sure you have agent running which handles your
clojars key.**

## Issues

This is **alpha** software! Expect problems! Please report issues in the
bugtracker at [the lighthouse tracker][lh].

-- 
Meikel Brandmeyer <mb@kotka.de>
Frankfurt am Main, January 2010

[Gradle]: http://www.gradle.org
[Groovy]: http://groovy.codehaus.org
[clj]:    http://clojure.org
[cg]:     http://bitbucket.org/kotarak/clojuresque
[lh]:     http://kotka.lighthouseapp.com/projects/45093-clojuresque/overview
[cr]:     http://clojars.org
[hudson]: http://build.clojure.org
[antbug]: https://issues.apache.org/bugzilla/show_bug.cgi?id=41090
[aot]:    http://groups.google.com/group/clojure/browse_thread/thread/6cef4fcf523f936/3cfe17ba2d2a8a23
