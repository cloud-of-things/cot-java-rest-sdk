# Cloud of Things Java Rest SDK

The [Cloud of Things](https://m2m.telekom.com/our-offering/cloud-of-things/) (German Cloud der Dinge) is a platform for the Internet of Things by T-Systems International GmbH. Inside this repository you will find a Java based SDK to interface with the Cloud of Things API.

_Current version is: 1.0.5_

## Usage

Add this to your `pom.xml` to include the SDK in your Maven Repo
```xml
<dependency>
    <groupId>com.telekom.m2m.cot</groupId>
    <artifactId>java-rest-client</artifactId>
    <version>1.0.5</version>
</dependency>
```

Example of a basic connect to the platform:
```java
CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform("hostname", new CotCredentials("tenant", "username", "password"));
InventoryApi inventoryApi = cotPlatform.getInventoryApi();
inventoryApi.get("idOfManagedObject");
```

Developers can find further information and ***examples*** in the [DEVELOPER.md](https://github.com/marquies/cot-java-rest-sdk/blob/develop/DEVELOPER.md) file.

## Java Doc

You can find the Java Doc of the latest release here: http://cloud-of-things.github.io/cot-java-rest-sdk/


## Release Notes

Short information about what has changed between releases.

### Release 1.0.5
* Provide functionality to get all SmartREST Templates by X-ID
* Resolve [issue #52](https://github.com/cloud-of-things/cot-java-rest-sdk/issues/52): provide http status code in thrown exception in getMeasurement()

### Release 1.0.4
* Improve usage of accept and content-type headers in rest client
* Improve usage of OkHttpClient so that lesser resources will be used
* Fix handling of binary data using byte array
* Remove double slashes in rest api paths to avoid http code 404
* Resolve [issue #51](https://github.com/cloud-of-things/cot-java-rest-sdk/issues/51): provide failure reason for failed operations
* Resolve [issue #53](https://github.com/cloud-of-things/cot-java-rest-sdk/issues/53): provide a new method for creation NewDeviceRequests
* Handle filters as enum and validate filters supported by cot
* Improve Notification class which now provides realtime action

### Release 1.0.2
* Update of okhttp.
* Better integration-tests.
* Better login-process (include tenant in username).
* NewDeviceRequestStatus-enum.
* Refactoring.

### Release 1.0.0
* Improved Examples: Add examples for SDK-Users (see DEVELOPER.md)
* Implemented Sensor Library
* Implemented Binaries API
* Final adjustments for MIT License
* Improve ManagedObject-deserialization
* Credentials for integration tests will be read from environment variables instead of property file
* Refactor device permission handling
* A lot of improvements and cleanups

### Release 0.6.1
* Provides and uses SmartRequest/-Response classes

### Release 0.6.0
* Complete Device control
* Complete Real-time notifications
* Complete Real-time statements
* Complete Device management library
* Complete Users API
* Complete Retention Rule API
* Complete SmartREST API
* Complete Audit API
* Refactor collection classes extending JsonArrayPagination base class

### Release 0.5.0
* Most important change is the removal of right now unused parameter tenant in CloudOfThingsPlatform constructor ([See commit 62079fe](https://github.com/cloud-of-things/cot-java-rest-sdk/commit/62079feee68dfc371b545cf2ed69fa9f858e5573)).
* Unsuccessful creation/store of objects in the CoT will now result in a CotSdkException.
* New method DeviceCredentialsApi.NewDeviceRequest(..).
* MeasurementsApi supports creating measurement collections.

### Release 0.4.0
* Improvements, e.g. connection exception handling
* Short information about what has changed between releases.
* Bulk Operations (beta)

### Release 0.3.0
* Complete Inventory
 * Get ManagedObjects in Collection
* Complete Alarm API
 * Retrieve collections
 * Update Alarms
* Complete Device Credentials API
* Complete IdentityApi

### Release 0.2.0
* Complete Events
* Complete Device registration process
* Implement removal of devices and its belongings
* Complete MeasurementsAPI
 * Refactor from single methods to Query objects. 
 * Filter Date
 * Filter FragmentType
 * ... and combinations
 * DELETE - delete a measurement collection
* Complete Inventory
 * DELETE a managed object reference
