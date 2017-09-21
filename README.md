# Cloud of Things Java Rest SDK

This is an SDK to interface with the [Cloud of Things](https://m2m.telekom.com/our-offering/cloud-of-things/) by T-Systems International GmbH

_Current version is: 0.6.0_

## Usage

Add this to your `pom.xml` to include the SDK in your Maven Repo
```xml
<dependency>
    <groupId>com.telekom.m2m.cot</groupId>
    <artifactId>java-rest-client</artifactId>
    <version>0.6.0</version>
</dependency>
```

Exampe of a basic connect to the platform:
```java
CloudOfThingsPlatform cotPlatform = new CloudOfThingsPlatform("hostname", "tenant", "username", "password");
InventoryApi inventoryApi = cotPlatform.getInventoryApi();
inventoryApi.get("idOfManagedObject");
```

## Java Doc

You can find the Java Doc of the latest release here: http://marquies.github.io/cot-java-rest-sdk/

## Developer Information

Developers can find information in the [Wiki](https://github.com/marquies/cot-java-rest-sdk/wiki)

## Completenes of API

**Completed Implemented**
* **Measurements**: Completely implemented
* **Events**: Completely implemented
* **Alarms**: Completely implemented
* **Inventory**: Completely implemented
* **Device credentials**: Completely implemented
* **Identity**: Completely implemented
* **Auditing**: Completely implemented
* **Retention rules**: Completely implemented
* **Users**: Completely implemented
* **SmartREST**: Completely implemented
* **Device control**: Completely implemented
* **Real-time notifications**: Completely implemented
* **Real-time statements**: Completely implemented
* **Device management library**: Completely implemented

**Not Implemented**
* **Sensor library**: Not implemented
* **Cumulocity Event Language**: Not implemented
* **Tenants**: Not implemented
* **Applications**: Not implemented
* **Binaries**: Not implemented

## Examples

See examples folder. (Needs improvements ;) )
Also see integration tests (*IT).

## Release Notes

Short information about what has changed between releases.

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
* Most important change is the removal of right now unused parameter tenant in CloudOfThingsPlatform constructor ([See commit 62079fe](https://github.com/marquies/cot-java-rest-sdk/commit/62079feee68dfc371b545cf2ed69fa9f858e5573)).
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
