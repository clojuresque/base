(ns clojuresque.tasks.repl
  (:require
    [clojuresque.cli :as cli]
    [clojure.tools.nrepl.server :as repl]))

(def barrier (promise))
(def server nil)

(cli/deftask start-repl
  "Start a nrepl server on the given port. The repl server may
  be stopped via clojuresque.tasks.repl/stop-server."
  [[port     p "server port"]]
  (let [p (Long/parseLong port)
        s (repl/start-server :port p)]
    (println "Repl started on port" port)
    (alter-var-root #'server (constantly s)))
  @barrier)

(defn stop-repl
  []
  (when server
    (repl/stop-server server)
    (deliver barrier true)))
