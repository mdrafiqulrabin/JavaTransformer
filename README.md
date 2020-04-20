# JavaTransformer
A tool to apply program transformations to Java methods, that generates new adversarial programs by inducing small (semantic-preserving) changes to original input programs.
- - -

# Version:
- Python 3.7.4
- javaparser 3.14.5

# JavaTransformer.jar:

- Create Jar file with Maven:
```
$ mvn clean compile assembly:single
# Output: target/jar/JavaTransformer.jar
```

- Create Jar file with Ant:
```
$ mvn dependency:copy-dependencies
$ ant jar
# Output: build/jar/JavaTransformer.jar
```

- Given input and output path, execute jar:
  ```
  # input_path  = Input directory to the original programs.
  # output_path = Output directory to the augmented programs.
  $ java -jar JavaAugmentation.jar "input_path" "output_path"
  ```

## Transformations:

- BooleanExchange
- LogStatement
- LoopExchange
- PermuteStatement
- ReorderCondition
- SwitchToIf
- TryCatch
- UnusedStatement
- VariableRenaming
