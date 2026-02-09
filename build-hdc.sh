#!/bin/bash

# HDC Idea Plugin Build Script
# This script builds the HDC version of the plugin for DevEco Studio

set -e

echo "============================================="
echo "  HDC Idea Plugin Builder"
echo "============================================="
echo ""

# Check if gradle wrapper exists
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found. Please run this script from the project root."
    exit 1
fi

# Parse arguments
CLEAN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --clean)
            CLEAN=true
            shift
            ;;
        --help)
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  --adb     Build original ADB version (default: HDC)"
            echo "  --clean   Clean before build"
            echo "  --help    Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Clean if requested
if [ "$CLEAN" = true ]; then
    echo "Cleaning build directory..."
    ./gradlew clean
    echo ""
fi

# Build the plugin
echo "Building HDC version (for DevEco Studio)..."
echo ""
./gradlew build

echo ""
echo "============================================="
echo "  Build Complete!"
echo "============================================="
echo ""
echo "HDC version built successfully!"
echo "Plugin JAR location: build/libs/"
echo ""
echo "To install:"
echo "1. Open DevEco Studio"
echo "2. Go to Settings/Preferences → Plugins"
echo "3. Click ⚙️ → Install Plugin from Disk..."
echo "4. Select the JAR file from build/libs/"
