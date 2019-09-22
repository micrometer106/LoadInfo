# LoadInfo

This is a recyclerView practice

Destination:<br />
==============
Load information from: https://jsonplaceholder.typicode.com/photos <br />
and show text information and thumbnail like below.

<img src="https://github.com/micrometer106/LoadInfo/blob/master/preview.jpg" width="270" height="561">

Feature
==============
- not using 3rd party lib to load thumbnail, using AsyncTask loading
- 3rd party: OkHttp, gson

TODO
=============
- when scrolling, you should cancel the AsyncTask of view which no need to show
