# Simple Integration Test

This integration test verifies all the phases of the `opeo-maven-plugin` along
with the `jeo-maven-plugin` and `eo-maven-plugin:xmir-to-phi`.
In this test, we use a simple "Hello World" application.


To exclusively run this test, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=simple -DskipTests
```