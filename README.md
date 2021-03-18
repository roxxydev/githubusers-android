# Github Users

Android project written in Kotlin which demonstrates MVVM/MVI design architecture and caching mechanism.

1. Retrieve data from an api with [Retrofit](https://square.github.io/retrofit/)
2. Cache data with [Room](https://developer.android.com/topic/libraries/architecture/room).
3. Display data in UI.

### Design Pattern
The project used MVI and Repository design pattern approach. State in app is defined by user's action which is called intent _(not the android Intent class)_ which the ViewModel will get and decide the state to be reflected to the View.

#### Libraries
* [Hilt](https://dagger.dev/hilt/) - Dependency injection
* [Room](https://developer.android.com/jetpack/androidx/releases/room) - Caching mechanism.
* [Retrofit](https://square.github.io/retrofit/) - API http network requests.
* [OkHttp](https://square.github.io/okhttp/) - Use as http client for logging interceptor.
* [Moshi](https://github.com/square/moshi) - Smaller and faster JSON serialization.
* [Timber](https://github.com/JakeWharton/timber) - Logging and crash reports.
* [Glide](https://github.com/bumptech/glide) - Image loader to views.
* [Material Design](https://material.io/) - Google's material design ui.

#### Setup
Run the following command to build the project
```
./gradlew assembleDevDebug
```
or
```
./gradlew assembleProdRelease
```

#### Installation
Installing apk to device can be done with the following commands. Note that debug apk is used in the commands.
- via Gradle
```
./gradlew installDevDebug
```
- via adb tool
```
adb install build/outputs/apk/debug/githubusers_dev_debug_0.0.1_1.apk
```

#### Linting
Lint issues or warnings can be checked by running
```
./gradlew lint
```
