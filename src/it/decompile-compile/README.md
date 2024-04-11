## Decompile-Compile Integration Test

This integration test decompiles XMIR produced
by [jeo-maven-plugin](https://github.com/objectionary/jeo-maven-plugin)
by `decompile` goal and then compiles it back to Java source code
using `compile` goal and `jeo-maven-plugin`
To run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=decompile-compile -DskipTests
```
