FROM payara/micro:5.194
COPY build/libs/gradle-microprofile-template.war /opt/payara/deployments
COPY configs/post-boot-command.txt /opt/configs/
CMD ["--deploymentDir", "/opt/payara/deployments", "--noCluster", "--postbootcommandfile", "/opt/configs/post-boot-command.txt"]
