## If running on Windows ensure you have choco install jq
# Otherwise it will fail.
curl.exe -s -k "http://Admin:Admin@localhost:8080/pluginManager/api/json?depth=1" | jq -r '.plugins[].shortName' | tee plugins.txt