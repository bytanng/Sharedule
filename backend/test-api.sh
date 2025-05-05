#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Testing Sharedule API"
echo "-------------------"

# Login and get token
echo -e "\n${GREEN}1. Logging in to get token${NC}"
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
    "username": "your_username",
    "password": "your_password"
}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]
then
    echo -e "${RED}Failed to get token. Make sure the server is running and credentials are correct.${NC}"
    exit 1
fi

echo "Token received successfully"

# Create an item
echo -e "\n${GREEN}2. Creating a new item${NC}"
curl -X POST http://localhost:8080/api/items \
-H "Content-Type: application/json" \
-H "Authorization: Bearer $TOKEN" \
-d '{
    "itemName": "Test Item",
    "itemDescription": "This is a test item",
    "itemPrice": 99.99,
    "itemStock": 10,
    "itemAvailable": true,
    "itemImage": "https://example.com/image.jpg"
}'

echo -e "\n\nTest completed!" 