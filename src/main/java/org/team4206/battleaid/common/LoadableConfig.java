package org.team4206.battleaid.common;

import static org.team4206.battleaid.Static.CONFIG_DIR;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.tomlj.Toml;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

/**
 * Utility to initialize Plain-Old-Java-Objects from *.toml(s). 
 * LoadableConfig can initialize all Java primitives (int, boolean, float, String, etc.) and also objects.<br><br>
 * 
 * In order for the initialization to succeed, the class must meet a few requirements:<br>
 * 1. Objects <i>must</i> extends LoadableConfig.<br>
 * 2. Fields of the object must be public.<br>
 * 3. The name of the field must match the key in the *.toml.<br>
 * 4. The input file must be under <i>src/main/deploy/configuration</i>.<br><br>
 * 
 * Nested classes <i>should</i> be static.<br>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * public class GenericSubsystem extends LoadableConfig {
 *      public double d;
 *      public String s;
 *      public int i;
 * 
 *      public GenericSubsystem(String filename){
 *          super.load(this, filename);
 *          
 *          // Optional print for debug
 *          LoadableConfig.print(this);
 *      }
 * }
 * 
 * .
 * .
 * .
 * 
 * GenericSubsystem gc = new GenericSubsystem("example.toml");
 * }</pre>
 */
public abstract class LoadableConfig {

    private Path path;

    /**
     * This custom exception is defined to trap when
     * the class of the field does not extend the
     * LoadableConfig class. This makes recursion
     * of LoadableConfigs more safe.
     */
    public class ExtensionException extends Exception {
        private static final String class_name = LoadableConfig.class.getName();

        public ExtensionException(Class<?> c) {
            super(c.getCanonicalName() + " is not an instance of " + class_name);
        }

        protected static boolean isLoadableConfig(Class<?> c) {
            return LoadableConfig.class.isAssignableFrom(c);
        }
    }

    /**
     * This custom exception is defined to trap when
     * there is missing content in the configuration file.
     * This is an elementary mistake that mostly points to
     * lack of discipline.
     */
    public class ContainsException extends Exception {
        public ContainsException(Field f) {
            super("'" + f.getName() + "' from "
                    + f.getDeclaringClass().getName() + " is not found in '"
                    + path.toString() + "'");
        }
    }

    /**
     * This function is how extensions of this class invoke the
     * initialization of all of the class fields. It is also
     * a 'helper' function to the actual recursive load function.
     */
    protected void load(LoadableConfig c, String filename) {
        try {
            this.path = Paths.get(CONFIG_DIR + filename);

            /**
             * Issues in the config file point to accidental edits
             * or to mistakes. Assume catastrophe.
             */
            TomlParseResult result = Toml.parse(path);
            List<TomlParseError> tpr = result.errors();
            if (tpr.size() > 0) {
                tpr.forEach(error -> System.err.println(error.toString()));
                throw new RuntimeException("Encountered issues in " + path.toString());
            }

            this.load(c, result);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private <T extends TomlTable> void load(LoadableConfig c, T tt) throws Exception {
        Field[] fields = c.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String type_name = f.getType().getName();
            String id = f.getName();

            /**
             * Fields of the class not in the configuration file
             * point to a careless user. There is no hand-holding
             * for lack of discipline. Assume the user is stupid.
             */
            if (!tt.contains(id)) {
                throw new ContainsException(f);
            }

            /**
             * This massive switch case is responsible for assigning
             * to the fields of a LoadableConfig the values
             * found inside the config file, that is, within
             * the *.toml. The default case is unique, as it
             * requires some unique casting and instance creation
             * for a class where the type is only determinable by
             * the name. The subclass MUST extend this one.
             */
            switch (type_name) {
                case "double" -> f.setDouble(c, tt.getDouble(id));
                case "java.lang.String" -> f.set(c, tt.getString(id));
                case "int" -> f.setInt(c, tt.getLong(id).intValue());
                case "boolean" -> f.setBoolean(c, tt.getBoolean(id));
                case "char" -> f.setChar(c, tt.getString(id).charAt(0));
                case "long" -> f.setLong(c, tt.getLong(id));
                case "float" -> f.setFloat(c, (float) tt.getDouble(id).doubleValue());
                case "short" -> f.setShort(c, (short) tt.getLong(id).intValue());
                case "byte" -> f.setByte(c, (byte) tt.getLong(id).byteValue());
                default -> {
                    Class<?> lc = Class.forName(type_name);
                    if (!ExtensionException.isLoadableConfig(lc)) {
                        throw new ExtensionException(lc);
                    } else {
                        /**
                         * This portion of the code invokes a new instance
                         * of the config class, calls this function recursively,
                         * and assigns the field to the newly mapped instance.
                         */
                        @SuppressWarnings("unchecked")
                        Constructor<? extends LoadableConfig> cnstrctr = (Constructor<? extends LoadableConfig>) lc
                                .getConstructor();
                        LoadableConfig nlc = cnstrctr.newInstance();
                        this.load(nlc, tt.getTable(id));
                        f.set(c, nlc);
                    }
                }
            }
        }

    }

    /**
     * Lets a user debug their config to verify initialized contents.<br>
     * Code taken from <a href="https://stackoverflow.com/questions/1526826/printing-all-variables-value-from-a-class">here</a>.
     */
    public static void print(LoadableConfig c) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(c.getClass().getCanonicalName());
        result.append(" {");
        result.append(newLine);

        // determine fields declared in this class only (no fields of superclass)
        Field[] fields = c.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                // requires access to private field:
                result.append(field.get(c));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");
        result.append(newLine);

        System.out.println(result.toString());
    }

}
