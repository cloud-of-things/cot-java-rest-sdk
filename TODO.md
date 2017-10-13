# Todos

* SmartREST real time notifications
 * maybe new SmartRequestTemplate(String csv)

* Real time notifications
 * Timing advice
 * No notification on delete of operations?

* Real time modules
 * Redeployment of modules doesn't always work
 * more input validation

* Device permissions
 * client side validation

* RetentionRule
 * verify that filters are not applicable

* Complete Inventory 
 * Write children (implemented, needs refactoring)
 * Query Language
 * GET supported measurements of a managed object
 * testMultipleManagedObjectsByFragment

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