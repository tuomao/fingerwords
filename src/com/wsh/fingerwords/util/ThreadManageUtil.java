package com.wsh.fingerwords.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程管理工具类
 * 
 * @作者 komojoemary
 * @version [版本号, 2011-2-24]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ThreadManageUtil implements Runnable
{
    /**
     * 请求队列(用于单线程)
     */
    private static List<ThreadObject> requestList = null;

    /**
     * 同步对象(用于单线程)
     */
    private static Object object = new Object();

    /**
     * ThreadManageUtil对象的实例(用于单线程)
     */
    private static ThreadManageUtil instance = null;

    /**
     * 当前的请求对象
     */
    private ThreadObject currentRequest = null;


    /**
     * 运行线程
     * 
     * @param 无
     * @return 无
     * @exception/throws 无
     * @see 无
     */
    public void run() {
        // 如果为多线程
//        if (isMultiThread) {
//            runHandle();
//        }
        // 非多线程
//        else {
            while (true) {
                // 如果不为空则为超时需要重连的请求，不从队列里重新获取
                if (currentRequest == null) {
                    synchronized (object) {
                        // 队列不为空
                        if (requestList.size() > 0) {
                            // 从队列中获取请求
                            currentRequest = (ThreadObject) requestList.get(0);
                            // 从请求队列中将当前请求删除
                            requestList.remove(0);
                        }
                        else {
                            try {
                                // 队列为空则交出对象锁，等待唤醒
                                object.wait();
                                continue;
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                runHandle();
                currentRequest = null;
            }
//        }
    }

    /**
     * 
     * 请求过程处理 过程
     * 
     * @param 无
     * @return 无
     * @exception/throws 无 无
     * @see 无
     */
    private void runHandle() {
        try {
            currentRequest.handleOperation();
            Thread.yield();
        }
        catch (Exception ce) {
            ce.printStackTrace();
        }
    }

    /**
     * 
     * 启动线程 当用单线程多任务排队请求时，将请求添加至请求队列中。当请求的是多线程时，开辟一个新的线程
     * 
     * @param request
     *            请求对象
     * @return 无
     * @exception/throws 无
     */
    public static void sendRequest(ThreadObject request) {
        // 多线程
//        if (request.isMultiThread()) {
//            ThreadManageUtil downloadHttp = new ThreadManageUtil();
//            downloadHttp.isMultiThread = true;
//            downloadHttp.currentRequest = request;
//            new Thread(downloadHttp).start();
//        }
//        else {
            // 单线程请求排队
            if (instance == null) {
                // 在当前线程对象为空时才进行线程初始化操作，同时初始化队列
                instance = new ThreadManageUtil();
                requestList = new ArrayList<ThreadObject>();
                new Thread(instance).start();
            }
            insertReqList(request);
//        }
    }

    /**
     * 
     * 请求添加方法
     * 
     * @param request
     *            需要添加的请求
     * @return 无
     * @exception/throws 无
     * @see 无
     */
    private static void insertReqList(ThreadObject request) {
        synchronized (object) {
            requestList.add(request);
            object.notify();
        }
    }

}
