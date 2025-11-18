# Droid Gangwar - Android Game

A beautiful, modern Android adaptation of the classic Gangwar text-based game. Build your criminal empire with stunning graphics and smooth gameplay instead of the old MUD-style interface.

## 🎮 Game Overview

You start as a small-time entrepreneur with $1000, 5 kilos of crack, and a pistol. Your goal is to build a massive gang empire, defeat the rival Squidies gang, and become the ultimate pimp king of the city.

### Key Features

- **Modern UI**: Beautiful card-based interface instead of text commands
- **Drug Empire**: Buy and sell various drugs with dynamic pricing
- **Gang Warfare**: Recruit members and engage in turn-based combat
- **Economic Simulation**: Banking, loans, and strategic money management
- **Real-time Combat**: Choose weapons, use drugs, and make tactical decisions
- **Offline Gameplay**: No internet required
- **Cross-Device**: Works on all modern Android devices

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (required for Gradle 8.0 and Android Gradle Plugin 8.1.4)
- **Android Studio** (latest version recommended, Arctic Fox or newer)
- **Android SDK** (API 24+) with build tools
- **Android device or emulator** for testing

### Building and Running

#### Using Makefile (Recommended)

```bash
# Build debug APK
make build

# Build and install on connected device
make install

# Build, install and run
make run

# Show device logs
make logcat

# Clean build artifacts
make clean
```

#### Using Gradle Directly

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on device
./gradlew installDebug

# Run tests
./gradlew test
```

### First Time Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd droid-gangwar
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the `droid-gangwar` folder

3. **Configure Device/Emulator**
   - Connect an Android device via USB, or
   - Create an emulator in Android Studio AVD Manager

4. **Build and Run**
   ```bash
   make run
   ```

## 🎯 How to Play

### Getting Started

1. **Launch the app** - The main menu appears
2. **Start New Game** - Enter your player name and gang name
3. **Explore the City** - Use the navigation drawer or tap location cards
4. **Build Your Empire** - Buy drugs, weapons, and recruit gang members
5. **Fight Rivals** - Engage in combat when you encounter enemies
6. **Final Battle** - When your gang reaches 10+ members, challenge the Squiddies!

### Core Mechanics

#### Drug Dealing
- Visit the **Crackhouse** to buy and sell drugs
- Prices fluctuate daily - buy low, sell high
- Different drugs have different profit margins

#### Combat System
- **Weapons**: Pistol, Uzi, Grenade, Missile Launcher, Barbed Wire Bat, Knife
- **Tactical Choices**: Attack, Defend, Use Drugs, or Flee
- **Gang Support**: Your recruited members join the fight
- **Health Management**: Use drugs to heal or boost performance

#### Economic Management
- **Bank**: Deposit money for safe keeping
- **Loans**: High-risk borrowing to fuel expansion
- **Daily Changes**: Drug prices fluctuate each day

#### Gang Building
- **Recruit Members**: Spend money at Pick n' Save
- **Gang Power**: More members increase combat effectiveness
- **Territory Control**: Larger gangs attract more followers

### Locations

- **🏙️ City**: Central hub with access to all locations
- **🏠 Crackhouse**: Buy/sell drugs and meet contacts
- **🔫 Gun Shack**: Purchase weapons and ammo
- **🏦 Bank**: Manage your finances
- **🍺 Bar**: Gather information and meet NPCs
- **ℹ️ Info Booth**: Buy special items and fake IDs
- **🌃 Alleyway**: Explore for hidden treasures
- **🏪 Pick n' Save**: Gang management and supplies

## 🛠️ Technical Details

### Architecture

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room persistence library
- **UI**: Material Design 3 with custom gradients
- **Navigation**: Navigation Component with drawer
- **Async**: Kotlin Coroutines

### File Structure

```
droid-gangwar/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/droidgangwar/
│   │   │   ├── data/          # Database and repository
│   │   │   ├── model/         # Game logic and data models
│   │   │   └── ui/            # Activities, fragments, ViewModels
│   │   └── res/               # Resources (layouts, colors, strings)
│   └── build.gradle           # App-level build configuration
├── build.gradle               # Project-level build configuration
├── Makefile                   # Build automation
├── .gitignore                 # Android-specific ignore rules
└── README.md                  # This file
```

### Key Components

- **GameState**: Core game data model
- **CombatSystem**: Turn-based combat logic
- **GameRepository**: Data persistence with Room
- **GameViewModel**: Business logic and state management
- **CityFragment**: Main game screen with location cards
- **MainActivity**: Navigation and app lifecycle

## 🎨 Design Philosophy

This Android version transforms the original text-based game into a modern mobile experience:

- **Visual Cards**: Each location is represented by a beautiful card with gradients and icons
- **Smooth Animations**: Card clicks and transitions provide satisfying feedback
- **Status Dashboard**: Real-time progress bars for health, money, and day progress
- **Material Design**: Modern Android UI patterns and components
- **Intuitive Navigation**: Drawer menu for easy location switching

## 🔧 Development

### Adding New Locations

1. Create fragment class in `ui/` package
2. Add layout XML in `res/layout/`
3. Update navigation graph in `res/navigation/`
4. Add menu item in `res/menu/`
5. Update ViewModel with location logic

### Modifying Game Balance

- **Drug Prices**: Edit `GameState.kt` drug price initialization
- **Weapon Stats**: Modify `CombatSystem.kt` weapon damage calculations
- **Difficulty**: Adjust enemy health and damage in combat logic

### Testing

```bash
# Run unit tests
make test

# Run lint checks
make lint

# Check connected devices
make devices
```

## 📱 Device Compatibility

- **Minimum API**: 24 (Android 7.0)
- **Target API**: 34 (Android 14)
- **Screen Sizes**: All modern Android devices supported
- **Orientations**: Portrait mode optimized

## 🚨 Content Warning

This game contains mature themes including:
- Drug dealing and substance abuse
- Violence and gang warfare
- Criminal activities
- Strong language and gritty urban themes

**This game is intended for entertainment purposes only.**

## 📄 License

This project is based on the original Gangwar game. See LICENSE file for details.

## 🐛 Troubleshooting

### Build Issues

#### Java Version Error
If you see "Unsupported class file major version 65", you need Java 17+:
```bash
# Check Java version
java -version

# Install Java 17+ (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-17-jdk

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

#### Plugin Resolution Error
If Android Gradle Plugin cannot be found:
```bash
# Clear Gradle cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Rebuild
make build
```

#### General Build Issues
```bash
# Clean and rebuild
make distclean
make build
```

### Device Connection Issues
```bash
# Check device connection
make devices

# Restart ADB
adb kill-server
adb start-server
```

### App Crashes
```bash
# View logs
make logcat
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with proper testing
4. Submit a pull request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add comments for complex logic
- Test all new features

---

**Remember**: In the streets, only the strong survive. Build your empire wisely, and may the best pimp win! 💰🔫🏙️
