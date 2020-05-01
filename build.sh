docker container rm holo-jenk -f
docker build --no-cache=true -t holo-jenkins .
docker run -d -p 8080:8080 --env-file env.file --name=holo-jenk holo-jenkins