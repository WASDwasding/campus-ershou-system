package com.campus.ershou.goods;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

@Mapper
public interface GoodsMapper {

    String GOODS_COLUMNS = """
            g.id, g.seller_id, g.title, g.description, g.category_id, g.condition_code, g.price,
            g.original_price, g.meetup_building_id, g.trade_note, g.status, g.stock, g.version,
            g.view_count, g.favorite_count, g.removed_reason, g.created_at, g.updated_at
            """;

    String LIST_FILTER = """
            <if test="keyword != null">
              AND (g.title LIKE #{keyword} ESCAPE '\\\\' OR IFNULL(g.description, '') LIKE #{keyword} ESCAPE '\\\\')
            </if>
            <if test="categoryId != null">AND g.category_id = #{categoryId}</if>
            <if test="condition != null">AND g.condition_code = #{condition}</if>
            <if test="minPrice != null">AND g.price &gt;= #{minPrice}</if>
            <if test="maxPrice != null">AND g.price &lt;= #{maxPrice}</if>
            <if test="meetupBuildingId != null">AND g.meetup_building_id = #{meetupBuildingId}</if>
            <if test="areaId != null">AND b.area_id = #{areaId}</if>
            """;

    @Insert("""
            INSERT INTO goods (
                seller_id, title, description, category_id, condition_code, price, original_price,
                meetup_building_id, trade_note, status, stock, version
            ) VALUES (
                #{sellerId}, #{title}, #{description}, #{categoryId}, #{conditionCode}, #{price}, #{originalPrice},
                #{meetupBuildingId}, #{tradeNote}, #{status}, #{stock}, #{version}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GoodsRow goods);

    @Select("SELECT " + GOODS_COLUMNS + " FROM goods g WHERE g.id = #{id}")
    GoodsRow findById(long id);

    @Select("""
            <script>
            SELECT
            """ + GOODS_COLUMNS + """
            FROM goods g
            INNER JOIN buildings b ON b.id = g.meetup_building_id
            WHERE g.status = 'ON_SALE'
            """ + LIST_FILTER + """
            ORDER BY
            <choose>
              <when test="sort == 'price_asc'">g.price ASC, g.id DESC</when>
              <when test="sort == 'price_desc'">g.price DESC, g.id DESC</when>
              <when test="sort == 'nearby'">
                CASE
                  WHEN #{dormBuildingId} IS NOT NULL AND g.meetup_building_id = #{dormBuildingId} THEN 0
                  WHEN #{dormAreaId} IS NOT NULL AND b.area_id = #{dormAreaId} THEN 1
                  ELSE 2
                END,
                g.id DESC
              </when>
              <otherwise>g.id DESC</otherwise>
            </choose>
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<GoodsRow> listOnSale(GoodsListQuery query);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM goods g
            INNER JOIN buildings b ON b.id = g.meetup_building_id
            WHERE g.status = 'ON_SALE'
            """ + LIST_FILTER + """
            </script>
            """)
    long countOnSale(GoodsListQuery query);

    @Select("""
            <script>
            SELECT
            """ + GOODS_COLUMNS + """
            FROM goods g
            WHERE g.seller_id = #{sellerId}
              AND g.status != 'DELETED'
              <if test="status != null">AND g.status = #{status}</if>
            ORDER BY g.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<GoodsRow> listMine(
            @Param("sellerId") long sellerId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM goods
            WHERE seller_id = #{sellerId}
              AND status != 'DELETED'
              <if test="status != null">AND status = #{status}</if>
            </script>
            """)
    long countMine(@Param("sellerId") long sellerId, @Param("status") String status);

    @Select("""
            SELECT
            """ + GOODS_COLUMNS + """
            FROM goods g
            WHERE g.seller_id = #{sellerId} AND g.status = #{status}
            ORDER BY g.id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<GoodsRow> listBySellerStatus(
            @Param("sellerId") long sellerId,
            @Param("status") String status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Select("""
            SELECT COUNT(*) FROM goods WHERE seller_id = #{sellerId} AND status = #{status}
            """)
    long countBySellerStatus(@Param("sellerId") long sellerId, @Param("status") String status);

    @Select("""
            <script>
            SELECT
            """ + GOODS_COLUMNS + """
            FROM goods g
            WHERE g.id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<GoodsRow> findByIds(@Param("ids") Collection<Long> ids);

    @Update("""
            UPDATE goods
            SET title = #{title},
                description = #{description},
                category_id = #{categoryId},
                condition_code = #{conditionCode},
                price = #{price},
                original_price = #{originalPrice},
                meetup_building_id = #{meetupBuildingId},
                trade_note = #{tradeNote},
                version = version + 1
            WHERE id = #{id} AND status = #{status}
            """)
    int updateContent(GoodsRow goods);

    @Update("""
            UPDATE goods
            SET status = #{toStatus}, version = version + 1
            WHERE id = #{id} AND seller_id = #{sellerId} AND status = #{fromStatus}
            """)
    int changeStatus(
            @Param("id") long id,
            @Param("sellerId") long sellerId,
            @Param("fromStatus") String fromStatus,
            @Param("toStatus") String toStatus
    );

    @Update("""
            UPDATE goods
            SET status = 'RESERVED', stock = stock - 1, version = version + 1
            WHERE id = #{id} AND status = 'ON_SALE' AND stock >= 1
            """)
    int reserve(long id);

    @Update("""
            UPDATE goods
            SET status = 'ON_SALE', stock = 1, version = version + 1
            WHERE id = #{id} AND status = 'RESERVED'
            """)
    int releaseReserved(long id);

    @Update("""
            UPDATE goods
            SET status = 'SOLD', version = version + 1
            WHERE id = #{id} AND status = 'RESERVED'
            """)
    int markSold(long id);

    @Update("""
            UPDATE goods
            SET status = 'OFF_SHELF', stock = 1, version = version + 1
            WHERE id = #{id} AND status = 'RESERVED'
            """)
    int offShelfReserved(long id);

    @Update("""
            UPDATE goods
            SET status = 'REMOVED', removed_reason = #{reason}, version = version + 1
            WHERE id = #{id} AND status = #{fromStatus}
            """)
    int removeFrom(@Param("id") long id, @Param("fromStatus") String fromStatus, @Param("reason") String reason);

    @Update("""
            UPDATE goods
            SET status = 'OFF_SHELF', removed_reason = NULL, version = version + 1
            WHERE id = #{id} AND status = 'REMOVED'
            """)
    int restoreRemoved(long id);

    @Update("""
            UPDATE goods
            SET status = 'OFF_SHELF', version = version + 1
            WHERE seller_id = #{sellerId} AND status = 'ON_SALE'
            """)
    int offShelfOnSaleBySeller(long sellerId);

    @Update("UPDATE goods SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementView(long id);

    @Update("""
            UPDATE goods
            SET favorite_count = favorite_count + #{delta}
            WHERE id = #{id} AND favorite_count + #{delta} >= 0
            """)
    int addFavoriteCount(@Param("id") long id, @Param("delta") int delta);

    @Insert("INSERT INTO favorites (user_id, goods_id) VALUES (#{userId}, #{goodsId})")
    int insertFavorite(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND goods_id = #{goodsId}")
    int deleteFavorite(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId} AND goods_id = #{goodsId}")
    int favoriteExists(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId}")
    long countFavorites(long userId);

    @Select("""
            <script>
            SELECT goods_id FROM favorites
            WHERE user_id = #{userId}
              AND goods_id IN
            <foreach collection="goodsIds" item="id" open="(" separator="," close=")">#{id}</foreach>
            </script>
            """)
    List<Long> favoritedIds(@Param("userId") long userId, @Param("goodsIds") Collection<Long> goodsIds);

    @Select("""
            SELECT f.goods_id
            FROM favorites f
            INNER JOIN goods g ON g.id = f.goods_id
            WHERE f.user_id = #{userId} AND g.status != 'DELETED'
            ORDER BY f.created_at DESC, f.goods_id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    List<Long> listFavoriteIds(@Param("userId") long userId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("""
            SELECT COUNT(*)
            FROM favorites f
            INNER JOIN goods g ON g.id = f.goods_id
            WHERE f.user_id = #{userId} AND g.status != 'DELETED'
            """)
    long countVisibleFavorites(long userId);

    @Delete("DELETE FROM goods_images WHERE goods_id = #{goodsId}")
    int deleteImages(long goodsId);

    @Insert("INSERT INTO goods_images (goods_id, file_id, sort) VALUES (#{goodsId}, #{fileId}, #{sort})")
    int insertImage(GoodsImageRow image);

    @Select("""
            <script>
            SELECT goods_id, file_id, sort
            FROM goods_images
            WHERE goods_id IN
            <foreach collection="goodsIds" item="id" open="(" separator="," close=")">#{id}</foreach>
            ORDER BY goods_id ASC, sort ASC
            </script>
            """)
    List<GoodsImageRow> listImages(@Param("goodsIds") Collection<Long> goodsIds);

    @Select("""
            <script>
            SELECT
            """ + GOODS_COLUMNS + """
            FROM goods g
            WHERE 1 = 1
            <if test="status != null">AND g.status = #{status}</if>
            <if test="sellerId != null">AND g.seller_id = #{sellerId}</if>
            <if test="keyword != null">AND g.title LIKE #{keyword} ESCAPE '\\\\'</if>
            ORDER BY g.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<GoodsRow> adminList(
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("sellerId") Long sellerId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    @Select("""
            <script>
            SELECT COUNT(*) FROM goods g
            WHERE 1 = 1
            <if test="status != null">AND g.status = #{status}</if>
            <if test="sellerId != null">AND g.seller_id = #{sellerId}</if>
            <if test="keyword != null">AND g.title LIKE #{keyword} ESCAPE '\\\\'</if>
            </script>
            """)
    long adminCount(@Param("status") String status, @Param("keyword") String keyword, @Param("sellerId") Long sellerId);
}
