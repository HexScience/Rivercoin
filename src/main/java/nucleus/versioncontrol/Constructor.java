package nucleus.versioncontrol;

import nucleus.exceptions.FileServiceException;
import nucleus.util.FileService;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Constructor<T>
{
    default T ConstructFromBytes(byte[] data) { return ConstructFromBytes(ByteBuffer.wrap(data)); }
    T ConstructFromBytes(ByteBuffer data);
    default T ConstructFromInput(FileService service) throws IOException, FileServiceException { return ConstructFromInput(service.as(DataInputStream.class)); }
    T ConstructFromInput(DataInputStream stream) throws IOException;
    T ConstructFromOther(final T other);
}