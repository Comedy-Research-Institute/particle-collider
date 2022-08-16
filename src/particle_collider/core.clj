(ns particle-collider.core
  (:import [java.awt Graphics2D Color Font]
           [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File])
  (:require
   [mikera.image.filters :as filt]
   [mikera.image.core :as img])
  (:gen-class))

(defn str->img [string filename]
  (let [file (File. (str "./" filename ".png"))
        width 250
        height 100
        image (BufferedImage. width height BufferedImage/TYPE_INT_ARGB)
        graphics (.createGraphics image)
        font-size 30
        font (Font. "TimesRoman" Font/BOLD font-size)]
    (.setColor graphics Color/BLACK)
    (.setFont graphics font)
    (.drawString graphics string 10 25)
    (ImageIO/write image "png" file)))

(str->img "epic" "resources/meme")

;; load an image from a resource file
(def ant (img/load-image-resource "meme.jpg"))

;; show the image, after applying an "invert" filter
(img/show (img/filter-image ant (filt/invert)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
