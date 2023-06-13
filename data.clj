(ns data
  (:require [cheshire.core :as json]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [babashka.http-client :as http]))

(def base-url "http://3.76.196.166:8080")
(def auth-cookie (str "sessionid=" (System/getenv "LB_SESSION_ID")))
(def ipfs-endpoint "https://ipfs.effect.ai/")

(defn copy-file-to-ipfs [url]
  (let [file (http/get
              url
              {:as :stream
               :headers
               {"cookie" auth-cookie}})
        ipfs-hash
        (->
         (http/post (str ipfs-endpoint "api/v0/add")
                    {:query-params {"pin" "true"}
                     :multipart [{:name "file" :content (:body file)}]})
         :body
         json/decode
         (get "Hash"))]
    (str ipfs-endpoint "ipfs/" ipfs-hash)))

(defn convert
  "Converts a full JSON export from LabelBox to a CSV for Effect
  Network. Image paths are constructed from the `base-url`."
  [file-name copy-images?]
  (let [data (take 5 (json/decode (slurp file-name)))]
    (prn "Found " (count data) " data points")
    (let [task-data
          (concat
           [["annotations" "id" "image"]]
           (for [{:strs [id annotations data] :as data-point} data]
             (let [img-url (str base-url (get data "ocr"))
                   ipfs-img (if copy-images?
                              (copy-file-to-ipfs img-url)
                              img-url)]
               [(json/encode annotations) id ipfs-img])))]

      (with-open [writer (io/writer "task-data.csv")]
        (csv/write-csv writer task-data :quote \')))))
