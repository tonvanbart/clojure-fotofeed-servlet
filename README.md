## Learning clojure

This is a small project trying to build a (clojure) servlet that reads a Picasa RSS feed and parses it to html.
When run in Jetty it will work, however this would not work on Google App Engine as I originally intended (the
idea was to include it as some widget on a static hosted website).
Shared here for my own convenience and to help out anybody trying to look for a simple example getting things running.

**Note:** I am still a Clojure beginner, so there's no guarantee (from me) that this code is "the right way to do it".
It does work, however.  :)

### interactive development

As far as the Clojure part of the code is concerned, interactive development is handled by starting an embedded Jetty
server from the REPL. The REPL itself is started from Maven:

    mvn clojure:repl
    (tonvanbart.fotofeed/boot)

This starts a Jetty instance running on port 8081, which picks up code edits immediately. To use another port change the
call to `(run-jetty)`.

For some more explanation see the original [blog entry](http://tonvanbart.wordpress.com/2012/04/11/picasa-photo-widget-in-clojure/).
