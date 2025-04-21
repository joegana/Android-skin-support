rem 运行此脚本之前,请把sdk/linker/build.gradle 中的curFlavor 定义注释掉
cls
echo "Upload skin frame work!"
echo
echo
REM from command: --static-backup
cmd /c gradlew  "androidx:skin-support:artifactoryPublish"
cmd /c gradlew  "androidx:skin-support-appcompat:artifactoryPublish"
cmd /c gradlew  "androidx:skin-support-cardview:artifactoryPublish"
cmd /c gradlew  "androidx:skin-support-constrain-layout:artifactoryPublish"
cmd /c gradlew  "androidx:skin-support-design:artifactoryPublish"
cmd /c gradlew  "androidx:skin-support-percentlayout:artifactoryPublish"
cmd /c gradlew  "third-part-support:circleimageview:artifactoryPublish"
cmd /c gradlew  "third-part-support:flycotablayout:artifactoryPublish"