# JavaTransformer
A tool to apply program transformations on Java **(\*.java)** methods for generating semantic adversarial input programs.
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

# References:

- Testing Neural Program Analyzers [[Paper](https://arxiv.org/abs/1908.10711)] [[GitHub](https://github.com/mdrafiqulrabin/tnpa-framework)]
- On the generalizability of Neural Program Models with respect to semantic-preserving program transformations [[Paper](https://arxiv.org/abs/2008.01566)] [[GitHub](https://github.com/mdrafiqulrabin/tnpa-generalizability)]
- JavaParser: https://github.com/javaparser/javaparser
