package com.riverssen.nucleus.mining;

import com.riverssen.nucleus.Start;
import com.riverssen.nucleus.exceptions.NKMinerException;
import com.riverssen.nucleus.math.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Shader
{
    private int mShaderPrograms[];
    private Map<String, Integer> mUniforms;

    public Shader(String name) throws Exception
    {
        String string_vertShader = Read(Start.class.getClass().getResourceAsStream("/mining/" + name + ".vertx"));
        String string_tsscShader = Read(Start.class.getClass().getResourceAsStream("/mining/" + name + ".tessc"));
        String string_tsseShader = Read(Start.class.getClass().getResourceAsStream("/mining/" + name + ".tesse"));
        String string_fragShader = Read(Start.class.getClass().getResourceAsStream("/mining/" + name + ".fragm"));

        mShaderPrograms = new int[5];
        mUniforms       = new HashMap<>();

        mShaderPrograms[0] = GL20.glCreateProgram();

        if (mShaderPrograms[0] == NULL) throw new Exception("could not create shader program.");

        mShaderPrograms[1] = createShader(string_vertShader, GL20.GL_VERTEX_SHADER, mShaderPrograms[0]);
        mShaderPrograms[3] = createShader(string_tsscShader, GL40.GL_TESS_CONTROL_SHADER, mShaderPrograms[0]);
        mShaderPrograms[4] = createShader(string_tsseShader, GL40.GL_TESS_EVALUATION_SHADER, mShaderPrograms[0]);
        mShaderPrograms[2] = createShader(string_fragShader, GL20.GL_FRAGMENT_SHADER, mShaderPrograms[0]);

        link(mShaderPrograms[0], mShaderPrograms[1], mShaderPrograms[3], mShaderPrograms[4], mShaderPrograms[2]);
    }

    public void bind()
    {
        glUseProgram(mShaderPrograms[0]);
    }

    public void registerUniform(String uniformName)
    {
        mUniforms.put(uniformName, glGetUniformLocation(mShaderPrograms[0], uniformName));
    }

    public int uniform(String uniformName)
    {
        return glGetUniformLocation(mShaderPrograms[0], uniformName);
    }

    public void uniform(String name, float u)
    {
        glUniform1f(mUniforms.get(name), u);
    }

    public void uniform(String name, int u)
    {
        glUniform1i(mUniforms.get(name), u);
    }

    public void uniform(String name, float ux, float uy)
    {
        glUniform2f(mUniforms.get(name), ux, uy);
    }

    public void uniform(String name, float ux, float uy, float uz)
    {
        glUniform3f(mUniforms.get(name), ux, uy, uz);
    }

    public void uniform(String name, float ux, float uy, float uz, float uw)
    {
        glUniform4f(mUniforms.get(name), ux, uy, uz, uw);
    }

    public void uniform(String name, Matrix4f matrix)
    {
        glUniformMatrix4fv(mUniforms.get(name), true, matrix.get());
    }

    private static String Read(InputStream resourceAsStream) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

        String text = "";

        String line = "";

        while ((line = reader.readLine()) != null)
            text += line + "\n";

        return text;
    }

    private static int createShader(String shaderCode, int shaderType, int programId) throws NKMinerException
    {
        int shaderId = GL20.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new NKMinerException("Error creating shader. Type: " + shaderType);
        }

        GL20.glShaderSource(shaderId, shaderCode);
        GL20.glCompileShader(shaderId);

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            throw new NKMinerException("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024) + " " + shaderType);
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
    }

    private static void link(int programId, int vertexShaderId, int tessellationControlShaderID, int tessellationEvaluationShaderID, int fragmentShaderId) throws NKMinerException
    {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
            throw new NKMinerException("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));

        if (vertexShaderId != 0)
            GL20.glDetachShader(programId, vertexShaderId);
        if (tessellationControlShaderID != 0)
            GL20.glDetachShader(programId, tessellationControlShaderID);
        if (tessellationEvaluationShaderID != 0)
            GL20.glDetachShader(programId, tessellationEvaluationShaderID);
        if (fragmentShaderId != 0)
            GL20.glDetachShader(programId, fragmentShaderId);
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0)
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
    }

    @Override
    protected void finalize()
    {
        GL20.glDeleteShader(mShaderPrograms[1]);
        GL20.glDeleteShader(mShaderPrograms[2]);
        GL20.glDeleteShader(mShaderPrograms[3]);
        GL20.glDeleteShader(mShaderPrograms[4]);
        GL20.glDeleteProgram(mShaderPrograms[0]);
    }
}