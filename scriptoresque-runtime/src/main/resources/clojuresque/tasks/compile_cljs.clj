(ns clojuresque.tasks.compile-cljs
  (:use
    [clojuresque.cli :only (deftask)])
  (:require
    [cljs.closure :as cljsc]))

(deftask main
  "Compile the clojurescript files in the named directory to the given output
  directory. Use the specified optimization level."
  [[input-dir     i "Input directory."]
   [output-dir    d "Output directory."]
   [output-file   o "Output file." "all.js"]
   [optimizations O "Optimisation level. [none,simple,whitespace,advanced.]"]
   [target        t "Target option. [none,nodejs]"]
   [pretty?       p "Pipe compiler output through pretty printer."]]
  (let [optimizations (and optimizations (keyword optimizations))
        target        (and target (keyword target))
        options       (merge {:output-to    output-file
                              :output-dir   output-dir
                              :pretty-print pretty}
                             (when optimizations
                               {:optimizations optimizations})
                             (when target
                               {:target target}))]
    (cljsc/build input-dir options)))
