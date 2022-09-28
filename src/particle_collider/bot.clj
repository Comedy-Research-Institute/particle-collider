(ns particle-collider.bot
  (:require
   [clojure.core.async :as async]
   [discljord.connections :as conn]
   [discljord.messaging :as msg]
   [clojure.string :as str]))


;; Contains all functions necessary to interact with the Discord API
;; TODO: figure out if url or proxy-url more useful
;;       see if we can get image as object from url
;;       attach created image to message





(defn is-image-message? [event-type event-data channel-id]
  "Description:
       Boolean expression which returns true if the message containts an
        attachment, and that attachment is an image.
   Arguments:
       event-type and event-data are the outputs of the event-ch stream
       event-type - Keyword: the message's event type
       event-data - HashMap: the event's attributes and their values
   Returns:
       boolean"
  (and (= :message-create event-type)
       (= (:channel-id event-data) channel-id)
       (not (:bot (:author event-data)))
       (> (count (:attachments event-data)) 0)))



(defn initialize-bot [token, intents, channel-id, image-function]
  "Description
       Initializes the bot
   Arguments
       token - string: an encrypted bot token
       intents - persistent hash set: set contains the keywords for bot permission scope in a server
       channel-id - string: a string of integers identifying the channel
   Returns: 
       Nil"

  (let [event-ch      (async/chan 100)
        connection-ch (conn/connect-bot! token event-ch :intents intents)
        message-ch    (msg/start-connection! token)] ;; start-connection returns a channel to be used for messsaging

    (try

      (loop []
        (let [[event-type event-data] (async/<!! event-ch)]
          (when (is-image-message? event-type event-data channel-id); <-- check attachment docs 
            (let [message-content (:content event-data)]

              ;; move outside is-image-message?
              (if (= "!exit" (str/trim (str/lower-case message-content)))
                (do
                  (msg/create-message! message-ch channel-id :content "Goodbye!")
                  (conn/disconnect-bot! connection-ch))

                (do
                  (let [image (:url second (:attachments event-data))
                        processed-image (image-function image)]
                     ;; create message with attachments \/ 
                    (println (second (:attachments event-data)))
                    (msg/create-message! message-ch channel-id :content message-content :attachments processed-image))))))
          (when (= :channel-pins-update event-type)
            (conn/disconnect-bot! connection-ch))
          (when-not (= :disconnect event-type)
            (recur))))

      (finally
        (msg/stop-connection! message-ch)
        (async/close!           event-ch)))))
