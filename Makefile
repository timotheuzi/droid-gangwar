# Droid Gangwar Android App Makefile
# Cross-platform build system for the Android Gangwar game

.PHONY: all build clean distclean help apk debug release install run logcat

# Default target
all: build

# Build the Android app in debug mode
build:
	@echo "Building Droid Gangwar Android app..."
	@command -v java >/dev/null 2>&1 || { echo "ERROR: Java is not installed."; echo "Please install Java 17+ (OpenJDK 17 or higher)."; echo "On Ubuntu/Debian: sudo apt install openjdk-17-jdk"; echo "Then set: export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"; exit 1; }
	@java -version 2>&1 | grep -q "version \"1[789]\." || java -version 2>&1 | grep -q "version \"2[0-9]\." || { echo "ERROR: Java 17+ is required. Current version:"; java -version; exit 1; }
	@echo "Java check passed. Building..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew assembleDebug

# Build release APK
release:
	@echo "Building Droid Gangwar release APK..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew assembleRelease

# Build debug APK
debug:
	@echo "Building Droid Gangwar debug APK..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew assembleDebug

# Build and install on connected device
install: build
	@echo "Installing Droid Gangwar on connected device..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew installDebug

# Build and run on connected device
run: install
	@echo "Launching Droid Gangwar on device..."
	@adb shell am start -n com.example.droidgangwar/.ui.MainActivity

# Clean build artifacts
clean:
	@echo "Cleaning Android build artifacts..."
	@./gradlew --stop
	@./gradlew clean
	@rm -rf app/build/
	@rm -rf build/
	@rm -rf .gradle/
	@rm -rf app/.cxx/

# Clean everything including dependencies
distclean: clean
	@echo "Performing deep clean..."
	@rm -rf .idea/
	@rm -rf *.iml
	@rm -rf app/*.iml
	@rm -rf local.properties
	@rm -rf app/src/main/assets/
	@find . -name "*.log" -delete
	@find . -name "*.tmp" -delete

# Show device logs
logcat:
	@echo "Showing Android device logs..."
	@adb logcat | grep -i "droidgangwar\|com.example.droidgangwar"

# Show connected devices
devices:
	@echo "Connected Android devices:"
	@adb devices

# Generate APK paths
apks:
	@echo "Generated APK files:"
	@find app/build/outputs/apk -name "*.apk" 2>/dev/null || echo "No APKs found. Run 'make build' first."

# Setup development environment
setup:
	@echo "Setting up Android development environment..."
	@echo "Make sure you have:"
	@echo "  - Android Studio installed"
	@echo "  - Android SDK configured"
	@echo "  - Device connected or emulator running"
	@echo ""
	@echo "To build and run:"
	@echo "  make build    # Build debug APK"
	@echo "  make install  # Install on device"
	@echo "  make run      # Build, install and run"

# Test the app
test:
	@echo "Running Android tests..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew test

# Lint check
lint:
	@echo "Running Android lint checks..."
	@export JAVA_HOME=/home/bim/jdk/jdk-21.0.2 && export PATH=$$JAVA_HOME/bin:$$PATH && ./gradlew lint

# Show help
help:
	@echo "Droid Gangwar Android App Build System"
	@echo "======================================="
	@echo ""
	@echo "Available targets:"
	@echo "  all       - Build the app in debug mode (default)"
	@echo "  build     - Build debug APK"
	@echo "  release   - Build release APK"
	@echo "  debug     - Build debug APK (same as build)"
	@echo "  install   - Build and install on connected device"
	@echo "  run       - Build, install and launch on device"
	@echo "  clean     - Clean build artifacts"
	@echo "  distclean - Clean everything including dependencies"
	@echo "  logcat    - Show device logs"
	@echo "  devices   - List connected devices"
	@echo "  apks      - Show generated APK file paths"
	@echo "  setup     - Show setup instructions"
	@echo "  test      - Run unit tests"
	@echo "  lint      - Run lint checks"
	@echo "  help      - Show this help"
	@echo ""
	@echo "Prerequisites:"
	@echo "  - Android SDK installed"
	@echo "  - Device connected or emulator running"
	@echo "  - ADB in PATH"
	@echo ""
	@echo "Usage examples:"
	@echo "  make build && make install  # Build and install"
	@echo "  make run                    # Build, install and run"
	@echo "  make logcat                 # Monitor device logs"
	@echo "  make clean && make          # Clean and rebuild"

# Development workflow
dev: clean build install run

# Quick build and test
quick: build install
