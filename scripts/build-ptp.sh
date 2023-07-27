#!/bin/bash

# Build ace-entities


# Fail script with error if any command fails
set -e

## If indicated install Mysql server
if [ "$1" = "--with-mysql" ]
then
  echo "mysql-server mysql-server/root_password password rootroot" | sudo debconf-set-selections
  echo "mysql-server mysql-server/root_password_again password rootroot" | sudo debconf-set-selections
  sudo apt-get -y install mysql-server
  sudo systemctl start mysql.service
  echo "root" > db.pwd # Root username
  echo "rootroot" >> db.pwd # Root pw
  sudo mysql -u root -prootroot < schema.sql
fi

# Run mvn on ace-entities
# https://stackoverflow.com/questions/65092032/maven-build-failed-but-exit-code-is-still-0
echo "*** Building and packaging PTP entities ***"
mvn --batch-mode clean verify | tee mvn_res
if grep 'BUILD FAILURE' mvn_res;then exit 1; fi;
if grep 'BUILD SUCCESS' mvn_res;then exit 0; else exit 1; fi;
rm mvn_res

if [ "$1" = "--with-mysql" ]
then
mv db.pwd apps/PTP
fi
