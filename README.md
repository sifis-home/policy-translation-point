# Policy Translation Point (PTP)

PTP translates high-level security policies like “Do not record sound in the living room tonight” into low-level policies in a [XACML](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=xacml) formalism. Furthermore, PTP detects potential conflicts linke redundancies and inconsistencies between high-level policies.

## HTTP APIs
The documentation of the HTTP APIs exposed by PTP is available [here](documentation/API-doc.pdf).

## Instructions to install PTP on [Docker](https://www.docker.com/)
To install PTP on Docker, you will need to create three different containers:
1. **ptp-server**: a container that hosts the backend server (a [Spring Boot](https://spring.io/projects/spring-boot) application written in Java) and the frontend (an [Angular](https://angular.io/) application written in TypeScript).
2. **ptp-db**: a container that hosts a MySql server. A [docker volume](https://docs.docker.com/storage/volumes/) will be attached to this container to persist the data permanently on the Docker Host, rather than just for the lifetime of the container.
3. **ptp-inspector**: a container to connect to the *ptp-db* container and run SQL commands in order to  import the initial database schema.

### 1 - Setup the Docker Environment
To make `ptp-server` and `ptp-db` communicate between each other, you have to create a Docker Network. The following command creates a new Docker Network called `ptp-network`
```
$ docker network create --driver=bridge ptp-network
```
Then, you can create the `ptp-db` container;
```
$ docker run --name ptp-db -v ptp-db:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootroot -e MYSQL_DATABASE=sifis-ptp --network ptp-network -d mysql:5.7
```
Here, `rootroot` is the password of the MySql root account, while `sifis-ptp`is the name of the database that will be exploited by the web application.

After creating the the container for the MySql server, you will have to create the `ptp-inspector`container:
```
$ docker run -dit --name ptp-inspector --network ptp-network alpine ash
```
Through the inspector, you can copy the SQL file of the database (available in the root folder of this project):
```
$ docker cp schema.sql ptp-inspector:/tmp/schema.sql
```
Then, attach our STDIN, STDOUT and STDERR to the container, in order to set it up properly:
```
$ docker container attach ptp-inspector
```
and install a MySQL client and an HTTP client through the following command:
```
# apk update && apk upgrade && apk add mysql-client curl
```
You now have a MySQL client as well as an SQL file in `/tmp/schema.sql` that you can import:
```
# mysql -h ptp-db -u root -p sifis-ptp < /tmp/schema.sql
```
The SQL schema should now be created, you can exit from the container by hitting `CTRL+P`  `CTRL+Q` successively in order to detach STDIN, STDOUT and STDERR from the container which will bring you back to your local shell session.

### 2 - Build and Deploy the Web Application
After setting up the Docker environment, you have to build the docker image for the `ptp-server` container.

First, run this command to generate the required Maven files and folders:
```
mvn -N wrapper:wrapper
```
Then, use this command to build the image:

```
$ docker build -t ptp-server .
```

Finally, you can run the `ptp-server` container:
```
$ docker run -d -p 8080:8080 --name ptp-server --network ptp-network ptp-server
```
The web application should be accessible at http://localhost:8080/
