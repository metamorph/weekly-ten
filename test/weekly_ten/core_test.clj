(ns weekly-ten.core-test
  (:require [clojure.test :refer :all]
            [weekly-ten
             [utils :refer :all]
             [core :refer :all]
             [server :as server]
             [client :as client]]))


(deftest http-response-test
  (testing "parsing-a-response"

    (let [request-str (str "HTTP 1.0 200 OK\n"
                       "Content-Type: text/plain\n"
                       "Content-Length: 9\n"
                       "\n"
                       "Hello Foo\nWassup?")
          request-stream (java.io.ByteArrayInputStream. (string->bytes request-str))
          parsed (client/read-http-response request-stream)]

      (is (= parsed {:status 200
                     :content-length "9"
                     :content-type "text/plain"
                     :body "Hello Foo\nWassup?"})))))



(deftest simple-protocol-it
  (testing "client-and-server"
    (let [query "Hello Dummy"
          server-future (future (server/run-server (create-simple-protocol-handler simple-query-handler) 9999))
          answer (client/query-server "localhost" 9999 query)]
      (is (= (clojure.string/reverse query) answer))
      (is (= :done (deref server-future 5000 :fail))))))
