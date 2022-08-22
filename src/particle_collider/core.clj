(ns particle-collider.core
  (:import [java.awt Graphics2D Color Font BasicStroke]
           [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File])
  (:require
   [mikera.image.filters :as filt]
   [mikera.image.core :as img]
   [clojure.java.io :as io]
   [clojure.core.async :as a]
   [discljord.connections :as c]
   [discljord.messaging :as m]
   [clojure.string :as s])
  (:gen-class))




(defn bot []

  (defonce token "OTY1MTIyMjU0MDk2NzExNzYw.GzWsW4.nI5S7gZGjiG9bMUw9HG1zCRgm-k10DeKjrTWeM")
  (defonce intents #{:guilds :guild-messages})
  (defonce channel-id "965124415216033832")

  (let [event-ch      (a/chan 100)
        connection-ch (c/connect-bot! token event-ch :intents intents)
        message-ch    (m/start-connection! token)]
    (try
      (loop []
        (let [[event-type event-data] (a/<!! event-ch)]
          (when (and (= :message-create event-type)
                     (= (:channel-id event-data) channel-id)
                     (not (:bot (:author event-data))))
            (let [message-content (:content event-data)]
              (if (= "!exit" (s/trim (s/lower-case message-content)))
                (do
                  (m/create-message! message-ch channel-id :content "Goodbye!")
                  (c/disconnect-bot! connection-ch))
                (m/create-message! message-ch channel-id :content message-content))))
          (when (= :channel-pins-update event-type)
            (c/disconnect-bot! connection-ch))
          (when-not (= :disconnect event-type)
            (recur))))
      (finally
        (m/stop-connection! message-ch)
        (a/close!           event-ch)))))
(bot)
(+ 3 3)




(defn batch-caption []
  "Description
      takes a list of image files and adds captions to each
   Arguments
   Output")


;; Captioning function with XY coords
(defn add-text-to-img [caption image x y]
  (let [graphics (.createGraphics image)
        font-size 100
        font (Font. "TimesRoman" Font/BOLD font-size)
        metrics (.getFontMetrics graphics font)]

    (def shape (.getOutline
                (. font
                   createGlyphVector
                   (. graphics getFontRenderContext)
                   "swage")))

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


;; Top and bottom captioning
(defn caption-image [caption filename]
  (let [image (img/load-image-resource filename)
        width (.getWidth image)
        height (.getHeight image)]

    ;; get image width, x=width/2
    ;; get image height, y1 = 0 y2 = height
    ;; TODO: test caption placement for x and y values
    (add-text-to-img caption image (/ width 2) (/ height 2))
    (def filename "meme.png")
    (ImageIO/write image "png"
                   (io/as-file
                    (str "resources/" (first (s/split filename #"\.")) ".modified.png")))))

(do
  (caption-image "top caption?" "meme.png")
  (def ant (img/load-image-resource "meme.modified.png"))
  (img/show ant))


;; load an image from a resource file
;; show the image, after applying an "invert" filter
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
