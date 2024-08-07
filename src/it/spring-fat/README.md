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

## Full Run

To exclusively run this test, execute the command below:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests 
```

## PHI Expressions

If you are interested in how a Spring Application with all its dependencies
looks using PHI expressions, you can use the following command:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests -Dinvoker.invokerPropertiesFile="invoker.phi.properties"
```

After running this command, you can find the PHI expressions in
the `./target/it/spring-fat/target/generated-sources/phi-expressions` directory.

**Pay attention!**
You need to run this command from the root directory of the project, not from
this directory.
So, you need to be inside the `opeo-maven-plugin` directory.

**Warning!**
Not all bytecode classes are represented in the PHI expressions. We still don't
implement all the bytecode instructions in the PHI expressions. Moreover, even
for some instructions that we do implement, we still have issues related to
incorrect XMIR representation. Therefore, in most cases, we just skip those
classes that either have unsupported instructions or have incorrect XMIR
representation. Moreover, the current representation of the PHI expressions is
not the final one. We are still working on improving it.

### Run normalizer

You can also run [normalizer](https://github.com/objectionary/normalizer) to
check if transformation to PHI expressions was successfully performed:

```shell
normalizer dataize --recursive --minimize-stuck-terms --as-package target/it/spring/target/generated-sources/phi-expressions/org/eolang/jeo/spring/Factorial.phi
```

## Jeo Only

If you need to check only the current version of `jeo-maven-plugin`
you can use the following command:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests -Dinvoker.invokerPropertiesFile="invoker.jeo.only.properties"
```

This might be helpful to ensure that the `jeo-maven-plugin` works correctly.

## Jeo and Opeo only

If you need to test **only** transformations without optimizations and
PHI printing, you can use the following command:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests -Dinvoker.invokerPropertiesFile="invoker.jeo.opeo.properties"
```

This might be helpful to exclude an optimization influence.

## Just Compile

If you need to test **only** the compilation process, you can use the following
command:

```shell
mvn clean integration-test -Dinvoker.test=spring-fat -DskipTests -Dinvoker.invokerPropertiesFile="invoker.nothing.properties"
```

## How to Run the Application Using Plain Java

If you want to run the application using plain Java, you need to build the
jar file first. First of all, you need to change the directory to the
`./target/it/spring-fat` directory. Then, you can run the following command
to compile fat-jar:

```shell
mvn clean package
```

After that, you can run the application using the following command:

```shell
java -cp target/opeo-spring-fat-it-@project.version@.jar org.eolang.jeo.spring.FactorialApplication 
```

We use [Spring Boot](https://spring.io/projects/spring-boot)
to run the application.
Spring Boot uses `spring.factories` configuration files in
its ['starter' dependencies](https://github.com/spring-projects/spring-boot/blob/66ff668b4db9ec6b10f99cff344069711b618d57/spring-boot-project/spring-boot-starters/README.adoc)
to determine which beans to use in the context.
Since we copy and unpack all the dependencies to the `target/classes` directory,
we overwrite the `target/classes/META-INF/spring.factories` file from several
Spring Boot starters multiple times.
As a result, when we start our application,
it might use an incorrect `spring.factories` file and, therefore, incorrect
beans.
To avoid this issue, we explicitly added the
required [spring.factories](src%2Fmain%2Fresources%2FMETA-INF%2Fspring.factories)
file to
the final jar file.
You can read more about this
problem [here](https://stackoverflow.com/questions/78618894/how-to-run-spring-boot-application-with-unpacked-dependencies).

## The First Results

Here is the summary of the first results of the `spring-fat` integration test:

### Total

- The application starts and runs successfully.
- Time is **11:08 min**.
- The total number of classes is **19511**.
- Phases: **disassemble**, **decompile**, **optimize**, **phi**, **compile**, *
  *assemble**.

### Disassemble

- All classes are successfully disassembled.
- The total number of classes is **19511**.
- The Disassembly phase takes **50s**.

### Decompile

- Some classes are not decompiled due to unsupported instructions. We skip a
  file if it contains at least one instruction we don't support yet. Skipped
  files are copied as is from the disassembled directory to the decompiled.
- The total number of truly decompiled classes is **4672**.
- The Decompile phase takes **? min**.

### Optimize

- We used the "staticize" optimization from the `ineo-maven-plugin`.
- The total number of optimized classes is **1**.
- The Optimize phase takes **? min**.

### Phi

- We print PHI expressions for all truly decompiled classes.
- Some classes are not printed due to some issues with the XMIR representation.
  We skip a file if it contains at least one issue.
- The total number of classes with PHI expressions is **4153**.
- The Phi phase takes **? min**.

### Compile

- We compile all the classes from different XMIR representations.
- The total number of compiled classes is **19511**.
- The Compile phase takes **? min**.

### Assemble

- We assemble all the classes back to the bytecode.
- The total number of assembled classes is **19511**.
- The Assembly phase takes **57s**.

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

## Troubleshooting

If you have any questions or trouble with the plugin, please submit an issue.

If you are a developer, and you have found a bug in a decompilation/compilation
process, please try to
run [this integration test](../../test/java/it/DetectiveIT.java) to
specify the place where the bug is located. Moreover, this test can
greatly help you to troubleshoot any problems with the plugin.
