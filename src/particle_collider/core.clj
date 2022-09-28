(ns particle-collider.core
  (:require [particle-collider.bot :refer [initialize-bot]]
            [particle-collider.image :refer [copyright-image]]))


;; load an image from a resource file
;; show the image, after applying an "invert" filter

(defonce token "OTY1MTIyMjU0MDk2NzExNzYw.GzWsW4.nI5S7gZGjiG9bMUw9HG1zCRgm-k10DeKjrTWeM")
(def intents #{:guilds :guild-messages})
(def channel-id "965124415216033832")

(defn -main []
  (initialize-bot token intents channel-id copyright-image))

(-main)

