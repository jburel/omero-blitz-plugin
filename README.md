[![Actions Status](https://github.com/ome/omero-blitz-plugin/workflows/Gradle/badge.svg)](https://github.com/ome/omero-blitz-plugin/actions)

## OMERO blitz Gradle plugin

The _omero-blitz-plugin_ is a [Gradle](https://gradle.org) plugin that provides
users and projects the ability to generate/compile the files required
to use _omero-blitz_.
The plugin can be built using Gradle 5 or Gradle 6.

From a high level, the omero-blitz-plugin consists of the following tasks/stages:

1. Process and split `xx.combined` files into chosen languages

## Usage

Include the following at the top of your _build.gradle_ file:

```groovy
plugins {
    id "org.openmicroscopy.blitz" version "x.y.z"
}
```


### Gradle Tasks

| Task name      | Depends On     |
| -------------- | -------------- |
| importMappings |                |
