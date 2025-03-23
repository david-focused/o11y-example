#!/bin/bash

# Color formatting for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m' # No Color

###########################################
# UTILITY FUNCTIONS
###########################################

press_to_continue() {
  echo
  read -p "Press any key to continue..." -n1 -s
  echo
  clear
}

check_services() {
  echo -e "${GREEN}Checking if services are accessible...${NC}"
  
  # Function to check a service's health
  check_service_health() {
    local service_name=$1
    local port=$2
    
    # Try health endpoint first
    local health_status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health 2>/dev/null || echo "Connection failed")
    if [ "$health_status" == "200" ] || [ "$health_status" == "404" ]; then
      echo -e "${GREEN}✓ $service_name${NC}"
      return 0
    else
      # Try a simple ping to the root if actuator health fails
      local ping_status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/ 2>/dev/null || echo "Connection failed")
      if [ "$ping_status" == "200" ] || [ "$ping_status" == "404" ] || [ "$ping_status" == "403" ]; then
        echo -e "${YELLOW}⚠ $service_name is accessible but health endpoint may not be enabled${NC}"
        return 0
      else
        echo -e "${RED}❌ $service_name is not accessible. Status: $health_status${NC}"
        echo "Make sure you've run 'make start-port-forwards' to set up port forwarding"
        return 1
      fi
    fi
  }
  
  check_service_health "order-service" 8080 || return 1
  return 0
}

###########################################
# TEST SCENARIO FUNCTIONS
###########################################

# Generate a random quantity between 1 and 20
generate_random_quantity() {
  echo $((RANDOM % 100 + 1))
}

# Generate a random shipping method
generate_random_shipping_method() {
  local quantity=$1
  local methods=("GROUND" "TWO_DAY" "NEXT_DAY")
  local random_index=$((RANDOM % 3))
  
  # Special case: 1 in 100 chance for quantity > 15 and NEXT_DAY shipping
  if [ "$quantity" -gt 15 ] && [ $((RANDOM % 25)) -eq 0 ]; then
    echo "NEXT_DAY"
    return
  fi
  
  echo "${methods[$random_index]}"
}

# create an order with the specified product ID
create_order() {
  local product_id=$1
  local quantity=${2:-0}
  local shipping_method=${3:-""}
  local silent=${4:-false}
  
  # Generate random quantity if not specified or zero
  if [ "$quantity" -le 0 ]; then
    quantity=$(generate_random_quantity)
    if [ "$silent" != "true" ]; then
    echo -e "${BLUE}Generated random quantity: $quantity${NC}"
  fi
  fi
  
  # Generate random shipping method if not specified
  if [ -z "$shipping_method" ]; then
    shipping_method=$(generate_random_shipping_method "$quantity")
    if [ "$silent" != "true" ]; then
      echo -e "${BLUE}Generated random shipping method: $shipping_method${NC}"
    fi
  fi
  
  # Build the JSON payload
  local json_data="{\"productId\":$product_id,\"quantity\":$quantity,\"shippingMethod\":\"$shipping_method\"}"
  
  local status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "$json_data" \
    http://localhost:8080/orders/create)
  
  if [ "$silent" != "true" ]; then
    local response=$(curl -s -X POST \
      -H "Content-Type: application/json" \
      -d "$json_data" \
      http://localhost:8080/orders/create)
    echo "$response"
  fi
  
  # Always return the status code
  return $status
}

run_happy_path() {
  echo -e "${BOLD}${GREEN}=== Happy Path Test ===${NC}"
  
  local product_id=$(date +%s)
  
  echo -e "${GREEN}Creating normal order (ID: $product_id)...${NC}"
  create_order "$product_id" 20 "GROUND"
  echo
}

run_order_service_error() {
  echo -e "${BOLD}${RED}=== Order Service Error Test (Invalid quantity) ===${NC}"
  
  local product_id=$(date +%s)
  
  echo -e "${RED}Creating order with invalid quantity to trigger error...${NC}"
  echo -e "${RED}Using high quantity (105) and GROUND shipping${NC}"
  create_order "$product_id" 105 "GROUND"
  echo
}

run_shipping_timeout() {
  echo -e "${BOLD}${YELLOW}=== Shipping Timeout Test ===${NC}"
  
  local product_id=$(date +%s)
  
  echo -e "${YELLOW}Creating order to trigger shipping timeout...${NC}"
  echo -e "${YELLOW}Using quantity (80) and NEXT_DAY shipping${NC}"
  create_order "$product_id" 80 "NEXT_DAY"
  echo
}

run_random_orders() {
  echo -e "${BOLD}${BLUE}=== Random Orders Test (100 requests) ===${NC}"
  
  local total_requests=100
  local failed_requests=0
  
  echo -e "${BLUE}Sending $total_requests random orders...${NC}"
  echo
  
  for ((i=1; i<=$total_requests; i++)); do
    # Progress indicator
    if ((i % 5 == 0)); then
      echo -ne "${BLUE}Progress: $i/$total_requests requests sent\r${NC}"
    fi
    
    local product_id=$((1000 + i))  # Start from 1001 to avoid conflicts
    local quantity=$((RANDOM % 100 + 10))  # 10-100

    local shipping_method=$(generate_random_shipping_method "$quantity")
    
    # Call create_order in silent mode
    create_order "$product_id" "$quantity" "$shipping_method" "true"
    
    # Small delay to avoid overwhelming the services
    sleep 0.05
  done
  
  echo -e "\n${GREEN}Completed sending $total_requests requests${NC}"
  echo
}



###########################################
# MENU AND PROGRAM FLOW
###########################################

show_menu() {
  echo
  echo -e "${BOLD}${GREEN}=== Observability Demo Menu ===${NC}"
  echo -e "${BLUE}Choose a test scenario:${NC}"
  echo "1) Happy Path"
  echo "2) Order Service Error Test (Invalid quantity)"
  echo "3) Shipping Timeout Test"
  echo "4) Random Orders Test (100 requests)"
  echo "0) Exit"
  echo
  read -p "Enter your choice [0-5]: " choice
  
  case $choice in
    1) clear; run_happy_path; press_to_continue ;;
    2) clear; run_order_service_error; press_to_continue ;;
    3) clear; run_shipping_timeout; press_to_continue ;;
    4) clear; run_random_orders; press_to_continue ;;
    0) echo "Exiting..."; exit 0 ;;
    *) echo -e "${YELLOW}Invalid option. Please try again.${NC}"; press_to_continue ;;
  esac
}

###########################################
# MAIN PROGRAM
###########################################

clear

if ! check_services; then
  echo -e "${YELLOW}Service check failed. Please ensure all services are running and port-forwarding is set up.${NC}"
  echo "Run 'make start-port-forwards' and try again."
  exit 1
fi

echo

while true; do
  show_menu
done