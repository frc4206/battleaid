# Implementation

<hr>

## Usage

Battleaid offers `LoadableConfig` to make defining and loading configuration files simple. `LoadableConfig` will automatically initialize class fields when an instance of the configuration is created.  This encourages reuse and supports scalability.

<hr>

### I. Defining Configurations

To define a configuration, you must create a class that meets these requirements: 

1. The class shall extend `LoadableConfig`.
2. All fields in the class shall be declared `public` and uninitialized.
2. Fields shall be typed as one of the following:
<table width="100%" border="0" cellpadding="0" summary="">
    <tr>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">int</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">long</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">short</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">byte</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">char</span></code></td>
    </tr>
    <tr>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">float</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">double</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">boolean</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">String</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">LoadableConfig</span></code></td>
    </tr>
    <tr>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">int[]</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">long[]</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">short[]</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">byte[]</span></code></td>
        <td width="20%" align="center"><code class="docutils literal notranslate"><span class="pre">char[]</span></code></td>
    </tr>
    <tr>
        <td width="25%" align="center"><code class="docutils literal notranslate"><span class="pre">float[]</span></code></td>
        <td width="25%" align="center"><code class="docutils literal notranslate"><span class="pre">double[]</span></code></td>
        <td width="25%" align="center"><code class="docutils literal notranslate"><span class="pre">boolean[]</span></code></td>
        <td width="25%" align="center"><code class="docutils literal notranslate"><span class="pre">String[]</span></code></td>
        <td width="25%" align="center"><code class="docutils literal notranslate"><span class="pre">LoadableConfig[]</span></code></td>
    </tr>
</table>

3. The class shall contain a `public` constructor with a single parameter `String filename` that calls `super.load(this, filename)`.
4. Nested config definitions shall have an empty, `public` constructor and no other constructors.  
6. Nested config definitions shall be outside the scope of the encompassing config definition.
7. Non-optional fields shall be annotated `@Required`.

For example:

```{code-block} java
:linenos:

// CarConfig.java
public class CarConfig extends LoadableConfig {
    public int mpg;
    public long odometer_measurement;
    @Required public Engine some_engine;
    @Required public boolean is_manual_transmission;
    @Required public Wheel the_wheels[];

    public CarConfig(String filename) {
        super.load(this, filename);
    }
}
```

In this case, the `Engine` and `Wheel` are nested `LoadableConfig`s:

```{code-block} java
:linenos:

// Engine.java
public class Engine extends LoadableConfig {
    @Required public int number_of_cylinders;
    public float liters;
    public String style;
    public short horsepower;

    public Engine(){};
}
```

```{code-block} java
:linenos:

// Wheel.java
public class Wheel extends LoadableConfig {
    @Required public float radius;
    @Required public String position;
    public int number_of_lugs;

    public Wheel(){};
}
```

```{note}
Optionally, one can call `LoadableConfig.print(this)` to view the class contents. 
```

<br>

### II. Creating Configuration Files

`LoadableConfig` uses [`TOML`](https://toml.io/en/) for configuration files.  To make a configuration file, you must create a file that meets these requirements:

1. The file shall be located under `src/main/deploy/configuration` and have the extension `.toml`.
2. The identifier shall be identical to the identifier of the class field.

Using the `CarConfig` from above, a configuration file might be the following: 

```{code-block} toml
:linenos:

# example-car.toml
mpg = 32
odometer_measurement = 151_000
is_manual_transmission = true

the_wheels = [
    { radius = 18.0, position = "front right" },
    { radius = 18.0, position = "front left" },
    { radius = 22.0, position = "back left" },
    { radius = 22.0, position = "back right" }
]

[some_engine]
number_of_cylinders = 8
style = "Inline"
liters = 5.7
horsepower = 381

```

<br>

### III. Utilizing the Config

Time to use our config!

```{code-block} java
:linenos:

// RobotContainer.java
public class RobotContainer { 
    public RobotContainer(){
        CarConfig cfg = new CarConfig("example-config.toml");

        // this subsystem needs that config!
        SomeSubsystem ss = new SomeSubsystem(cfg);
    }
}
```

<hr>

## Making Changes

### Steps

1. Make a change in your `*.toml`.
2. Run `./gradlew deployStandalonefrcStaticFileDeployroborio` in your terminal.
3. Restart robot code:

<div style="text-align:center">

![Restart Robot Code](./restart-robot-code.png)
</div>

<hr>

## Notes

### I. 

An effective strategy is to combine a subsystem with a configuration.  You can nest the config class in a subsystem class by adding the `static` modifier:

```{code-block} java
:linenos:

// ExampleSubsystem.java
public class ExampleSubsystem {
    private ExampleSubsystem.Config cnfg;

    public static class Config {
        public int some_data; 

        public Config(String filename){
            super.load(this, filename);
        }
    }

    public ExampleSubsystem(ExampleSubsystem.Config cnfg)
    {
        this.cnfg = cnfg;
    }
}
```

```{code-block} java
:linenos:

// RobotContainer.java
public class RobotContainer { 
    public RobotContainer(){
        // now the semantic meaning of the config is embedded
        // into the namespacing of our program 
        ExampleSubsystem.Config cnfg = new ExampleSubsystem.Config("config.toml");
        ExampleSubsystem ss = new ExampleSubsystem(cnfg);
    }
}
```
