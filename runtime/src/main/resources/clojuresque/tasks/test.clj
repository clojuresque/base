(ns clojuresque.tasks.test
  (:use
    [clojure.test :only (run-tests) :as t]
    [clojuresque.cli :only (deftask)]
    [clojuresque.util :only (namespaces)]))

(defn check-result
  [result]
  (when (or (pos? (:fail result)) (pos? (:error result)))
    (System/exit 1)))

(deftask test-namespaces
  "Run all tests in the namespaces of the given files by virtue of clojure.test."
  [files]
  (let [namespaces (namespaces files)]
    (apply require namespaces)
    (check-result (apply run-tests namespaces))))

; For now: do stuff manually for explicitly named tests.
(deftask test-vars
  "Run only the named tests by virtue of clojure.test."
  [tests]
  (let [tests (group-by (comp symbol namespace) (map read-string tests))]
    (apply require (keys tests))
    (binding [t/*report-counters* (ref t/*initial-report-counters*)]
      (doseq [[nspace test-vars] tests]
        (let [ns-obj          (the-ns nspace)
              once-fixture-fn (t/join-fixtures
                                (::t/once-fixtures (meta ns-obj)))
              each-fixture-fn (t/join-fixtures
                                (::t/each-fixtures (meta ns-obj)))]
          (t/do-report {:type :begin-test-ns :ns ns-obj})
          (once-fixture-fn
            (fn []
              (doseq [v (map resolve test-vars)]
                (when (:test (meta v))
                  (each-fixture-fn (fn [] (t/test-var v)))))))
          (t/do-report {:type :end-test-ns :ns ns-obj})))
      (let [summary (assoc @t/*report-counters* :type :summary)]
        (t/do-report summary)
        (check-result summary)))))
