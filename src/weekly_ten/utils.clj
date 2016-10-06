(ns weekly-ten.utils)

(defn int->word4
  "Convert an integer to a 4-byte word."
  [i]
  (-> (java.nio.ByteBuffer/allocate Integer/BYTES)
      (.putInt i)
      (.array)
      (bytes)))

(defn bytes->string "Convert bytes to string" [bytes] (apply str (map char bytes)))

(defn string->bytes "Converts string to bytes" [s] (byte-array (map byte s)))

(defn word4->int [w]
  "Converts a 4-byte word to an integer"
  (-> (java.nio.ByteBuffer/allocate Integer/BYTES)
      (.put (bytes w))
      (.flip)
      (.getInt)))

(defn read-bytes "Reads 'len' bytes from the input-stream" [input-stream len]
  (let [buf (byte-array len)
        _ (.read input-stream buf)]
    buf))
