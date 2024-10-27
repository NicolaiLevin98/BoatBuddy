# BoatBuddy: Architecture
## General architecture description

The app has a data layer, a domain layer and a presentation/UI layer.

### Data layer

In the remote folder _(data->remote)_, there is a folder for each API used in the project: 
Locationforecast, MetAlerts and Oceanforecast (from api.met.no), and Se havnivå/waterlevel 
(from kartverket.no). Each API has its own DataSource, and a file in which the response from the API 
is deserialized into data classes (using kotlinx.serialization for JSON and Jackson for XML). 
In the remote folder there is also a Client class, with http clients for the APIs, and a _repositories_
folder, with a separate repository for each DataSource + one for Geocoder (android.location.Geocoder 
class), used to fetch either the location name or coordinates. These repositories interact with the 
ViewModels in the presentation layer either directly, or through the domain layer.  

- **Locationforecast API:**
  - LocForecastDataSource, SerializableLocForecast, LocForecastRepo
- **MetAlerts API:**
  - MetAlertsDataSource, SerializableMetAlerts, MetAlertsRepository
- **Oceanforecast API:**
  - OceanForecastDataSource, SerializableOceanForecast, OceanForecastRepo
- **Se havnivå/Waterlevel API:**
  - WaterLevelDataSource, SerializableWaterLevel, WaterLevelRepo 
- **Geocoder**
  - GeocoderRepository

Additionally, in the _model_ folder there are created data classes for information we have selected 
to be later displayed.

### Domain layer

Some parts of the app’s business logic are handled in the domain layer use cases.
We decided to create two use cases: _FetchWeatherUseCase_ and _MapInfoUseCase_. The use cases were 
created in order to avoid too large classes and code duplication, and to improve readability in 
ViewModels. 

#### FetchWeatherUseCase:

This use case fetches data from only one repository: LocForecastRepo, which is the repository for 
the Locationforecast API. It returns weather data for a chosen location in form of a list. 
The use case was created to avoid too large of a ViewModel, and to avoid duplicating the code as the
data it returns is used by two different screens, with two different ViewModels. 
The use case interacts with the HomeScreen’s ViewModel (HomeViewModel). It is also used by the 
second use case in the layer - MapInfoUseCase.

#### MapInfoUseCase:

This use case fetches data from three repositories: OceanForecastRepo (Oceanforecast API),  
WaterLevelRepo (Se havnivå/waterlevel API) and GeocodingRepository (Geocoder), and the described above,
FetchWeatherUseCase. It returns information about the weather, location and the nearby bodies of water. 
The use case was created in order to combine the data from different sources in one function and to 
avoid MapScreenViewModel being too large of a class. The use case interacts with this view model. 

### Presentation/UI layer:

The app has four screens: HomeScreen, MapScreen, InfoScreen and LocationPermissionScreen. 
HomeScreen, MapScreen and InfoScreen have their own ViewModels. Additionally, all screens take DataStoreViewModel
as a parameter.  
All screens except for LocationPermissionScreen take navController as a parameter. The BottomBarDesign 
composable (which can be found in _ui->navigationComponents_) is used on the same screens.

#### DataStoreViewModel

DataStoreViewModel interacts with Application. Its instance is created in MainActivity and then passed
to all the screens as a parameter. It handles the user preferences, to be specific the theme: dark mode
or standard. 

#### HomeScreen:

Files for this screen can be found in: _ui->homeScreen_  

HomeScreen is the “main” screen of the app. Most of its components, like for example HelloBox or 
SearchBar, are created as separate composables, and can be found in the HomeScreenComponents folder 
(_ui->homeScreen->homeScreenComponents_).  

The screen has a ViewModel - HomeViewModel, which uses information from three data layer classes:
MetAlertsRepository, WaterLevelRepo and GeocodingRepository, and one domain layer use case: 
FetchWeatherUseCase.

The HomeScreen’s main file (HomeScreen.kt) includes multiple composables and a sealed class UiState 
used for handling of API response (success, loading or error). Based on its return value, the 
corresponding components/composebles are displayed, for example LoadingScreen() when the UiState is 
Loading, and ContentScreen() when the UiState is Success.

#### MapScreen:

Files for this screen can be found in: _openstreetmap_ and _ui->openstreetmapScreen_  

The main file for this screen is MapScreen.kt  

To display the map on the MapScreen, the app uses OSMdroid with an OpenSeaMap tile overlay. 
There is no official documentation for using OSMdroid with Jetpack Compose (as of today), and thus 
no official standard standard to how the map should be implemented. 
The current implementation in BoatBuddy works as follows: 
- The composable rememberMapViewWithLifecycle (found in _MapLifeCycle.kt_), returns a mapView. This composable is created and displayed by another composable - OsmMapView (_OsmMapView.kt_), which is called from the MapScreen and thus could be described as a MapScreen component.
- The map has on-click Markers, implemented using MapEventsReceiverImpl class which can be found in _MapLifeCycle.kt_
- _MapLifeCycle.kt_ and _OsmMapView.kt_ can be found in the _openstreetmap_ folder

Other UI components of the MapScreen are: 
- MapWeatherDataDisplay (in _MapWeatherDisplay.kt_) 
- MapSearchBar (_MapSearchBar.kt_)
- WeatherButton (_WeatherButton.kt_)  

Finally, the MapScreen has its ViewModel - MapScreenViewModel, the instance of which is created in 
the MapScreen’s constructor. The ViewModel interacts with the domain layer use case - mapInfoUseCase
and the GeocodingRepository.  

#### InfoScreen:

Files for this screen can be found in: _ui->myInfoScreenUi_  

InfoScreens main tasks are to display hardcoded information and the user's current coordinates. It 
also has the dark mode toggle button. Its own ViewModel (LocationViewModel) interacts with Application 
and handles the state of userGeoPoint - the coordinates to be displayed. Besides the main file 
(InfoScreen.kt) it has two components: InfoBoatCategory and RSinfo.  

#### LocationPermissionScreen

Files for this screen can be found in: _ui->locationPermissionScreen_  

This is a screen that is displayed when the location permissions are not yet obtained. It takes
requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>> as a parameter and gives
the user the option to give the app permission to access their location.

### Others (utilities, services, objects etc.):

#### LocationManager:

LocationManager is an object responsible for requesting and obtaining location permissions, managing 
location updates and fetching location data (latitude and longitude). It's tasks are handled mostly
in MainActivity. When permissions are not yet obtained the user is shown LocationPermissionScreen,
which takes requestLocationPermissionsLauncher as a parameter.

#### NavHost and navController:

Created in MainActivity, responsible for navigation between the application’s screens  

#### NetworkUtility object:

NetworkUtility object, which can be found in the _networkconnectivity_ folder, is responsible for 
monitoring whether the user has internet connection. In order to acheive this, its function 
registerNetworkCallback is called in MainActivity. The ViewModels of the relevant screens (MapScreen 
and HomeScreen) use the object and expose the network connectivity state to the screens.

## Android-recommended design patterns and object-oriented principles:

This section is written based on three articles from developer.android.com website, accessed in 
April-May 2024:  
- Common modularization patterns (https://developer.android.com/topic/modularization/patterns)
- Recommendations for Android architecture (https://developer.android.com/topic/architecture/recommendations#layered-architecture)
- Architecting your Compose UI (https://developer.android.com/develop/ui/compose/architecture)  

### Low coupling:

When it comes to low coupling, the project does not utilize dependency injection (DI). It could be 
therefore argued that certain modules within the project might be too dependent on each other. 
An example of this can be HomeViewModel - the class is tightly coupled to specific implementations 
of its dependencies which are created in HomeViewModel’s constructor.  
However, the project does implement low coupling to some degree, as the code is modularized both by 
layers (presentation and data layer) and feature (the layers consist of multiple files with, for 
example, separate repositories for each DataSource and a ViewModel for each screen). With that being 
said, in the future, as the codebase size increases, DI should be implemented to reduce coupling 
between classes/objects and improve testability of the code.  

### High cohesion:

As the project is implemented with the android recommended layer-based architecture, the code 
modules automatically act together as a system. The modules also have clearly defined responsibilities 
as this is something inherent to the layers, with data layer exposing data to the app, and together 
with domain layer, containing business logic of the app, and presentation/UI layer displaying data 
on the screen and being responsible for user interaction. Additionally each layer is separated into 
multiple files to define their responsibilities in an even clearer way. For example: DataSources 
have their repositories, screens have their separate view models and some screen components are put 
in separate files.  

### MVVM:

The app implements the Model-View-ViewModel pattern with the UI (Screens and their components) 
being the View, ViewModels being the ViewModel, and the domain and data layer classes being the Model.  
A simplified example:
- **Model:** LocForecastDataSource, MetAlertsDataSource, WaterLevelDataSource, LocForecastRepository, FetchWeatherUseCase, WaterLevelRepo, MetAlertsRepository, GeocodingRepository
- **View:** HomeScreen.kt (which includes the HomeScreen() composable)
- **ViewModel:** HomeViewModel and DataStoreViewModel

### UDF:

UDF is implemented by having variables in ViewModels for the screens that use MutableStateFlow and 
expose data as StateFlow. This ensures one-way flow of data (state flows down and events flow up). 
Corresponding variables in the screen classes use collectAsState, allowing the Composable functions 
to receive the emitted values and represent them as the current state of the UI. An example of this 
can be the HomeScreen variable marineAlertsUiState and the HomeViewModel variables 
_marineAlertsUiState and marineAlertsUiState.

## API Level:

The minimum SDK version chosen for the project is 26 (API level 26 and Android 8). The reason behind 
this choice is the app’s usage of Instant.now() from java.time library. Instant.now() is used in 
WaterLevelDataSource, in order to fetch information about tides and water levels at correct time.  

According to the data available in Android Studio (on 08.05.2024), this choice excludes ~6.3% of 
Android devices. As the functionality is rather important for the app, and the percentage of 
excluded devices could be argued to be fairly low, we concluded that  changing the minimum SDK from 
24 to 26 is a reasonable choice for the project.
More details about the decision, including a screenshot from Android Studio can be found in the 
project report, chapters 2.2 and 3.4.
The target SDK is 34.  

## External libraries:

- Kotlinx Serialization 
- Jackson 
- Java Stream 
- Ktor 
- Jetpack Compose 
- AndroidX Lifecycle
- Espresso 
- Google Play Services Location 
- MOCKK 
- JUNIT 
- Google truth 
- OSMDroid  

Described in README.md  

## Potential improvements, issues to be addressed in future releases & further development: 

### Coarse and fine location

According to the Get coarse location article on the source.android.com website 
(https://source.android.com/docs/automotive/location/coarse-location), accessed on 08.05.2024, 
requesting only the coarse location from the user is something the developers are encouraged to do 
for user privacy reasons, and therefore can be described as best practice.
This was something our team did not discover early enough to successfully implement it and thus, 
the app functions based on data obtained by requesting access to the user’s fine location. 
This is a problem we are aware of, and would adress in the near future.

### Map/osmdroid

Though the current implementation works, it is important to note that the OSMdroid library does not 
currently have any official documentation for Jetpack Compose. While our team succeeded in implementing 
the most important functionalities as composables, we did not manage to do it for all the elements we 
wished to include. It is possible that, at some point, official documentation for compose appears, 
allowing for implementing a more optimal solution. However, if it does not, there might be some 
potential issues in the future, for instance, in case of a significant part of the implementation 
becoming deprecated.
Depending on the severity of such potential issues, a change to another library providing map 
services might be needed.

### Geocoder: reliability and deprecation

#### Reliability

According to the Geocoder article from developers.android.com (https://developer.android.com/reference/android/location/Geocoder#getFromLocationName(java.lang.String,%20int,%20android.location.Geocoder.GeocodeListener)), 
accessed on 09.05.2024:

> Geocoding services may provide no guarantees on availability or accuracy. 
> Results are a best guess, and are not guaranteed to be meaningful or correct.
> Do not use this API for any safety-critical or regulatory compliance purpose.

Our team is aware of the library's limitations however, because of of our time constraints we 
decided to still use it in the project due to its simplicity. Implementing a different library would 
require more time and therefore is something that would have to be adressed in the future.  

#### Deprecation

In our code (in GeocodingRepository.kt), we are using two deprecated methods for the Geocoder class. 
These are:

- geocoder.getFromLocationName
- geocoder.getFromLocation 

The reason for our team not updating the methods to the newer solution, is that it requires API level
33 (Tiramisu), which according to data available in Android Studio on 08.05.2024, includes only 
~22.4% of Android devices. Choosing the new, updated solution would therefore exclude the majority 
of users, which is something our team would prefer not to do. In the future however, as the 
percentage changes, updating the methods might be the right choice.
