# Added lines for Docker
apt-get update
apt-get install -y build-essential npm
npm install redoc-cli -g
# Original file
redoc-cli build ./target/openapi.json  --options.hideHostname
mv ./redoc-static.html ./target