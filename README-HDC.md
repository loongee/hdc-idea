# HDC Idea - HarmonyOS Device Controller Plugin

A plugin for DevEco Studio to speed up your day-to-day HarmonyOS/OpenHarmony development.

This plugin is a port of the popular [ADB Idea](https://github.com/pbreault/adb-idea) plugin, adapted for HarmonyOS development using HDC (HarmonyOS Device Controller) commands.

## Features

| Feature | HDC Command |
|---------|-------------|
| **Uninstall App** | `hdc uninstall <bundleName>` |
| **Kill App** | `hdc shell aa force-stop <bundleName>` |
| **Start App** | `hdc shell aa start -b <bundleName> -a <abilityName>` |
| **Restart App** | Kill + Start |
| **Clear App Data** | `hdc shell bm clean -n <bundleName> -c -d` |
| **Clear App Data and Restart** | Clear Data + Start |
| **Start App With Debugger** | `hdc shell aa start -D -b <bundleName> -a <abilityName>` |
| **Restart App With Debugger** | Kill + Start with Debugger |
| **Grant Permissions** | `hdc shell bm grant -n <bundleName> -p <permission>` |
| **Revoke Permissions** | `hdc shell bm revoke -n <bundleName> -p <permission>` |
| **Revoke Permissions and Restart** | Revoke + Start |

## Usage

There are two ways to invoke commands:

1. **Menu**: `Tools` → `HDC Idea` → Select command
2. **Quick Actions**: 
   - macOS: `Cmd + Shift + H`
   - Windows/Linux: `Ctrl + Shift + Alt + H`
3. **Find Actions**: Search for "HDC" in Find Actions (`Cmd/Ctrl + Shift + A`)

## HDC vs ADB Command Mapping

| Concept | Android (ADB) | HarmonyOS (HDC) |
|---------|---------------|-----------------|
| App Identifier | Package Name | Bundle Name |
| Entry Component | Activity | Ability |
| App Package Format | APK | HAP |
| Activity Manager | `am` | `aa` (Ability Assistant) |
| Package Manager | `pm` | `bm` (Bundle Manager) |
| List Devices | `adb devices` | `hdc list targets` |
| Connect Device | `adb connect` | `hdc tconn` |
| File Push | `adb push` | `hdc file send` |
| File Pull | `adb pull` | `hdc file recv` |

## Installation

### From Local Build

1. Clone this repository
2. Build the HDC version:
   ```bash
   ./gradlew -PbuildHdc=true build
   ```
3. Install the plugin in DevEco Studio:
   - Go to `Settings/Preferences` → `Plugins` → `⚙️` → `Install Plugin from Disk...`
   - Select the built plugin JAR from `build/libs/`

### Prerequisites

- DevEco Studio 3.0 or later
- HarmonyOS SDK with HDC tool
- Set one of the following environment variables:
  - `HDC_HOME`: Path to HDC executable directory
  - `DEVECO_SDK_HOME`: Path to DevEco SDK
  - `HarmonyOS_SDK_HOME`: Path to HarmonyOS SDK

## Configuration

The plugin automatically detects HDC from these locations (in order):

1. `HDC_HOME` environment variable
2. `DEVECO_SDK_HOME/toolchains/hdc`
3. `HarmonyOS_SDK_HOME/toolchains/hdc`
4. Default installation paths:
   - macOS: `/Applications/DevEco-Studio.app/Contents/sdk/toolchains/hdc`
   - Windows: `%LOCALAPPDATA%\Huawei\sdk\toolchains\hdc.exe`
   - Linux: `/opt/Huawei/DevEco-Studio/sdk/toolchains/hdc`
5. System PATH

## Project Configuration

The plugin reads the Bundle Name and Main Ability from your project's configuration files:

- **Stage Model**: `entry/src/main/module.json5` and `AppScope/app.json5`
- **FA Model**: `entry/src/main/config.json`

Example `app.json5`:
```json
{
  "app": {
    "bundleName": "com.example.myapp",
    "vendor": "example",
    "versionCode": 1000000,
    "versionName": "1.0.0"
  }
}
```

Example `module.json5`:
```json
{
  "module": {
    "name": "entry",
    "type": "entry",
    "mainElement": "EntryAbility",
    "abilities": [
      {
        "name": "EntryAbility",
        "srcEntry": "./ets/entryability/EntryAbility.ets"
      }
    ]
  }
}
```

## Building from Source

### Build ADB Version (Original)
```bash
./gradlew build
```

### Build HDC Version (HarmonyOS)
```bash
./gradlew -PbuildHdc=true build
```

Or use the provided build script:
```bash
cp build.gradle.hdc.kts build.gradle.kts
./gradlew build
```

## Differences from ADB Idea

| Feature | ADB Idea | HDC Idea |
|---------|----------|----------|
| WiFi Control | ✅ Supported | ❌ Not available |
| Mobile Data Control | ✅ Supported | ❌ Not available |
| Debugger Attachment | ✅ Full support | ⚠️ Basic support |
| Device Selection | Android Device Chooser | Custom HDC Device Chooser |

## Troubleshooting

### HDC not found
- Ensure HarmonyOS SDK is properly installed
- Set the `HDC_HOME` environment variable to point to the HDC directory
- Verify HDC works by running `hdc list targets` in terminal

### Bundle Name not detected
- Make sure your project has a valid `app.json5` or `config.json`
- Check that `bundleName` is correctly specified in the configuration

### Device not found
- Connect your HarmonyOS device via USB or WiFi
- Enable Developer Mode on the device
- Run `hdc list targets` to verify device connectivity

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the same license as the original ADB Idea plugin.

## Credits

- Original [ADB Idea](https://github.com/pbreault/adb-idea) plugin by Philippe Breault
- HarmonyOS/OpenHarmony port by the community

## Related Projects

- [ADB Idea](https://github.com/pbreault/adb-idea) - Original Android plugin
- [DevEco Studio](https://developer.harmonyos.com/cn/develop/deveco-studio) - HarmonyOS IDE
- [HDC Documentation](https://gitee.com/openharmony/developtools_hdc) - OpenHarmony HDC Tool
