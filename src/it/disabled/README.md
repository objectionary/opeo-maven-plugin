# 'Disable Plugin' Integration Test

This test checks that the plugin can be disabled by setting the `disabled`
property to `true` in the configuration:

```xml

<configuration>
  <disabled>true</disabled>
</configuration>
```

To run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=disabled -DskipTests
```
