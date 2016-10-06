(ns weekly-ten.core-test
  (:require [clojure.test :refer :all]
            [weekly-ten
             [core :refer :all]
             [server :as server]
             [client :as client]]))


(deftest simple-protocol-it
  (testing "client-and-server"
    (let [query "Hello Dummy"
          server-future (future (server/run-server simple-protocol-handler 9999))
          answer (client/query-server "localhost" 9999 query)]
      (is (= (clojure.string/reverse query) answer))
      (is (= :done (deref server-future 5000 :fail))))))
