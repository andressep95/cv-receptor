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
echo "AVAILABLE ENDPOINTS:"
echo "=================================================================="
echo ""
echo "1. Process CV/Resume (upload file)"
echo "   POST ${NGROK_URL}/api/v1/resume/process"
echo "   - Accepts: multipart/form-data"
echo "   - Parameters: file, language, instructions"
echo ""
echo "2. Receive AWS processed data"
echo "   POST ${NGROK_URL}/api/v1/resume/aws-response"
echo "   - Accepts: application/json"
echo "   - Receives: JSON with processed CV data"
echo ""
echo "=================================================================="
echo "EXAMPLE CURL COMMANDS:"
echo "=================================================================="
echo ""
echo "# Upload a CV for processing:"
echo "curl -X POST ${NGROK_URL}/api/v1/resume/process \\"
echo "  -F \"file=@cv.pdf\" \\"
echo "  -F \"language=es\" \\"
echo "  -F \"instructions=extract text with OCR\""
echo ""
echo "# Receive AWS processed data (webhook):"
echo "curl -X POST ${NGROK_URL}/api/v1/resume/aws-response \\"
echo "  -H \"Content-Type: application/json\" \\"
echo "  -d '{\"status\":\"completed\",\"extracted_text\":\"CV content...\"}'"
echo ""
echo "=================================================================="
echo ""