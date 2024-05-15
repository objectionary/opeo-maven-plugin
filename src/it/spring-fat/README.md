# Full Spring Fat Jar Integration Test

This integration test verifies all the phases of the `opeo-maven-plugin` along
with the `jeo-maven-plugin` and `ineo-maven-plugin`. In this test, we use a
Spring Framework-based application, build a fat jar application, and then apply
all the transformations and optimizations to it.

The distinctive aspect of this test is its comprehensive process of handling
application dependencies alongside the application code itself. It downloads all
required dependencies, unpacks them, and compiles the entire application. Then,
it applies transformations via the `jeo-maven-plugin` and receives the `jeo`
representation, which it then tries to decompile
using `opeo-maven-plugin:decompile`. Next, it applies optimization via
`ineo-maven-plugin:optimize`, and then it compiles the optimized representation
back to the `jeo` representation using `opeo-maven-plugin:compile`. Finally, it
assembles the compiled `jeo`representation back into bytecode using
`jeo-maven-plugin:assemble`. By doing all this steps, it can ensure that
the `opeo-maven-plugin` does not break the application and works as expected.

In short, the process is as follows:

1. **Compile the application and download all its dependencies**
2. **Unpack dependencies**
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
mvn clean integration-test invoker:run -Dinvoker.test=spring-fat -DskipTests
```

## The First Results

Here is the summary of the first results of the `spring-fat` integration test:

- The application starts and runs successfully.
- The average test time is approximately **826 seconds (13 minutes)**.
- The total number of classes is **19511 (19516 — sometimes the number is
  different? Why?)**.
- The Disassembly phase takes approximately **1 minute**.
- The Decompile phase takes approximately **K minutes**.
- The Compile phase takes approximately **L minutes**.
- The Assembly phase takes approximately **56 seconds**.

## Developer Notes

### Excluded from the default build pipeline

This test remains rather long and has not yet been optimized. As a result, it
is excluded from the default build pipeline. To run this test alongside all
others, you need to activate the `long` Maven profile. Use the command below:

```bash
mvn clean install -Plong
```

### Bytecode verification

Some Spring Boot components were compiled with optional dependencies, and these
dependencies are not present in the corresponding POM files. Therefore, we
cannot find these dependencies during the test. However, the current
implementation of bytecode verification requires that all classes are loaded by
the `ClassLoader` before verification, which is not possible in this case. As a
result, we skip bytecode verification for this test.

```xml

<skipVerification>true</skipVerification>
```

You can read more about this problem [in
the `jeo-maven-plugin` configuration](https://github.com/objectionary/jeo-maven-plugin/tree/master/src/it/spring-fat#bytecode-verification).


