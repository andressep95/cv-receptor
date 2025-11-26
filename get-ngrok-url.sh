#!/bin/bash

# Script to retrieve the ngrok public URL from the API

echo "Fetching ngrok tunnel URL..."
echo ""

# Wait for ngrok to be ready
sleep 3

# Get the public URL from ngrok API
NGROK_URL=$(curl -s http://localhost:4040/api/tunnels | grep -o '"public_url":"https://[^"]*' | grep -o 'https://[^"]*' | head -1)

if [ -z "$NGROK_URL" ]; then
    echo "ERROR: Could not retrieve ngrok URL"
    echo ""
    echo "Make sure:"
    echo "  1. docker-compose is running (run: docker-compose up -d)"
    echo "  2. ngrok container is up (check: docker ps)"
    echo "  3. Wait a few seconds and try again"
    echo ""
    echo "You can also check manually at: http://localhost:4040"
    exit 1
fi

echo "SUCCESS: ngrok tunnel is ready!"
echo ""
echo "Public URL: $NGROK_URL"
echo "Web UI: http://localhost:4040"
echo ""
echo "=================================================================="
echo ""
echo "Available endpoints:"
echo ""
echo "  POST ${NGROK_URL}/cv-processed"
echo "    - Receives JSON data from CV processing"
echo ""
echo "=================================================================="
echo ""
echo "Test the endpoint with curl:"
echo ""
echo "  curl -X POST ${NGROK_URL}/cv-processed \\"
echo "    -H \"Content-Type: application/json\" \\"
echo "    -d '{\"name\": \"test\", \"email\": \"test@example.com\"}'"
echo ""
echo "=================================================================="
