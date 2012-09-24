(ns tonvanbart.fotofeed
  (:use clojure.core
    clojure.xml
    cheshire.core
    ring.util.response
    ring.util.servlet
    ring.adapter.jetty
    ring.middleware.reload
    ring.middleware.params
    ring.middleware.stacktrace)
  (:gen-class :extends javax.servlet.http.HttpServlet))

; example feed of one single Picasa album.
(def example-feed "https://picasaweb.google.com/data/feed/base/user/113853148746021127763/albumid/5630409920713617121?alt=rss&kind=photo&hl=en_US")

(defn pictures-from-feed
  "Return a JSON array containing the picture URLs from a Google Picasa feed
  if the given URL is not for picasaweb return empty array"
  [str-url]
  (if (.startsWith str-url "https://picasaweb.google.com")
    (let [feed-url (java.net.URL. str-url)
          ^java.net.HttpURLConnection connection (.openConnection feed-url)
          xmltags (xml-seq (parse (.openStream connection)))]
      (generate-string
        (for [tag xmltags
            :when (= :enclosure (:tag tag))]
        (:url (:attrs tag)))))
    "[]"))

(defn html-from-feed
  "Return html containing the picture URLs from a Google Picasa feed
  if the given URL is not for picasaweb return empty array"
  [str-url]
  (if (.startsWith str-url "https://picasaweb.google.com")
    (let [feed-url (java.net.URL. str-url)
          ^java.net.HttpURLConnection connection (.openConnection feed-url)
          xmltags (xml-seq (parse (.openStream feed-url)))]
      (apply str
        (for [tag xmltags
            :when (= :enclosure (:tag tag))]
        (str "<img src=\"" (:url (:attrs tag)) "\" width=\"50%\" height=\"50%\">"))))
    "[]"))

(defn jsonp-response
  [url callback]
  {
    :status 200
    :Content-Type "text/javascript"
    :body (str callback "('" (html-from-feed url) "')" )
    })

(defn error-response
  []
  {
   :status 400
   :body (str "request should contain both 'url' and 'callback'")})

(defn handle-request
  "extract url and callback from wrapped request, return jsonp response.
  in case of any errors return empty array"
  [req]
  (let [req-params (:query-params req)]
    (if
      (and
        (contains? req-params "url")
        (contains? req-params "callback"))
      (jsonp-response (req-params "url") (req-params "callback"))
      (error-response))))

; generate a servlet
(defservice (wrap-params handle-request ))

; test code
(def app
  (-> #'handle-request
    (wrap-reload '(tonvanbart.fotofeed))
    (wrap-params)
    (wrap-stacktrace)))

(defn boot []
  "starts Jetty from a REPL with the handler, for development
  Run this in a REPL started with mvn clojure:repl "
  (run-jetty #'app {:port 8081}))




