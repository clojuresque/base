(ns clojuresque.tasks.test
  (:use
    [clojure.test :only (run-tests)]
    [clojuresque.cli :only (deftask)]
    [clojuresque.util :only (namespaces)]))

(deftask main
  "Run all tests in the namespaces of the given files by virtue of clojure.test."
  [files]
  (let [namespaces (namespaces files)]
    (apply require namespaces)
    (let [result (apply run-tests namespaces)]
      (when (or (pos? (:fail result)) (pos? (:error result)))
        (System/exit 1)))))
