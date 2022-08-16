(ns particle-collider.core
  (:import [java.awt Graphics2D Color Font]
           [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File])
  (:require
   [mikera.image.filters :as filt]
   [mikera.image.core :as img]
   [clojure.java.io :as io]
   [clojure.core.async :as async]
   [discljord.connections :as conn]
   [discljord.messaging :as msg])
  (:gen-class))

(def token "e0af87480e28ee376b2b677790f24a9b393dfc788ba75696a2443ee6a99e559c")
(def intents #{:guilds :guild-messages})

(let [event-ch     (async/chan 100)
      connection-ch (conn/connection-bot!)
      message-ch (msg/start-connection! token)])


(defn str->img [caption filename]
  (let [width 250
        height 100
        image (img/load-image-resource filename)
        graphics (.createGraphics image)
        font-size 30
        font (Font. "TimesRoman" Font/BOLD font-size)]
    (.setColor graphics Color/BLACK)
    (.setFont graphics font)
    (.drawString graphics caption 10 25)
    (ImageIO/write image "png" (io/as-file (io/resource filename)))))


(str->img "epic" "meme.png")

;; load an image from a resource file
(def ant (img/load-image-resource "meme.png"))

;; show the image, after applying an "invert" filter
(img/show (img/filter-image ant (filt/invert)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
