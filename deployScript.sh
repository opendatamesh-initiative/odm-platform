#!/bin/bash

export DEBIAN_FRONTEND=noninteractive

echo 'debconf debconf/frontend select Noninteractive' | sudo debconf-set-selections

sudo apt-get update ; sudo apt-get install dialog apt-utils -y; sudo apt-get install openjdk-17-jdk openjdk-17-jre -y

#mkdir -p /home/$USER/src/main/resources/db/migration && cp /home/$USER/data.csv /home/$USER/src/main/resources/db/migration/data.csv

nohup java -jar /home/$USER/odm-platform-pp-registry-server-1.0.0.jar > /home/$USER/odm-platform-pp-registry-server-1.0.0.log 2>&1 &

nohup java -jar /home/$USER/odm-platform-pp-devops-server-1.0.0.jar > /home/$USER/odm-platform-pp-devops-server-1.0.0.log 2>&1 &

echo Script lanciato
