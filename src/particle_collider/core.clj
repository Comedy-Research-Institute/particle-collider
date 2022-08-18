(ns particle-collider.core
  (:import [java.awt Graphics2D Color Font BasicStroke]
           [java.awt.image BufferedImage]
           [javax.imageio ImageIO]
           [java.io File])
  (:require
   [mikera.image.filters :as filt]
   [mikera.image.core :as img]
   [clojure.java.io :as io]
   [clojure.core.async :as async]
   [discljord.connections :as conn]
   [discljord.messaging :as msg]
   [clojure.string :as str])
  (:gen-class))


(def token "e0af87480e28ee376b2b677790f24a9b393dfc788ba75696a2443ee6a99e559c")
(def intents #{:guilds :guild-messages})
(def channel-id "965124415216033832")

(defn bot []
  
(let [event-ch     (async/chan 100)
      connection-ch (conn/connect-bot! token event-ch :intents intents)
      message-ch (msg/start-connection! token)]
  (try
    (loop []
      (let [[event-type event-data] (async/<!! event-ch)]
        (when (and (= :message-create event-type event-data)
                   (= (:channel-id event-data) channel-id)
                   (not (:bot (:author event-data))))
          (let [message-content (:content event-data)]
            (if (= "!exit" (str/trim (str/lower-case message-content)))
              (do
                (msg/create-message! message-ch channel-id :content "Goodbye!")
                (conn/disconnect-bot! connection-ch))
              (msg/create-message! message-ch channel-id :content message-content))))
        (when-not (= :disconnect event-type)
          (recur))))
    (finally
      (msg/stop-connection! message-ch)
      (async/close! event-ch))))

)
(+ 3 3)




(defn batch-caption []
  "Description
      takes a list of image files and adds captions to each
   Arguments
   Output"
  )


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
                    (str "resources/" (first (str/split filename #"\.")) ".modified.png")))))

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
