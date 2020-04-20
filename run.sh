#!/bin/bash
clear
echo "Creating JavaTransformer.jar"
mvn clean compile assembly:single
java -jar target/jar/JavaTransformer.jar > target/default.log
echo "Saved JAR into target/jar/"
