(ns img-importer.core
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clojure.pprint :refer :all]
            [clojure.string :as s]
            [clojure.java.shell :refer [sh]]
            [pantomime.mime :as pm]))

(def cubes (parse-string (slurp (io/resource "cubes.json"))))

(defn indexed [f v]
  (let [idv (map vector (iterate inc 0) v)]
    (doseq [[index value] idv]
      (f value index))))

(defn curl-save [uri file]
  (pprint (str "saving " uri " to " file))
  (sh "curl" "-L" "-q" "-s" uri "-o" file))

(doseq [{:strs [images title]} cubes]
  (sh "mkdir" "resources/cubes")
  (let [path (s/replace (s/lower-case title) #" " "_")]
    (sh "rm" "-rf" (str "resources/cubes/" path))
    (sh "mkdir" (str "resources/cubes/" path))
    (indexed (fn [img i]
               (let [fname (str "resources/cubes/" path "/" i)]
                 (curl-save img fname)
                 (let [ext (-> fname
                               (io/file)
                               pm/mime-type-of
                               pm/extension-for-name)]
                   (sh "mv" fname (str fname ext)))))
             images)))
