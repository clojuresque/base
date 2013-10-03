(ns clojuresque.util)

(defn namespace-of-file
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

(defn namespaces
  [files]
  (distinct (keep namespace-of-file files)))

(defn safe-require
  [& nspaces]
  (binding [*unchecked-math*     *unchecked-math*
            *warn-on-reflection* *warn-on-reflection*]
    (apply require nspaces)))

(defn resolve-required
  [fully-qualified-sym]
  (let [slash  (.indexOf ^String fully-qualified-sym "/")
        nspace (symbol (subs fully-qualified-sym 0 slash))
        hfn    (symbol (subs fully-qualified-sym (inc slash)))]
    (safe-require nspace)
    (ns-resolve nspace hfn)))
