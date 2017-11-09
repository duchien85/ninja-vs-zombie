## Ninja vs Zombie
[![Build Status](https://travis-ci.org/twistezo/ninja-vs-zombie.svg?branch=master)](https://travis-ci.org/twistezo/ninja-vs-zombie)

### Description
Android/Desktop 2d platform fully animated game (non-commercial)

### Tools
Java, libGDX, Photoshop

### Features
- keys/touch steering
- random generating enemies
- enemies following player
- health/killed bars
- FPS counter
- debug mode (actor bounds, drag’n’drop player)

### Build/Run
Android:
```
1. Create `local.properties` file with android SKD path:
   sdk.dir=C\:\\Users\\*YOUR_USERNAME*\\AppData\\Local\\Android\\Sdk
2. Generate .apk:
   gradlew android:assembleRelease
3. Generated .apk path:
   /android/build/outputs/apk/
```

Desktop:
```
1. Generate .jar:
   gradlew desktop:dist or ./gradlew.bat desktop:dist
2. Generated .jar path:
   /desktop/build/libs/
3. To run:
   gradlew desktop:run or ./gradlew.bat desktop:run
```

### Screenshots
<table>
    <tr>
        <td>
            <img src="http://i.imgur.com/vTVokMw.png" width="500">
        </td>
        <td>
            <img src="http://i.imgur.com/0P37rBC.png" width="500">
        </td>
    </tr>
    <tr>
        <td>
            <img src="http://i.imgur.com/cjgQ1xC.png" width="500">
        </td>
    </tr>
</table>
