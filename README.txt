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

## Getting started

Create a sandbox directory. Add a subdirectory `lib` and put the `clojure.jar`
as well as the `clojuresque.jar` there. Create some test namespace in
`src/main/clojure/test/example.clj`:

    (ns test.example
      (:gen-class))
    
    (defn -main
      []
      (println "Hello, World!"))

Now create the `build.gradle` script:

    buildscript {
        repositories {
            flatDir name: 'lib', dirs: 'lib'
        }
        dependencies {
            classpath name: 'clojuresque'
        }
    }
    
    usePlugin(de.kotka.gradle.ClojurePlugin)
    
    repositories {
        flatDir name: 'lib', dirs: 'lib'
    }
    
    configurations {
        compileOnly {
            visible = false
            transitive = false
        }
        compile.extendsFrom(compileOnly)
    }
    
    dependencies {
        compileOnly name: 'clojuresque'
        compile name: 'clojure'
    }

This looks rather verbose, but I hope to simplify it later on. First we
declare a dependency (and where to find it) for the build script itself.
This is necessary, since the we need the plugin already to define the build
logic itself.

Next we define the repositories and dependencies for the project being
build. Since we need the `ClojureCompile` class from the `clojuresque.jar`
in the classpath for compilation we introduce a special `compileOnly`
configuration. It's private the finally deployed archives of the project
won't depend on it at runtime. Additionally we add a dependency on the
new configuration to the pre-defined `compile` configuration.

Now build the project with `gradle build` and test the result:

    ceres:..gradle-plugin/test% java -cp lib/clojure.jar:build/libs/test-unspecified.jar test.example
    Hello, World!
    ceres:..gradle-plugin/test%

Please note, that the name of the generated jar depends on how you named
the sandbox directory.

## Issues

This is **alpha** software! Expect problems! Please report issues in the
bugtracker at [bitbucket in the 'Issues' tab][cg].

-- 
Meikel Brandmeyer <mb@kotka.de>
Frankfurt am Main, November 2009

[Gradle]: http://www.gradle.org
[Groovy]: http://groovy.codehaus.org
[clj]:    http://clojure.org
[cg]:     http://bitbucket.org/kotarak/clojuresque
