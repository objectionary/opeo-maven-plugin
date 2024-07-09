## Streams to PHI

This integration tests verifies how the java streams are transformed into the
PHI expressions.
To run only this test, use the following command:

```shell
mvn clean integration-test -Dinvoker.test=streams -DskipTests
```

The discussion related to this test is presented in the
GitHub [issue](https://github.com/objectionary/opeo-maven-plugin/issues/329).
So, if you have any questions, fill free to ask them there or raise a new issue.

_____

Here we are tying to verify how the Java Streams are transformed into the PHI
expressions, particularly the following code:

```java
String[] strings = new String[10];
for (int i = 0; i < strings.length; i++) {
    strings[i] = String.valueOf(i);
}
int sum = Arrays.stream(strings)
    .filter(s -> !s.equals(""))
    .mapToInt(s -> Integer.parseInt(s))
    .sum();
System.out.println(sum);
```

You can find the original code in the
[Main.java](src/main/java/org/eolang/streams/Main.java) class