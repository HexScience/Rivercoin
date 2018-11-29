package nucleus.protocols.protobufs.generator;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Generator
{
    private static interface Interface{
        void addField(String type, String name);
        String compile();
    }
    private static interface Command{
        void run(String text, File out);
    }
    private static class JavaCommand implements Command{

        private String getTypeOf(String object, Map<String, String> conversions, Map<String, String> conversionDefinitions, int type)
        {
            if (type == 0)
            {
                if (conversionDefinitions.containsKey(object))
                {
                    String convertd = conversionDefinitions.get(object);
                    String toReturn = conversions.get(conversionDefinitions.get(object));

                    if (toReturn == null)
                    {
                        if (convertd.matches("\\w+\\[\\d+\\]"))
                        {
                            String A = convertd.replaceAll("\\[\\d+\\]", "");
                            String B = convertd.replace(A, "").replace("[", "").replace("]", "");

                            return conversions.get(A) + "[]";
                        } else return convertd;
                    }

                    return toReturn;
                }

                if (object.contains("::"))
                {
                    String list = object.split("::")[1];

                    String actualObject = getTypeOf(list, conversions, conversionDefinitions, type);

                    return "List<" + actualObject + ">";
                }

                if (conversions.get(object) == null)
                {
                    if (object.matches("\\w+\\[\\d+\\]"))
                    {
                        String A = object.replaceAll("\\[\\d+\\]", "");
                        String B = object.replace(A, "").replace("[", "").replace("]", "");

                        return conversions.get(A) + "[]";
                    }
                }

                return conversions.get(object);
            } else if (type == 1)
            {
                if (conversionDefinitions.containsKey(object))
                {
                    String convertd = conversionDefinitions.get(object);
                    String toReturn = conversions.get(conversionDefinitions.get(object));

                    if (toReturn == null)
                    {
                        if (convertd.matches("\\w+\\[\\d+\\]"))
                        {
                            String A = convertd.replaceAll("\\[\\d+\\]", "");
                            String B = convertd.replace(A, "").replace("[", "").replace("]", "");
                            return conversions.get(A) + "[" + B + "]";
                        }
                    }
                }

                if (object.contains("::"))
                {
                    String list = object.split("::")[1];

                    String actualObject = getTypeOf(list, conversions, conversionDefinitions, type);

                    return "List<>()";
                }

                if (conversions.get(object) == null)
                {
                    if (object.matches("\\w+\\[\\d+\\]"))
                    {
                        String A = object.replaceAll("\\[\\d+\\]", "");
                        String B = object.replace(A, "").replace("[", "").replace("]", "");

                        return conversions.get(A) + "[]";
                    }
                }

                return conversions.get(object) + "()";
            } else if (type == 3)
            {
                return "null";
            } else {
                return "null";
            }
        }

        private List<String> iterate(Iterator<String> strings)
        {
            ArrayList<String> list = new ArrayList<>();

            while (strings.hasNext())
                list.add(strings.next());

            return list;
        }

        @Override
        public void run(String text, File out)
        {
            String newText = "";

            Map<String, String> exportTypes = new HashMap<>();
            exportTypes.put("uint_8", "write");
            exportTypes.put("int_8", "write");
            exportTypes.put("uint_32", "writeInt");
            exportTypes.put("int_32", "writeInt");
            exportTypes.put("uint_64", "writeLong");
            exportTypes.put("int_64", "writeLong");
            exportTypes.put("float", "writeFloat");
            exportTypes.put("double", "writeDouble");

            Map<String, String> convertTypes = new HashMap<>();
            convertTypes.put("uint_8", "byte");
            convertTypes.put("int_8", "byte");
            convertTypes.put("uint_32", "int");
            convertTypes.put("int_32", "int");
            convertTypes.put("uint_64", "long");
            convertTypes.put("int_64", "long");
            convertTypes.put("float", "float");
            convertTypes.put("double", "double");

            Map<String, String> definitionMap = new HashMap<>();

            JSONObject object = new JSONObject(text);
            JSONObject definitions = (JSONObject) object.get("definitions");
            JSONObject classobjcts = (JSONObject) object.get("classes");
            JSONObject parameters  = (JSONObject) object.get("parameters");

            for (String o : definitions.keySet())
                definitionMap.put(o, definitions.get(o).toString());

            for (String className : classobjcts.keySet())
            {
                convertTypes.put(className, className);
            }

            new File(out.toString() + "/output/").mkdirs();

            for (String className : classobjcts.keySet())
            {
                File outputFile = new File(out.toString() + "/output/" + className + ".java");

                String class_ = "package " + parameters.get("Java-Package").toString() + ";\n\n\nimport java.io.*;\n\npublic class " + className + "\n{\n";

                for (Object variable : classobjcts.getJSONArray(className))
                {
                    JSONObject var = (JSONObject) variable;

                    String name = var.keys().next();
                    String type = var.getString(name);

                        class_ += "\tprivate " + getTypeOf(type, convertTypes, definitionMap, 0) + " " + name;
                        String end = getTypeOf(type, convertTypes, definitionMap, 1) + "";

                        if (end.startsWith("null"))
                            class_ += ";\n";
                        else if (end.endsWith("]"))
                            class_ += " = new " + end + ";\n";
                        else class_ += " = new " + end + ";\n";
                }

                for (Object variable : classobjcts.getJSONArray(className))
                {
                    JSONObject var = (JSONObject) variable;

                    String name = var.keys().next();
                    String type = var.getString(name);

                    class_ += "\n\t//GETTERS\n\n";

                    class_ += "\tpublic " + getTypeOf(type, convertTypes, definitionMap, 0) + " get" + name.substring(0, 1).toUpperCase() + name.substring(1) + "() { return " + name + "; }\n";
                }

                for (Object variable : classobjcts.getJSONArray(className))
                {
                    JSONObject var = (JSONObject) variable;

                    String name = var.keys().next();
                    String type = var.getString(name);

                    class_ += "\n\t//SETTERS\n\n";

                    class_ += "\tprivate void " + " set" + name.substring(0, 1).toUpperCase() + name.substring(1) + "(" + getTypeOf(type, convertTypes, definitionMap, 0) + " " + name + ") { this." + name + " = " + name + "; }\n";
                }

                class_ += "\n\n\n\tpublic void write(final DataOutputStream stream) throws IOException\n\t{\n";

                for (Object variable : classobjcts.getJSONArray(className))
                {
                    JSONObject var = (JSONObject) variable;

                    String name = var.keys().next();
                    String type = var.getString(name);

                    class_ += "\t\tstream.write(" + getTypeOf(type, convertTypes, definitionMap, 3) + ");\n";
                }

                class_ += "\t}\n\n\n\tpublic void read(final DataInputStream stream) throws IOException\n\t{\n";

                for (Object variable : classobjcts.getJSONArray(className))
                {
                    JSONObject var = (JSONObject) variable;

                    String name = var.keys().next();
                    String type = var.getString(name);

                    class_ += "\t\tthis." + name + " = stream.read(" + getTypeOf(type, convertTypes, definitionMap, 3) + ");\n";
                }

                class_ += "\t}";

                if (class_.endsWith("\n"))
                    class_ += "}";
                else class_ += "\n}";

//                System.out.println(class_);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                    writer.write(class_);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                JSONObject var = (JSONObject) className;
//                System.out.println(var);
//                JSONObject clss = classobjcts.getJSONObject(className);
//                List<String>      keys = iterate(clss.keys());
//

            }
        }
    }
    public static void main(String ...args)
    {
        Map<String, Command> commands = new HashMap<>();

        commands.put("java", new JavaCommand());

        File buff = new File(args[1]);

        String text = "";

        try{
            BufferedReader reader = new BufferedReader(new FileReader(buff));

            String line = "";

            while ((line = reader.readLine()) != null)
                text += line + "\n";

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commands.get(args[0]).run(text, buff.getParentFile());
    }
}
