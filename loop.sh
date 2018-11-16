#!/bin/sh -ex

DEFAULT_JVM_OPTS=-Djna.library.path=dep

java $DEFAULT_JVM_OPTS -classpath target/classes:lib/jna-5.0.0.jar dragontail.Loop $*
