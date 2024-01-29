# 1. Install it npm install -g @redocly/cli
# 2. make this script executable with chmod +x generate-redoc.sh
# 3. Run it

# Set your Swagger JSON endpoint URL
SWAGGER_JSON_URL="http://localhost:8001/api/v1/pp/v3/api-docs"

# Set the output directory
OUTPUT_DIR="./target"

# Generate Redoc documentation using @redocly/cli
npx @redocly/cli bundle $SWAGGER_JSON_URL -o $OUTPUT_DIR/redoc-static.html --options.hideHostname --theme ./scripts/theme.json

# OLD
#redoc-cli build ./target/openapi.json  --options.hideHostname
#mv ./redoc-static.html ./target