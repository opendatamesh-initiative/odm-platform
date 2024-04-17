#!/bin/bash

# Check if version parameter is provided
if [ -z "$1" ]; then
    echo "Version parameter is missing."
    exit 1
fi

DOC_REPO_URL="git@github.com:opendatamesh-initiative/odm-site-platform.git"
DOC_REPO_NAME="odm-site-platform"
DOC_DIR="odm-site-platform/docs/assets/redoc/doc"
SCRIPT_DIR="docs/assets/redoc/scripts"
VERSION="$1"

# Clone doc repository
git clone $DOC_REPO_URL
cd $DOC_DIR

if [ -d "$VERSION" ]; then

  echo "Subdirectory $VERSION exists. Overriding its content..."

  # Move files to the subdirectory
  cp ../../../../../redocly-docs/* $VERSION

else

  echo "Subdirectory $VERSION does not exist. Creating it..."

  # Create the subdirectory
  mkdir $VERSION

  # Move files to the subdirectory
  cp ../../../../../redocly-docs/* $VERSION

fi

# Generate index.html for github pages
cd ../../../../
./$SCRIPT_DIR/updateDocPage.sh

# Add changes to the staging area
git add .

# Commit changes
git commit -m "Override or create subdirectory $DIRECTORY and add docs inside it"

# Push changes
git push origin main
git checkout pre-deploy
git merge main --no-edit
git push

# Clean up
cd ..
rm -rf $DOC_REPO_NAME