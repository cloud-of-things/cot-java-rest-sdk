# Developer Information

To try the examples it might be easiest to build the complete jar with all
the dependencies like this:

mvn -P build-archives -DskipITs -DskipTests package

Further Information can be found in the Wiki: https://github.com/marquies/cot-java-rest-sdk/wiki

To run the integration tests you should now use 'mvn integration-test'

## Try Examples ##

### Get Credentials ###

- Create CoT account

Export your Cloud of Things credentials as environment variables. These are required by the examples:

    export COT_REST_CLIENT_HOST="your-host"
    export COT_REST_CLIENT_TENANT="your-tenant"
    export COT_REST_CLIENT_USER="your-user"
    export COT_REST_CLIENT_PASSWORD="your-password"

### Compiling ###

Compile the CoT REST SDK and install it into your local maven repository:

    mvn clean install -DskipITs -DskipTests -Dgpg.skip

Compile the example classes:

    mvn compile -f examples/pom.xml

Create a REST client jar that contains all dependencies:

    mvn -P build-archives -DskipITs -DskipTests package

### Executing Examples ###

Create a new device named ``try-examples``:

    java -cp examples/target/classes:target/java-rest-client-with-dependencies-0.7.0-SNAPSHOT.jar DeviceCreator --create try-examples

This generates output that contains the ID of the new device, e.g.:

    The new device 'try-examples' has the ID 199332

Remember the ID of the device, it will be needed to trigger an alarm.

Start an alarm watcher:

    java -cp examples/target/classes:target/java-rest-client-with-dependencies-0.7.0-SNAPSHOT.jar AlarmWatcher

Trigger an alarm in a new terminal window:

    java -cp examples/target/classes:target/java-rest-client-with-dependencies-0.7.0-SNAPSHOT.jar AlarmTrigger 199332 my_alarm_identifier CRITICAL "My alarm message."