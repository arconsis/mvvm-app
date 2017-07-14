# mvvm-app
This app is a demonstration of an Android app using a MVVM approach

It is a simple app for taking notes and writing them to a backend.
Technologies used are:
 - Android DataBinding
 - ObjectHerder
 - RxJava2
 - Retrofit
 - DroitatedDB
 
 ## ObjectHerder
 To be able to compile the app you need the ObjectHerder in your local Maven Repository.
 Get the ObjectHerder from here: https://github.com/arconsis/objectherder-android
 Checkout the develop branch and install the artifact using: gradlew install
 
 ## Android architecture componenets
 Since Google I/O 2017 the new Android architecture components where introduced. You can find a first use of it for holding the ViewModel instead of the ObjectHerder on the feature branch architecture_componenets
