package nucleus.mining;

import nucleus.Start;
import nucleus.algorithms.*;
import nucleus.exceptions.NKMinerException;
import nucleus.exceptions.NKMinerInstanceAlreadyExistsException;
import nucleus.exceptions.NKMinerNullInstanceException;
import nucleus.mining.engine.abstractions.FrameBufferObject;
import nucleus.mining.engine.abstractions.Mesh;
import nucleus.system.Parameters;
import nucleus.util.ByteUtil;
import nucleus.util.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.math.BigInteger;
import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


import org.lwjgl.opencl.*;

import java.util.concurrent.*;

import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.opencl.KHRICD.*;
import static org.lwjgl.system.Pointer.*;

import static nucleus.mining.InfoUtil.*;

public class NKMiner
{
    private static NKMiner instance;

    private long mWindow;
    private float mQuad[];
    private int   mIndices[];
    private int   mShaderPrograms[];
    private class Element{
        private int vertices;
        private int vbo;

        private int coords;
        private int cbp;
    }
    private Element element;
    private int     mTime;
    private int     mLightColour;
    private int     mLightColour2;
    private int     mNonce2;
    private int     mNkHash;

    private class FrameBufferObject{
        public int  fbo;
        private int textureID;
    }

    private static final int width = 1280, height = 720;

    private FrameBufferObject fbo;

    private NKMiner() throws NKMinerException
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new NKMinerException("Unable to initialize GLFW");

        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
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

        float vertices[] =
//                {
//                    -1, -1, 0,
//                    -1,  1, 0,
//                    1,  1, 0,
//                    1, -1, 0
//                };
                {
                        // Left bottom triangle
                        -1f, 1f, 0f,
                        -1f, -1f, 0f,
                        1f, -1f, 0f,


                        // Right top triangle
                        1f, -1f, 0f,
                        1f, 1f, 0f,
                        -1f, 1f, 0f
                };

        float verticeswithcoords[] =
            {
                -1, -1, 0,
                    -1, -1,
                1, -1, 0,
                    1, -1,
                1, 1, 0,
                    1, 1,
                -1, -1, 0,
                    -1, -1,
                1, 1, 0,
                    1, 1,
                -1, 1, 0,
                    -1, 1
            };

//        mIndices = new int[]
//                {
//                    0, 1, 2,
//                    0, 2, 3
//                };

        element = new Element();
//        FloatBuffer verticese = BufferUtils.createFloatBuffer(vertices.length);
//        verticese.put(vertices);
//        verticese.flip();

        element.vertices = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(element.vertices);

        element.vbo = GL15.glGenBuffers();
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, element.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticeswithcoords, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false,20, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 20, 12);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);


//        element.coords = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(element.coords);
//
//        element.cbp = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, element.cbp);
//        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, coords, GL15.GL_STATIC_DRAW);
//        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false,0, 0);
//        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//
//        GL30.glBindVertexArray(0);

        String vertShader = null;
        String fragShader = null;

        /**
         * Bind the vertex array to bypass validation errors.
         */
        GL30.glBindVertexArray(element.vertices);

        try
        {
            vertShader = Read(Start.class.getClass().getResourceAsStream("/mining/shader.vertx"));
            fragShader = Read(Start.class.getClass().getResourceAsStream("/mining/shader.fragm"));
        } catch (IOException e)
        {
            e.printStackTrace();
            throw new NKMinerException("cannot read shader files.");
        }

        mShaderPrograms = new int[3];

        mShaderPrograms[0] = GL20.glCreateProgram();

        if (mShaderPrograms[0] == NULL) throw new NKMinerException("could not create main shader program.");

        createVertexShader(vertShader, mShaderPrograms);
        createFragmentShader(fragShader, mShaderPrograms);

        link(mShaderPrograms[0], mShaderPrograms[1], mShaderPrograms[2]);


        mTime = GL20.glGetUniformLocation(mShaderPrograms[0], "nonce");
        mLightColour = GL20.glGetUniformLocation(mShaderPrograms[0], "LightColor");
        mLightColour2 = GL20.glGetUniformLocation(mShaderPrograms[0], "LightColor2");
        mNonce2 = GL20.glGetUniformLocation(mShaderPrograms[0], "nonce2");

        fbo = new FrameBufferObject();

        fbo.fbo = GL30.glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo.fbo);

        fbo.textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, fbo.textureID);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width*4, height*4, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);


        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        int depthrenderbuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthrenderbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width*4, height*4);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthrenderbuffer);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, fbo.textureID, 0);

        int DrawBuffers[] = {GL_COLOR_ATTACHMENT0};
        glDrawBuffers(DrawBuffers);


        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new NKMinerException("failed to create a framebuffer object.");


        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

//        glViewport(0,0,width,height);
//        clInit();

        Logger.alert("NKMiner initialized successfully.");
    }

    private void clInit() throws NKMinerException
    {
        MemoryStack stack = stackPush();

        IntBuffer pi = stack.mallocInt(1);
        clGetPlatformIDs(null, pi);

        if (pi.get(0) == 0)
            throw new NKMinerException("No OpenCL platforms found.");

        PointerBuffer platforms = stack.mallocPointer(pi.get(0));

        clGetPlatformIDs(platforms, (IntBuffer) null);

        PointerBuffer ctxProps = stack.mallocPointer(3);
        ctxProps
                .put(0, CL_CONTEXT_PLATFORM)
                .put(2, 0);

        IntBuffer errcode_ret = stack.callocInt(1);
        for (int p = 0; p < platforms.capacity(); p++)
        {
            long platform = platforms.get(p);
            ctxProps.put(1, platform);

            Logger.prt("\n-------------------------");
            Logger.prt("NEW PLATFORM: " + platform + "\n");

            CLCapabilities platformCaps = CL.createPlatformCapabilities(platform);

//            printPlatformInfo(platform, "CL_PLATFORM_PROFILE", CL_PLATFORM_PROFILE);
            printPlatformInfo(platform, "CL_PLATFORM_VERSION", CL_PLATFORM_VERSION);
            printPlatformInfo(platform, "CL_PLATFORM_NAME", CL_PLATFORM_NAME);
            printPlatformInfo(platform, "CL_PLATFORM_VENDOR", CL_PLATFORM_VENDOR);
            printPlatformInfo(platform, "CL_PLATFORM_EXTENSIONS", CL_PLATFORM_EXTENSIONS);
            if (platformCaps.cl_khr_icd)
                printPlatformInfo(platform, "CL_PLATFORM_ICD_SUFFIX_KHR", CL_PLATFORM_ICD_SUFFIX_KHR);

            Logger.prt("");

            clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, null, pi);

            PointerBuffer devices = stack.mallocPointer(pi.get(0));
            clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, devices, (IntBuffer) null);

            for (int d = 0; d < devices.capacity(); d++)
            {
                long device = devices.get(d);

                CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);

                Logger.prt("\n\t** NEW DEVICE: " + device + "\n");

                Logger.prt("\tCL_DEVICE_TYPE = " + (getDeviceInfoLong(device, CL_DEVICE_TYPE) == CL_DEVICE_TYPE_CPU ? "CPU" : (getDeviceInfoLong(device, CL_DEVICE_TYPE) == CL_DEVICE_TYPE_GPU ? "GPU" : getDeviceInfoLong(device, CL_DEVICE_TYPE))));
//                Logger.prt("\tCL_DEVICE_VENDOR_ID = " + getDeviceInfoInt(device, CL_DEVICE_VENDOR_ID));
                Logger.prt("\tCL_DEVICE_MAX_COMPUTE_UNITS = " + getDeviceInfoInt(device, CL_DEVICE_MAX_COMPUTE_UNITS));
                Logger.prt("\tCL_DEVICE_MAX_WORK_ITEM_DIMENSIONS = " + getDeviceInfoInt(device, CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS));
                Logger.prt("\tCL_DEVICE_MAX_WORK_GROUP_SIZE = " + getDeviceInfoPointer(device, CL_DEVICE_MAX_WORK_GROUP_SIZE));
                Logger.prt("\tCL_DEVICE_MAX_CLOCK_FREQUENCY = " + getDeviceInfoInt(device, CL_DEVICE_MAX_CLOCK_FREQUENCY));
                Logger.prt("\tCL_DEVICE_ADDRESS_BITS = " + getDeviceInfoInt(device, CL_DEVICE_ADDRESS_BITS));
                Logger.prt("\tCL_DEVICE_AVAILABLE = " + (getDeviceInfoInt(device, CL_DEVICE_AVAILABLE) != 0));
                Logger.prt("\tCL_DEVICE_COMPILER_AVAILABLE = " + (getDeviceInfoInt(device, CL_DEVICE_COMPILER_AVAILABLE) != 0));

                printDeviceInfo(device, "CL_DEVICE_NAME", CL_DEVICE_NAME);
                printDeviceInfo(device, "CL_DEVICE_VENDOR", CL_DEVICE_VENDOR);
                printDeviceInfo(device, "CL_DRIVER_VERSION", CL_DRIVER_VERSION);
                printDeviceInfo(device, "CL_DEVICE_PROFILE", CL_DEVICE_PROFILE);
                printDeviceInfo(device, "CL_DEVICE_VERSION", CL_DEVICE_VERSION);
                printDeviceInfo(device, "CL_DEVICE_EXTENSIONS", CL_DEVICE_EXTENSIONS);
                if (caps.OpenCL11)
                    printDeviceInfo(device, "CL_DEVICE_OPENCL_C_VERSION", CL_DEVICE_OPENCL_C_VERSION);
            }


            for (int d = 0; d < devices.capacity(); d++)
            {
                long device = devices.get(d);


                String name = getDeviceInfoStringUTF8(device, CL_DEVICE_NAME);


                CLContextCallback contextCB;
                long context = clCreateContext(ctxProps, device, contextCB = CLContextCallback.create((errinfo, private_info, cb, user_data) ->
                {
                    System.err.println("[LWJGL] cl_context_callback");
                    System.err.println("\tInfo: " + memUTF8(errinfo));
                }), NULL, errcode_ret);
                InfoUtil.checkCLError(errcode_ret);

                long buffer = clCreateBuffer(context, CL_MEM_READ_ONLY, 128, errcode_ret);
                InfoUtil.checkCLError(errcode_ret);

                CLMemObjectDestructorCallback bufferCB1 = null;
                CLMemObjectDestructorCallback bufferCB2 = null;

                long subbuffer = NULL;

                CLMemObjectDestructorCallback subbufferCB = null;

                int errcode;

                CLCapabilities caps = CL.createDeviceCapabilities(device, platformCaps);


                CountDownLatch destructorLatch;

                if (caps.OpenCL11)
                {
                    destructorLatch = new CountDownLatch(3);

                    errcode = clSetMemObjectDestructorCallback(buffer, bufferCB1 = CLMemObjectDestructorCallback.create((memobj, user_data) ->
                    {
                        Logger.prt("\t\tBuffer destructed (1): " + memobj);
                        destructorLatch.countDown();
                    }), NULL);
//                    checkCLError(errcode);

                    errcode = clSetMemObjectDestructorCallback(buffer, bufferCB2 = CLMemObjectDestructorCallback.create((memobj, user_data) ->
                    {
                        Logger.prt("\t\tBuffer destructed (2): " + memobj);
                        destructorLatch.countDown();
                    }), NULL);
//                    checkCLError(errcode);

                    try (CLBufferRegion buffer_region = CLBufferRegion.malloc())
                    {
                        buffer_region.origin(0);
                        buffer_region.size(64);

                        subbuffer = nclCreateSubBuffer(buffer,
                                CL_MEM_READ_ONLY,
                                CL_BUFFER_CREATE_TYPE_REGION,
                                buffer_region.address(),
                                memAddress(errcode_ret));
//                        checkCLError(errcode_ret);
                    }

                    errcode = clSetMemObjectDestructorCallback(subbuffer, subbufferCB = CLMemObjectDestructorCallback.create((memobj, user_data) ->
                    {
                        Logger.prt("\t\tSub Buffer destructed: " + memobj);
                        destructorLatch.countDown();
                    }), NULL);
//                    checkCLError(errcode);
                } else
                {
                    destructorLatch = null;
                }


                /**
                 * Choose a capable GPU
                 */
                if (name.toLowerCase().contains("nvidia") || name.toLowerCase().contains("amd"))
                {
                    long queue = clCreateCommandQueue(context, device, NULL, errcode_ret);

                    int errcb[] = new int[1];

                    CL10GL.clCreateFromGLTexture2D(context, CL_MEM_READ_WRITE, GL_TEXTURE_2D, 0, fbo.textureID, errcb);

                    if (errcb[0] != CL_SUCCESS)
                        throw new NKMinerException("could not create a clTexture2d.");


                    PointerBuffer ev = BufferUtils.createPointerBuffer(1);

                    ByteBuffer kernelArgs = BufferUtils.createByteBuffer(4);
                    kernelArgs.putInt(0, 1337);

                    CLNativeKernel kernel;
                    errcode = clEnqueueNativeKernel(queue, kernel = CLNativeKernel.create(
                            args -> System.out.println("\t\tKERNEL EXEC argument: " + memByteBuffer(args, 4).getInt(0) + ", should be 1337")
                    ), kernelArgs, null, null, null, ev);
                    checkCLError(errcode);

                    long e = ev.get(0);

                    CountDownLatch latch = new CountDownLatch(1);

                    CLEventCallback eventCB;
                    errcode = clSetEventCallback(e, CL_COMPLETE, eventCB = CLEventCallback.create((event, event_command_exec_status, user_data) -> {
                        System.out.println("\t\tEvent callback status: " + getEventStatusName(event_command_exec_status));
                        latch.countDown();
                    }), NULL);
                    checkCLError(errcode);

                    try {
                        boolean expired = !latch.await(500, TimeUnit.MILLISECONDS);
                        if (expired) {
                            System.out.println("\t\tKERNEL EXEC FAILED!");
                        }
                    } catch (InterruptedException exc) {
                        exc.printStackTrace();
                    }
                    eventCB.free();

                    errcode = clReleaseEvent(e);
                    checkCLError(errcode);
                    kernel.free();

                    kernelArgs = BufferUtils.createByteBuffer(POINTER_SIZE * 2);

                    kernel = CLNativeKernel.create(args -> {
                    });

                    long time   = System.nanoTime();
                    int  REPEAT = 1000;
                    for (int i = 0; i < REPEAT; i++)
                        clEnqueueNativeKernel(queue, kernel, kernelArgs, null, null, null, null);


                    clFinish(queue);


                    time = System.nanoTime() - time;

                    System.out.printf("\n\t\tEMPTY NATIVE KERNEL AVG EXEC TIME: %.4fus\n", (double)time / (REPEAT * 1000));

                    errcode = clReleaseCommandQueue(queue);
                    checkCLError(errcode);
                    kernel.free();
                }
            }
        }
    }

    private static void get(FunctionProviderLocal provider, long platform, String name)
    {
        Logger.prt(name + ": " + provider.getFunctionAddress(platform, name));
    }

    private static void printPlatformInfo(long platform, String param_name, int param)
    {
        Logger.prt("\t" + param_name + " = " + getPlatformInfoStringUTF8(platform, param));
    }

    private static void printDeviceInfo(long device, String param_name, int param)
    {
        Logger.prt("\t" + param_name + " = " + getDeviceInfoStringUTF8(device, param));
    }

    private static String getEventStatusName(int status)
    {
        switch (status) {
            case CL_QUEUED:
                return "CL_QUEUED";
            case CL_SUBMITTED:
                return "CL_SUBMITTED";
            case CL_RUNNING:
                return "CL_RUNNING";
            case CL_COMPLETE:
                return "CL_COMPLETE";
            default:
                throw new IllegalArgumentException(String.format("Unsupported event status: 0x%X", status));
        }
    }

    private static void link(int programId, int vertexShaderId, int fragmentShaderId) throws NKMinerException
    {
        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == 0)
            throw new NKMinerException("Error linking Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));

        if (vertexShaderId != 0)
            GL20.glDetachShader(programId, vertexShaderId);
        if (fragmentShaderId != 0)
            GL20.glDetachShader(programId, fragmentShaderId);
        GL20.glValidateProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == 0)
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(programId, 1024));
    }

    private static void createVertexShader(String shaderCode, int program[]) throws NKMinerException
    {
        program[1] = createShader(shaderCode, GL20.GL_VERTEX_SHADER, program[0]);
    }

    private static void createFragmentShader(String shaderCode, int program[]) throws NKMinerException
    {
        program[2] = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER, program[0]);
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
            throw new NKMinerException("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderId, 1024));
        }

        GL20.glAttachShader(programId, shaderId);

        return shaderId;
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

    public static NKMiner init() throws NKMinerException
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

    private void draw(float nonce_a, float r, float g, float b, float r2, float g2, float b2, float r3, float g3, float b3)
    {
        glUseProgram(mShaderPrograms[0]);
        GL20.glUniform1f(mTime, nonce_a);
        GL20.glUniform3f(mLightColour, r, g, b);
        GL20.glUniform3f(mLightColour2, r2, g2,  b2);
        GL20.glUniform3f(mNonce2, r3, g3,  b3);
//        glVertexPointer(3, GL_FLOAT, 0, element.vertices);
//        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_BYTE, element.vbo);

        GL30.glBindVertexArray(element.vertices);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);


        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);


        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void step(float nonce_a, float r, float g, float b, float r2, float g2, float b2, float r3, float g3, float b3)
    {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo.fbo);
        glViewport(0,0, width*4, height*4);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        draw(nonce_a, r, g, b, r2, g2, b2, r3, g3, b3);


        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glBindFramebuffer(GL_READ_FRAMEBUFFER , fbo.fbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width*2, height*2, GL_COLOR_BUFFER_BIT, GL_NEAREST);

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
        GL15.glDeleteBuffers(element.vbo);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(element.vertices);
    }

    public static NKMiner getInstance() throws NKMinerException
    {
        if (instance == null)
            throw new NKMinerNullInstanceException();

        return instance;
    }
}