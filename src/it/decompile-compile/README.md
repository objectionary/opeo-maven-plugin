## Decompile-Compile Integration Test

This integration test decompiles XMIR produced
by [jeo-maven-plugin](https://github.com/objectionary/jeo-maven-plugin)
by `decompile` goal and then compiles it back to `jeo-maven-plugin`
representation using `compile` goal.

To run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=decompile-compile -DskipTests
```
