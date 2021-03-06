package com.springboot.miaosha.service;

import com.springboot.miaosha.domain.Goods;
import com.springboot.miaosha.domain.MiaoshaUser;
import com.springboot.miaosha.domain.OrderInfo;
import com.springboot.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: lei
 * @Description:
 * @Date: Created in 2019-02-28 15:46
 */

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减库存，下订单，写入秒杀订单
        goodsService.reduceStock(goods);

        //order_info, miaosha_order
        return orderService.createOrder(user, goods);
    }


}
