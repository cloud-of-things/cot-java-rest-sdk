# Todos

* Complete Inventory 
 * Write children (implemented, needs refactoring)
 * Query Language
 * GET supported measurements of a managed object
 * GET a managed object reference collection
 * POST - add a managed object reference to the collection
 * GET a managed object reference
 * testMultipleManagedObjectsByFragment

* Complete DeviceControl
 * Operations Filter with Agent
 * Bulk Operations

* Refactor FilterBuilder
 * Currently all filters are allowed with any *Collection. Would be better if just available filters work.

* Refactor the basics, Clean Code
* Implement Geo Features
* Make available different environments (base URL)
* Implement Mass Retrieval of Measurements 
* Make it available on Maven
* Notifications
 * MeasurementsAPI
 * ManagedObject

# Done

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