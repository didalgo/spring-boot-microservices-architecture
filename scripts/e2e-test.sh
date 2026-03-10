#!/bin/bash

# ========================================
# End-to-End Testing Script
# ========================================

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  End-to-End Testing${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""

BASE_URL="http://localhost:8080"
TESTS_PASSED=0
TESTS_FAILED=0

# Function to test endpoint
test_endpoint() {
    local name="$1"
    local method="$2"
    local url="$3"
    local headers="$4"
    local body="$5"
    local expected_status="${6:-200}"
    
    echo -e "${YELLOW}Testing: $name${NC}"
    
    # Build curl command
    local curl_cmd="curl -s -w '\n%{http_code}' -X $method"
    
    if [ -n "$headers" ]; then
        curl_cmd="$curl_cmd $headers"
    fi
    
    if [ -n "$body" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json' -d '$body'"
    fi
    
    curl_cmd="$curl_cmd '$url'"
    
    # Execute request
    response=$(eval $curl_cmd 2>/dev/null)
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n-1)
    
    # Check status code
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "  ${GREEN}✓ PASSED${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        echo "$response_body"
    else
        echo -e "  ${RED}✗ FAILED: Expected $expected_status, got $http_code${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        echo ""
    fi
}

# Test 1: Health Checks
echo -e "\n${CYAN}--- Health Checks ---${NC}\n"

test_endpoint "Config Server Health" "GET" "http://localhost:8888/actuator/health"
test_endpoint "Eureka Server Health" "GET" "http://localhost:8761/actuator/health"
test_endpoint "API Gateway Health" "GET" "$BASE_URL/actuator/health"
test_endpoint "Auth Service Health" "GET" "http://localhost:8084/actuator/health"

# Test 2: Authentication
echo -e "\n${CYAN}--- Authentication ---${NC}\n"

login_body='{
  "username": "admin",
  "password": "admin123"
}'

response=$(curl -s -w '\n%{http_code}' -X POST \
    -H 'Content-Type: application/json' \
    -d "$login_body" \
    "$BASE_URL/auth/login" 2>/dev/null)

http_code=$(echo "$response" | tail -n1)
response_body=$(echo "$response" | head -n-1)

if [ "$http_code" = "200" ]; then
    echo -e "${YELLOW}Testing: Login with admin${NC}"
    echo -e "  ${GREEN}✓ PASSED${NC}"
    TESTS_PASSED=$((TESTS_PASSED + 1))
    
    # Extract token
    TOKEN=$(echo "$response_body" | grep -o '"accessToken":"[^"]*' | sed 's/"accessToken":"//')
    echo -e "  ${GRAY}Token obtained: ${TOKEN:0:20}...${NC}"
    
    # Test 3: Create Order
    echo -e "\n${CYAN}--- Order Management ---${NC}\n"
    
    order_body='{
      "customerId": "CUST-001",
      "productId": "PROD-001",
      "quantity": 2,
      "totalAmount": 199.99
    }'
    
    response=$(curl -s -w '\n%{http_code}' -X POST \
        -H "Authorization: Bearer $TOKEN" \
        -H 'Content-Type: application/json' \
        -d "$order_body" \
        "$BASE_URL/api/orders" 2>/dev/null)
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" = "201" ]; then
        echo -e "${YELLOW}Testing: Create Order${NC}"
        echo -e "  ${GREEN}✓ PASSED${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        
        # Extract order ID
        ORDER_ID=$(echo "$response_body" | grep -o '"orderId":"[^"]*' | sed 's/"orderId":"//')
        echo -e "  ${GRAY}Order created: $ORDER_ID${NC}"
        
        # Test 4: Get Order by ID
        test_endpoint "Get Order by ID" "GET" "$BASE_URL/api/orders/$ORDER_ID" "-H 'Authorization: Bearer $TOKEN'"
        
        # Test 5: Get All Orders (admin only)
        test_endpoint "Get All Orders (admin)" "GET" "$BASE_URL/api/orders" "-H 'Authorization: Bearer $TOKEN'"

        # Test 6: Get My Orders
        test_endpoint "Get My Orders" "GET" "$BASE_URL/api/orders/my-orders" "-H 'Authorization: Bearer $TOKEN'"
    else
        echo -e "${YELLOW}Testing: Create Order${NC}"
        echo -e "  ${RED}✗ FAILED: Expected 201, got $http_code${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
    
    # Test 6: Payment
    echo -e "\n${CYAN}--- Payment Management ---${NC}\n"
    
    payment_body='{
      "orderId": "ORD-TEST-001",
      "amount": 100.50,
      "paymentMethod": "CREDIT_CARD",
      "customerEmail": "customer@example.com",
      "cardNumber": "4532015112830366"
    }'
    
    response=$(curl -s -w '\n%{http_code}' -X POST \
        -H "Authorization: Bearer $TOKEN" \
        -H 'Content-Type: application/json' \
        -d "$payment_body" \
        "$BASE_URL/api/payments" 2>/dev/null)
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n-1)
    
    if [ "$http_code" = "201" ]; then
        echo -e "${YELLOW}Testing: Process Payment${NC}"
        echo -e "  ${GREEN}✓ PASSED${NC}"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        
        # Extract payment ID
        PAYMENT_ID=$(echo "$response_body" | grep -o '"paymentId":"[^"]*' | sed 's/"paymentId":"//')
        echo -e "  ${GRAY}Payment created: $PAYMENT_ID${NC}"
        
        # Test 7: Get Payment
        test_endpoint "Get Payment by ID" "GET" "$BASE_URL/api/payments/$PAYMENT_ID" "-H 'Authorization: Bearer $TOKEN'"
    else
        echo -e "${YELLOW}Testing: Process Payment${NC}"
        echo -e "  ${RED}✗ FAILED: Expected 201, got $http_code${NC}"
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
else
    echo -e "${YELLOW}Testing: Login with admin${NC}"
    echo -e "  ${RED}✗ FAILED: Expected 200, got $http_code${NC}"
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# Test 8: Unauthorized Access
echo -e "\n${CYAN}--- Security ---${NC}\n"

test_endpoint "Unauthorized Access (should fail)" "GET" "$BASE_URL/api/orders" "" "" "401"

# Summary
echo -e "\n${CYAN}========================================${NC}"
echo -e "${CYAN}  Test Summary${NC}"
echo -e "${CYAN}========================================${NC}"
echo -e "  ${GREEN}Passed: $TESTS_PASSED${NC}"
echo -e "  ${RED}Failed: $TESTS_FAILED${NC}"
echo -e "  Total:  $((TESTS_PASSED + TESTS_FAILED))"
echo -e "${CYAN}========================================${NC}"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}All tests passed! ✓${NC}"
    exit 0
else
    echo -e "\n${RED}Some tests failed! ✗${NC}"
    exit 1
fi
