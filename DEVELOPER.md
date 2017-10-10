# Developer Information

To try the examples it might be easiest to build the complete jar with all
the dependencies like this:

mvn -P build-archives -DskipITs -DskipTests package

Further Information can be found in the Wiki: https://github.com/marquies/cot-java-rest-sdk/wiki

To run the integration tests you should now use 'mvn integration-test'

## Try Examples ##

- Create CoT account
- export credentials (examples should read them from ENV to make this work)

    export COT_REST_CLIENT_HOST="your-host"
    export COT_REST_CLIENT_TENANT="your-tenant"
    export COT_REST_CLIENT_USER="your-user"
    export COT_REST_CLIENT_PASSWORD="your-password"

- compile SDK and install it into local maven repository
- compile examples
- compile JAR with all dependencies
- execute examples
  - create device
  - start alarm watcher
  - create alarms