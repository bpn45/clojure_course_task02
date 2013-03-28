(ns clojure-course-task02.core
  (:require [clojure.java.io :as io]
            [clojure.core.reducers :as r])
  (:gen-class))


(defn directory? [str]
  (.isDirectory (io/as-file str)))

(defn get-file-vec [path]
 (when (directory? path) (vec (.list (io/as-file path)))))

(defn find-files [file-name path]
  (let [filevec (get-file-vec path)
        matcher (re-pattern file-name)
        fullpath (if (= (last path) \/) path (str path "/"))
        dirlist (->> filevec
                     (r/map #(str fullpath  %))
                     (r/filter directory?)
                     (into '()))
        filelist (->> filevec 
                     (r/remove #(directory? (str fullpath %)))
                     (r/filter #(re-matches matcher %))
                      (into '()))]
     (flatten (concat  filelist (pmap #(find-files file-name %) dirlist)))))

(defn usage []
  (println "Usage: $ run.sh file_name path"))

(defn -main [file-name path]
  (if (or (nil? file-name)
          (nil? path))
    (usage)
    (do
      
      (println "Searching for " file-name " in " path "...")
      (dorun (map println (find-files file-name path)))
       (shutdown-agents))))
