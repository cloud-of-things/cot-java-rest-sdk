# Cloud of Things Java Rest SDK

This is an SDK to interface with the [Cloud of Things](https://m2m.telekom.com/our-offering/cloud-of-things/) by T-Systems International GmbH

_Current version is: 1.0.0_

## Usage

Add this to your `pom.xml` to include the SDK in your Maven Repo
```xml
<dependency>
    <groupId>com.telekom.m2m.cot</groupId>
    <artifactId>java-rest-client</artifactId>
    <version>1.0.0</version>
</dependency>
```

Exampe of a basic connect to the platform:
```java
CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform("hostname", new CotCredentials("tenant", "username", "password"));
InventoryApi inventoryApi = cotPlatform.getInventoryApi();
inventoryApi.get("idOfManagedObject");
```

## Developer Information and Examples

Developers can find information in the [Wiki](https://github.com/cloud-of-things/cot-java-rest-sdk/wiki) and DEVELOPER.md file.

## Java Doc

You can find the Java Doc of the latest release here: http://cloud-of-things.github.io/cot-java-rest-sdk/


## Release Notes

Short information about what has changed between releases.

<<<<<<< HEAD
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
