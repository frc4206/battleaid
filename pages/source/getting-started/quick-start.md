
# Quick Start

<hr>

Battleaid is not published on Maven as a public package (yet), and is only available as a _GitHub package_.  This requires more setup by the user, but the result functions exactly as any other remote resource.

<hr>

## Steps

1. Generate a GitHub access token (classic) with `read:packages` permission and copy it your clipboard.  [GitHub has a tutorial](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens#creating-a-personal-access-token-classic).

```{tip}
Keep your token somewhere safe and easily accessible!  If you decide to use a different computer or move to a different project you will need to repeat these steps!
```

2. Create the file `gradle.properties` in the root directory of your FRC robot project and add your username and token (_without_ < or >):
```{code-block} properties
:linenos:

github.username = <your_github_username_here>
github.token = <your_token_here>
```

```{tip}
You can also put `gradle.properties` under `~/.gradle` if you want your authentication to be system wide.
```

3. If it does not already exist, create the file `.gitignore` in your project root directory.  Add `gradle.properties` to the list of entries.

```{danger}
If `gradle.properties` is pushed to the remote repository containing your token (which is basically a password), your token will be publicly exposed.  This puts your account at risk.
```

4. In your FRC robot project in WPILib, add this to the top of your `build.gradle` file:
```java
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/frc4206/battleaid")
        credentials {
            username = project.findProperty("github.username") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```
5. Add Battleaid to your list of dependencies:
```{code-block}
dependencies {
    ...
    implementation 'org.team4206:battleaid:<version_number>' # e.g. 0.1.0
}
```

Don't know what version number to use?  The [releases are on GitHub](https://github.com/frc4206/battleaid/releases).

6. Open a terminal and run `./gradlew build`.  This will download the package.

```{tip}
If the build is successful but you still see squiggly red errors in your code, you may need to refresh the Java Lanaguage Server in order for Battleaid to be visible:
- `Cntrl` + `Shft` + `P`
- `>Java: Clean Java Language Server Workspace`
```

<hr>

If all of the above steps are performed correctly, Battleaid will be listed as an external dependency in the `Java Projects` primary sidebar view container.