# Todos

* General
 * verify the format of all the IDs (string vs. number)

* Integration tests
 * make sure that all test data is precisely cleaned up

* CloudOfThingsRestClient
 * verify the necessity of Content-Type headers

* Null
 * EventApi: return null if event is not found
 * Array methods: return empty array instead of null?

* SmartREST real time notifications
 * More robust error handling
 * More efficient mass subscription
 * maybe new SmartRequestTemplate(String csv)

* RetentionRule
 * verify that filters are not applicable

* Complete Inventory 
 * Write children (implemented, needs refactoring)
 * Query Language
 * GET supported measurements of a managed object
 * testMultipleManagedObjectsByFragment

* Complete DeviceControl
 * Operations Filter with Agent
 * Bulk Operations

* Refactor FilterBuilder
 * Currently all filters are allowed with any *Collection. Would be better if just available filters
   work, especially for DELETE.

* Refactor the basics, Clean Code
* Implement Geo Features
* Make available different environments (base URL)
* Implement Mass Retrieval of Measurements 
* Notifications
 * MeasurementsAPI
 * ManagedObject

# Done 0.3.0
* Complete Inventory
 * Get ManagedObjects in Collection
* Complete Alarm API
 * Retrieve collections
 * Update Alarms
* Complete Device Credentials API
* Complete IdentityApi
 

# Done 0.2.0

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