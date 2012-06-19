(ns clojuresque.tasks.compile
  (:use
    [clojuresque.cli :only (deftask)]
    [clojuresque.util :only (namespaces)]))

(deftask main
  "Compile (or at least require) the namespaces contained in the named
  files. Optionally reflection warnings might be emitted."
  [[compile?            c "Compile the namespaces."]
   [require?            r "Require the namespaces."]
   [warn-on-reflection? w "Turn on reflections warnings."]
   files]
  (let [mode (cond
               compile clojure.core/compile
               require clojure.core/require
               :else   (throw
                         (Exception.
                           "You must choose a mode: compile or require.")))
        namespaces (namespaces files)]
    (binding [*warn-on-reflection* warn-on-reflection
              *compile-path*       (System/getProperty "clojure.compile.path")]
      (doseq [nspace namespaces]
        (mode nspace)))))
