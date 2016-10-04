(ns weekly-ten.core-test
  (:require [clojure.test :refer :all]
            [weekly-ten.core :refer :all]))


(defn query-client [query]
  (let [out  (java.io.PipedOutputStream.)
        in   (java.io.ByteArrayOutputStream.)
        sout (java.io.PipedInputStream. out)]

    (.start (Thread. (fn [] (simple-protocol-handler sout in))))

    ;; Implement the client protocol
    ;; Read the ready string.
    (let [ready (bytes->string (read-bytes in 6))]
      (prn ready))))


