package nucleus.mining;

import nucleus.Start;
import nucleus.exceptions.NKMinerException;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class Mesh
{
    private int vbo, ibo, sze;

    public Mesh(String name, String extention) throws IOException, NKMinerException
    {
        DataInputStream stream = new DataInputStream(Start.class.getClass().getResourceAsStream("/mining/" + name + "." + extention));

        byte dataset[] = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (stream.available() > 0)
        {
            int i = stream.read(dataset);
            baos.write(dataset, 0, i);
        }

        baos.flush();
        baos.close();

        byte data[] = baos.toByteArray();

        stream.close();

        ByteBuffer file = MemoryUtil.memAlloc(data.length);
        file.put(data);
        file.flip();

        AIScene scene = Assimp.aiImportFileFromMemory(file,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs |
                        Assimp.aiProcess_CalcTangentSpace |
                        Assimp.aiProcess_LimitBoneWeights,
                extention
        );

        if (scene == null)
            throw new NKMinerException("could not load PipeDreamObject data.");

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        float vertices[] = new float[mesh.mNumVertices() * 8];
        int indices[] = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
        int index = 0;

        for (int v = 0; v < mesh.mNumVertices(); v ++)
        {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D normal   = mesh.mNormals().get(v);
            AIVector3D texCoord = mesh.mTextureCoords(0).get(v);

            vertices[index ++] = position.x();
            vertices[index ++] = position.y();
            vertices[index ++] = position.z();
            vertices[index ++] = normal.x();
            vertices[index ++] = normal.y();
            vertices[index ++] = normal.z();
            vertices[index ++] = texCoord.x();
            vertices[index ++] = texCoord.z();
        }


        index = 0;

        for(int f = 0; f < mesh.mNumFaces(); f++)
        {
            AIFace face = mesh.mFaces().get(f);
            for(int ind = 0; ind < face.mNumIndices(); ind++)
                indices[index ++] = (face.mIndices().get(ind));
        }

        this.mesh(vertices, indices);
    }

    public Mesh(float vertices[], int indices[])
    {
        this.mesh(vertices, indices);
    }

    private void mesh(float vertices[], int indices[])
    {
        sze = indices.length;

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
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
