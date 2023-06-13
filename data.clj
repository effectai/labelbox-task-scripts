(ns data
  (:require [cheshire.core :as json]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def base-url "http://3.76.196.166:8080")

(defn convert
  "Converts a full JSON export from LabelBox to a CSV for Effect
  Network. Image paths are constructed from the `base-url`."
  [file-name]
  (let [data (take 5 (json/decode (slurp file-name)))]
    (prn "Found " (count data) " data points")
    (let [task-data
          (concat
           [["id" "annotations" "image"]]
           (for [{:strs [id annotations data] :as data-point} data]
             (let [img-url (str base-url (get data "ocr"))]
               [id (json/encode annotations) img-url])))]

      (with-open [writer (io/writer "task-data.csv")]
        (csv/write-csv writer task-data :quote \')))))
