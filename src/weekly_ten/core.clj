(ns weekly-ten.core
  (:gen-class)
  (:require [weekly-ten.server :as server]))

(defn simple-query-handler
  "Answer a query"
  [query]
  (apply str (reverse query)))

(defn simple-protocol-handler
  "Implement the simple protocol:
  - When a connection is received, it must send six (6) bytes to the client: “ready\n”.
  - It must read a 4-byte word from the client, indicating the length of the query to read.
  - It must read that many bytes as the query, and then perform the requested query (see above).
  - It must send a 4-byte word to the client, indicating the length of the response.
  - It must send the response."
  [in out]
  (.write out (into-array Byte/TYPE (seq "ready\n"))))

(defn -main
  [& args]
  (if (= (first args) "server")
    (weekly-ten.server/start simple-protocol-handler 9999)
    (throw (Exception. "Noop"))))
