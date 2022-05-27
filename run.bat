echo "Compile and running function service ..."
docker build --tag web-rest . & mvnw.cmd clean install & docker-compose up