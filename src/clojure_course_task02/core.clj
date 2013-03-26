(ns clojure-course-task02.core
  (:require [clojure.java.io :as io]
             [clojure.core.reducers :as r])
  (:gen-class))


(defn isdirectory? [str]
  (.isDirectory (io/as-file str)))

(defn get-file-vec [path]
 (when (isdirectory? path) (vec (.list (io/as-file path)))))

(defn find-files [file-name path]
  (let [filevec (get-file-vec path) matcher (re-pattern file-name)
        fullpath (if (= (get path (dec (.length path))) \/) path (str path "/"))
        dirlist (filter isdirectory? (map #(str fullpath  %)  filevec))]
            (loop [filelist (filter #(re-matches matcher %) (remove #(isdirectory? (str fullpath %)) filevec)) dirlist dirlist]
              (if (empty? dirlist)
                filelist
                (recur (concat filelist (deref (future (find-files file-name (first dirlist))))) (next dirlist))))))

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
