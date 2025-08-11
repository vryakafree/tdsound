#!/usr/bin/env sh

APP_HOME="$(cd "$(dirname "$0")" && pwd -P)"
JAVA_EXE="$(command -v java)"
exec "$JAVA_EXE" -Dorg.gradle.appname=gradlew -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
