#!/bin/sh -ex

export CLASSES=target/classes
export SOURCE=src/main/java
export CLASSPATH=lib/jna-5.0.0.jar

rm -r $CLASSES
mkdir -p $CLASSES

cp -r src/main/resources/* $CLASSES
javac -g -classpath $CLASSPATH -d $CLASSES `find $SOURCE -name '*.java'`
