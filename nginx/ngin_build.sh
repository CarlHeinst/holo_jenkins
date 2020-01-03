docker container rm ngin -f
docker build -t holo-ngin .
docker run -d -p 80:80 --name=ngin holo-ngin