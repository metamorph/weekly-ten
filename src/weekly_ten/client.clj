(ns weekly-ten.client
  (:require [weekly-ten.utils :refer :all]))

(defn url-encode [s] (java.net.URLEncoder/encode s "UTF-8"))

(defn read-all-bytes [in]
  (let [reader (fn [in buf acc]
                 (let [len (.read in buf)]
                   (if (= -1 len)
                     acc
                     (recur in buf (apply conj acc (take len buf))))))]
    (reader in (byte-array 1024) [])))

(defn read-http-response
  "Read a HTTP response into a map."
  [in]
  (let [data  (bytes->string (read-all-bytes in))
        lines (clojure.string/split-lines data)]
    (update (reduce (fn [resp line]
                      (if (nil? (:body resp))

                        (condp re-find line
                          #"(.*)\s?:\s?(.*)"    :>> (fn [[_ k v]] (assoc resp (keyword (clojure.string/lower-case k)) v))
                          #"HTTP.*(\d\d\d).*" :>> (fn [[_ c]] (assoc resp :status (Integer/parseInt c)))
                          #"^$"                 :>> (fn [_] (assoc resp :body [])))

                        (update resp :body #(conj % line))))
                    {} lines) :body #(clojure.string/join "\n" %))))

(defn phteven-client [text]
  (let [endpoint     "api.phteven.io"
        path         "/translate"
        encoded-text (format "text=%s" (url-encode text))
        content-length (count (string->bytes encoded-text))]
    (with-open [socket (java.net.Socket. (java.net.InetAddress/getByName endpoint) (int 80))
                in     (.getInputStream socket)
                out    (.getOutputStream socket)]
      ;; Write the request
      (.write out (string->bytes (format "POST %s HTTP/1.0\n" path)))
      (.write out (string->bytes "Content-Type: application/x-www-form-urlencoded; charset=utf-8\n"))
      (.write out (string->bytes (format "Host: %s\n" endpoint)))
      (.write out (string->bytes (format "Content-Length: %d\n" content-length)))
      (.write out (string->bytes "\n"))
      (.write out (string->bytes encoded-text))
      ;; Read the response
      (let [response (read-http-response in)
            status (:status response)]
        (if (= 200 status)
          (:body response)
          (throw (RuntimeException. (format "Bad return code: %d" status))))))))

(defn query-client
  "Given input/output streams - send a query to the client and return the response as a string."
  [in out query]
  (let [ready       (bytes->string (read-bytes in 6))
        bytes-query (string->bytes query)]
    (.write out (int->word4 (count bytes-query)))
    (.write out bytes-query)
    (let [answer-len   (word4->int (read-bytes in 4))
          answer-bytes (read-bytes in answer-len)]
      (bytes->string answer-bytes))))

(defn query-server
  "Send a query to a server over the 'simple' protocol."
  [host port query]
  (with-open [socket (java.net.Socket. (java.net.InetAddress/getByName host) (int port))
              in     (.getInputStream socket)
              out    (.getOutputStream socket)]
    (query-client in out query)))

