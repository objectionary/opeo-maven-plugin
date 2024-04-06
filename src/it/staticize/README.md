## Staticize Optimization

This integration test checks the entire benchmark suite in conjunction with the
[ineo-maven-plugin](https://github.com/objectionary/ineo-maven-plugin)
optimizations. We utilize `staticize` optimization in this test.
To run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=staticize -DskipTests
```

_____

The integration test comprises the following steps, executed in order:

1. `jeo-maven-plugin:disassemble`, phase `process-classes`
2. `opeo-maven-plugin:decompile`, phase `process-classes`
3. `ineo-maven-plugin:staticize`, phase `process-classes`
4. `jeo-maven-plugin:assemble`, phase `generate-test-sources`
5. `exec-maven-plugin:exec`, phase `generate-test-sources`
6.

Despite the seemingly confusing declaration order, the actual execution sequence
is as presented above.