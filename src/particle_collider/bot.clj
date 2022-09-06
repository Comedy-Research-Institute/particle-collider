(ns particle-collider.bot
  (:require
   [clojure.core.async :as async]
   [discljord.connections :as conn]
   [discljord.messaging :as msg]
   [clojure.string :as str]))


;; Contains all functions necessary to interact with the Discord API

(defonce token "OTY1MTIyMjU0MDk2NzExNzYw.GzWsW4.nI5S7gZGjiG9bMUw9HG1zCRgm-k10DeKjrTWeM")
(def intents #{:guilds :guild-messages})
(def channel-id "965124415216033832")



(defn initialize-bot [token, intents, channel-id]
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
          (print event-data)
          (when (and (= :message-create event-type)
                     (= (:channel-id event-data) channel-id)
                     (not (:bot (:author event-data)))
                     (> (count (:attachments event-data)) 0))
            (let [message-content (:content event-data)]
              (if (= "!exit" (str/trim (str/lower-case message-content)))
                (do

                  (msg/create-message! message-ch channel-id :content "Goodbye!")
                  (conn/disconnect-bot! connection-ch))

                (msg/create-message! message-ch channel-id :content message-content))))
          (when (= :channel-pins-update event-type)
            (conn/disconnect-bot! connection-ch))
          (when-not (= :disconnect event-type)
            (recur))))
      (finally
        (msg/stop-connection! message-ch)
        (async/close!           event-ch)))))

;; (initialize-bot token intents channel-id)