(ns tuenti-api.core
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [digest]))

(def version "0.5")
(def api-key "MDI3MDFmZjU4MGExNWM0YmEyYjA5MzRkODlmMjg0MTU6MC43NzQ4ODAwMCAxMjc1NDcyNjgz")
(def api-url "http://api.tuenti.com/api/")

(defn- send-request [request]
  (let [response (client/post api-url
               {:body (json/write-str request)
                :connection "keep-alive"
                :user-agent "Tuenti/1.2 CFNetwork/485.10.2 Darwin/10.3.1"
                :accept-language "es-es"
                :content-type :json
                :accept :json
                })
        [decoded] (json/read-str (:body response) :key-fn keyword)]
    decoded))

(defn- build-request [method parameters]
  {:version version
   :requests [[method parameters]]})

(defn- execute-auth-request [session method parameters]
  (let [auth-request (assoc (build-request method parameters) :session_id (:session_id session))]
    (send-request auth-request)))

(defn get-friends [session]
  (execute-auth-request
   session "getFriendsData"
   {:fields ["name" "surname" "avatar" "sex" "status" "phone_number" "chat_server"]}))

(defn me [session] (:user_id session))

(defn get-profile-wall-with-status
  ([session] (get-profile-wall-with-status session (me session)))
  ([session user-id] (get-profile-wall-with-status session user-id 0))
  ([session user-id page] (get-profile-wall-with-status session user-id page 10))
  ([session user-id page size]
   (execute-auth-request session "getProfileWallWithStatus" {:user_id user-id :page page :size size})))

(defn set-status [session status]
   (execute-auth-request session "setUserData" {:status status}))

(defn get-personal-notifications [session]
   (execute-auth-request
    session "getUserNotifications"
    {:types ["unread_friend_messages" "unread_spam_messages" "new_profile_wall_posts"
             "new_friend_requests" "accepted_friend_requests" "new_photo_wall_posts"
             "new_tagged_photos" "new_event_invitations" "new_profile_wall_comments"]}))

(defn get-friends-notifications
  ([session] (get-friends-notifications session 0))
  ([session page] (get-friends-notifications session page 10))
  ([session page size]
   (execute-auth-request session "getFriendsNotifications" {:page page :page_size size})))

(defn get-inbox
  ([session] (get-inbox session 0))
  ([session page] (get-inbox session page 10))
  ([session page size]
   (execute-auth-request session "getInbox" {:page page :page_size size})))

(defn get-sentbox
  ([session] (get-sentbox session 0))
  ([session page] (get-sentbox session page 10))
  ([session page size]
   (execute-auth-request session "getSentBox" {:page page :page_size size})))

(defn get-spambox
  ([session] (get-spambox session 0))
  ([session page] (get-spambox session page 10))
  ([session page size]
   (execute-auth-request session "getSpamBox" {:page page :page_size size})))

(defn get-thread
  ([session thread-key] (get-thread session thread-key 0))
  ([session thread-key page] (get-thread session thread-key page 10))
  ([session thread-key page size]
   (execute-auth-request session "getThread" {:thread_key thread-key :page page :page_size size})))

(defn send-message [session user-id thread-key message]
   (execute-auth-request session "sendMessage" {:recipient user-id :thread_key thread-key :body message}))

(defn get-albums
  ([session] (get-albums session (me session)))
  ([session user-id] (get-albums session user-id 0))
  ([session user-id page] (get-albums session user-id page 10))
  ([session user-id page size]
   (execute-auth-request session "getUserAlbums" {:user_id user-id :page page :albums_per_page size})))

(defn get-album-photos
  ([session album-id] (get-album-photos session (me session) album-id))
  ([session user-id album-id] (get-album-photos session user-id album-id 0))
  ([session user-id album-id page]
   (execute-auth-request session "getAlbumPhotos" {:user_id user-id :album_id album-id :page page})))

(defn get-photo-tags [session photo-id]
   (execute-auth-request session "getPhotoTags" {:photo_id photo-id}))

(defn add-post-to-photo-wall [session photo-id message]
   (execute-auth-request session "addPostToPhotoWall" {:photo_id photo-id :body message}))

(defn get-photo-wall
  ([session photo-id] (get-photo-wall session photo-id 0))
  ([session photo-id page] (get-photo-wall session photo-id 0 10))
  ([session photo-id page size]
   (execute-auth-request session "getPhotoWall" {:photo_id photo-id :page page :post_per_page size})))

(defn get-upcoming-events
  ([session] (get-upcoming-events session 10))
  ([session size] (get-upcoming-events session size :with-birthdays))
  ([session size birthdays]
   (execute-auth-request session "getUpcomingEvents"
                         {:desired_number size
                          :include_friend_birthdays (not= birthdays :without-birthdays)})))

(defn get-event [session event-id]
   (execute-auth-request session "getEvent" {:event_id event-id}))

(defn get-event-wall [session event-id]
   (execute-auth-request session "getEvent" {:event_id event-id}))

(defn get-event-wall
  ([session event-id] (get-event-wall session event-id 0))
  ([session event-id page] (get-event-wall session event-id 0 10))
  ([session event-id page size]
   (execute-auth-request session "getEventWall" {:event_id event-id :page page :post_per_page size})))

(defn- execute-request [method parameters]
  (let [request (build-request method parameters)]
    (send-request request)))

(defn- get-challenge []
  (execute-request "getChallenge" {:type "login"}))

(defn- generate-passcode [password challenge]
  (digest/md5 (str challenge (digest/md5 password))))

(defn connect [email password]
  (let [c (get-challenge)]
    (execute-request "getSession"
                     {:passcode (generate-passcode password (:challenge c))
                      :seed (:seed c)
                      :email email
                      :timestamp (:timestamp c)
                      :application_key api-key
                      })))
