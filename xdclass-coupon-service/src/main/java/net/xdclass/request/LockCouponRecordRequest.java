package net.xdclass.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@ApiModel(value = "优惠券锁定对象",description = "优惠券锁定对象")
@Data
public class LockCouponRecordRequest {


    /**
     * 优惠券记录id列表
     */
    @ApiModelProperty(value = "优惠券记录id列表",example = "[1,2,3]")
    private List<Long> lockCouponRecordIds;


    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号",example = "3234fw234rfd232")
    private String orderOutTradeNo;

}
