# JavaTransformer
A tool to apply program transformations on Java **(\*.java)** methods for generating semantic-preserving transformed programs.
- - -

# Used Version(s):
- openjdk 1.8.0_222
- javaparser 3.24.4

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
  # output_path = Output directory to the transformed programs.
  $ java -jar JavaTransformer.jar "input_path" "output_path"
  ```
  Note:  
  > The scope of transformation is at the method level, so each **(\*.java)** file should contain only a single method.  
  > To modify the scope, first check [getParseUnit](https://github.com/mdrafiqulrabin/JavaTransformer/blob/31bab80927b9b86de0650cc1c0f659edae89ebdc/src/main/java/Common.java#L48) for CompilationUnit.  
  > Or, convert java files into single method-only files using [JavaMethodExtractor](https://github.com/mdrafiqulrabin/tnpa-generalizability/tree/master/JavaMethodExtractor) before applying transformation.

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
