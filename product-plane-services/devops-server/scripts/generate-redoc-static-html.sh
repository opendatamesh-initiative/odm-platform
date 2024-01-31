#!/bin/bash

redocly build-docs ./target/openapi.json --output redoc-static.html --theme.openapi.hideHostname
mv ./redoc-static.html ./target