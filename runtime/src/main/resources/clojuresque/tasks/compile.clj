(ns clojuresque.tasks.compile
  (:use
    clojuresque.cli))

(defn find-namespace
  [file]
  (let [of-interest '#{ns clojure.core/ns}
        eof         (Object.)
        input       (clojure.lang.LineNumberingPushbackReader.
                      (java.io.FileReader. file))
        in-seq      (take-while #(not (identical? % eof))
                                (repeatedly #(read input false eof)))
        candidate   (first
                      (drop-while
                        #(or (not (instance? clojure.lang.ISeq %))
                             (not (contains? of-interest (first %))))
                        in-seq))]
    (when candidate
      (second candidate))))

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
        seen (atom #{})
        namespaces (map find-namespace files)]
    (binding [*warn-on-reflection* warn-on-reflection
              *compile-path*       (System/getProperty "clojure.compile.path")]
      (doseq [nspace namespaces
              :when  (and nspace (not (contains? @seen nspace)))]
        (swap! seen conj nspace)
        (mode nspace)))))
