package org.gracefulshutdown.controller;

import org.gracefulshutdown.aspect.GracefulShutdownControllerAspect;
import org.gracefulshutdown.business.ShutdownTraceHelper;
import org.gracefulshutdown.common.RunStatusEnum;
import org.gracefulshutdown.hook.AsncThreadMonitorHook;
import org.gracefulshutdown.http.HttpConstant;
import org.gracefulshutdown.http.Response;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/graceful/shutdown")
public class TransactionOrderController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionOrderController.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/execute/hook/async")
    public Response<String> executorControllerAsyncHook(@RequestBody Object param) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AtomicInteger count = new AtomicInteger(0);
        ExecutorsUtils.singleThreadPool().submit(new Runnable() {
            @Override
            public void run() {
                while(count.get() <= 60) {
                    if (count.get() % 10 == 0) {
                        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  当前count为：{}，执行时间：{}", count, sdf.format(calendar.getTime()));
                    }
                    count.getAndIncrement();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("线程休眠异常");
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return new Response<String>().success();
    }

    @PostMapping("/execute/hook/sync")
    public Response<String> executorControllerHook(@RequestBody Object param) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AtomicInteger count = new AtomicInteger(0);
        while(count.get() <= 30) {
            if (count.get() % 10 == 0) {
                logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  当前count为：{}，执行时间：{}", count, sdf.format(calendar.getTime()));
            }
            count.getAndIncrement();
            try {
                Thread.sleep(1000);
                // yewu
                System.out.println("=====================");
            } catch (InterruptedException e) {
                logger.error("线程休眠异常");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("成功执行到此处");
        return new Response<String>().success();
    }

    @GetMapping("/app/status")
    public Response<String> getAppRunStatus() {
        Response<String> response = new Response<>();
        try {
            String runStatus = ShutdownTraceHelper.getAppRunStatus();
            if (!StringUtils.hasText(runStatus)) {
                logger.warn("当前未获取指定环境变量RUN_STATUS");
                response.setResult(RunStatusEnum.UNKNOW.getStatus());
                response.setMsg("当前环境变量值设置有误，无法确认运行状态");
                return response;
            }
            RunStatusEnum statusEnum = RunStatusEnum.of(runStatus);
            if (Objects.isNull(statusEnum)) {
                logger.warn("当前环境变量设置值有误，请确认");
                response.setResult(RunStatusEnum.UNKNOW.getStatus());
                response.setMsg("当前环境变量值设置有误，无法确认运行状态");
                return response;
            }
            response.setResult(statusEnum.getStatus());
        } catch (Exception ee) {
            logger.error("执行获取系统环境变量异常");
            response.setCode(HttpConstant.Code.INTERNAL_SERVER_ERROR);
            response.setResult(RunStatusEnum.UNKNOW.getStatus());
            response.setMsg("执行获取系统环境变量异常");
        }
        return response;
    }

    @GetMapping("/controller/status")
    public Response<String> getControllerStatus() {
        Response<String> response = new Response<>();
        try {
            Integer runRequestNum = GracefulShutdownControllerAspect.getActiveRequest().get();
            // 向数据库中进行更新当前仍在运行的请求个数
        } catch (Exception ee) {
            logger.error("异常", ee);
        }
        return response;
    }

    @GetMapping("/async/status")
    public Response<String> getAsyncStatus() {
        Response<String> response = new Response<>();
        try {
            AsncThreadMonitorHook monitorHook = new AsncThreadMonitorHook();
            monitorHook.run();
            // 向数据库中进行更新当前仍在运行的请求个数
        } catch (Exception ee) {
            logger.error("异常", ee);
        }
        return response;
    }
}
