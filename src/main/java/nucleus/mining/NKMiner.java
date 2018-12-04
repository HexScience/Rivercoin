package nucleus.mining;

import nucleus.algorithms.*;
import nucleus.exceptions.NKMinerException;
import nucleus.exceptions.NKMinerInstanceAlreadyExistsException;
import nucleus.exceptions.NKMinerNullInstanceException;
import nucleus.math.Matrix4f;
import nucleus.math.Vector3f;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;
import nucleus.util.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.math.BigInteger;
import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class NKMiner
{
    private static NKMiner instance;

    private long mWindow;
    private float mQuad[];
    private int   mIndices[];
    private class VBO{
        private int vbo;
        private int ibo;
        private int sze;
    }

//    private class Element{
//        private int vertices;
//        private int vbo;
//
//        private int coords;
//        private int cbp;
//    }
//    private Element element;
    private int     mTime;
    private int     mLightColour;
    private int     mLightColour2;
    private int     mNonce2;
    private int     mNkHash;
    private int     mMDL;
    private int     mMVP;
    private FBO     fbo;
    private VAOMesh vaoMesh;
    private Mesh    mesh;

    private Shader  tlsds;

    private static final int width = 1280, height = 720;

    private NKMiner() throws Exception
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new NKMinerException("Unable to initialize GLFW");

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        mWindow = glfwCreateWindow(width, height, "NKMiner", NULL, NULL);

        if (mWindow == NULL)
            throw new NKMinerException("Failed to create a GLFW window.");

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(mWindow, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    mWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(mWindow);
        glfwSwapInterval(1);

        glfwSetWindowCloseCallback(mWindow, (i)->{System.exit(0);});

        glfwShowWindow(mWindow);

        glfwMakeContextCurrent(mWindow);

        GL.createCapabilities();


        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
//        glEnableVertexAttribArray(2);

        float vertices[] =
        {
                -1, -1, 0,
                0, -1, 0,
                -1, -1,
                1, -1, 0,
                0, -1, 0,
                1, -1,
                1, 1, 0,
                1, -1, 1,
                1, 1,

                -1, -1, 0,
                0, -1, 0,
                -1, -1,
                1, 1, 0,
                0, -1, 0,
                1, 1,
                -1, 1, 0,
                0, -1, 0,
                -1, 1
        };

        int indices[] = {0, 1, 2, 3, 4, 5};

        glFrontFace(GL_CCW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_TEXTURE_2D);

        vaoMesh = new VAOMesh(vertices, indices);
//        mesh    = VAOMesh.Mesh("cube", "obj");



        /**
         * Bind the vertex array to bypass validation errors.
         */
//        GL30.glBindVertexArray(element.vertices);

        tlsds = new Shader("shader");
        tlsds.registerUniform("nonce");
        tlsds.registerUniform("mvp");
        tlsds.registerUniform("model");
        tlsds.registerUniform("LightColor");
        tlsds.registerUniform("LightColor2");
        tlsds.registerUniform("nonce2");

        fbo = new FBO(width * 4, height * 4);

        glBindVertexArray(0);

//        glViewport(0,0,width,height);
//        clInit();

        Logger.alert("NKMiner initialized successfully.");
    }

    public static NKMiner init() throws Exception
    {
        if (instance != null)
            throw new NKMinerInstanceAlreadyExistsException();
        return (instance = new NKMiner());
    }

    /**
     * @param sha3_algo
     * @param sha512_algo
     * @param block
     * @return a 160 byte array which can be used to fetch 20 (long int) seeds.
     */
    public static byte[] buildSeeds(Sha3 sha3_algo, Sha512 sha512_algo, byte block[], long nonce)
    {
        byte strong_seed[] = sha512_algo.encode(ByteUtil.concatenate(block, ByteUtil.encode(nonce)));

        strong_seed = ByteUtil.concatenate(strong_seed, sha3_algo.encode(strong_seed));
        strong_seed = ByteUtil.concatenate(strong_seed, sha512_algo.encode(sha3_algo.encode(strong_seed)));

        return strong_seed;
    }

    float x = 0;
    Vector3f camPos = new Vector3f(0,0,-12.0F);

    private void draw(float nonce_a, float r, float g, float b, float r2, float g2, float b2, float r3, float g3, float b3)
    {
//        glPolygonMode( GL_FRONT_AND_BACK, x*50.0F % 500 > 250 ? GL_LINE : GL_FILL);
//        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE);

        tlsds.bind();

        for (int i = 0; i < 1; i ++)
        {
            tlsds.uniform("nonce", nonce_a);
            tlsds.uniform("LightColor", r, g, b);
            tlsds.uniform("LightColor2", r2, g2, b2);
            tlsds.uniform("nonce2", r3, g3, b3);

            Matrix4f pos = new Matrix4f().InitTranslation(0,0, 0),
                    rot = new Matrix4f().InitRotation(0, 0,0),
                    scl = new Matrix4f().InitScale(3.0f,3.0f,-1),

                    cpos = new Matrix4f().InitTranslation(-camPos.GetX(), -camPos.GetY(), -camPos.GetZ()),
                    crot = new Matrix4f().InitRotation(0,0,0),

                    cproj = new Matrix4f().InitPerspective((float) Math.toRadians(90.0), ((float) width) / ((float) height), 0.01F, 1000.0F);

            Matrix4f model = new Matrix4f().InitIdentity().Mul(pos.Mul(rot.Mul(scl)));
            Matrix4f projv = cproj.Mul(crot.Mul(cpos));

            tlsds.uniform("mvp", projv.Mul(model));
            tlsds.uniform("model", model);

            vaoMesh.render();
        }
//        glBindVertexArray(element.vertices);
//        glEnableVertexAttribArray(0);
//        glEnableVertexAttribArray(1);
//        glEnableVertexAttribArray(2);
//
//        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,32, 0);
//        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 12);
//        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 32, 24);
//
//        glBindBuffer(GL_ARRAY_BUFFER, element.vbo);
//        glDrawArrays(GL_PATCHES, 0, 6);
//        glBindVertexArray(0);
//        glBindBuffer(GL_ARRAY_BUFFER, 0);

//        GL30.glBindVertexArray(0);
    }

    public void step(float nonce_a, float r, float g, float b, float r2, float g2, float b2, float r3, float g3, float b3)
    {
        fbo.bind();

        glClearColor(0.1f, 0.0f, 0.1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        draw(nonce_a, r, g, b, r2, g2, b2, r3, g3, b3);

        fbo.unbind();
        fbo.display(width, height);

        glfwSwapBuffers(mWindow);

        glfwPollEvents();
    }

    public void looptest()
    {
//        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        while (true)
        {
            step(x += 0.1f, 1, 1, 1, 1, 0, 0, 3, 1, 6);
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public byte[] mine(byte data[], BigInteger difficulty)
    {
        byte result[] = do_mine(data, difficulty);
        if (new BigInteger(result).abs().compareTo(difficulty) >= 0)
        {
            System.err.println("error");
            System.exit(0);
        }
        return result;
    }

    private byte[] do_mine(byte data[], BigInteger difficulty)
    {
        long nonce = 0;
        Sha256 sha256_algo = new Sha256();
        Sha3 sha3_algo = new Sha3();
        Sha512 sha512_algo = new Sha512();
        Blake384 blake384_algo = new Blake384();
        Blake blake_algo = new Blake();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        /** Se the result to something too big **/
        byte resultHash[] = null;
        BigInteger result = Parameters.toInteger(Parameters.MINIMUM_DIFFICULTY).add(BigInteger.TEN);

        Random random = new Random();

        while (result.abs().compareTo(difficulty) >= 0)
        {
            /**
             * Generate seeds and scene information
             * and reduce to 48 bytes.
             */
            byte        seeds[] =   blake384_algo.encode(buildSeeds(sha3_algo, sha512_algo, data, nonce ++));
            ByteBuffer  seedGen =   ByteBuffer.wrap(seeds);
            ByteBuffer  noncebf =   ByteBuffer.wrap(ByteUtil.encode(nonce));

            float colour[]      =   {((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f};
            float colour2[]     =   {((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f};
            float nonce2[]      =   {((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f, ((float) ((seedGen.getInt() % 255))) / 255.0f};

            random.setSeed(seedGen.getLong());
            float b = random.nextFloat();
            random.setSeed(seedGen.getInt());

            float fnonce        = (float) Math.abs((noncebf.getFloat() * noncebf.getFloat()) + Math.pow(random.nextFloat() + 1, 6) + Math.pow(b + 1, 6));

            /**
             * Render the scene
             */
            long now = System.currentTimeMillis();

            step                    (fnonce, colour[0], colour[1], colour[2], colour2[0], colour2[1], colour2[2], nonce2[0], nonce2[1], nonce2[2]);

            long step_finish_time = System.currentTimeMillis();

            Logger.prt((step_finish_time - now));


            /**
             * Generate The Hash
             */

            //TODO: Buffer size is x4 times bigger.

            GL11.glReadBuffer(GL11.GL_FRONT);
            int width   = NKMiner.width;
            int height  = NKMiner.height;
            int bpp = 4;
            int bytes[] = new int[width * height];
            byte bytez[] = new byte[width * height * bpp];
            ByteBuffer buffer = ByteBuffer.allocate(bytez.length);
            IntBuffer  intsz  = buffer.asIntBuffer();

            GL15.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bytes);

            intsz.put(bytes);
            buffer.flip();

            resultHash     = sha256_algo.encode(blake_algo.encode(buffer.array()));

//            long now_ = System.currentTimeMillis();
            result         = new BigInteger(resultHash).abs();

//            System.out.println(difficulty + "\n" + result + "\n\n");

//            Logger.prt(System.currentTimeMillis() - now_ + "\n-----");
        }

        return resultHash;
    }

    @Override
    protected void finalize() throws Throwable
    {
        delete();
        super.finalize();
    }

    private void delete()
    {
        glfwDestroyWindow(mWindow);
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//        GL15.glDeleteBuffers(element.vbo);
//        GL30.glBindVertexArray(0);
//        GL30.glDeleteVertexArrays(element.vertices);
    }

    public static NKMiner getInstance() throws NKMinerException
    {
        if (instance == null)
            throw new NKMinerNullInstanceException();

        return instance;
    }
}