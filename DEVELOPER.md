# Developer Information

## Try Examples ##

### Get Credentials ###

Get a free Cloud of Things demo account at [http://m2m.telekom.com/our-offering/cloud-of-things/](http://m2m.telekom.com/our-offering/cloud-of-things/).

Export your Cloud of Things credentials as environment variables. These are required by the examples:

    export COT_REST_CLIENT_HOST="your-host"
    export COT_REST_CLIENT_TENANT="your-tenant"
    export COT_REST_CLIENT_USER="your-user"
    export COT_REST_CLIENT_PASSWORD="your-password"

### Compiling ###

Compile the CoT REST SDK and install it into your local maven repository:

    mvn clean install --activate-profiles build-archives -DskipITs -DskipTests -Dgpg.skip

Compile the example classes and their dependencies:

     mvn --activate-profiles build-archives -f examples/pom.xml package

### Executing Examples ###

Create a new device named ``try-examples``:

    java -cp examples/target/java-rest-client-examples-with-dependencies-0.7.0-SNAPSHOT.jar DeviceCreator --create try-examples

This generates output that contains the ID of the new device, e.g.:

    The new device 'try-examples' has the ID 199332

Remember the ID of the device, it will be needed to trigger an alarm.

Show all devices that are registered in the Cloud of Things:

    java -cp examples/target/java-rest-client-examples-with-dependencies-0.7.0-SNAPSHOT.jar InventoryPrinter

The created device should show up there.

Start an alarm watcher:

    java -cp examples/target/java-rest-client-examples-with-dependencies-0.7.0-SNAPSHOT.jar AlarmWatcher

Trigger an alarm in a new terminal window:

    java -cp examples/target/java-rest-client-examples-with-dependencies-0.7.0-SNAPSHOT.jar AlarmTrigger 199332 my_alarm_identifier CRITICAL "My alarm message."
    
## Run Tests ##

Run the unit tests:

    mvn test

Run the integration tests:

    mvn integration-test
    
## Build the SDK ##
    
Use the following command to build a jar that contains the SDK and all its dependencies:

    mvn -P build-archives -DskipITs -DskipTests package
