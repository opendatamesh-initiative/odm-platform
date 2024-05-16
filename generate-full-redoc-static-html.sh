#!/bin/bash

# Generate openapi1.json and redoc-static.html for each module
mvn clean verify -DskipTests -Pgenerate-doc

# Create a directory for aggregated documentation
AGGREGATED_DOC_DIR="redocly-docs"

# Check if the doc directory already exists
if [ -d "$AGGREGATED_DOC_DIR" ]; then
    # If it exists, remove it and create a new one
    rm -rf "$AGGREGATED_DOC_DIR"
fi

# Create a new doc directory
mkdir "$AGGREGATED_DOC_DIR"

# Find all redoc-static.html files
REDOC_FILES=$(find . -type f -name "redoc-static.html")

# Check if any redoc-static.html files were found
if [ -z "$REDOC_FILES" ]; then
    echo "No redoc-static.html files found in target directories."
    exit 1
fi

# Copy and rename all redoc-static.html files to the aggregated documentation directory
for FILE in $REDOC_FILES; do
    MODULE_NAME=$(dirname "$(dirname "$FILE")")
    NEW_NAME="${AGGREGATED_DOC_DIR}/${MODULE_NAME##*/}.html"
    cp "$FILE" "$NEW_NAME"
    echo "Renamed and copied $FILE to $NEW_NAME"
done

echo "Aggregated documentation generated successfully in $AGGREGATED_DOC_DIR"
