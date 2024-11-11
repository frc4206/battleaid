# battleaid

In October of 1571, Christendom faced destruction at the hands of the invading Ottoman Turks and their undefeated navy.  The only defense for Christendom was an improvised fleet from various Catholic countries, 'The Holy League', which paled in size to the Ottoman's.  In desperation, Pope Pius V called on all of Europe to pray the Rosary and ask for the intercession of the Blessed Virgin Mary.  
Against overwhelming odds, the Holy League emerged victorious, shattering the perceived invincibility of the Ottomans.  Pope Pius V, attributing the victory to the Virgin Mary's intercession, instituted the feast day "Our Lady of Victory" in thanksgiving.

**battleaid** is dedicated to Our Lady of Victory.

```
O Victorious Lady! Thou who has ever such powerful influence with thy Divine Son, 
in conquering the hardest of hearts, intercede for those for whom we pray, 
that their hearts being softened by the rays of Divine Grace, they may return to the
unity of the true Faith, through Christ, our Lord. 
Amen.

- Father Baker, circa 1874
```

## Introduction

**battleaid** is a library containing various features designed to aid in the development of FRC robot projects for Team 4206, the Robovikes.

### Quick Start

1. Generate a GitHub access token (classic) with `read:packages` permission and copy it your clipboard.

2. Open a terminal in your project and run the following: 
    - `export USERNAME=<your_github_username_here>`
    - `export TOKEN=<the_token_from_github>`

3. In your FRC robot project in WPILib, add this to the top of your `build.gradle` file:
```
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/frc4206/battleaid")
        credentials {
            username = project.findProperty("USERNAME")
            password = project.findProperty("TOKEN")
        }
   }
}
```
4. Add the following to your list of dependencies:
```
dependencies {
    ...
    implementation 'org.team4206:battleaid:<version_number>'
}
```

5. In your terminal, run `./gradlew build`.