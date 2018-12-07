package com.riverssen.nucleus.mining;

import com.riverssen.nucleus.exceptions.NKMinerException;
import com.riverssen.nucleus.util.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.FunctionProviderLocal;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.riverssen.nucleus.mining.InfoUtil.*;
import static com.riverssen.nucleus.mining.InfoUtil.checkCLError;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10.CL_COMPLETE;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_WRITE;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clEnqueueNativeKernel;
import static org.lwjgl.opencl.CL10.clFinish;
import static org.lwjgl.opencl.CL10.clReleaseCommandQueue;
import static org.lwjgl.opencl.CL10.clReleaseEvent;
import static org.lwjgl.opencl.CL11.*;
import static org.lwjgl.opencl.CL11.clSetEventCallback;
import static org.lwjgl.opencl.CL11.clSetMemObjectDestructorCallback;
import static org.lwjgl.opencl.KHRICD.CL_PLATFORM_ICD_SUFFIX_KHR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.Pointer.POINTER_SIZE;

public class OCL
{
    private static final Logger Logger = com.riverssen.nucleus.util.Logger.get("NKMiner");
    private void clInit(FBO fbo) throws NKMinerException
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
}