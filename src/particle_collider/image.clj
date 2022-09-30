(ns particle-collider.image
  (:import [java.awt Graphics2D Color Font BasicStroke FontMetrics]
           [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File])
  (:require
   [mikera.image.filters :as filt]
   [mikera.image.core :as img]
   [clojure.java.io :as io]
   [clojure.string :as s])
  (:gen-class))


(defn load-image-from-file [filename]
  (img/load-image-resource filename))


(defn load-image-from-url [url]
  (println "Loading image from URL" url)
  (ImageIO/read (io/as-url url)))



(defn write-image-to-disk [image filename]
  (ImageIO/write image "png"
                 (io/as-file
                  (str "resources/" (first (s/split filename #"\.")) ".modified.png")))
  ((str "resources/" (first (s/split filename #"\.")) ".modified.png")))



;; Captioning function with XY coords
;; Todo: add docstring instead of overhead comment
(defn add-text-to-img [caption image x y font-size]
  (let [graphics (.createGraphics image)
        font (Font. "TimesRoman" Font/BOLD font-size)
        metrics (.getFontMetrics graphics font)]

    (def shape (.getOutline
                (. font
                   createGlyphVector
                   (. graphics getFontRenderContext)
                   caption)))

    (.translate graphics (int (- x (/ (.stringWidth metrics caption) 2))) (int y))
    (.setColor graphics Color/WHITE)
    (.setStroke graphics (new BasicStroke 2.0))
    (.draw graphics shape)
    (.setColor graphics Color/BLACK)
    (.fill graphics shape)

    (comment (.setColor graphics Color/BLACK)
             (.setFont graphics font)
             (.drawString graphics caption (- x (/ (.stringWidth metrics caption) 2)) y))
    image))


;; ;; Top and bottom captioning
;; (defn caption-image [caption filename]
;;   (let [image (img/load-image filename)
;;         height (.getHeight image)
;;         width (.getWidth image)]
;;     ;; get image height, y1 = 0 y2 = height
;;     ;; TODO: test caption placement for x and y values
;;     (add-text-to-img caption image (/ width 2) (/ height 2) 100)
;;     (def filename "meme.png")))


(defn image-to-file-object [image name]
  (io/as-file (write-image-to-disk image name)))

(defn copyright-image [image image-origin]

  "Description: adds a copyright notice to the bottom of the image
   Arguments: filename - a string containing the name of a file in resources
   Returns: true on successful image write, false on failure"

;; TODO: add font smoothing
  (println "Copyrighting image...")
  (let [notice "THIS MEME PROPERTY OF THE COMEDY RESEARCH INSTITUTE"
        image (case image-origin
                :url (load-image-from-url image)
                :file (load-image-from-file image)
                (println "Incorrect image source argument. Should be \"url\" or \"file\""))
        height (.getWidth image)
        width (.getHeight image)
        vertical-padding (* 0.01 height)
        horizontal-padding (* 0.1 width)
        graphics (.createGraphics image)
        font-size 40
        font (Font. "TimesRoman" Font/BOLD font-size)
        metrics (.getFontMetrics graphics font)
        ;; multiply by ratio of width of string to allocated space
        font-size (* font-size (/ (- width (* 2 horizontal-padding)) (.stringWidth metrics notice)))]
    (print (.stringWidth metrics notice) "\n" font-size)
    (add-text-to-img notice image (/ width 2) (- height vertical-padding) font-size)))



