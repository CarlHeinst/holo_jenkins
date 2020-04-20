## This script can be used when you create an initial Jenkins instance to export all of the plugins that Jenkins recommends you should install.

## If running on Windows ensure you have choco install jq
# Otherwise it will fail.
curl.exe -s -k "http://admin:admin@localhost:8080/pluginManager/api/json?depth=1" | jq -r '.plugins[].shortName' | tee plugins.txt
##