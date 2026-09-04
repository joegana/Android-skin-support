#!/bin/bash
# 运行此脚本之前,请把sdk/linker/build.gradle 中的curFlavor 定义注释掉
clear
echo "Upload skin frame work!"
export JAVA_HOME=$JAVA17_HOME
export PATH=$JAVA_HOME/bin:./:$PATH
echo
echo
# from command: --static-backup
gradlew  "androidx:skin-support:artifactoryPublish"
gradlew  "androidx:skin-support-appcompat:artifactoryPublish"
gradlew  "androidx:skin-support-cardview:artifactoryPublish"
gradlew  "androidx:skin-support-constrain-layout:artifactoryPublish"
gradlew  "androidx:skin-support-design:artifactoryPublish"
gradlew  "androidx:skin-support-percentlayout:artifactoryPublish"
gradlew  "third-part-support:circleimageview:artifactoryPublish"
gradlew  "third-part-support:flycotablayout:artifactoryPublish"





