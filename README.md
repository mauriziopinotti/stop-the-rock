Stop The Rock
=============

Architecture
------------

The app is built using an MVVM architecture to satisfy the separation of concerns principle. 

I found this architecture to be the most appropriate due to its simplicity and less verbosity if compared to MVP or
other software architecture.

Furthermore, it's sponsored by Google which uses it in Android Architecture Components: hence, adhearing to it helps
collaboration as new developers working on my code won't have to figure out how I architectured the app, since I just
followed known guidelines.

Libraries
---------

The EndlessScrollView has been inspired by https://github.com/ciandt-mobile/android-recyclerview-binding


Going further
-------------

What I would had done having more time:
- responsive two-columns layout for tablets
- unit tests with mock jsons
- review all warnings in the build output
- sonarqube pass
- better error handling
- profiling of application code
