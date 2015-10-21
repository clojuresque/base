(ns clojuresque.hiccup.core
  "Library for rendering a tree of vectors into a string of HTML.
  Pre-compiles where possible for performance.")

(refer 'clojuresque.hiccup.compiler)
(refer 'clojuresque.hiccup.util)

(defmacro html
  "Render Clojure data structures to a string of HTML."
  [options & content]
  (if-let [mode (and (map? options) (:mode options))]
    (binding [*html-mode* mode]
      `(binding [*html-mode* ~mode]
         ~(apply compile-html content)))
    (apply compile-html options content)))

(def ^{:doc "Alias for hiccup.util/escape-html"}
  h escape-html)
