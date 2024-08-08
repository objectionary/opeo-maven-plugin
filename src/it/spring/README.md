# Spring Integration Test

This integration test verifies all the phases of the `opeo-maven-plugin` along
with the `jeo-maven-plugin` and `ineo-maven-plugin`. In this test, we use a
Spring Framework-based application, build a fat jar application, and then apply
all the transformations and optimizations to it.

In short, the test is organised as follows:

1. **Compile the application**
3. **disassemble**: Apply transformation via the `jeo-maven-plugin:disassemble`
   goal for all the unpacked dependencies, including the application itself.
4. **Decompile**: Apply decompilation via the `opeo-maven-plugin:decompile` goal
5. **Optimize**: Apply optimization via the `ineo-maven-plugin:optimize` goal
6. **Compile**: Apply compilation via the `opeo-maven-plugin:compile` goal
7. **Assemble**: Apply back transformation via the `jeo-maven-plugin:assemble`
   goal
8. **Build the application**: Compiles the optimized application back into a
   fat jar.
9. **Run the application**: Run the optimized application to verify that it
   works correctly.

To exclusively run this test, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=spring -DskipTests
```

## PHI Expressions

You might find PHI expressions in the `target/generated-sources/phi-expressions`
directory.
To generate them, you don't need to run any special command.

### Run normalizer

You can also run [normalizer](https://github.com/objectionary/normalizer) to
check if transformation to PHI expressions was successfully performed:

```shell
normalizer dataize --recursive --minimize-stuck-terms --as-package target/it/spring/target/generated-sources/phi-expressions/org/eolang/jeo/spring/Factorial.phi
```

## Notes

It is the shortest version
of the [spring-fat integration test](../spring-fat/README.md).