#!/bin/sh

# This utility script is used to wipe out all previous files from an Update Site before rebuilding it.

rm artifacts.jar
rm content.jar
rm -rf features
rm -rf plugins

