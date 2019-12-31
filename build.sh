docker container rm holo-jenk -f
docker build -t holo-jenkins .
docker run -d -p 8080:8080 --name=holo-jenk holo-jenkins