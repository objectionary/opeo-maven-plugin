# Fuse Optimization

This is an integration test that checks the entire benchmark suite in
conjunction with
the [ineo-maven-plugin](https://github.com/objectionary/ineo-maven-plugin)
optimizations. Specifically, we employ the `fuse` optimization technique in this
test.

To run only this test, use the following command:

```shell
mvn clean integration-test invoker:run -Dinvoker.test=fuse -DskipTests"
```
