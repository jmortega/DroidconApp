# Overview

Android app for Droidcon NYC 2015.  Designed and built by Touch Lab team
 members Liam, Izzy, Sam, and Kevin.

# Server

The server isn't open source at this time, but it would be relatively simple to modify the app to
work with local data and seed it with json.  If you'd like to use the server, get in touch (info [at] touchlab.co)

# Code

## New Stuff

### Kotlin

A large portion of the app is written in [Kotlin](http://kotlinlang.org/).  Android Studio support is pretty good at this point,
although some parts need to be Java.  Specifically anything that runs through annotation processing.

### Squeaky

A fork of ORMLite that is Android specific and uses source generation instead of reflection.  Performance
is significantly improved, and it supports immutable fields as well as views.  Note *quite* open source
yet.  Need to clean things up a bit.  Was going to announce it at the conference, but time ran short.

## Open Source Tools

[Picasso](http://square.github.io/picasso/)
[Retrofit](http://square.github.io/retrofit/)
[OKHttp](http://square.github.io/okhttp/)
[EventBus](https://github.com/greenrobot/EventBus)
[SuperToasts](https://github.com/JohnPersano/SuperToasts)
[VectorCompat](https://github.com/wnafee/vector-compat)
[Circle Image View](https://github.com/hdodenhof/CircleImageView)