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

(defn save-image [img fname]
  (curl-save img fname)
  (rename-with-ext fname))

(defn save-images [images path]
  (into [] (map-iv (fn [i img]
                     (let [fname (str path "/" i)]
                       (save-image img fname)))
                   images)))

(defn save-cubes [cubes path]
  (sh "mkdir" path)
  (for [{:strs [images title background] :as cube} cubes]
    (let [key (s/replace (s/lower-case title) #" " "_")
          cube-path (str path "/" key)]
      (sh "rm" "-rf" cube-path)
      (sh "mkdir" cube-path)
      (let [saved {"images" (save-images images cube-path)
                   "background" (if-not (empty? background)
                                  (save-image background (str cube-path "/background"))
                                  "")}]
        (merge cube saved)))))

(let [root "/Users/HereNow/code/ib5k/fakelove/cubes/cl-cuber/cl-chrome-imagecube/core/static"
      saved (save-cubes cubes (str root "/googleIO/images/cubes"))
      relative (for [{:strs [images background] :as cube} saved]
                 (letfn [(make-rel [path]
                           (s/replace path root "/static_dev"))]
                   (let [relative {"images" (for [img images]
                                              (make-rel img))
                                   "background" (if-not (empty? background)
                                                  (make-rel background)
                                                  "")}]
                    (merge cube relative))))]
  (spit (str root "/googleIO/scripts/saved_cubes.json") (generate-string relative))
  (pprint "Finished pulling images!"))
