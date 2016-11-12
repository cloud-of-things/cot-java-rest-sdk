# Cloud of Things Java Rest SDK

This is an SDK to interfere with the [Cloud of Things](https://m2m.telekom.com/our-offering/cloud-of-things/) by Deutsche Telekom AG

## Usage

Add this to your `pom.xml` to include the SDK in your Maven Repo
```xml
<dependency>
    <groupId>com.telekom.m2m.cot</groupId>
    <artifactId>java-rest-client</artifactId>
    <version>0.1.0</version>
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

## Completenes of API

**Completed Implemented**
* **Measurements**: Complete implemented
* **Events**: Complete implemented

**Partly Implemented**
* **Inventory**: Partly implemented
* **Device control**: Partly implemented
* **Alarms**: Partly implemented
* **Device credentials**: Partly implemented
* **Identity**: Partly implemented

**Not Implemented**
* **SmartREST**: Not implemented
* **Users**: Not implemented
* **Auditing**: Not implemented
* **Device management library**: Not implemented
* **Sensor library**: Not implemented
* **Real-time statements**: Not implemented
* **Cumulocity Event Language**: Not implemented
* **Real-time notifications**: Not implemented
* **Tenants**: Not implemented
* **Applications**: Not implemented
* **Binaries**: Not implemented

## Examples

See examples folder. (Needs improvements ;) )