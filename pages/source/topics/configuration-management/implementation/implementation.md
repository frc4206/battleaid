# Implementation

<hr>

## Usage

Battleaid offers `LoadableConfig` to make defining and loading configuration files simple.

<hr>

### Defining Configurations

To make a configuration, you must define a class that meets these requirements: 

1. The class shall extend `LoadableConfig`.
2. Any fields in the class shall be declared `public`. 
3. The class shall contain a `public` constructor with a single parameter `String filename`.
4. The constructor shall call `super.load(this, filename)`.

For example:

```{code} java
import org.team4206.battleaid.common.LoadableConfig;

public class ExampleConfig extends LoadableConfig {
    public double d;
    public byte b;
    public String str;
    public int i;
    public float f;
    public boolean bool;
    public char c;
    public short s;

    public ExampleConfig(String filename) {
        super.load(this, filename);
    }
}
```
