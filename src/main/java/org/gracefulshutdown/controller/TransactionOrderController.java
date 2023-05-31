package org.gracefulshutdown.controller;

import org.gracefulshutdown.http.Response;
import org.gracefulshutdown.thread.util.ExecutorsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/graceful/shutdown")
public class TransactionOrderController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionOrderController.class);

    private static final AtomicInteger count = new AtomicInteger(0);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/execute/hook")
    public Response<String> executorControllerHook(@RequestBody Object param) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
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
}
