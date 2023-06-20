package net.xdclass.model;

import lombok.Data;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Data
public class ProductMessage {


    /**
     * 消息队列id
     */
    private long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 库存锁定taskId
     */
    private long taskId;
}
