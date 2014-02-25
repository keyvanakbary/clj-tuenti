(ns tuenti-api.core
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]
            [digest]))

(def ^:private version "0.5")
(def ^:private api-key "MDI3MDFmZjU4MGExNWM0YmEyYjA5MzRkODlmMjg0MTU6MC43NzQ4ODAwMCAxMjc1NDcyNjgz")
(def ^:private api-url "https://api.tuenti.com/api/")

(defn- do-post [request]
  (io! (http/post api-url
                    {:body            (json/write-str request)
                     :connection      "keep-alive"
                     :user-agent      "Tuenti/1.2 CFNetwork/485.10.2 Darwin/10.3.1"
                     :accept-language "es-es"
                     :content-type    :json
                     :accept          :json
                     })))

(defn- parse-response [response]
  (first (json/read-str (:body response) :key-fn keyword)))

(defn- send-request [request]
  (-> request do-post parse-response))

(defn- build-request [method parameters]
  {:version  version :requests [[method parameters]]})

(defn- execute-auth-request [session method & parameters]
  (let [auth-request (assoc (build-request method parameters) :session_id (:session_id session))]
    (send-request auth-request)))

(defn get-friends [session]
  (execute-auth-request
    session "getFriendsData"
    :fields ["name" "surname" "avatar" "sex" "status" "phone_number" "chat_server"]))

(defn me [session] (:user_id session))

(defn get-profile-wall-with-status [session & {:keys [user-id page size] :or {user-id (me session) page 0 size 10}}]
  (execute-auth-request session "getProfileWallWithStatus" :user_id user-id :page page :size size))

(defn set-status [session status]
  (execute-auth-request session "setUserData" :status status))

(defn get-personal-notifications [session]
  (execute-auth-request
    session "getUserNotifications"
    :types ["unread_friend_messages" "unread_spam_messages" "new_profile_wall_posts"
            "new_friend_requests" "accepted_friend_requests" "new_photo_wall_posts"
            "new_tagged_photos" "new_event_invitations" "new_profile_wall_comments"]))

(defn get-friends-notifications [session & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getFriendsNotifications" :page page :page_size size))

(defn get-inbox [session & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getInbox" :page page :page_size size))

(defn get-sentbox [session & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getSentBox" :page page :page_size size))

(defn get-spambox [session & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getSpamBox" :page page :page_size size))

(defn get-thread [session thread-key & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getThread" :thread_key thread-key :page page :page_size size))

(defn send-message [session user-id thread-key message]
  (execute-auth-request session "sendMessage" :recipient user-id :thread_key thread-key :body message))

(defn get-albums [session & {:keys [user-id page size] :or {user-id (me session) page 0 size 10}}]
  (execute-auth-request session "getUserAlbums" :user_id user-id :page page :albums_per_page size))

(defn get-album-photos [session album-id & {:keys [user-id page] :or {user-id (me session) page 0}}]
  (execute-auth-request session "getAlbumPhotos" :user_id user-id :album_id album-id :page page))

(defn get-photo-tags [session photo-id]
  (execute-auth-request session "getPhotoTags" :photo_id photo-id))

(defn add-post-to-photo-wall [session photo-id message]
  (execute-auth-request session "addPostToPhotoWall" :photo_id photo-id :body message))

(defn get-photo-wall [session photo-id & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getPhotoWall" :photo_id photo-id :page page :post_per_page size))

(defn get-upcoming-events [session & {:keys [size birthdays] :or {size 10 birthdays :with-birthdays}}]
  (execute-auth-request session "getUpcomingEvents"
                        :desired_number size
                        :include_friend_birthdays (not= birthdays :without-birthdays)))

(defn get-event [session event-id]
  (execute-auth-request session "getEvent" :event_id event-id))

(defn get-event-wall [session event-id]
  (execute-auth-request session "getEvent" :event_id event-id))

(defn get-event-wall [session event-id & {:keys [page size] :or {page 0 size 10}}]
  (execute-auth-request session "getEventWall" :event_id event-id :page page :post_per_page size))

(defn- execute-request [method parameters]
  (send-request (build-request method parameters)))

(defn- get-challenge []
  (execute-request "getChallenge" {:type "login"}))

(defn- generate-passcode [password challenge]
  (digest/md5 (str challenge (digest/md5 password))))

(defn connect! [email password]
  (let [{:keys [challenge seed timestamp]} (get-challenge)]
    (execute-request "getSession"
                     {:passcode        (generate-passcode password challenge)
                      :seed            seed
                      :email           email
                      :timestamp       timestamp
                      :application_key api-key
                      })))

