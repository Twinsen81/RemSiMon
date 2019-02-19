<img alt="Icon" src="app/src/main/res/mipmap-xxhdpi/ic_launcher.png?raw=true" align="left" hspace="1" vspace="1">

# Remote Site Monitor (RemSiMon)

A pet project to practice using various Android frameworks and libraries<br>
This is a work in progress ...
<BR/>
#### The app's tasks
The app is architectured to be extendable with various types of jobs that the app runs on a regular basis. These jobs are called tasks. Currently there are two types of tasks:
* <B>Pinging task</B>: pings a URL with a given frequency
* <B>JSON request task</B> requests a JSON-formatted data from a given URL, extracts and displays selected fields from the received data

For the sake of faster development and testability the tasks are being executed in the background thread of the app, not a service. This will be later and the execution will be carried out either by a service or WorkManager.

[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)
![minSdkVersion 21](https://img.shields.io/badge/minSdkVersion-21-red.svg?style=true)
![compileSdkVersion 28](https://img.shields.io/badge/compileSdkVersion-27-blueviolet.svg?style=true)

<p align="center">
  <img alt='Sample 1' src="/art/rsm_sampleshot1.png">
  <img alt='Sample 2' src="/art/rsm_sampleshot2.png">
</p>

## Frameworks and libraries used in the project

### Dependency injection and view binding
* [Dagger](https://google.github.io/dagger/)
* [Butter Knife](http://jakewharton.github.io/butterknife/)

### Testing
* [Espresso](https://developer.android.com/training/testing/espresso/)
* [Mockito](https://site.mockito.org/)
* [Robolectric](http://robolectric.org/)
* [RESTMock](https://github.com/andrzejchm/RESTMock)

### Network requests
* [Okhttp](http://square.github.io/okhttp/)
* [Retrofit](http://square.github.io/retrofit/)

### Others

* [Joda-Time](https://www.joda.org/joda-time/)
* [Room](https://developer.android.com/topic/libraries/architecture/room.html)
* [Timber](https://github.com/JakeWharton/timber)
* [Moshi](https://github.com/square/moshi)
* [Gson](https://github.com/google/gson)
* [Guava](https://github.com/google/guava)

## TODO

* Move tasks execution to WorkManager
* Nicer results formatting for JSON tasks (custom value formatting, aliases for field names)
* Notifications (values reaching certain setpoints and ping failures)
* Trend charts for JSON tasks with the history depth > 1

## License

    Copyright 2019 Evgeniy Plokhov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
