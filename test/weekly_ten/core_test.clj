(ns weekly-ten.core-test
  (:require [clojure.test :refer :all]
            [weekly-ten.core :refer :all]))

(defn query-client [query]
  (let [out  (java.io.PipedOutputStream.)
        in   (java.io.PipedInputStream.)
        sin  (java.io.PipedInputStream. out)
        sout (java.io.PipedOutputStream. in)]

    (.start (Thread. (fn [] (simple-protocol-handler sin sout))))

    ;; Implement the client protocol
    (let [ready       (bytes->string (read-bytes in 6))
          bytes-query (string->bytes query)]
      (.write out (int->word4 (count bytes-query)))
      (.write out bytes-query)
      (let [answer-len   (word4->int (read-bytes in 4))
            answer-bytes (read-bytes in answer-len)]
        (bytes->string answer-bytes)))))


