package net.xdclass.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

public class OrderItemVO {

    /**
     * 商品id
     */
    @JsonProperty("product_id")
    private Long productId;


    /**
     * 购买数量
     */
    @JsonProperty("buy_num")
    private Integer buyNum;

    /**
     * 商品标题
     */
    @JsonProperty("product_title")
    private String productTitle;

    /**
     * 图片
     */
    @JsonProperty("product_img")
    private String productImg;

    /**
     * 商品单价
     */
    private BigDecimal amount;

    /**
     * 总价格，单价+数量
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getBuyNum() {
        return buyNum;
    }

    public void setBuyNum(Integer buyNum) {
        this.buyNum = buyNum;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 商品单价 * 购买数量
     * @return
     */
    public BigDecimal getTotalAmount() {

        return this.amount.multiply(new BigDecimal(this.buyNum));
    }

    @Override
    public String toString() {
        return "OrderItemVO{" +
                "productId=" + productId +
                ", buyNum=" + buyNum +
                ", productTitle='" + productTitle + '\'' +
                ", productImg='" + productImg + '\'' +
                ", amount=" + amount +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
