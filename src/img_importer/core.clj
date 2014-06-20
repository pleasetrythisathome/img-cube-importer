(ns img-importer.core
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]
            [clojure.pprint :refer :all]
            [clojure.string :as s]
            [clojure.java.shell :refer [sh]]
            [pantomime.mime :as pm]))

(def cubes (parse-string (slurp (io/resource "cubes.json"))))

(defn map-iv [f v]
  (let [idv (map vector (iterate inc 0) v)]
    (for [[index value] idv]
      (f index value))))

(defn curl-save [uri file]
  (sh "curl" "-L" "-q" "-s" uri "-o" file))

(defn rename-with-ext [file]
  (let [ext (-> file
                (io/file)
                pm/mime-type-of
                pm/extension-for-name)
        with-ext (str file ext)]
    (sh "mv" file with-ext)
    with-ext))

(defn save-images [images path]
  (into [] (map-iv (fn [i img]
                     (let [fname (str path "/" i)]
                       (curl-save img fname)
                       (rename-with-ext fname)))
                   images)))

(defn save-cubes [cubes path]
  (sh "mkdir" path)
  (for [{:strs [images title] :as cube} cubes]
    (let [key (s/replace (s/lower-case title) #" " "_")
          cube-path (str path "/" key)]
      (sh "rm" "-rf" cube-path)
      (sh "mkdir" cube-path)
      (assoc cube "images" (save-images images cube-path)))))

(let [saved (save-cubes cubes "/Users/HereNow/code/ib5k/fakelove/cubes/cl-cuber/cl-chrome-imagecube/core/static/googleIO/images/cubes")]
  (pprint saved)
  (spit "/Users/HereNow/code/ib5k/fakelove/cubes/cl-cuber/cl-chrome-imagecube/core/static/googleIO/scripts/cubes.json" (generate-string saved)))
