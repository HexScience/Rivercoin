package com.riverssen.nucleus.gtp;

import com.riverssen.nucleus.exceptions.FileServiceException;
import com.riverssen.nucleus.json.JSONHelper;
import com.riverssen.nucleus.net.server.IpAddress;
import com.riverssen.nucleus.system.Context;
import com.riverssen.nucleus.util.FileService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GlobalTransferProtocol
{
    private FileService     dir;
    private Configuration   configuration;

    public class Configuration
    {
        private long max_allowed_bytes;

        private Configuration(final FileService service) throws IOException, FileServiceException
        {
            if (service.newFile("config.json").exists())
                load(service.newFile("config.json"));
            else mkconfig(service.newFile("config.json"));
        }

        private void load(FileService service) throws IOException, FileServiceException
        {
            BufferedReader reader = service.<BufferedReader>as(BufferedReader.class);
            String data = "";

            String line = "";

            while ((line = reader.readLine()) != null)
                data += line + "\n";

            JSONObject object = new JSONObject(data);

            max_allowed_bytes = object.getJSONObject("config").getLong("MAX_ALLOWED_BYTE_USAGE");
        }

        private void mkconfig(FileService service) throws IOException, FileServiceException
        {
            BufferedWriter writer = service.<BufferedWriter>as(BufferedWriter.class);

            writer.write(JSONHelper.json().insert("MAX_ALLOWED_BYTE_USAGE", "12000000000").toString("config"));
            writer.flush();
            writer.close();
        }
    }

    private Context context;

    public GlobalTransferProtocol(Context context, FileService service) throws IOException, FileServiceException
    {
        this.dir            = service.newFile("gtp_db");
        this.context        = context;
        this.configuration  = new Configuration(dir);
    }

    public final void seedElement(gIdentifier elementIdentifier)
    {
    }

    public final void sendElement(gIdentifier elementIdentifier, IpAddress address)
    {
        context.getServerManager().sendMessages(address, loadElement().toMessageList());
    }

    private final gElement loadElement()
    {
        return null;
    }
}
