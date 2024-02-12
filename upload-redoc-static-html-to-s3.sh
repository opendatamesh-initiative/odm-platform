#!/bin/bash

# Check if environment variables are set
if [ -z "$AWS_ACCESS_KEY_ID" ] || [ -z "$AWS_SECRET_ACCESS_KEY" ] || [ -z "$AWS_DEFAULT_REGION" ]; then
    echo "AWS environment variables are not set. Please set AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, and AWS_DEFAULT_REGION."
    exit 1
fi

# Check if version parameter is provided
if [ -z "$1" ]; then
    echo "Version parameter is missing."
    exit 1
fi

# Check if bucket name parameter is provided
if [ -z "$2" ]; then
    echo "Bucket Name parameter is missing."
    exit 1
fi

# Version from command line argument
version="$1"

# Fixed bucket names and versions
bucket_name="$2"

# Subdirectory for the specified version
subdirectory="v${version}/"

# Upload all files from redocly-docs directory to S3 bucket
aws s3 sync redocly-docs/ s3://$bucket_name/$subdirectory --acl public-read --content-type "text/html"

# Print URLs of the uploaded HTML files
echo "HTML files uploaded and accessible at: https://$bucket_name.s3.amazonaws.com/$subdirectory<module>-server-redoc-static.html (<module> could be: 'blueprint', 'registry' or 'devops')"