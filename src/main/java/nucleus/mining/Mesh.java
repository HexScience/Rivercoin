package nucleus.mining;

import nucleus.Start;
import nucleus.exceptions.NKMinerException;
import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_PATCHES;

public class Mesh
{
    /**
     * This class uses a VAO to render,
     * don't let the field names confuse
     * you.
     */
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

        class vertex_struct{
            float v_x, v_y, v_z,
            n_x, n_y, n_z,
            c_x, c_y;

            vertex_struct(float v_x, float v_y, float v_z,
                                 float n_x, float n_y, float n_z,
                                 float c_x, float c_y)
            {
                this.v_x = v_x;
                this.v_y = v_y;
                this.v_z = v_z;
                this.n_x = n_x;
                this.n_y = n_y;
                this.n_z = n_z;
                this.c_x = c_x;
                this.c_y = c_y;
            }
        }

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

        float actualVertices[] = new float[(indices.length) * 8];

        index = 0;

        for (int i = 0; i < indices.length; i ++)
        {
            actualVertices[index ++] = vertices[indices[i] * 8];
            actualVertices[index ++] = vertices[indices[i] * 8 + 1];
            actualVertices[index ++] = vertices[indices[i] * 8 + 2];
            actualVertices[index ++] = vertices[indices[i] * 8 + 3];
            actualVertices[index ++] = vertices[indices[i] * 8 + 4];
            actualVertices[index ++] = vertices[indices[i] * 8 + 5];
            actualVertices[index ++] = vertices[indices[i] * 8 + 6];
            actualVertices[index ++] = vertices[indices[i] * 8 + 7];
        }

        this.mesh(actualVertices, indices);
    }

    public Mesh(float vertices[], int indices[])
    {
        this.mesh(vertices, indices);
    }

    private void mesh(float vertices[], int indices[])
    {
        sze = vertices.length / 8;



        vbo = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vbo);

        ibo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, ibo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
//
//
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//        glEnableVertexAttribArray(2);
//
//
//        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
//        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
//        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);
//
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//        GL30.glBindVertexArray(0);


//        vbo = glGenBuffers();
//        ibo = glGenBuffers();
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//
//        sze = indices.length;


//        sze = indices.length;
//
//        vbo = glGenBuffers();
//        ibo = glGenBuffers();
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

//        sze = indices.length;
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//
//        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
//        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
//        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);
    }

    public void render()
    {
        glBindVertexArray(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, ibo);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);

        glDrawArrays(GL_PATCHES, 0, sze);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);



//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//        glEnableVertexAttribArray(2);
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
//        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 12);
//        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 24);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
//        glDrawElements(GL_PATCHES, sze, GL_UNSIGNED_INT, 0);
//        System.out.println(sze);
//
//        glDisableVertexAttribArray(0);
//        glDisableVertexAttribArray(1);
//        glDisableVertexAttribArray(2);



//        GL20.glEnableVertexAttribArray(0);
//        GL20.glEnableVertexAttribArray(1);
//        GL20.glEnableVertexAttribArray(2);
//
//        glBindBuffer(GL_ARRAY_BUFFER, vbo);
//        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
//        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
//        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);
//
//        GL20.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
//        glDrawElements(GL_PATCHES, sze, GL_UNSIGNED_INT, 0);
    }
}
