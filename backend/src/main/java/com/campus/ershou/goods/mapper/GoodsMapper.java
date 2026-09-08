package com.campus.ershou.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ershou.goods.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 扣减库存：stock >= 购买数量才成功。影响行数为 0 表示超卖，下单必须失败。
     */
    @Update("UPDATE goods SET stock = stock - #{quantity}, version = version + 1 "
            + "WHERE id = #{goodsId} AND stock >= #{quantity} AND deleted = 0")
    int deductStock(@Param("goodsId") Long goodsId, @Param("quantity") int quantity);
}
