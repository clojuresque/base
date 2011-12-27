(ns clojuresque.codox.main
  "Main namespace for generating documentation"
  (:use [clojuresque.codox.reader :only (read-namespaces)]
        [clojuresque.codox.writer.html :only (write-docs)]))

(defn generate-docs
  "Generate documentation from source files."
  ([]
     (generate-docs {}))
  ([options]
     (let [namespaces (apply read-namespaces (:sources options))]
       (write-docs (assoc options :namespaces namespaces)))))
