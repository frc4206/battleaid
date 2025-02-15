package org.team4206.battleaid.common;

import static org.team4206.battleaid.Static.CONFIG_DIR;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseError;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

/**
 * Utility class that initializes Plain-Old-Java-Objects from *.toml(s). <br>
 * LoadableConfig can initialize all Java primitives (int, boolean, float, String, etc.) and also objects. <br>
 * 
 * <a href="https://toml.io/">TOML</a>
 */
public abstract class LoadableConfig {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Required {}

    private Path path;

    private static String lc_indnt = "    ";

    /**
     * Raised when a field is an object that does
     * not extend LoadableConfig.  Populated objects
     * <i>must</i> extend LoadableConfig.
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
     * Raised when configuration file is missing
     * fields defined by the subclass.
     */
    public class ContainsException extends Exception {
        public ContainsException(Field f) {
            super("'" + f.getName() + "' from "
                    + f.getDeclaringClass().getName() + " is not found in '"
                    + path.toString() + "'");
        }
    }

    /**
     * This function is the 'hook' into LoadableConfig.
     * When called, all fields of the class will be populated
     * by the content contained with the file from 'filename'.
     */
    protected void load(LoadableConfig c, String filename) {
        try {
            this.path = Paths.get(CONFIG_DIR + File.separator + filename);

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

    <T extends TomlTable> void load(LoadableConfig c, T tt) throws Exception {
        Field[] fields = c.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            String type_name = f.getType().getName();
            String id = f.getName();
			boolean required = f.isAnnotationPresent(Required.class);

			/**
			 * Fields that have the @Required annotation
			 * MUST be present in the config file.  If they
			 * are not, an exception is thrown.
			 */
			if(required && !tt.contains(id))
				throw new ContainsException(f);

			/**
			 * Skip optional and non-present fields.
			 */
			if(!required && !tt.contains(id))
				continue;

            /**
             * This helper function is called if the field for arrays
             */
            if (f.getType().isArray()) {
                loadArray(c, f, tt);
                continue;
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

    protected void loadArray(LoadableConfig c, Field f, TomlTable tt) throws Exception {
        String id = f.getName();
        Class<?> array_kind = f.getType().getComponentType();
        String type = array_kind.getName();

        if (!tt.isArray(id))
            throw new IllegalArgumentException("Field '" + id + "' is supposed to be an array");

        TomlArray ta = tt.getArray(id);
        Object na = java.lang.reflect.Array.newInstance(array_kind, ta.size());

        for (int i = 0; i < ta.size(); i++) {
            /**
             * This massive switch case is responsible for assigning 
             * array values the configuration file.  The default case is 
             * unique, requiring some unique casting and instance creation.  
             * The class MUST extend LoadableConfig.
             */
            switch (type) {
                case "double" -> java.lang.reflect.Array.setDouble(na, i, ta.getDouble(i));
                case "java.lang.String" -> java.lang.reflect.Array.set(na, i, ta.getString(i));
                case "int" -> java.lang.reflect.Array.setInt(na, i, (int) ta.getLong(i));
                case "boolean" -> java.lang.reflect.Array.setBoolean(na, i, ta.getBoolean(i));
                case "char" -> java.lang.reflect.Array.setChar(na, i, ta.getString(i).charAt(0));
                case "long" -> java.lang.reflect.Array.setLong(na, i, (long) ta.getLong(i));
                case "float" -> java.lang.reflect.Array.setFloat(na, i, (float) ta.getDouble(i));
                case "short" -> java.lang.reflect.Array.setShort(na, i, (short) ta.getLong(i));
                case "byte" -> java.lang.reflect.Array.setByte(na, i, (byte) ta.getLong(i));
                default -> {
                    Class<?> lc = Class.forName(type);
                    if (!ExtensionException.isLoadableConfig(lc)) {
                        throw new ExtensionException(lc);
                    } else {
                        /**
                         * This portion of the code invokes a new instance
                         * of the config class, calls this function recursively,
                         * and assigns the field in the array.
                         */
                        @SuppressWarnings("unchecked")
                        Constructor<? extends LoadableConfig> cnstrctr = (Constructor<? extends LoadableConfig>) lc
                                .getConstructor();
                        LoadableConfig nlc = cnstrctr.newInstance();
                        this.load(nlc, ta.getTable(i));
                        java.lang.reflect.Array.set(na, i, nlc);
                    }
                }
            }
        }

        // finally, set the array
        f.set(c, na);
    }

    public static void print(LoadableConfig c) {
        StringBuilder sb = new StringBuilder();
        buildString(c, "", sb);
        System.out.println(sb);
    }

    private static void buildString(LoadableConfig c, String indnt, StringBuilder sb) {
        // if we printing something in a list, add the ident
        if(sb.length() > 0 && sb.charAt(sb.length() - 2) != ':')
            sb.append(indnt);

        sb.append(c.getClass().getCanonicalName()).append(" {\n");

        Field[] fields = c.getClass().getDeclaredFields();
        String new_indnt = indnt + lc_indnt; // Increase indent for nested fields

        for (Field field : fields) {
            // add the field name
            sb.append(new_indnt).append(field.getName()).append(": ");

            field.setAccessible(true);
            Object value;

            // field.get(c) throws an error
            try {
                value = field.get(c);
            } catch (Exception ex) {
                // this should never happen
                sb.append("?\n");
                continue;
            }

            /**
             * This statement switches the behavior of the printing
             * depending on the type.  Most primitive types will 
             * print with normal, expected behavior in the default
             * case, but we have to create special prints for
             * Strings, chars, null, arrays, and Objects
             */
            if (value == null)
                sb.append("null\n");
            else if (value.getClass().isArray())
                buildArray(value, new_indnt, sb);
            else if (value instanceof String s) 
                sb.append("\"").append(s).append("\"\n");
            else if (value instanceof Character ch) 
                sb.append("'").append(ch).append("'\n");
            else if (value instanceof LoadableConfig lc)
                buildString(lc, new_indnt, sb);
            else 
                sb.append(value).append("\n");
        }
        sb.append(indnt).append("}\n");
    }

    private static void buildArray(Object array, String indent, StringBuilder sb) {
        int length = java.lang.reflect.Array.getLength(array);

        Class<?> componentType = array.getClass().getComponentType();

        /** 
         * Printing an array objects is treated differently
         * so that it's more readable.
         */
        if (LoadableConfig.class.isAssignableFrom(componentType)) {
            String newIndent = indent + lc_indnt;
            sb.append("[\n");
            for (int i = 0; i < length; i++) {
                Object element = java.lang.reflect.Array.get(array, i);
                if (element instanceof LoadableConfig lc) {
                    buildString(lc, newIndent, sb);
                } else {
                    sb.append(newIndent).append(element);
                }
                // if not the last item in array
                if (i < length - 1) {
                    // remove recursive new line and add a comma
                    sb.setLength(sb.length() - 1);
                    sb.append(",\n");
                }
            }
            sb.append(indent).append("]\n");
            return;
        }
        
        /**
         * Primitive type arrays are printed as expected,
         * except for Strings and chars which need quotes 
         * and ticks respectively for readability.
         */
        sb.append("[ ");
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(", ");
            Object element = java.lang.reflect.Array.get(array, i);

            if (element instanceof String s) {
                sb.append("\"").append(s).append("\"");
            } else if (element instanceof Character ch) {
                sb.append("'").append(ch).append("'");
            } else {
                sb.append(element);
            }
        }
        sb.append(" ]\n");
    }

    public static void setIndent(String indent)
    {
        LoadableConfig.lc_indnt = indent;
    }
}
