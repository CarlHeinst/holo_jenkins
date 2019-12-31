# Holo Jenkins

To test clone down the repo, then execute:

```bash
docker container rm holo-jenk -f
docker build -t holo-jenkins .
docker run -d -p 8080:8080 --name=holo-jenk holo-jenkins
```

This will create a new instance of jenkins which has been prepopulated with the super insecure "admin" "admin" credentials.

> Do Not Ever use this in Production!