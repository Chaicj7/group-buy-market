package com.chaicj.domain.activity.model.valobj;

import com.chaicj.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityDiscountVO {

    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 折扣配置
     */
    private GroupBuyDiscountVO groupBuyDiscount;
    /**
     * 商品信息
     */
    private SCSkuActivityVO scSkuActivity;
    /**
     * 拼团方式（0自动成团、1达成目标拼团）
     */
    private Integer groupType;
    /**
     * 拼团次数限制
     */
    private Integer takeLimitCount;
    /**
     * 拼团目标
     */
    private Integer target;
    /**
     * 拼团时长（分钟）
     */
    private Integer validTime;
    /**
     * 活动状态（0创建、1生效、2过期、3废弃）
     */
    private Integer status;
    /**
     * 活动开始时间
     */
    private Date startTime;
    /**
     * 活动结束时间
     */
    private Date endTime;
    /**
     * 人群标签规则标识
     */
    private String tagId;
    /**
     * 人群标签规则范围
     */
    private String tagScope;

    public boolean isVisible() {
        if(StringUtils.isBlank(this.tagScope)) return TagScopeEnumVO.VISIBLE.getAllow();
        String[] split = this.tagScope.split(Constants.SPLIT);
        if (split.length > 0 && Objects.equals(split[0], "1") && StringUtils.isNotBlank(split[0])) {
            return TagScopeEnumVO.VISIBLE.getRefuse();
        }
        return TagScopeEnumVO.VISIBLE.getAllow();
    }

    public boolean isEnable() {
        if(StringUtils.isBlank(this.tagScope)) return TagScopeEnumVO.VISIBLE.getAllow();
        String[] split = this.tagScope.split(Constants.SPLIT);
        if (split.length == 2 && Objects.equals(split[1], "2") && StringUtils.isNotBlank(split[1])) {
            return TagScopeEnumVO.ENABLE.getRefuse();
        }
        return TagScopeEnumVO.ENABLE.getAllow();
    }
}
