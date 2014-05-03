(ns tuenti-api.test.core
  (:use [clojure.test]
        [tuenti-api.core]
        [tuenti-api.test.util]))

(deftest test-get-friends
  (expect-auth-call
   (str "[\"getFriendsData\",{\"fields\":["
          "\"name\",\"surname\",\"avatar\",\"sex\",\"status\",\"phone_number\",\"chat_server\""
        "]}]")
   (get-friends)))

(deftest test-get-profile
  (expect-auth-call
   (str "[\"getUsersData\",{\"ids\":[1],\"fields\":["
            "\"favorite_books\",\"favorite_movies\",\"favorite_music\",\"favorite_quotes\",\"hobbies\",\"website\","
            "\"about_me_title\",\"about_me\",\"birthday\",\"city\",\"province\",\"name\",\"surname\",\"avatar\",\"sex\","
            "\"status\",\"phone_number\",\"chat_server\""
        "]}]")
   (get-profile {:user-id 1})))

(deftest test-get-profile-wall-with-status
  (expect-auth-call
   "[\"getProfileWallWithStatus\",{\"user_id\":1,\"page\":2,\"page_size\":3}]"
   (get-profile-wall-with-status {:user-id 1 :page 2 :size 3})))

(deftest test-set-status
  (expect-auth-call
   "[\"setUserData\",{\"status\":\"status\"}]"
   (set-status "status")))

(deftest test-get-personal-notifications
  (expect-auth-call
   (str "[\"getUserNotifications\",{\"types\":["
            "\"unread_friend_messages\",\"unread_spam_messages\",\"new_profile_wall_posts\",\"new_friend_requests\","
            "\"accepted_friend_requests\",\"new_photo_wall_posts\",\"new_tagged_photos\",\"new_event_invitations\","
            "\"new_profile_wall_comments\""
        "]}]")
   (get-personal-notifications)))

(deftest test-get-friends-notifications
  (expect-auth-call
   "[\"getFriendsNotifications\",{\"page\":1,\"page_size\":2}]"
   (get-friends-notifications {:page 1 :size 2})))

(deftest test-get-inbox
  (expect-auth-call
   "[\"getInbox\",{\"page\":1,\"page_size\":2}]"
   (get-inbox {:page 1 :size 2})))

(deftest test-get-sentbox
  (expect-auth-call
   "[\"getSentBox\",{\"page\":1,\"page_size\":2}]"
   (get-sentbox {:page 1 :size 2})))

(deftest test-get-spambox
  (expect-auth-call
   "[\"getSpamBox\",{\"page\":1,\"page_size\":2}]"
   (get-spambox {:page 1 :size 2})))

(deftest test-get-thread
  (expect-auth-call
   "[\"getThread\",{\"thread_key\":1,\"page\":2,\"page_size\":3}]"
   (get-thread 1 {:page 2 :size 3})))

(deftest test-send-message
  (expect-auth-call
   "[\"sendMessage\",{\"recipient\":1,\"thread_key\":2,\"body\":\"message\"}]"
   (send-message 1 2 "message")))

(deftest test-get-albums
  (expect-auth-call
   "[\"getUserAlbums\",{\"user_id\":1,\"page\":2,\"albums_per_page\":3}]"
   (get-albums {:user-id 1 :page 2 :size 3})))

(deftest test-get-album-photos
  (expect-auth-call
   "[\"getAlbumPhotos\",{\"user_id\":1,\"album_id\":2,\"page\":3}]"
   (get-album-photos 2 {:user-id 1 :page 3})))

(deftest test-get-photo-tags
  (expect-auth-call
   "[\"getPhotoTags\",{\"photo_id\":1}]"
   (get-photo-tags 1)))

(deftest test-add-post-to-photo-wall
  (expect-auth-call
   "[\"addPostToPhotoWall\",{\"photo_id\":1,\"body\":\"message\"}]"
   (add-post-to-photo-wall 1 "message")))

(deftest test-get-photo-wall
  (expect-auth-call
   "[\"getPhotoWall\",{\"photo_id\":1,\"page\":2,\"post_per_page\":3}]"
   (get-photo-wall 1 {:page 2 :size 3})))

(deftest test-get-upcoming-events
  (expect-auth-call
   "[\"getUpcomingEvents\",{\"desired_number\":1,\"include_friend_birthdays\":false}]"
   (get-upcoming-events {:size 1 :birthdays :without-birthdays})))

(deftest test-get-event
  (expect-auth-call
   "[\"getEvent\",{\"event_id\":1}]"
   (get-event 1)))
