package nucleus.mining.engine.abstractions;

import java.io.InputStream;

public interface Texture
{
    void load(InputStream stream);
    void create(int w, int h, float r, float g, float b);
    void create(int w, int h, byte[] bytes);
    void bind(int i);
    void bindAsRenderTarget(int i);
}