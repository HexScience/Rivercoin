package nucleus.mining.engine.abstractions;

import java.io.InputStream;

public interface Mesh
{
    void load(InputStream stream);
    void create(float vertices[], float normals[], int indices[]);
    void render();
}
