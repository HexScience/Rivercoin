package nucleus.mining;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class Mesh
{
    private int vbo, ibo, sze;

    public Mesh(String name)
    {
    }

    public Mesh(float vertices[], int indices[])
    {
    }

    public void render()
    {
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);

        GL20.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glDrawElements(GL_PATCHES, sze, GL_UNSIGNED_INT, 0);


        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
    }
}
