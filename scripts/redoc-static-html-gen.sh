# Added lines for Docker
apt-get update
#apt-get install -y build-essential npm
apt-get install -y npm
npm i -g redoc-cli
# Original file
redoc-cli build ./target/openapi.json  --options.hideHostname
mv ./redoc-static.html ./target