package nucleus.json;

public class JSONHelper
{
    public static class JSONObject{
        private StringBuffer buffer;

        private JSONObject()
        {
            buffer = new StringBuffer();
        }

        public void insert(String name, String data)
        {
            buffer.append((buffer.length() > 0 ? ",\n" : "") + "\"" + name + "\": \"" + data + "\"");
        }

        public String toString(String name)
        {
            return "\"" + name + "\":{\n" + buffer.toString() + "\n}";
        }
    }

    public static JSONObject json()
    {
        return new JSONObject();
    }
}